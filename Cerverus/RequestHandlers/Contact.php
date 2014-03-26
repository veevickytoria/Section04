<?php
namespace RequestHandlers;

//TODO implement /User/Relationship/#/
class Contact extends RequestHandler{ 
    //relationship name for contacts
    const CONTACT_RELATION = "CONTACT";
    
    public function __construct($client) {
        parent::__construct($client, 'Users', 'ID');
    }
    
    protected function nodeToOutput($node) {
        $output['userID']=$node->getId();
        $contacts = array();
        foreach(NodeUtility::getNodeRelations($node, CONTACT_RELATION, Relationship::DirectionOut) as $rel){
            $friend = $rel->getEndNode();
            array_push($contacts, array("contactID"=>$friend->getId(), "relationID"=>$rel->getId()));
        }
        $output["contacts"] = $contacts;
        return $output;
    }

    protected function setNodeProperties($node, $postList) {
        //none to set
        return;
    }

    protected function setNodeRelationships($node, $postList) {
        if(!checkValidContactIDs($postList["contacts"])){
            return json_encode(array('errorID'=>'5', 'errorMessage'=>' is an unrecognized node ID in the database'));
        }
        //map the new 
        foreach($postList['contacts'] as $newFriend){
            $node->relateTo(NodeUtility::getNodeByID($newFriend['contactID'], $this->client), CONTACT_RELATION);
        }
        
    }
    
    public function PUT($putList) {
        $node = NodeUtility::getNodeByID($putList['userID'], $this->client);
        setNodeRelationships($node, $putList);
        return $this->nodeToOutput($node);
    }
    
    public function POST($postList) {
        $node = NodeUtility::getNodeByID($postList['userID'], $this->client);
        NodeUtility::deleteSpecificNodeRelations($node, array(CONTACT_RELATION), Relationship::DirectionOut);
        setNodeRelationships($node, $postList);
        return $this->nodeToOutput($node);
    }
    
    private function checkValidContactIDs($contacts){
        //make sure all contact IDs passed are valid users
        foreach($contacts as $friend){
            if(!checkInIndex(NodeUtility::getNodeByID($friend["contactID"], $this->client), $this->index, $this->indexKey)) {
               return false;
            }
        }
        return true;
        
    }

}
