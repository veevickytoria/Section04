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
			$userID = $userNode->getId();
			
			//load up the notifications into an array
			foreach($relationArray as $rel){
			
				$node = $rel->getStartNode();
				$tempArray=$node->getProperties();
				$array=array();
				$array['shouldNotify']=$tempArray['shouldNotify'];
				$array['whenToNotify']=$tempArray['whenToNotify'];
				$array['settings']=$tempArray['settings'];
				$array['nodeID']=strval($tempArray['nodeID']);
				$array['type']=$rel->getType();
		
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
    
		//check if node is for real
		if ($node != null){
	
		//check if node has user index
		$userNode = $userIndex->findOne('email', $node->getProperty('email'));
		
		//check if it is a user
		if($userNode != NULL){
		
			//get the notifications related
			$relationArray = $userNode->getRelationships(array('SETFOR'), Relationship::DirectionIn);
			
			//load up the notifications into an array
			
			$setti = $relationArray[0]->getStartNode();
        
			if(sizeof($setti) > 0){
                if(strcasecmp($postContent->field, 'shouldNotify') ==0){
                        $setti->setProperty('shouldNotify', $postContent->value);
                        $setti->save();
                        $array = $setti->getProperties();
                        $array['nodeID']=$setti->getId();
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'type') ==0){
                        $setti->setProperty('type', $postContent->value);
                        $setti->save();
                        $array = $setti->getProperties();
                        $array['nodeID']=$setti->getId();
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'nodeID') ==0){
                        $setti->setProperty('nodeID', $postContent->value);
                        $setti->save();
                        $array = $setti->getProperties();
                        $array['nodeID']=$setti->getId();
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'settings') ==0){
                        $setti->setProperty('settings', $postContent->value);
                        $setti->save();
                        $array = $setti->getProperties();
                        $array['nodeID']=$setti->getId();
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'whenToNotify') ==0){
                        $setti->setProperty('whenToNotify', $postContent->value);
                        $setti->save();
                        $array = $setti->getProperties();
                        $array['nodeID']=$setti->getId();
                        echo json_encode($array);
                }
        }
		}
		}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE') == 0){      
	//delete settings DELETE
	//get the id
	$id=$_GET['id'];
        
	//get the node
	$node = $client->getNode($id);
	//make sure the node exists
	if($node != NULL){
		//check if node has settings index
		$setti = $settiIndex->findOne('ID', ''.$id);
						
		//only delete the node if it's a note
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
			$errorarray = array('errorID' => '4', 'errorMessage'=>'Given node ID is not a settings node');
			echo json_encode($errorarray);
		}
	} else {
		//return an error if ID doesn't point to a node
		$errorarray = array('errorID' => '5', 'errorMessage'=>'Given node ID is not recognized in database');
		echo json_encode($errorarray);
	}
}else{
        echo $_SERVER['REQUEST_METHOD'] ." request method not found in UserSettings";
}
?>
