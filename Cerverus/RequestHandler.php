<?php

class NodeUtility{
    
    public static function getNodeByID($id, $client){
        $node = $client->getNode($id);
	if ($node == NULL)  {
            /* Should we handle errors elsewhere? */
            echo json_encode(array('errorID'=>'XX', 'errorMessage'=>$id . ' is an unrecognized node ID in the database'));
            return NULL;
	} else {
            return $node;
	}
    }
    
    public static function storeNodeInDatabase($node){
        if ($node == NULL) {return false;}
        $node->save();
    }
    
    public static function deleteNodeFromDatabase($node){
        if ($node == NULL) {return false;}
        $this->deleteAllNodeRelationships($node); 
        $node->delete();
        return true;
    }
    
    public static function getAllNodeRelations($node){
        if ($node == NULL) {return false;}
        $relations = array();
        foreach($node->getRelations() as $rel){
            $relations->add($rel);
        }
        return $relations;        
    }
    
    public static function getNodeRelations($node, $relationName, $direction){
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
    
    public static function getNodesFromRelations($relations, $direction){
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
    
    public static function deleteAllNodeRelations($node){
        $relationArray = $node->getRelationships();
	foreach($relationArray as $rel) {
			$rel->delete();
	}
    }
    
    public static function nodeTypeCheck($node, $nodeTypeShouldBe){
        return ($node == $nodeTypeShouldBe);
    }
    
    public static function setPropertyFromList($node, $nodeProperty, $listProperty, $list){
        $node->setProperty($nodeProperty, $list->$listProperty);
    }
}

abstract class RequestHandler{
    
    //properties
    protected $me;  //represents the node; is this necessary, or do nodes only
                    //exist for the duration of a single handleRequest?
    protected $client;
    
    protected $nodeType;
                            
    function __construct($client){
        $this->client = $client;
    }
    
    //returns a json string of the contents of the node
    protected abstract function nodeToOutput();
    // sets the properties of a node from a list (for creating a new node)
    protected abstract function setNodeProperties($node, $postList);
    
    //requests
    public function GET($id){
        $node = NodeUtility::getNodeByID($id);
        if (!NodeUtility::nodeTypeCheck($node, $this->nodeType)) {return false;} //make fancier message
        return nodeToOutput($node);
    }
    
    // does not store relationships; overwrite 
    public function PUT($putList)
    {
        $id = $putList["id"];
        $field = $putList["field"];
        $value = $putList["value"];
        
        $node = NodeUtility::getNodeByID($id);
        if (NodeUtility::isValidField($node, $field)){
            $node->setProperty($field, $value);
            NodeUtility::storeNodeInDatabase($node);        
            return nodeToOutput($node);
        }
        return false; //TODO fancy error message / exception
    }
    
    public function POST($postList)
    {
        $node = $this->client->makeNode();
        // make sure it passes by reference
        NodeUtility::setNodeProperties($node, $postList);
        NodeUtility::storeNodeInDatabase($node);
        return nodeToOutput($node);
    }
    
    public function DELETE($id, $nodeType)
    {
        $node = getNodeByID($id);
        if (!nodeTypeCheck($node, $nodeType)) {return false;} //make fancier message        
        deleteNodeFromDatabase(NodeUtility::getNodeByID($id));
        return true; //make fancy delete message
    }
    
}

class UserHandler extends RequestHandler{
    
    function __construct($client){
        parent::__construct($client);
        $this->nodeType = "USER";
    }
          
    protected function nodeToOutput(){
        
    }
            
    protected function setNodeProperties($node, $postList){
        
    }
}

class UserHandlerALL extends UserHandler{
    
    public function GET($id, $nodeType){
        /*
        $node = NodeUtility::getNodeByID($id);
        if (!NodeUtility::nodeTypeCheck($node, $nodeType)) {return false;} //make fancier message
        return nodeToOutput($node);
        */
        
        
    }
    
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