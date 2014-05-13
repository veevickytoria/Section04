<?php

namespace Everyman\Neo4j;
require_once 'RequestHandler.php';

class UserComments extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "Users", "email");
    }
	protected function nodeToOutput($node) {
        if ($node == NULL) {return false;} //make pretty exception
        $nodeInfo = array_merge(array("commentID"=>$node->getId()), $node->getProperties());
        return $nodeInfo;
        
    }
	public function GET($id1){
        //GET getUsercomments
        $userNode = NodeUtility::getNodeByID($id1, $this->client);
        if ($userNode == NULL)
            return false;
		$commentRels = NodeUtility::getNodeRelations($userNode, "commentBy","in");
		$commentArray = array();
		foreach($commentRels as $rel){
			$commentNode = $rel->getStartNode();
			$propertyArray = $this->nodeToOutput($commentNode);
			array_push($commentArray, $propertyArray);
		}
		$lastArray = array('comments'=>$commentArray);
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