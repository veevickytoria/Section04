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
        
        array_push($this->relationList, "createdBy");
                
        $this->nestedRelationList["attendance"] = "userID";      
    }
    
    protected function addRelationsToArray($node, $infoArray) {
        foreach($this->relationList as $relationType) {
            $relationList = NodeUtility::getNodeRelations($node, $relationType, "out");
            $infoArray[$relationType] = $relationList[0]->getEndNode()->getId();
        }
        return $infoArray;
    }

}