<?php

namespace Everyman\Neo4j;

require_once "NodeUtility.php";

abstract class RequestHandler {

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
     * The list of properties to be stored in the node (e.g. title, description)
     * @var array<string>
     */
    protected $propertyList = array();

    /**
     * The list of relationships to be stored with the node (e.g. createdBy,
     * memberOf). In the format relationList<nameOfRelation, nameOfRelation,...>
     * e.g. relationList<createdBy,ownedBy>
     * @var array<string>
     */
    protected $relationList = array();
    
    /**
     * The list of relationships with multiple values to be stored with the
     * node (e.g. attendance, members). In the format:
     * nestedRelationList<<nameOfRelation, nameOfIndividual>, ...>
     * e.g. nestedRelationList<<meetings, meetingID>,<notes, noteID>>
     * @var array<array<string, string>> 
     */
    protected $nestedRelationList = array();

    /**
     * The name of the ID field for this node (e.g. userID, noteID)
     * @var String
     */
    protected $idName;

    /**
     * Creates a new request handler given a client, name for the index, and
     * key for the index
     * 
     * @param Client $client    Client to be used
     * @param String $indexName Name of the index
     * @param String $indexKey  Key to the index
     */
    function __construct($client, $indexName, $indexKey) {
        $this->client = $client;
        $this->index = new Index\NodeIndex($client, $indexName);
        $this->index->save();
        $this->indexKey = $indexKey;
    }

    /**
     * Like a toString method, this function takes in a node and returns an
     * array of information about the given node. Includes properties and
     * related nodes. !TODO FINISH. CURRENTLY DOES NOT DEAL WITH RELATIONS
     * @param Node $node
     * @return array if successful, false if node is null
     */
    protected function nodeToOutput($node) {
        if ($node == NULL) {
            return false;
        } //TODO make pretty exception
        $nodeInfo = array();

        $nodeInfo[$this->idName] = $node->getId();

        foreach ($this->propertyList as $property) {
            $nodeInfo[$property] = $node->getProperty($property);
        }

        $nodeInfo = $this->addRelationsToArray($node, $nodeInfo);
        
        $nodeInfo = $this->addNestedRelationsToArray($node, $nodeInfo);

        return $nodeInfo;
        /*
          $checkedRelations = array();
          foreach (NodeUtility::getAllNodeRelations($node) as $relation){
          $relationName = $relation->getType();
          $relationDirection = $relation->getDirection();
          }
         */
        /*
          foreach (NodeUtility::getAllNodeRelations($node) as $relation){
          $nodeInfo[$relation->]
          $nodeInfo["name of relation"] = NodeUtility::getNodesFromRelations($relations, $direction)
          }
         */
        /*
          foreach ($this->relationList as $relation){
          $nodeInfo[$relation] = NodeUtility::getNodesFromRelations($relation, $direction);
          }
         */
        //$nodeInfo['createdBy'] = $node->getRe
    }

    /**
     * Add each relation of a given node to a given array
     * @param Node $node Node to get relations of
     * @param array $infoArray Array to add relations to
     */
    protected abstract function addRelationsToArray($node, $infoArray);

    /**
     * Add each nested relation of a given node to a given array
     * @param Node $node Node to get nested relations of
     * @param array $infoArray Array to add nested relations
     * TODO Make the nested items display properly
     *  this:       {"meetings":[{"meetingID":"##"},...]}
     *  not this:   {"meetings":["{meetingID:##:}",...]}
     */
    //protected abstract function addNestedRelationsToArray($node, $infoArray)
    protected function addNestedRelationsToArray($node, $infoArray) {
        $keys = array_keys($this->nestedRelationList);
        foreach($keys as $key) {
            $valueName = $this->nestedRelationList[$key];
            $relationList = NodeUtility::getNodeRelations($node, $key, "out");
            $relatedNodes = array();
            foreach($relationList as $relation){
                $item = array();
                $item[$valueName]=$relation->getEndNode()->getId();
                array_push($relatedNodes, $item);
            }
            $infoArray[$key] = $relatedNodes;
        }
        return $infoArray;
    }
    
