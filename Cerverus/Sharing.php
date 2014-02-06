<?php
/**
 * Include the API PHP file for neo4j
 */
namespace Everyman\Neo4j;
require("phar://neo4jphp.phar");


    if (isset($_SERVER['HTTP_ORIGIN'])) {
        header("Access-Control-Allow-Origin: *");
        header('Access-Control-Allow-Credentials: true');
        header('Access-Control-Max-Age: 86400');    // cache for 1 day
    }

    // Access-Control headers are received during OPTIONS requests
    if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
        if (isset($_SERVER['HTTP_ACCESS_CONTROL_REQUEST_METHOD']))
            header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");         

        if (isset($_SERVER['HTTP_ACCESS_CONTROL_REQUEST_HEADERS']))
			header("Access-Control-Allow-Headers: GET, POST, PUT, DELETE, OPTIONS");
        exit(0);
    }

/**
 *	Create a graphDb connection 
 */
$client= new Client();

//get the note index
$noteIndex = new Index\NodeIndex($client, 'Note');
$noteIndex->save();
//get the user index
$userIndex = new Index\NodeIndex($client, 'Users');
$userIndex->save();

$permissionRel = 'PERMISSION_TO_MERGE_WITH';
$noteCreatedRel = 'CREATED';


if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0){
    //get the post content
    $postContent = json_decode(@file_get_contents('php://input'));
    $note = $client->getNode($postContent->noteID);
    //make sure node is in the DB
    $checker = checkIfNode($note);
    if(!$checker['valid']){
        var_dump($checker);
        echo json_encode($checker['message']);
        return;
    }
    //make sure node is a note
    $checker = checkIfIndex($noteIndex, 'ID', $note->getID());
    if(!$checker['valid']){
        echo json_encode($checker['message']);
        return;
    }
   

    $errors = array();
    $success = array();
    for($i=0; $i<count($postContent->users); $i++){
        $u = ((array)($postContent->users[$i]));
        $user = $client->getNode($u['userID']);
        
        //user not node
        $checker = checkIfNode($user);
        if(!$checker['valid']){            
            array_push($errors, $checker['message']);
            continue;
        }
        //user not user
        $checker = checkIfIndex($userIndex, 'email', $user->getProperty('email'));
        if(!$checker['valid']){            
            array_push($errors, $checker['message']);
            continue;
        }
        //already have relationshiip
        $checker = checkIfNodeInRels($user->getId(), $note, array($permissionRel), Relationship::DirectionIn, 'start');
        if($checker['valid']){                   
            array_push($errors, $checker['message']);
            continue;
        }
        //make the relation
        $user->relateTo($note, $permissionRel)->save();
        //store the success
        array_push($success, array('userID'=>''.$user->getId()));
    } 
    echo json_encode(array_merge(array("success"=>$success),array('errors'=>$errors)));
} 
else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE')==0) {
    
    //get note and make sure it's valid
    $note = $client->getNode($_GET['noteID']);    
    $checker = checkIfNode($note);    
    if(!$checker['valid']){
        echo json_encode($checker['message']);
        return;
    }
    $checker = checkIfIndex($noteIndex, 'ID', $note->getId());
    if(!$checker['valid']){
        echo json_encode($checker['message']);
        return;
    }
    
    //get user and make sure it's valid
    $user = $client->getNode($_GET['userID']);
    $checker = checkIfNode($user);    
    if(!$checker['valid']){
        echo json_encode($checker['message']);
        return;
    }
    $checker = checkIfIndex($userIndex, 'email', $user->getProperty('email'));
    if(!$checker['valid']){
        echo json_encode($checker['message']);
        return;
    }
    
    //remove the relationship between the two
    $rels = $note->getRelationships(array($permissionRel), Relationship::DirectionIn);
    foreach($rels as $rel){
        if($rel->getStartNode()->getId() == $user->getId()){
            $rel->delete();
            echo json_encode(array("successful"=>"successful"));
            return;
        }
    }
    echo json_encode(array("errorID"=>"15", "errorMessage"=>$user->getId()." didn't have permission to merge with ". $note->getId()));
}
else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0){
    //get the user node
    $user = $client->getNode($_GET['userID']);   
    //make sure user node
    $checker = checkIfNode($user);    
    if(!$checker['valid']){
        echo json_encode($checker['message']);
        return;
    }
    $checker = checkIfIndex($userIndex, 'email', $user->getProperty('email'));
    if(!$checker['valid']){
        echo json_encode($checker['message']);
        return;
    }
    
    $sharingWith = $user->getRelationships(array($permissionRel), Relationship::DirectionOut);
    $response = array();
    $foundUser = FALSE;
    foreach($sharingWith as $permission){
        $note = $permission->getEndNode();
        $user = $note->getFirstRelationship(array($noteCreatedRel), Relationship::DirectionIn)->getStartNode();
        //determine if the user is already in the array
        for($pos = 0; $pos < count($response); $pos++){
            //user exists in response, so update it
            if($response[$pos]['userID'] == $user->getId()){
                array_push($response[$pos]['notes'], array("noteID"=>"".$note->getId(), "noteTitle"=>$note->getProperty('title')));
                $foundUser = TRUE;
                break;
            }
        }
        //  {"users": [{"userID":"12", "notes":["1", "2"]}, {"userID":"11", "notes":[]}
        //user wasn't found in the response, so add it
        if(!$foundUser){
            $notes = array(array("noteID"=>"".$note->getId(), "noteTitle"=>$note->getProperty('title')));
            $userNotes = array("userID"=>"".$user->getId(), "userName"=>$user->getProperty('name'), "notes"=>$notes);
            array_push($response, $userNotes);
        }  
    }
    echo json_encode($response);
    //TODO implement getting users who gave you merge capabilies to a note
    //list of users sharing with me (id and name)
    //list notes thye're sharing (id and title)
}


