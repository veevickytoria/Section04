<?php

namespace Everyman\Neo4j;
require_once 'RequestHandler.php';

class UserLogin extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "User", "ID");
        
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
        //check email is valid
        $email = $postList['email'];
        //check password is valid
        
        //user index
        $userIndex = new Index\NodeIndex($this->client, 'Users');
        //get all users
        $matchedEmail = $userIndex->findOne('email', $email);
        if (sizeof($matchedEmail) > 0){
            echo $matchedEmail;
            return true;
        } else {
            echo "notfound";
            return false;
        }
            
        //for each user
        
        //check email
        
        //if email matches, check password
        
        //if pass matches, return userid
        
        //if pass doesn't match, return invalid pass
        
        //if email doesn't match, return invalid email
    }
    
}