<?php

namespace Everyman\Neo4j;
require("phar://neo4jphp.phar");


/**
 *	Create a graphDb connection 
 */
$client = new Client();
	
	//get the User index
	$userIndex = new Index\NodeIndex($client, 'Users');
	$userIndex->save();

if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET') == 0){
	/*=================
	Get Contacts Method 
	==================*/
	
	if (!isset($_GET['id'])) {
		echo json_encode(array('errorID'=>'XX', 'errorMessage'=>'ID was not set. Usage: contact<forwardslash>@id.'));
	 } else {
	
		$userNode = $client->getNode($_GET['id']);
		
		if ($userNode != NULL) {
			//output new contact list
			$outputArray['userID']=$userNode->getId();
			$relationArray = $userNode->getRelationships(array(), Relationship::DirectionOut);
			$contactOutputArray = array();
			$u=0;
			foreach($relationArray as $rel){
				$relType = $rel->getType();
				if($relType == 'CONTACT') {
					$contactNode=$rel->getEndNode();
					$uArray = array();
					$uArray['contactID']=$contactNode->getId();
					$contactOutputArray[$u++] = $uArray;
				} 
			}
			$outputArray['contacts'] = $contactOutputArray;
			
			echo json_encode($outputArray);
		} else {
		echo json_encode(array('errorID'=>'XX', 'errorMessage'=>$_GET['id']. ' is an unrecognized node ID in the database'));
		}
	}

}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0){
	/*=================
	Set Contacts Method
	=================*/
	$postContent = json_decode(@file_get_contents('php://input'));
	$userNode = $client->getNode($postContent->userID);
	if ($userNode == null) {
		echo json_encode(array('errorID'=>'XX', 'errorMessage'=>$postContent->userID. ' is an unrecognized node ID in the database'));
	} else {

		//remove old contact relationships
		$relationArray = $userNode->getRelationships(array('CONTACT'), Relationship::DirectionOut);
		foreach($relationArray as $rel) {
			$rel->delete();
		}
		
		//add new contact relationships
		$contactArray = $postContent->contacts;
		foreach($contactArray as $item){
			$user = $client->getNode($item->contactID);
			$attRel = $userNode->relateTo($user, 'CONTACT')->save();
		}
		
		//output new contact list
		$outputArray['userID']=$userNode->getId();
		$relationArray = $userNode->getRelationships(array(), Relationship::DirectionOut);
			$contactOutputArray = array();
			$u=0;
			foreach($relationArray as $rel){
				$relType = $rel->getType();
				if($relType == 'CONTACT') {
					$contactNode=$rel->getEndNode();
					$uArray = array();
					$uArray['contactID']=$contactNode->getId();
					$contactOutputArray[$u++] = $uArray;
				} 
			}
			$outputArray['contacts'] = $contactOutputArray;
		
			echo json_encode($outputArray);
	}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'PUT')==0){		
	/*====================
	Append Contacts Method
	=====================*/
	$postContent = json_decode(@file_get_contents('php://input'));
	$userNode = $client->getNode($postContent->userID);
	if ($userNode == null) {
		echo json_encode(array('errorID'=>'XX', 'errorMessage'=>$postContent->userID. 'is an unrecognized node ID in the database'));
	} else {
		//add new contact relationships
		$contactArray = $postContent->contacts;
		foreach($contactArray as $item){
			$user = $client->getNode($item->contactID);
			$attRel = $userNode->relateTo($user, 'CONTACT')->save();
		}
		
		//output new contact list
		$outputArray['userID']=$userNode->getId();
		$relationArray = $userNode->getRelationships(array(), Relationship::DirectionOut);
		$contactOutputArray = array();
		$u=0;
		foreach($relationArray as $rel){
			$relType = $rel->getType();
			if($relType == 'CONTACT') {
				$contactNode=$rel->getEndNode();
				$uArray = array();
				$uArray['contactID']=$contactNode->getId();
				$contactOutputArray[$u++] = $uArray;
			} 
		}
		$outputArray['contacts'] = $contactOutputArray;
	
		echo json_encode($outputArray);		
	}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE')==0){		
	/*====================
	Delete Contacts Method
	=====================*/
	if (!isset($_GET['id'])) {
		echo json_encode(array('errorID'=>'XX', 'errorMessage'=>'ID was not set. Usage: contact<forwardslash>@id.'));
	} else {
		$id = $_GET['id'];
		$userNode = $client->getNode($_GET['id']);
	
		if ($userNode == null) {
			echo json_encode(array('errorID'=>'XX', 'errorMessage'=>$id. ' is an unrecognized node ID in the database'));
		} else {
			//remove all contact relationships
			$relationArray = $userNode->getRelationships(array('CONTACT'), Relationship::DirectionOut);
			foreach($relationArray as $rel) {
				$rel->delete();
			}
			
			//output new contact list
			$outputArray['userID']=$userNode->getId();
			$relationArray = $userNode->getRelationships(array(), Relationship::DirectionOut);
			$contactOutputArray = array();
			$u=0;
			foreach($relationArray as $rel){
				$relType = $rel->getType();
				if($relType == 'CONTACT') {
					$contactNode=$rel->getEndNode();
					$uArray = array();
					$uArray['contactID']=$contactNode->getId();
					$contactOutputArray[$u++] = $uArray;
				} 
			}
			$outputArray['contacts'] = $contactOutputArray;
		
			echo json_encode($outputArray);		
		}
	}
}

?>