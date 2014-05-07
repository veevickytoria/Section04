<?php

namespace Everyman\Neo4j;
require_once 'RequestHandler.php';

class Project extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "Project", "ID");
        
        $this->idName = "projectID";
        
        array_push($this->propertyList, "projectTitle");
        
        $this->nestedRelationList["meetings"] = "meetingID";
        $this->nestedRelationList["notes"] = "noteID";
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