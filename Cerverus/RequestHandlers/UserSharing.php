<?php

namespace Everyman\Neo4j;
require_once 'RequestHandler.php';

class UserSharing extends RequestHandler {
    
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
        
        $this->relationName = "sharedNote";
    }
    
    protected function nodeToOutput($node) {
        $output = parent::nodeToOutput($node);
        unset($output['password']);
        return $output;
    }
    
    public function GET($userID) {
        //get the user
        $userNode = NodeUtility::getNodeByID($userID, $this->client);
        
        //get related notes
        $sharedRelations = NodeUtility::getNodeRelations($userNode, $this->relationName, Relationship::DirectionIn);
        $sharedNodes = NodeUtility::getNodesFromRelations($sharedRelations, "DirectionIn");
        
        //output the related notes
        $notesArray = array();
        foreach($sharedNodes as $sharedNode){
            $noteArray = array();
            $noteArray["noteID"] = $sharedNode->getId();
            array_push($notesArray, $noteArray);
        }
        
        $outputArray = array();
        $outputArray["notes"] = $notesArray;
        return $outputArray;
        
        //git commit the updated controller
    }
    
    public function POST() {
        return "POST not allowed with this URL!";
    }
    
    public function PUT() {
        return "PUT not allowed with this URL!";
    }
    
    public function DELETE() {
        return "DELETE not allowed with this URL!";
    }
    
}