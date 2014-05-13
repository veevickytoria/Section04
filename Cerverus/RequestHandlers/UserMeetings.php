<?php

namespace Everyman\Neo4j;
require_once 'RequestHandler.php';

class UserMeetings extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "Users", "email");
    }
	protected function nodeToOutput($node) {
        if ($node == NULL) {return false;} //make pretty exception
        $nodeInfo = array_merge(array("meetingID"=>$node->getId()), $node->getProperties());
        return $nodeInfo;
        
    }
	public function GET($id1){
        //GET getUserMeetings
        $userNode = NodeUtility::getNodeByID($id1, $this->client);
        if ($userNode == NULL)
            return false;
		$createdMeetingRels = NodeUtility::getNodeRelations($userNode, "createdBy","in");
		$attendedMeetingRels = NodeUtility::getNodeRelations($userNode, "attendance", "in");
		$meetingRels = $createdMeetingRels + $attendedMeetingRels;
		$meetingArray = array();
		foreach($meetingRels as $rel){
			$meetingNode = $rel->getStartNode();
			$propertyArray = $this->nodeToOutput($meetingNode);
			array_push($meetingArray, $propertyArray);
		}
		$lastArray = array('meetings'=>$meetingArray);
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