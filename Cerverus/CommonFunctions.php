<?php
/**
 * Common functions available for other files.
 */

namespace Everyman\Neo4j;
require("phar://neo4jphp.phar");

function getNodeFromRequest($client) {
	if (!isset($_GET['id'])) {
		echo json_encode(array('errorID'=>'XX', 'errorMessage'=>'ID was not set.'));
		return NULL;
	} else {
		return getNodeByID($_GET['id'], $client);
	}
}

function getNodeFromPostContent($name, $client) {
	/*
	$postContent = json_decode(@file_get_contents('php://input'));
	
	if ($postContent == NULL) {
		echo json_encode(array('errorID'=>'XX', 'errorMessage'=>'Post content was not provided.'));
		return NULL;
	}
	
	if (!isset($postContent->$name)) {
		echo json_encode(array('errorID'=>'XX', 'errorMessage'=>$name. ' was not set.'));
		return NULL;
	} else {
		return getNodeByID($postContent->$name, $client);
	}
	*/
	return getNodeByID(getValueFromPostContents($name), $client);
}

function getNodeByID($id, $client) {
	$node = $client->getNode($id);
	if ($node == NULL)  {
		echo json_encode(array('errorID'=>'XX', 'errorMessage'=>$id . ' is an unrecognized node ID in the database'));
		return NULL;
	} else {
		return $node;
	}
}

function getValueFromPostContents($valueName){
	$postContent = json_decode(@file_get_contents('php://input'));
	if ($postContent == NULL) {
		echo json_encode(array('errorID'=>'XX', 'errorMessage'=>'Post content was not provided.'));
		return NULL;
	}	
	
	if (!isset($postContent->$valueName)) {
		echo json_encode(array('errorID'=>'XX', 'errorMessage'=>$valueName. ' was not set.'));
		return NULL;
	} else {
		return $postContent->$valueName;
	}
}

function getContacts($userNode) {
	$outputArray = array();
	
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
				$uArray['relationID']=$rel->getId();
				$contactOutputArray[$u++] = $uArray;
			} 
		}
		$outputArray['contacts'] = $contactOutputArray;
	} else {
		$outputArray['contacts'] = "";
	}
	
	return $outputArray;
}
 
 function getRelatedNodeIDs($node, $relationValue, $relationName, $direction) {
	$outputArray = array();
	if ($node != NULL) {
		//$outputArray['nodeID']=$node->getId();
		if ($direction == "IN") {
			$relationArray = $node->getRelationships(array(), Relationship::DirectionIn);
		} else if ($direction == "OUT") {
			$relationArray = $node->getRelationships(array(), Relationship::DirectionOut);
		} else {
			return null;
		}
		$nodeOutputArray = array();
		$i = 0;
		foreach($relationArray as $rel){
			$relType = $rel->getType();
			if($relType == $relationValue) {
				if ($direction == "IN") {
					$relatedNode=$rel->getStartNode();
				} else if ($direction == "OUT") {
					$relatedNode=$rel->getEndNode();
				} else {
					return null;
				}
				$nArray = array();
				$nArray[$relationName]=$relatedNode->getId();
				$nodeOutputArray[$i++] = $nArray;
			}
		}
		return $nodeOutputArray;
		$outputArray['relatedNodes'] = $nodeOutputArray;
		
	} else {
		$outputArray['relatedNodes'] = "";
	}
	
	return $outputArray;
}

function getRelatedNodes($node, $relationValue, $direction) {
	$outputArray = array();
	if ($node != NULL) {
		//$outputArray['nodeID']=$node->getId();
		if ($direction == "IN") {
			$relationArray = $node->getRelationships(array(), Relationship::DirectionIn);
		} else if ($direction == "OUT") {
			$relationArray = $node->getRelationships(array(), Relationship::DirectionOut);
		} else {
			return null;
		}
		$nodeOutputArray = array();
		$i = 0;
		foreach($relationArray as $rel){
			$relType = $rel->getType();
			if($relType == $relationValue) {
				if ($direction == "IN") {
					$relatedNode=$rel->getStartNode();
				} else if ($direction == "OUT") {
					$relatedNode=$rel->getEndNode();
				} else {
					return null;
				}
				$nArray = array();
				//$nArray[$relationName]=$relatedNode->getId();
				$nodeOutputArray[$i++] = $relatedNode;
			}
		}
		return $nodeOutputArray;		
	} else {
		return $outputArray;
	}
	
	return $outputArray;
}

function getRelatedNodesWithRelations($node, $relationType, $relationName, $direction) {
	$outputArray = array();
	
	if ($node != NULL) {
		//output new contact list
		//$outputArray['nodeID']=$node->getId();
		if ($direction == "IN") {
			$relationArray = $node->getRelationships(array(), Relationship::DirectionIn);
		} else if ($direction == "OUT") {
			$relationArray = $node->getRelationships(array(), Relationship::DirectionOut);
		} else {
			return null;
		}
		$outputArray = array();
		$i = 0;
		foreach($relationArray as $rel){
			$relType = $rel->getType();
			if($relType == $relationType) {
				if ($direction == "IN") {
					$relatedNode=$rel->getStartNode();
				} else if ($direction == "OUT") {
					$relatedNode=$rel->getEndNode();
				} else {
					return null;
				}
				$nArray = array();
				$nArray[$relationName]=$relatedNode->getId();
				$nArray['relationID']=$rel->getId();
				$outputArray[$i++] = $nArray;
			}
		}
	} else {
		$outputArray['relatedNodes'] = "";
	}
	return $outputArray;
}
 
 
 function deleteNode($node, $type){
	//check if node exists
	
	//check if node is proper type
	
	//remove all relationships
	
	//delete node
 }
 
 function deleteAllRelationships($node){
	$relationArray = $node->getRelationships();
	foreach($relationArray as $rel) {
			$rel->delete();
	}
 }
 
/*
//getting post contents

function readPostContents(){

}

//creating a node
//params: type

	//abstract
		//new from postcontent
		
		//new from database
		//add to database
		//read from database
		//edit
		//delete from database
	abstract class NodeType{
		protected $ID;
		
		abstract function createFromPostContent();
		abstract function createFromDatabase();
		abstract function readFromDatabase();
		abstract function edit();
		abstract function delete();
		
	}
		
	//user
	class User extends NodeType{
		private $displayName;
		private $email;
		private $phone;
		private $company;
		private $title;
		private $location;
		
		public function readFromDatabase($nodeID, $client){
			
		}
		
	}
	//meeting
	//project - title, meetings, notes, members
	

//editing a node

//returning arrays

//
*/
?>