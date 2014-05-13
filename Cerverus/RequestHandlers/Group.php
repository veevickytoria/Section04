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

}