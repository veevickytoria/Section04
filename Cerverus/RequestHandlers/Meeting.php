<?php

namespace Everyman\Neo4j;
require_once 'RequestHandler.php';

class Meeting extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "Meeting", "ID");
        
        $this->idName = "meetingID";
        
        array_push($this->propertyList, "title");
        array_push($this->propertyList, "location");
        array_push($this->propertyList, "datetime");
        array_push($this->propertyList, "endDatetime");
        array_push($this->propertyList, "description");
        
        $this->relationList["createdBy"] = "userID";
                
        $this->nestedRelationList["attendance"] = "userID";      
    }
    
    /**
     * Add each relation of a given node to a given array
     * @param Node $node Node to get relations of
     * @param array $infoArray Array to add relations to
     */
    protected function addRelationsToArray($node, $infoArray) {
        $relationNames = array_keys($this->relationList);
        foreach ($relationNames as $relationName) {
            $relationList = NodeUtility::getNodeRelations($node, $relationName, "out");            
            $APIName = $this->relationList[$relationName];
            $infoArray[$APIName] = $relationList[0]->getEndNode()->getId();
        }
        return $infoArray;
    }
    
    /**
     * Sets a node's relationships from a list (for creating a new node)
     * @param Node $node
     * @param array $postList
     */
    protected function setNodeRelationships($node, $postList) {
        $relationNames = array_keys($this->relationList);
        foreach ($relationNames as $relationName) {
            $APIName = $this->relationList[$relationName];
            $relatedID = $postList[$APIName];
            $relatedNode = NodeUtility::getNodeByID($relatedID, $this->client);
            $node->relateTo($relatedNode, $relationName)->save();
        }
    }
    
}