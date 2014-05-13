<?php

namespace Everyman\Neo4j;
require_once 'RequestHandler.php';

class Task extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "Task", "ID");
        
        $this->idName = "taskID";
        
        array_push($this->propertyList, "title");
        array_push($this->propertyList, "isCompleted");
        array_push($this->propertyList, "description");
        array_push($this->propertyList, "deadline");
        array_push($this->propertyList, "dateCreated");
        array_push($this->propertyList, "dateAssigned");
        array_push($this->propertyList, "completionCriteria");
        
        $this->relationList["assignedTo"] = "assignedTo";
        $this->relationList["assignedFrom"] = "assignedFrom";
        $this->relationList["createdBy"] = "createdBy";
    }
    
}