<?php

abstract class NodeContainer{
    
    //properties
    protected $me;  //represents the node; is this necessary, or do nodes only
                    //exist for the duration of a single handleRequest?
    protected $client;
                            
    function __construct($client){
        $this->client = $client;
    }
    
    //common functions
    //!CONSIDER: make these functions static?
    protected function getNodeByID($id){
        $node = $this->client->getNode($id);
	if ($node == NULL)  {
            /* Should we handle errors elsewhere? */
            echo json_encode(array('errorID'=>'XX', 'errorMessage'=>$id . ' is an unrecognized node ID in the database'));
            return NULL;
	} else {
            return $node;
	}
    }
    
    protected function storeNodeInDatabase($node){
        if ($node == NULL) {return false;}
        $node->save();
    }
    
    protected function deleteNodeFromDatabase($node){
        if ($node == NULL) {return false;}
        $this->deleteAllNodeRelationships($node); 
        $node->delete();
        return true;
    }
    
    protected function getAllNodeRelations($node){
        if ($node == NULL) {return false;}
        $relations = array();
        foreach($node->getRelations() as $rel){
            $relations->add($rel);
        }
        return $relations;        
    }
    
    protected function getNodeRelations($node, $relationName, $direction){
        /*
        $relationArray = array();
        if ($direction == "IN") {
            $relationArray = $node->getRelationships(array(), Relationship::DirectionIn);
        } else if ($direction == "OUT") {
            $relationArray = $node->getRelationships(array(), Relationship::DirectionOut);
        } else {return false;}
        */
        
        $relationArray = $node->getRelationships(array(), Relationship::$direction);
        $specifiedRelation = array();
        
        foreach ($relationArray as $rel){
            if ($relationName == $rel->getType()) {
                $specifiedRelation->add($rel);
            }
        }
        
        return $specifiedRelation;
   }
    
    protected function getNodesFromRelations($relations, $direction){
        $nodeToRetrieve;
        if ($direction == "DirectionIn") {
            $nodeToRetrieve = getStartNode();
        } else if ($direction == "DirectionOut"){
            $nodeToRetrieve = getEndNode();
        } else {return false;}
        
        $relatedNodes = array();
        foreach ($relations as $rel){
            $relatedNodes->add($rel->nodeToRetrieve);
        }
        return $relatedNodes;
    }
    
    protected function deleteAllNodeRelations($node){
        $relationArray = $node->getRelationships();
	foreach($relationArray as $rel) {
			$rel->delete();
	}
    }
    
    protected function nodeTypeCheck($node, $nodeTypeShouldBe){
        return ($node == $nodeTypeShouldBe);
    }
    
    protected function setPropertyFromList($node, $nodeProperty, $listProperty, $list){
        $node->setProperty($nodeProperty, $list->$listProperty);
    }
    
    abstract protected function nodeToOutput();
    
    //requests
    public function handleGetRequest($id, $nodeType){
        $node = getNodeFromDatabase($id);
        if (!nodeTypeCheck($node, $nodeType)) {return false;}
        nodeToOutput($node);
        return true;
    }
    public abstract function handlePutRequest($putList);
    public abstract function handlePostRequest($postList);
    public abstract function handleDeleteRequest($id, $nodeType);
}

class UserContainer extends NodeContainer{
    
    protected function nodeToOutput(){}

    public function handlePutRequest($putList){}
    public function handlePostRequest($postList){
        //assume putList is a list of stuff
        //$userNode = $client->makeNode();
        $userNode;
        setPropertyFromList($userNode, 'email', 'email', $postList);
        setPropertyFromList($userNode, 'password', 'password', $postList);
        setPropertyFromList($userNode, 'phone', 'phone', $postList);
        setPropertyFromList($userNode, 'company', 'company', $postList);
        setPropertyFromList($userNode, 'title', 'title', $postList);
        setPropertyFromList($userNode, 'location', 'location', $postList);
        setPropertyFromList($userNode, 'name', 'name', $postList);
        $userNode->setProperty('nodeType','User');
        storeNodeInDatabase($userNode);
        return true;
    }
    public function handleDeleteRequest($id, $nodeType){}
    
}

class MeetingContainer extends NodeContainer{
    
    protected function nodeToOutput(){}
    
    public function handlePutRequest($putList){}
    public function handlePostRequest($postList){}
    public function handleDeleteRequest($id, $nodeType){}
    
}
    
    echo("Hello\n");

?>