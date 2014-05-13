<?php

namespace Everyman\Neo4j;
require_once "RequestHandler.php";

/**
 * This class handles Comment requests. It specifies the properties and relations
 * of Comments.
 *
 * @author millerns
 */
class Comments extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "Comment", "ID");
        
        $this->idName = "commentID";
        
        array_push($this->propertyList, "datePosted");
        array_push($this->propertyList, "content");
        
		$this->relationList["commentBy"] = "commentBy";
        $this->relationList["commentOn"] = "commentOn";
    }
}
