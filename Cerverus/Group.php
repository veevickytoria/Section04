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
$client = new Client();

	//get the group index
	$groupIndex = new Index\NodeIndex( $client , 'groups' );
	$groupIndex->save();
	
	//get the User index
	$userIndex = new Index\NodeIndex($client, 'Users');
	$userIndex->save();
	 
if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0 ){
	//creategroup
	//get the json string post content
	$postContent = json_decode( @file_get_contents( 'php://input' ));
	
	//create the node
	$groupNode = $client->makeNode();
	
	//sets the property on the node
	$groupNode->setProperty( 'groupTitle', $postContent->groupTitle );
	$groupNode->setProperty('nodeType','Group');
	
	//add the node in the db
	$groupNode->save();
	
	//add user relationships
	$userArray = $postContent->members;
    foreach($userArray as $item){
		$user = $client->getNode($item->userID);
		$array=$user->getProperties();
		if(array_key_exists('nodeType', $array)){
			if(strcasecmp($array['nodeType'], 'User')!=0){
				echo json_encode(array('errorID'=>'11', 'errorMessage'=>$item->userID.' is an not a user node.'));
				return 1;
			}
		} 
        $attRel = $groupNode->relateTo($user, 'MEMBER_OF_GROUP')->save();
    }	
		
	//add the index        
    $response= $groupIndex->add($groupNode, 'ID', $groupNode->getID());
	
	//return info on new groupNode
	//$outputArray=array();
	$outputArray=$groupNode->getProperties();
	$outputArray['groupID']=$groupNode->getId();
	
	//get relationships
	//Arrays are three levels deep
	// 1-outputArray: overall output array 
	// 2-userOutputArray: all related users
	// 3-uArray: individual users
	$relationArray = $groupNode->getRelationships(array(), Relationship::DirectionOut);
	$userOutputArray = array();
	$u=0;
	foreach($relationArray as $rel){
		$relType = $rel->getType();
		if($relType == 'MEMBER_OF_GROUP') {
			$userNode=$rel->getEndNode();
			$uArray = array();
			$uArray['userID']=$userNode->getId();
			$userOutputArray[$u++] = $uArray;
		} 
	}
	$outputArray['members'] = $userOutputArray;
	unset($outputArray['nodeType']);
	echo json_encode($outputArray);
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 ){
	//get groupInfo
	 $groupNode=$client->getNode($_GET['id']);
	 
	if ($groupNode != null){
	
		//return info on specified groupNode
		$outputArray=$groupNode->getProperties();
		if(array_key_exists('nodeType', $outputArray)){
			if(strcasecmp($outputArray['nodeType'], 'Group')!=0){
				echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a group node.'));
				return 1;
			}
		} 
		$outputArray['groupID']=$groupNode->getId();
		
		//get relationships
		//Arrays are three levels deep
		// 1-outputArray: overall output array 
		// 2-userOutputArray: all related users
		// 3-uArray: individual users
		$relationArray = $groupNode->getRelationships(array(), Relationship::DirectionOut);
		$userOutputArray = array();
		$u=0;
		foreach($relationArray as $rel){
			$relType = $rel->getType();
			if($relType == 'MEMBER_OF_GROUP') {
				$userNode=$rel->getEndNode();
				$uArray = array();
				$uArray['userID']=$userNode->getId();
				$array = $userNode->getProperties();
				if(array_key_exists('nodeType', $array)){
					if(strcasecmp($array['nodeType'], 'User')!=0){
						echo json_encode(array('errorID'=>'11', 'errorMessage'=>$userNode->getId().' is an not a user node.'));
						return 1;
					}
				} 
				$userOutputArray[$u++] = $uArray;
			} 
		}
		$outputArray['members'] = $userOutputArray;
		
		echo json_encode($outputArray);
	} else {
		echo "Node not found.";
	}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'PUT')==0){
	//updategroup
	$postContent = json_decode(@file_get_contents('php://input'));
	$groupNode=$client->getNode($postContent->groupID);
	$updated = 0;
	$array=$groupNode->getProperties();
		if(array_key_exists('nodeType', $array)){
			if(strcasecmp($array['nodeType'], 'Group')!=0){
				echo json_encode(array('errorID'=>'11', 'errorMessage'=>$postContent->groupID.' is an not a group node.'));
				return 1;
			}
		} 
	if (sizeof($groupNode) > 0){
		if(strcasecmp($postContent->field, 'groupTitle')==0){
			$groupNode->setProperty('groupTitle', $postContent->value);
			$groupNode->save();
			$updated = 1;
		}else if (strcasecmp($postContent->field, 'members')==0){
			$relationArray = $groupNode->getRelationships(array('MEMBER_OF_GROUP'), Relationship::DirectionOut);
			foreach($relationArray as $rel) {
				$rel->delete();
			}
			$userArray = $postContent->value;
			foreach($userArray as $item){
				$user = $client->getNode($item->userID);
				$attRel = $groupNode->relateTo($user, 'MEMBER_OF_GROUP')->save();
			}
			$updated = 1;
		}
		
		if ($updated==1){
			///return info on specified groupNode
			$outputArray=$groupNode->getProperties();
			$outputArray['groupID']=$groupNode->getId();
			
			//get relationships
			//Arrays are three levels deep
			// 1-outputArray: overall output array 
			// 2-userOutputArray: all related users
			// 3-uArray: individual users
			$relationArray = $groupNode->getRelationships(array(), Relationship::DirectionOut);
			$userOutputArray = array();
			$u=0;
			foreach($relationArray as $rel){
				$relType = $rel->getType();
				if($relType == 'MEMBER_OF_GROUP') {
					$userNode=$rel->getEndNode();
					$uArray = array();
					$uArray['userID']=$userNode->getId();
					$userOutputArray[$u++] = $uArray;
				} 
			}
			$outputArray['members'] = $userOutputArray;
			
			echo json_encode($outputArray);
		} else{
			echo "No node updated";
		}
	}else{
		echo "FALSE node not found";
	}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE')==0){
	//delete group DELETE
	//get the id
	$id=$_GET['id'];
        
	//get the node
	$node = $client->getNode($id);
	$array=$node->getProperties();
		if(array_key_exists('nodeType', $array)){
			if(strcasecmp($array['nodeType'], 'Group')!=0){
				echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a group node.'));
				return 1;
			}
		} 
	//make sure the node exists
	if($node != NULL){
		//check if node has group index
		$group = $groupIndex->findOne('ID', ''.$id);
						
		//only delete the node if it's a note
		if($group != NULL){
			//get the relationships
			$relations = $group->getRelationships();
			foreach($relations as $rel){
				//remove all relationships
				$rel->delete();
			}
			
			//delete node and return true
			$group->delete();
			$array = array('valid'=>'true');
			echo json_encode($array);
		} else {
			//return an error otherwise
			$errorarray = array('errorID' => '4', 'errorMessage'=>'Given node ID is not a group node');
			echo json_encode($errorarray);
		}
	} else {
		//return an error if ID doesn't point to a node
		$errorarray = array('errorID' => '5', 'errorMessage'=>'Given node ID is not recognized in database');
		echo json_encode($errorarray);
	}
}
?>
