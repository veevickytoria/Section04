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
        array_push($this->propertyList, "description");
        array_push($this->propertyList, "dateCreated");
        array_push($this->propertyList, "content");
        
        array_push($this->relationList, "createdBy");
    }
}
