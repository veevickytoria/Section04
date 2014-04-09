<?php
//namespace RequestHandlers;
namespace Everyman\Neo4j;
require_once "RequestHandler.php";

//TODO implement /User/Relationship/#/
class Contact extends RequestHandler{ 
    //relationship name for contacts
    const CONTACT_RELATION = "CONTACT";
    const CONTACT_DIRECTION = Relationship::DirectionOut;
    
    public function __construct($client) {
        parent::__construct($client, 'Users', 'ID');
    }
    
    protected function nodeToOutput($node) {
        $output['userID']=$node->getId();
        $contacts = array();
        foreach(NodeUtility::getNodeRelations($node, Contact::CONTACT_RELATION, Relationship::DirectionOut) as $rel){
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
            $node->relateTo(NodeUtility::getNodeByID($newFriend['contactID'], $this->client), Contact::CONTACT_RELATION);
        }
        
    }
    
    public function PUT($putList) {
        $node = NodeUtility::getNodeByID($putList['userID'], $this->client);
        setNodeRelationships($node, $putList);
        return $this->nodeToOutput($node);
    }
    
    public function POST($postList) {
        $node = NodeUtility::getNodeByID($postList['userID'], $this->client);
        NodeUtility::deleteSpecificNodeRelations($node, array(Contact::CONTACT_RELATION), Relationship::DirectionOut);
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

//================this section is for testing with our test suite without controller==========

$postContent = json_decode(file_get_contents('php://input'));
$client = new Client();
$Contact = new Contact($client);


if (strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0 ){
    echo $Contact->POST($postContent);
	
}else if( strcasecmp($_SERVER['REQUEST_METHOD'],'GET') == 0){
    echo $Contact->GET($_GET['id']);
	
}else if( strcasecmp($_SERVER['REQUEST_METHOD'],'PUT') == 0){
    echo $Contact->PUT($postContent);
	
}else if( strcasecmp($_SERVER['REQUEST_METHOD'],'DELETE') == 0){
    echo $Contact->DELETE($_GET['id']);
}else{
    echo $_SERVER['REQUEST_METHOD'] ." request method not found";
}
 
