<?php
namespace Everyman\Neo4j;
require "phar://neo4jphp.phar";
require_once "RequestHandler.php";

class Group extends \Everyman\Neo4j\RequestHandler{
    const GROUP_RELATIONSHIP = "MEMBER_OF";
    const GROUP_DIRECTION = Relationship::DirectionIn;
    
    public function __construct($client){
        parent::__construct($client, "Groups", "ID");
        $this->propertyList = array("groupTitle");
        $this->relationList = array("members");
        $this->idName = "groupID";
    }
    
    protected function nodeToOutput($node){
        $nodeInfo = parent::nodeToOutput($node);
        $nodeInfo['members'] = array();
        foreach($node->getRelationships(array($this::GROUP_RELATIONSHIP), $this::GROUP_DIRECTION) as $rel){
            array_push($nodeInfo['members'], array('userID'=>$rel->getStartNode()->getId()));
        }
        return json_encode($nodeInfo);
    }    
    
    public function POST($postList) {
        $node = $this->client->makeNode();
        // make sure methods pass $node by reference
        $this->setNodeProperties($node, $postList);
        NodeUtility::storeNodeInDatabase($node);
        $this->index->add($node, $this->indexKey, $node->getId());
        $this->relateMembersToGroup($node, $postList->members);
        return $this->nodeToOutput($node);
    }
    
    public function PUT($putList){
        $idName =  $this->idName;
        $id = $putList->$idName;
        $field = $putList->field;
        $value = $putList->value;
        $node = NodeUtility::getNodeByID($id, $this->client);
        
        if (in_array($field, $this->propertyList)){
            $node->setProperty($field, $value);
            NodeUtility::storeNodeInDatabase($node);
        } else if (in_array($field, $this->relationList)){
            NodeUtility::deleteSpecificNodeRelations($node, array($this::GROUP_RELATIONSHIP), $this::GROUP_DIRECTION);
            $this->relateMembersToGroup($node, $putList->members);
        } else {
            return false;
        }
        return $this->nodeToOutput($node);
    }
    
    private function relateMembersToGroup($groupNode, $members){
        foreach($members as $member){
            $user = NodeUtility::getNodeByID($member->userID, $this->client);
            $user->relateTo($groupNode, $this::GROUP_RELATIONSHIP)->save();               
        }
    }
    
    
}//================this section is for testing with our test suite without controller==========

$postContent = json_decode(file_get_contents('php://input'));
$client = new Client();
$Group = new Group($client);


if (strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0 ){
    echo $Group->POST($postContent);
	
}else if( strcasecmp($_SERVER['REQUEST_METHOD'],'GET') == 0){
    echo $Group->GET($_GET['id']);
	
}else if( strcasecmp($_SERVER['REQUEST_METHOD'],'PUT') == 0){
    echo $Group->PUT($postContent);
	
}else if( strcasecmp($_SERVER['REQUEST_METHOD'],'DELETE') == 0){
    echo $Group->DELETE($_GET['id']);
}else{
    echo $_SERVER['REQUEST_METHOD'] ." request method not found";
}
 
