<?php

namespace Everyman\Neo4j;
require_once 'RequestHandler.php';

class UserGroups extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "Users", "email");
    }
	protected function nodeToOutput($node) {
        if ($node == NULL) {return false;} //make pretty exception
        $nodeInfo = array_merge(array("groupID"=>$node->getId()), $node->getProperties());
        return $nodeInfo;
        
    }
	public function GET($id1){
        //GET getUserMeetings
        $userNode = NodeUtility::getNodeByID($id1, $this->client);
        if ($userNode == NULL)
            return false;
		$membersRels = NodeUtility::getNodeRelations($userNode, "members","in");
		$membersArray = array();
		foreach($membersRels as $rel){
			$groupNode = $rel->getStartNode();
			$propertyArray = $this->nodeToOutput($groupNode);
			array_push($membersArray, $propertyArray);
		}
		$lastArray = array('groups'=>$membersArray);
		return $lastArray;
	}
	function addRelationsToArray($node, $infoArray){
		foreach($this->relationList as $relationType) {
			$relationList = NodeUtility::getNodeRelations($node, $relationType, "out");
			$infoArray;
			$node = $relationList[0]->getEndNode();
			
		}
        return $infoArray;
	}
}