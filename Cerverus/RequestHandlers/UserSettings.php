<?php

namespace Everyman\Neo4j;
require_once "RequestHandler.php";

/**
 * This class handles UserSettings requests. It specifies the properties and relations
 * of UserSettings.
 *
 * @author millerns
 */
class UserSettings extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "UserSettings", "ID");
        
        $this->idName = "userSettingsID";
        
        array_push($this->propertyList, "shouldNotify");
        array_push($this->propertyList, "whenToNotify");
        array_push($this->propertyList, "tasks");
        array_push($this->propertyList, "groups");
        array_push($this->propertyList, "meetings");
        array_push($this->propertyList, "projects");      
    }
}
