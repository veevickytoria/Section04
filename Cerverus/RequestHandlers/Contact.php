<?php

namespace Everyman\Neo4j;
require_once 'RequestHandler.php';

class Contact extends RequestHandler{     
    const CONTACT_RELATION = "CONTACT";
    
    public function __construct($client) {
        parent::__construct($client, 'Users', 'email');
    }
    
    protected function nodeToOutput($node) {
        $output = array('userID'=>$node->getId());
        
        $outContacts = $node->getRelationships(array(Contact::CONTACT_RELATION), Relationship::DirectionOut);
        $inContacts = $node->getRelationships(array(Contact::CONTACT_RELATION), Relationship::DirectionIn);
        $contacts = array();
        foreach($outContacts as $rel){
                $contactNode=$rel->getEndNode();
                $uArray = array();
                $uArray['contactID']=$contactNode->getId();
                $uArray['relationID']=$rel->getId();
                array_push($contacts, $uArray);
        }			
        foreach($inContacts as $rel){
                $contactNode=$rel->getStartNode();
                $uArray = array();
                $uArray['contactID']=$contactNode->getId();
                $uArray['relationID']=$rel->getId();
                array_push($contacts, $uArray);
        }
        $output['contacts'] = $contacts;
        
        return $output;
    }
        
    
    public function GET($id) {
        return $this->nodeToOutput(NodeUtility::getNodeByID($id, $this->client));
    }
    
    public function POST($postList) {
        $node = NodeUtility::getNodeByID($postList['userID'], $this->client);
        NodeUtility::deleteSpecificNodeRelations($node, array(Contact::CONTACT_RELATION), Relationship::DirectionAll);
        $this->setNodeRelationships($node, $postList['contacts']);
        return $this->nodeToOutput($node);
    }
    
    public function PUT($putList) {
        $user = NodeUtility::getNodeByID($putList['userID'], $this->client);
        $this->setNodeRelationships($user, $this->removeCurrentFriends($putList['contacts'], $user));        
        return $this->nodeToOutput($user);
    }
    
    public function DELETE($id) {
        $user = NodeUtility::getNodeByID($id, $this->client);
        NodeUtility::deleteSpecificNodeRelations($user, array(Contact::CONTACT_RELATION), Relationship::DirectionAll);
        return $this->nodeToOutput($user);
    }
    
    
    protected function setNodeRelationships($node, $contacts) {
        if(!$this->checkValidContactIDs($contacts)){
            return json_encode(array('errorID'=>'5', 'errorMessage'=>' is an unrecognized node ID in the database'));
        }
        //map the new 
        foreach($contacts as $newFriend){
            $node->relateTo(NodeUtility::getNodeByID($newFriend->contactID, $this->client), Contact::CONTACT_RELATION)->save();
        }
    }
    
    private function removeCurrentFriends($contacts, $user){
        //get the contacts who this user points to
        $outRels = $user->getRelationships(array(Contact::CONTACT_RELATION), Relationship::DirectionOut);
        $outContacts = array();
        foreach($outRels as $rel){ array_push($outContacts, $rel->getEndNode()->getId()); }
        //get the contacts who point to this user
        $inRels = $user->getRelationships(array(Contact::CONTACT_RELATION), Relationship::DirectionIn);
        $inContacts = array();
        foreach($inRels as $rel){ array_push($inContacts, $rel->getEndNode()->getId()); }
        
        //remove all the contacts that this user is already connected to
        $newContacts = array();
        for($i=0; $i<count($contacts); $i++){
            if(!(in_array($contacts[$i]->contactID, $outContacts)  &&  in_array($contacts[$i]->contactID, $inContacts))){
                array_push($newContacts, $contacts[$i]);
            }
        }
        
        return $newContacts;
    }
    
    
    private function checkValidContactIDs($contacts){
        //make sure all contact IDs passed are valid users
        foreach($contacts as $friend){
            $user = NodeUtility::getNodeByID($friend->contactID, $this->client);
            if(!$this->index->findOne($this->indexKey, $user->getProperty($this->indexKey))) {
                return false;
            }
        }
        return true;       
    }
}