//assumes the $nodeID is the startnode in the relationships
function nodeInArrayStart($nodeID, $relArray){
    foreach($relArray as $rel){
        if($rel->getStartNode()->getId() == $nodeID){
            return true;
        }
    }
    return false;
}

//assumes the $nodeID is the endnode in the relationship
function nodeInArrayEnd($nodeID, $relArray){
    foreach($relArray as $rel){
        if($rel->getEndNode()->getId() == $nodeID){
            return true;
        }
    }
    return false;
}

//checks if the nodeid given is in a set of relations taken from $nodeRelsFrom
function checkIfNodeInRels($nodeIDToCheck, $nodeRelsFrom, $relTypes, $direction, $endToCheck){
    if(strcasecmp($endToCheck, 'end') == 0){
        if(!nodeInArrayEnd($nodeIDToCheck, $nodeRelsFrom->getRelationships($relTypes, $direction))){
            return array('valid'=>FALSE);
        }
        return array('valid'=>TRUE,  'message'=>array('errorID'=>'13', 'errorMessage'=>$nodeIDToCheck.' already related to '.$nodeRelsFrom->getId()));
    } else if (strcasecmp($endToCheck, 'start') == 0){
        if(!nodeInArrayStart($nodeIDToCheck, $nodeRelsFrom->getRelationships($relTypes, $direction))){
            return array('valid'=>FALSE);
        }
        return array('valid'=>TRUE,  'message'=>array('errorID'=>'13', 'errorMessage'=>$nodeIDToCheck.' already related to '.$nodeRelsFrom->getId()));
    } else {
        return array('valid'=>FALSE, 'message'=>array('errorID'=>'14', 'errorMessage'=>'Direction not recognized in checkIfNodeInRels function'));
    }
}

//checks if a node is valid
function checkIfNode($node){
    if($node==NULL){
        return array('valid'=>FALSE, 'message'=>array('errorID'=>'5', 'errorMessage'=>$node->getId().' node ID is not recognized in database'));
    }
    return array('valid'=>TRUE);
}

//cheks if a node is part of an index
function checkIfIndex($index, $key, $value){
    if($index->findOne($key, $value) == NULL){
        return array('valid'=>FALSE, 'message'=>array('errorID'=>'11', 'errorMessage'=>$value.' is not of the node type '.$index->getName()));
    }
    return array('valid'=>TRUE);
}
