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