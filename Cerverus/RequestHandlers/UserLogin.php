<?php

namespace Everyman\Neo4j;
require_once 'RequestHandler.php';

class UserLogin extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "User", "email");
        
        $this->idName = "userID";
        
        array_push($this->propertyList, "email");
        array_push($this->propertyList, "name");
        array_push($this->propertyList, "password");
        array_push($this->propertyList, "title");
        array_push($this->propertyList, "phone");
        array_push($this->propertyList, "location");
        array_push($this->propertyList, "company");
    }

    /**
     * Check if email and password combination is valid
     * @param type $postList
     */
    public function POST($postList) {
        $email = $postList['email'];
        $index = $this->index;
        $matchedEmail = $index->findOne($this->indexKey, $email);
        if ($matchedEmail == null){
            return "Email not found";
        }
        
        $nodePassword = $matchedEmail->getProperty("password");
        if ($nodePassword == $postList['password']){
            $outputArray = array();
            $outputArray[$this->idName] = ($matchedEmail->getId());
            return $outputArray;
        }
        
        return "Password is invalid";
    }   
}