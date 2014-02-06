<?php
/**
 * Include the API PHP file for neo4j
 */
namespace Everyman\Neo4j;
require("phar://neo4jphp.phar");
require_once 'Neo4j.php';


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
 *        Create a graphDb connection 
 */

$client= new Client();

        //get the index
        $noteIndex = new Index\NodeIndex($client, 'Note');
        $noteIndex->save();
if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0){
        //createNote method
        
        //get the json string post content
        $postContent = json_decode(@file_get_contents('php://input'));

        //create the node
        $noteNode = $client->makeNode();
        //sets the property on the node
        $noteNode->setProperty('title', $postContent->title)
                ->setProperty('description', $postContent->description)
                ->setProperty('dateCreated', $postContent->dateCreated)
                ->setProperty('content', $postContent->content)
                ->setProperty('nodeType','Note')
                ->save();
        
        //relate the Note to the user who created it
        $user = $client->getNode($postContent->createdBy);
        $userRel = $user->relateTo($noteNode, 'CREATED')->save();
        $response= $noteIndex->add($noteNode, 'ID', $noteNode->getId());
        
        //TODO make this relate to a group, project, or meeting
        
        //get properties on the node
        $noteProps= $noteNode->getProperties();
        unset($noteProps['nodeType']);
        echo json_encode(array_merge(array("noteID"=>$noteNode->getID()), $noteProps));
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0){
        //getNoteInfo
        $noteNode=$client->getNode($_GET['noteID']);                        
        $createdByRel = $noteNode->getRelationships(array('CREATED'), Relationship::DirectionIn);
        $createdBy = $createdByRel[0]->getStartNode(); 
        $tempArray=$noteNode->getProperties();
      	unset($tempArray['nodeType']);
        echo json_encode(array_merge(array("noteID"=>$noteNode->getId()), $tempArray));
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'PUT')==0){
        //updateNote
        $postContent = json_decode(@file_get_contents('php://input'));
        
        $note=$client->getNode($postContent->noteID);

        if(sizeof($note) >0){
        	$array = $note->getProperties();
		if(array_key_exists('nodeType', $array)){
			if(strcasecmp($array['nodeType'], 'Note')!=0){
				echo json_encode(array('errorID'=>'11', 'errorMessage'=>$postContent->noteID.' is an not a note node.'));
				return 1;
			}
		} 
                //get userID of who created the node
                $createdByRel = $note->getRelationships(array('CREATED'), Relationship::DirectionIn);
                $createdBy = $createdByRel[0]->getStartNode();        
                $array = array("noteID"=>$note->getId(),"createdBy"=>$createdBy->getId());
                
                //edit the title property
                if (strcasecmp($postContent->field, 'title') ==0){
                        $note->setProperty('title', $postContent->value);
                        $note->save();
                        $tempArray=$note->getProperties();
                        unset($tempArray['nodeType']);
                        echo json_encode(array_merge($array, $tempArray));
                //edit the title property
                }else if(strcasecmp($postContent->field, 'description') ==0){
                        $note->setProperty('description', $postContent->value);
                        $note->save();
                        $tempArray=$note->getProperties();
                        unset($tempArray['nodeType']);
                        echo json_encode(array_merge($array, $tempArray));
                //edit the title property
                }else if(strcasecmp($postContent->field, 'content') ==0){
                        $note->setProperty('content', $postContent->value);
                        $note->save();
                        $tempArray=$note->getProperties();
                        unset($tempArray['nodeType']);
                        echo json_encode(array_merge($array, $tempArray));
                //tell the user they can't change the person who created the note
                }else if(strcasecmp($postContent->field, 'createdBy') ==0){
                        echo json_encode(array("errorID"=>"16", "errorMessage"=>"createdBy field is immutable."));
                //tell the user they can't change the date it was created
                }else if(strcasecmp($postContent->field, 'dateCreated') ==0){
                        echo json_encode(array("errorID"=>"16", "errorMessage"=>"dateCreated field is immutable."));
                //unrecognized paramater
                }else{
                        echo json_encode(array("errorID"=>"17", "errorMessage"=>$postContent->field." is an unrecognized field."));
                }
        //general 'you done fucked up' error message
        }else{
                echo json_encode(array('errorID' => '5', 'errorMessage'=>'Given node ID is not recognized in database'));
        }
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE')==0){
        //deleteNote
        //get the id
        $id=$_GET['noteID'];
        
        //get the node
        $node = $client->getNode($id);
        //make sure the node exists
        if($node != NULL){
        	$array = $note->getProperties();
		if(array_key_exists('nodeType', $array)){
			if(strcasecmp($array['nodeType'], 'Note')!=0){
				echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['noteID'].' is an not a note node.'));
				return 1;
			}
		} 
                //check if node has note index
                $note = $noteIndex->findOne('ID', ''.$id);
                                                
                //only delete the node if it's a note
                if($note != NULL){
						$array = $note->getProperties();
						if(array_key_exists('nodeType', $array)){
							if(strcasecmp($array['nodeType'], 'Note')!=0){
								echo json_encode(array('errorID'=>'11', 'errorMessage'=>$id.' is an not a note node.'));
								return 1;
							}
						}   
                        //get the relationships
                        $relations = $note->getRelationships();
                        foreach($relations as $rel){
                                //remove all relationships
                                $rel->delete();
                        }
                        
                        //delete node and return true
                        $note->delete();
                        $array = array('valid'=>'true');
                        echo json_encode($array);
                } else {
                        //return an error otherwise
                        $errorarray = array('errorID' => '11', 'errorMessage'=>'Given node ID is not a note node');
                        echo json_encode($errorarray);
                }
        } else {
                //return an error if ID doesn't point to a node
                $errorarray = array('errorID' => '5', 'errorMessage'=>'Given node ID is not recognized in database');
                echo json_encode($errorarray);
        }
}else{
        echo $_SERVER['REQUEST_METHOD'] ." request method not found";
}
?>
