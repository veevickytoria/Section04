<?php
/**
 * Include the API PHP file for neo4j
 */
 
namespace Everyman\Neo4j;
require_once("phar://neo4jphp.phar");

/**
 *        Create a graphDb connection 
 */
$client= new Client();

        //get the index
		
$settiIndex = new Index\NodeIndex($client, 'UserSettings');
$settiIndex->save();
$userIndex = new Index\NodeIndex($client, 'Users');
$userIndex->save();

if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET') == 0){ 	

	//getnode
    $node=$client->getNode($_GET['id']);
    
	//check if node is for real
	if ($node != null){
	
		//check if node has user index
		$userNode = $userIndex->findOne('email', $node->getProperty('email'));
		
		//check if it is a user
		if($userNode != NULL){
		
			//get the notifications related
			$relationArray = $userNode->getRelationships(array('SETFOR'), Relationship::DirectionIn);
			
			
			//find statistics
			$i = 0;
			$userID = $userNode->getId();
			
			//load up the notifications into an array
			foreach($relationArray as $rel){
			
				$node = $rel->getStartNode();
				$tempArray=$node->getProperties();
				$array=array();
				$array['shouldNotify']=$tempArray['shouldNotify'];
				$array['whenToNotify']=$tempArray['whenToNotify'];
				$array['type']=$rel->getType();
				$array['tasks']=$tempArray['tasks'];
				$array['groups']=$tempArray['groups'];
				$array['meetings']=$tempArray['meetings'];
				$array['projects']=$tempArray['projects'];
				
				$i = $i + 1;
		
			}
			
			//echo array back
			echo json_encode($array);
		}	
	
	} else {
		echo json_encode(array('errorID'=>'8', 'errorMessage'=>'NotificationGet: Specified node does not exist.'));
	}
	
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'PUT') == 0){
        //updateNotification
        
        //get the json string post content
        $postContent = json_decode(@file_get_contents('php://input'));
        
        $node=$client->getNode($postContent->userID);
		if($node != null){
		$userNode = $userIndex->findOne('email',$node->getProperty('email'));
		if($userNode!=NULL)
		{
		
			//get the notifications related
			$relationArray = $userNode->getRelationships(array('SETFOR'), Relationship::DirectionIn);

			//load up the notifications into an array

			$setti = $relationArray[0]->getStartNode();
        if(sizeof($setti) > 0){
                if(strcasecmp($postContent->field, 'shouldNotify') ==0){
                        $setti->setProperty('shouldNotify', $postContent->value);
                        $setti->save();
                        $array = $setti->getProperties();
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'type') ==0){
                        $setti->setProperty('type', $postContent->value);
                        $setti->save();
                        $array = $setti->getProperties();
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'tasks') ==0){
                        $setti->setProperty('tasks', $postContent->value);
                        $setti->save();
                        $array = $setti->getProperties();
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'groups') ==0){
                        $setti->setProperty('groups', $postContent->value);
                        $setti->save();
                        $array = $setti->getProperties();
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'meetings') ==0){
                        $setti->setProperty('meetings', $postContent->value);
                        $setti->save();
                        $array = $setti->getProperties();
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'projects') ==0){
                        $setti->setProperty('projectss', $postContent->value);
                        $setti->save();
                        $array = $setti->getProperties();
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'whenToNotify') ==0){
                        $setti->setProperty('whenToNotify', $postContent->value);
                        $setti->save();
                        $array = $setti->getProperties();
                        echo json_encode($array);
                }
                        $array = $setti->getProperties();
                        echo json_encode($array);
		}
		}
		}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE') == 0){      
	//delete notification DELETE
        //get the id
        $id=$_GET['id'];
		deleteSettings($client, $id[0]);
        
}else{
        echo $_SERVER['REQUEST_METHOD'] ." request method not found in Notification";
}
function createSettings($client, $userID, $shouldNotify, $whenToNotify, $settings){
	//create notification POST
	//get the json string post content
	$settiIndex = new Index\NodeIndex($client, 'UserSettings');
	$settiIndex->save();
	$userIndex = new Index\NodeIndex($client, 'Users');
	$userIndex->save();
	//create the node
	$settiNode= $client->makeNode();
	$settiNode->save();
	//sets the property on the node
	$settiNode->setProperty('shouldNotify', $shouldNotify)->setProperty('whenToNotify', $whenToNotify)->
	setProperty('type', 'UserSettings')
			->setProperty('settingsID', $settiNode->getID())
			->setProperty('settings',$settings);
	
	//actually add the node in the db
	$settiNode->save();
	
	//create a relation to the user who made the meeting
	$user = $client->getNode($userID);
	$settiRel = $settiNode->relateTo($user, 'SETFOR')
			->save();
	
	//add the index        
	$response= $settiIndex->add($settiNode, 'ID', $settiNode->getID());
	
	//return the created meeting id
	$array=array();
	$array['settingsID']=$settiNode->getId();
	echo json_encode($array);
	return $settiNode->getID();
}
function deleteSettings($client, $userID){
        $settiIndex = new Index\NodeIndex($client, 'UserSettings');
        $settiIndex->save();
		$userIndex = new Index\NodeIndex($client, 'Users');
		$userIndex->save();
        //get the node
        $node = $client->getNode($userID);
        //make sure the node exists
        if($node != NULL){
                //check if node has notification index
                $setti = $settiIndex->findOne('ID', $node->getID());
                                
                //only delete the node if it's a notification
                if($setti != NULL){
                        //get the relationships
                        $relations = $setti->getRelationships();
                        foreach($relations as $rel){
                                //remove all relationships
                                $rel->delete();
                        }                
                        
                        //delete node and return true
                        $setti->delete();
                       $array = array('valid'=>'true');
 			echo json_encode($array);
                } else {
                        //return an error otherwise
                        $errorarray = array('errorID' => '4', 'errorMessage'=>'Given node ID is not a notification node');
			 //return an error otherwise
			echo '"errorID":"4", "errorMessage":"Given node ID is not a notification node"}';
 		}
		echo json_encode($errorarray);
	} else {
      //return an error if ID doesn't point to a node
		echo '{"errorID":"5", "errorMessage":"Given node ID is not recognized in database"}';
		$errorarray = array('errorID' => '5', 'errorMessage'=>'Given node ID is not recognized in database');
		echo json_encode($errorarray);
	}
}
?>
