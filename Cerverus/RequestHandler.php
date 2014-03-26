<?php
namespace Everyman\Neo4j;

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
        
        $relationArray = $node->getRelationships(array(), $direction);
        $specifiedRelation = array();
        
        foreach ($relationArray as $rel){
            if ($relationName == $rel->getType()) {
                array_push($specifiedRelation, $rel);
                //$specifiedRelation->add($rel);
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
    public static function deleteSpecificNodeRelations($node, $relationshipTypes, $direction){
        $relationArray = $node->getRelationships($relationshipTypes, $direction);
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
    
    public static function checkInIndex($node, $index, $indexKey="ID"){
        $match= $index->findOne($indexKey, $node->getId());
        return ($match != null);
    }
    
    /**
     * Updates the specified relation type of a given node. Deletes the old
     * relations of the given type, then adds new relations of the given type
     * with the given values
     * 
     * @param node $node    Node to update
     * @param String $relation  Type of relation to update
     * @param String $direction DirectionIn or DirectionOut
     * @param array $newValues  Array of nodes to be related with the given node
     * @return Boolean  True if successful
     */
    public static function updateRelation($node, $relationName, $direction, $newValues, $client){
        //delete old relations
        $relationArray = $node->getRelationships(array('relation'), Relationship::$direction);
        foreach($relationArray as $rel) {
            $rel->delete();
        }        
        //turn new values into an array if it is not already
        if (!is_array($newValues)){
            $newValues = array($newValues);
        }
        //add new relations
        foreach($newValues as $newVal){
          $relatedNode = NodeUtility::getNodeByID($newVal, $client);
          if ($direction == "DirectionIn"){              
              $relatedNode->relateTo($node, $relationName)->save();
          } else if ($direction == "DirectionOut"){
              $node->relateTo($relatedNode, $relationName)->save();
          }
        }
        return true;
    }
}

abstract class RequestHandler{
    
    /**
     * The client to be used for database requests.
     * 
     * @var Client 
     */
    protected $client;
    
    /**
     * The index used to keep track of nodes of this type.
     * 
     * @var Index
     */
    protected $index;
    
    /**
     * The name of the value used as a key to the index.
     * 
     * @var String
     */
    protected $indexKey;
    
    /**
     * 
     * Creates a new request handler given a client, name for the index, and
     * key for the index
     * 
     * @param Client $client    Client to be used
     * @param String $indexName Name of the index
     * @param String $indexKey  Key to the index
     */
    function __construct($client, $indexName, $indexKey){
        $this->client = $client;
        $this->index = new Index\NodeIndex($client, $indexName);
        $this->index->save();
        $this->indexKey = $indexKey;
    }
    
    //returns a json string of the contents of the node
    protected abstract function nodeToOutput($node);
    // sets a node's properties from a list (for creating a new node)
    protected abstract function setNodeProperties($node, $postList);
    // sets a node's relationships from a list (for creating a new node)
    protected abstract function setNodeRelationships($node, $postList);
    
    /**
     * The request for retrieving a node from the database and returning its
     * information.
     * 
     * @param int $id The id of the node to retrieve.
     * @return array 
     */
    public function GET($id){
        $node = NodeUtility::getNodeByID($id, $this->client);
        /*
        if (!NodeUtility::checkInIndex($node, $this->index, $this->indexKey))
            {return false;} //!!Use fancier error message
        */
        return $this->nodeToOutput($node);
    }
    
    /**
     * The request for editing a node which already exists in the database.
     * 
     * !Is currently not finished. We need to determine how to handle differen-
     * tiating between editing simple fields and editing relationships. In the
     * mean time, subclasses should overwrite this method and specify their
     * specific implementation.
     * 
     * @param array $putList The node to edit, the field to edit, and the value
     *      it will be edited to.
     * @return array Information about the edited node.
     */
    public abstract function PUT($putList);
    /*
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
    */
    
    /**
     * The request for adding a new node to the database. Uses the template
     * pattern to allow sub-classes to specify their class-specific behavior.
     * The methods setNodeProperties and setNodeRelationships are used in this
     * way.
     * 
     * @param array $postList The parameters used to specify the node.
     * @return array Information about the newly created node.
     */
    public function POST($postList)
    {
        $node = $this->client->makeNode();
        // make sure methods pass $node by reference
        $this->setNodeProperties($node, $postList);
        NodeUtility::storeNodeInDatabase($node);
        $this->setNodeRelationships($node, $postList, $this->client);
        $this->index->add($node, 'ID', $node->getId());
        return $this->nodeToOutput($node);
    }
    
    /**
     * Deletes a node from the database. First deletes all relationships related
     * to this node, then removes the node from the index, and finally deletes
     * the node itself.
     * 
     * @param int $id   ID of node to be deleted.
     * @return boolean  Indicates success or failure.
     */
    public function DELETE($id)
    {
        $node = getNodeByID($id);
        if (!NodeUtility::checkInIndex($node, $this->index, $this->indexKey))
            {return false;} //!!Use fancier error message   
        $this->index->remove($node);
        NodeUtility::deleteNodeFromDatabase(NodeUtility::getNodeByID($id));
        return true; //make fancy delete message
    }
    
    
    
}

?>