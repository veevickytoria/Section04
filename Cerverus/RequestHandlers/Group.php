<?php

namespace Everyman\Neo4j;
require_once 'RequestHandler.php';

class Group extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "Group", "ID");
        
        $this->idName = "groupID";
        
        array_push($this->propertyList, "groupTitle");
        
        $this->nestedRelationList["members"] = "userID";        
    }
    
    protected function addRelationsToArray($node, $infoArray) {
        foreach($this->relationList as $relationType) {
            $relationList = NodeUtility::getNodeRelations($node, $relationType, "out");
            $infoArray[$relationType] = $relationList[0]->getEndNode()->getId();
        }
        return $infoArray;
    }

}