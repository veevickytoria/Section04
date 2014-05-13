<?php

namespace Everyman\Neo4j;
require_once 'RequestHandler.php';

class UserProjects extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "Users", "email");
    }
	protected function nodeToOutput($node) {
        if ($node == NULL) {return false;} //make pretty exception
        $nodeInfo = array("projectID"=>$node->getId());
        return $nodeInfo;
        
    }
	public function GET($id1){
        //GET getUserProjects
        $userNode = NodeUtility::getNodeByID($id1, $this->client);
        if ($userNode == NULL)
            return false;
		$membersRels = NodeUtility::getNodeRelations($userNode, "members","in");
		$membersArray = array();
		foreach($membersRels as $rel){
			$projectNode = $rel->getStartNode();
			$propertyArray = $this->nodeToOutput($projectNode);
			array_push($membersArray, $propertyArray);
		}
		$lastArray = array('projects'=>$membersArray);
		return $lastArray;
	}
	public function POST(){
		return "Cannot create or POST on this request. Try GET";
	}
	public function DELETE(){
		return "Cannot delete on this request. Try GET";
	}
	public function PUT(){
		return "Cannot update or PUT on this request. Try GET";
	}
}