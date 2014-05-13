<?php

namespace Everyman\Neo4j;
require_once 'RequestHandler.php';

class User extends RequestHandler {
    
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
    
    protected function nodeToOutput($node) {
        $output = parent::nodeToOutput($node);
        unset($output['password']);
        return $output;
    }
    
    //prevents duplicate emails
    public function PUT($putList) {        
        if ($putList["field"] == "email"){
            $newEmail = $putList["value"];
            $duplicate = $this->index->findOne('email', $newEmail);
            if($duplicate == NULL){
                $node = NodeUtility::getNodeByID($this->idName, $this->client);
                $this->index->remove($node);
                $this->index->add($node, 'email', $newEmail);
            } else {
                return (array('errorID'=>'11', 'errorMessage'=>'Email already linked to another account'));
            }
        }        
        return parent::PUT($putList);
    }
    
    //Other stuff that might be used later
//    function isUserNode($node){
//        if($node !=null){
//            $node=$node->getProperties();
//            $match= $this->index->findOne('email', $node->getProperty('email'));
//
//            if($match!= null){
//                return true;
//            }else{
//                echo json_encode(array('errorID'=>'11', 'errorMessage'=>$node->getId().' is an not a user node.'));
//                return false;
//            }
//        }else{
//            $errorarray = array('errorID' => '5', 'errorMessage'=>'Given node ID is not recognized in database');
//            echo json_encode($errorarray);
//            return false;
//        }
//    }
//	
//    function updateSettings($userID, $shouldNotify, $whenToNotify, $tasks, $groups, $meetings, $projects){
//        $settiIndex = new Index\NodeIndex($this->client, 'UserSettings');
//        $settiIndex->save();
//
//        $tempSettingNode= $this->client->makeNode();
//        $tempSettingNode->save();
//
//        $tempSettingNode->setProperty('shouldNotify', $shouldNotify)
//                        ->setProperty('whenToNotify', $whenToNotify)
//                        ->setProperty('type', 'UserSettings')
//                        ->setProperty('tasks',$tasks)
//                        ->setProperty('groups',$groups)
//                        ->setProperty('meetings',$meetings)
//                        ->setProperty('projects',$projects);
//
//        setSettingsUserRelation($userID, $tempSettingNode);
//
//        $settiIndex->add($tempSettingNode, 'ID', $tempSettingNode->getID());
//
//        return $tempSettingNode->getID();
//    }
//	
//    function createSettings($userID){
//        $settiIndex = new Index\NodeIndex($this->client, 'UserSettings');
//        $settiIndex->save();
//
//
//        $tempSettingNode= $this->client->makeNode();
//        $tempSettingNode->save();
//
//        $tempSettingNode->setProperty('shouldNotify', "")
//                        ->setProperty('whenToNotify', "")
//                        ->setProperty('type', 'UserSettings')
//                        ->setProperty('tasks',"")
//                        ->setProperty('groups',"")
//                        ->setProperty('meetings',"")
//                        ->setProperty('projects',"");
//        $tempSettingNode->save();
//
//        $this->setSettingsUserRelation($userID, $tempSettingNode);
//
//        $settiIndex->add($tempSettingNode, 'ID', $tempSettingNode->getID());
//
//        return $tempSettingNode->getID();
//    }
//	
//    function deleteSettings($userNode){
//        $settiIndex = new Index\NodeIndex($this->client, 'UserSettings');
//        $settiIndex->save();
//
//        $userNode = $this->client->getNode($userID);
//        if(isUserNode($userNode)){
//            $relationArray = $userNode->getRelationships(array('SETFOR'), Relationship::DirectionIn);
//
//            //load up the notifications into an array
//            $setti = $relationArray[0]->getStartNode();
//
//            //only delete the node if it's a notification
//            if($setti != NULL){
//                //get the relationships
//                $relations = $setti->getRelationships();
//                foreach($relations as $rel){
//                    //remove all relationships
//                    $rel->delete();
//                }      
//                //delete node and return true
//                $setti->delete();
//                return json_decode(array('valid'=>'true'));
//            } else {
//                //return an error otherwise
//                $errorArr = array('errorID' => '4', 'errorMessage'=>'Given node ID is not a notification node');
//                return json_encode($errorArr);
//            }
//        }
//    }
//	
//    function setSettingsUserRelation($userID, $settingsNode){
//        $user = $this->client->getNode($userID);
//        $settingsNode->relateTo($user, 'SETFOR')->save();
//    }     
}

//================this section is for testing with our test suite without controller==========

//$postContent = json_decode(file_get_contents('php://input'));
//$client = new Client();
//$User = new User($client);
//
//
//if (strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'Login')==0) {
//    echo $User->login($postContent->email,$postContent->password);
//	
//}else if (strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0 ){
//    echo $User->POST($postContent);
//	
//}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'push')==0){
//    echo $User.push($postContent->message);
//	
//}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'Users')==0){
//    echo $User->getAllUsers();
//	
//}else if( strcasecmp($_SERVER['REQUEST_METHOD'],'GET') == 0){
//    echo $User->GET($_GET['id']);
//	
//}else if( strcasecmp($_SERVER['REQUEST_METHOD'],'PUT') == 0){
//    echo $User->PUT($postContent);
//	
//}else if( strcasecmp($_SERVER['REQUEST_METHOD'],'DELETE') == 0){
//    echo $User->DELETE($_GET['id']);
//}else{
//    echo $_SERVER['REQUEST_METHOD'] ." request method not found";
//}
 