    /**
     * sets a node's properties from a list (for creating a new node)
     * @param Node $node
     * @param array $postList
     */
    protected function setNodeProperties($node, $postList) {
        foreach ($this->propertyList as $property) {
            $node->setProperty($property, $postList[$property]);
        }
    }

    /**
     * Sets a node's relationships from a list (for creating a new node)
     * @param Node $node
     * @param array $postList
     */
    protected function setNodeRelationships($node, $postList) {
        foreach ($this->relationList as $relation) {
            $relatedID = $postList[$relation];
            $relatedNode = NodeUtility::getNodeByID($relatedID, $this->client);
            $node->relateTo($relatedNode, $relation)->save();
        }
    }
    
    protected function setNodeNestedRelationships($node, $postList){
        $keys = array_keys($this->nestedRelationList);
        foreach ($keys as $key){
            $relatedIDs = $postList[$key];
            $valueName = $this->nestedRelationList[$key];
            foreach ($relatedIDs as $relatedID){
                $relatedNode = NodeUtility::getNodeByID($relatedID->$valueName, $this->client);
                $node->relateTo($relatedNode, $key)->save();
            }
        }
    }

    /**
     * The request for retrieving a node from the database and returning its
     * information.
     * 
     * @param int $id The id of the node to retrieve.
     * @return array 
     */
    public function GET($id) {
        $node = NodeUtility::getNodeByID($id, $this->client);
        if ($node == NULL)
            return false;
        if (!NodeUtility::checkInIndex($node, $this->index, $this->indexKey)) {
            return false;
        } //TODO Use fancier error message

        return $this->nodeToOutput($node);
    }

     /**
     * Recursively searches an array for a key
     * @param type $toFind
     * @param type $array
     * @return boolean
     */
    private function in_array_r($toFind, $anArray){
        foreach($anArray as $item){
            if(($item == $toFind) || (is_array($item) && $this->in_array_r($toFind, $item))){
                return true;
            }
        }
        return false;
    }
    
    /**
     * The request for editing a node which already exists in the database.
     * 
     * @param array $putList The node to edit, the field to edit, and the value
     *      it will be edited to.
     * @return array Information about the edited node.
     * 
     */
    public function PUT($putList) {
        $id = $putList[$this->idName];
        $field = $putList["field"];
        $value = $putList["value"];
        $node = NodeUtility::getNodeByID($id, $this->client);

        if (in_array($field, $this->propertyList)) {
            $node->setProperty($field, $value);
            NodeUtility::storeNodeInDatabase($node);
        } else if (in_array($field, $this->relationList)) {
            NodeUtility::updateRelation($node, $field, "DirectionOut", $value, $this->client);
        } else if (array_key_exists($field, $this->nestedRelationList)){
            NodeUtility::updateNestedRelation($node, $field, "DirectionOut", $value, $this->client, $this->nestedRelationList[$field]);
        }else {
            echo ("notInArrays");
            return false;
        }
        return $this->nodeToOutput($node);
    }
    
   
    
    /**
     * The request for adding a new node to the database. Uses the template
     * pattern to allow sub-classes to specify their class-specific behavior.
     * The methods setNodeProperties and setNodeRelationships are used in this
     * way.
     * 
     * @param array $postList The parameters used to specify the node.
     * @return array Information about the newly created node.
     */
    public function POST($postList) {
        $node = $this->client->makeNode();
        // make sure methods pass $node by reference
        $this->setNodeProperties($node, $postList);
        NodeUtility::storeNodeInDatabase($node);
        $this->setNodeRelationships($node, $postList);
        $this->setNodeNestedRelationships($node, $postList);
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
    public function DELETE($id) {
        $node = NodeUtility::getNodeByID($id, $this->client);
        if (!NodeUtility::checkInIndex($node, $this->index, $this->indexKey)) {
            return false;
        } //!!Use fancier error message   
        $this->index->remove($node);
        //NodeUtility::deleteNodeFromDatabase(NodeUtility::getNodeByID($id));
        NodeUtility::deleteNodeFromDatabase($node);
        return true; //make fancy delete message
    }

}

?>