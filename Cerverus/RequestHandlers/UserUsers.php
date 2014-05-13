<?php

namespace Everyman\Neo4j;
require_once 'RequestHandler.php';

class UserUsers extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "Users", "email");
    }
	protected function nodeToOutput($node) {
        if ($node == NULL) {return false;} //make pretty exception
        $nodeInfo = array_merge(array("userID"=>$node->getId()), $node->getProperties());
        unset($nodeInfo['password']);
        return $nodeInfo;
        
    }
	public function GET(){
	        $response = array();
        foreach($this->index->query($this->indexKey.':*') as $user){
            array_push($response, $this->nodeToOutput($user));
        }
        return array("users"=>$response);
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