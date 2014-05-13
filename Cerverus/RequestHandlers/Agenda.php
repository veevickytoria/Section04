<?php

namespace Everyman\Neo4j;
require_once "RequestHandler.php";

/**
 * This class handles Note requests. It specifies the properties and relations
 * of Notes.
 *
 * @author millerns
 */
class Note extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "Note", "ID");
        
        $this->idName = "noteID";
        
        array_push($this->propertyList, "title");
        array_push($this->propertyList, "meeting");
        array_push($this->propertyList, "user");
        array_push($this->propertyList, "content");
        
        array_push($this->relationList, "createdBy");
    }

    protected function addRelationsToArray($node, $infoArray) {
        
        //NodeUtility::getAllNodeRelations($node);
        
        //$relationList = NodeUtility::getNodeRelations($node, "createdBy", "out");
        
        
        foreach($this->relationList as $relationType) {
            $relationList = NodeUtility::getNodeRelations($node, $relationType, "out");
            $infoArray[$relationType] = $relationList[0]->getEndNode()->getId();
        }
        return $infoArray;
         
    }

}
