<?php

namespace Everyman\Neo4j;
require_once 'RequestHandler.php';

class ContactRelations extends RequestHandler{     
    
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
        //do nothing
    }
    
    public function POST($postList) {
        //do nothing
    }
    
    public function PUT($putList) {
        //do nothing
    }
    
    public function DELETE($id) {
        $rel = $this->client->getRelationship($id);
        if($rel == NULL){
            return array("errorID"=>"12", "errorMessage"=>$id." is not a valid relationship ID");
        }
        if(strcmp($rel->getType(), Contact::CONTACT_RELATION) == 0){
            $rel->delete();
            return array("valid"=>"true");
        }
    }
}
