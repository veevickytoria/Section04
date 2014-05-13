<?php

namespace Everyman\Neo4j;
require_once 'RequestHandler.php';

class UserNotes extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "Users", "email");
    }
	protected function nodeToOutput($node) {
        if ($node == NULL) {return false;} //make pretty exception
        $nodeInfo = array_merge(array("noteID"=>$node->getId()), $node->getProperties());
        return $nodeInfo;
        
    }
	public function GET($id1){
        //GET getUserNotes
        $userNode = NodeUtility::getNodeByID($id1, $this->client);
        if ($userNode == NULL)
            return false;
		$noteRels = NodeUtility::getNodeRelations($userNode, "createdBy","in");
		$noteArray = array();
		foreach($noteRels as $rel){
			$noteNode = $rel->getStartNode();
			$propertyArray = $this->nodeToOutput($noteNode);
			array_push($noteArray, $propertyArray);
		}
		$lastArray = array('notes'=>$noteArray);
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