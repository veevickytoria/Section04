<?php
namespace Everyman\Neo4j;
require "phar://neo4jphp.phar";
require_once "RequestHandler.php";

/**
 * 
 */
class User extends RequestHandler{
		
    function __construct($client){	
            parent::__construct($client, "Users", "email");
            array_push($this->propertyList, "name", "password", "email", "phone", "company", "title", "location" );
    }
	
    protected function nodeToOutput($node) {
        if ($node == NULL) {return false;}
        $nodeInfo = array_merge(array("userID"=>$node->getId()), $node->getProperties());
        unset($nodeInfo['password']);
        return json_encode($nodeInfo);
        
    }
	
    function login($email, $password){
        $userNode=$this->index->findOne('email', $email);
        if(sizeof($userNode)!=0){
            $properties = $userNode->getProperties();

            if(strcasecmp($password, $properties['password']) == 0){
                    return json_encode(array("userID"=>$userNode->getId()));
            }else{
                    return json_encode(array("errorID"=>1, "errorMessage"=>"Invalid email or password"));
            }
        }else{
            return json_encode(array("errorID"=>1, "errorMessage"=>"Invalid email or password"));
        }
    }
	
    /*
     * register a user
     */
    public function POST($postContent){
        if($this->index->findOne('email', $postContent->email) == NULL){
            $userNode = $this->client->makeNode();
            $userNode->setProperty('email', $postContent->email)
                    ->setProperty('password',$postContent->password)
                    ->setProperty('phone',$postContent->phone)
                    ->setProperty('company',$postContent->company)
                    ->setProperty('title',$postContent->title)
                    ->setProperty('location',$postContent->location)
                    ->setProperty('name',$postContent->name)
                    ->save();
            
            $this->index->add($userNode, $this->indexKey, $postContent->email);
            $this->createSettings($userNode->getId());
            return $this->nodeToOutput($userNode);
        } else {
            return json_encode(array('errorID'=>'13', 'errorMessage'=>$postContent->email.' already exists for a user.'));            
        }
    }
    
    public function GET($id){
        $node = NodeUtility::getNodeByID($id, $this->client);
        if (!NodeUtility::checkNodeInIndex($node, $this->index, $this->indexKey, $node->getProperty('email')))
            {return array("errorID"=>11, "errorMessage"=>$id." is not a vaild User node.");} //!!Use fancier error message
        return $this->nodeToOutput($node);
    }
	
    function getAllUsers(){
        $response = array();
        foreach($this->index->query($this->indexKey.':*') as $user){
            array_push($response, json_decode($this->nodeToOutput($user)));
        }
        return json_encode(array("users"=>$response));
    }
	
    function getUserInfo($userId){
        $userNode=$this->client->getNode($userId);

        if($this->index->findOne('user', $userId) != null){
            return json_encode(array('errorID'=>'11', 'errorMessage'=>$userId.' is an not a user node.'));
        }

        $array = $userNode->getProperties();
        unset($array['password']);

        return json_encode($array);
    }
	
    function PUT($putList){
        $user = NodeUtility::getNodeByID($putList->userID, $this->client);
        if ($user == NULL){
            return json_encode(array('errorID'=>'10', 'errorMessage'=>$user->getId().' is an not a node.'));
        }
        
        if (!(NodeUtility::checkNodeInIndex($user, $this->index, $this->indexKey, $user->getProperty('email'))) ){
            return json_encode(array('errorID'=>'11', 'errorMessage'=>$user->getId().' is an not a user node.'));
        }
        
        if(in_array($putList->field, $this->propertyList)){	
            if(strcasecmp($putList->field, 'email') == 0){
                $this->updateUserEmail($putList->value, $user);
            }

            $user->setProperty($putList->field, $putList->value);
            NodeUtility::storeNodeInDatabase($user);
            return $this->nodeToOutput($user);		
        }        
    }    
	
    function updateUserEmail($newEmail, $user){
        if($this->index->findOne('email', $newEmail) == NULL){
            $this->index->remove($user);
            $this->index->add($user, 'email', $newEmail);
        } else {
            return json_encode(array('errorID'=>'13', 'errorMessage'=>'Email already linked to another account'));
        }
    }
    
    public function DELETE($id){
        $node = NodeUtility::getNodeByID($id, $this->client);
        if (!NodeUtility::checkNodeInIndex($node, $this->index, $this->indexKey, $node->getProperty('email')))
            {return json_encode(array('errorID'=>'11', 'errorMessage'=>$id.' is an not a user node.'));} //!!Use fancier error message   
        $this->index->remove($node);            
        
        $relationArray = $node->getRelationships(array('SETFOR'), Relationship::DirectionIn);
        $userSettings = $relationArray[0]->getStartNode();
        NodeUtility::deleteNodeFromDatabase($userSettings);
        NodeUtility::deleteNodeFromDatabase($node);
        
        return json_encode(array("valid"=>"valid"));
    }
	
	
	
	
    //===========================end of request methods, following are utility methods==============
    //TODO: move to request
    function deleteRelationships($node){
        $relations = $node->getRelationships();

        foreach($relations as $rel){
            //remove all relationships
            $rel->delete();
        }
    }
	
    function isUserNode($node){
        if($node !=null){
            $node=$node->getProperties();
            $match= $this->index->findOne('email', $node->getProperty('email'));

            if($match!= null){
                return true;
            }else{
                echo json_encode(array('errorID'=>'11', 'errorMessage'=>$node->getId().' is an not a user node.'));
                return false;
            }
        }else{
            $errorarray = array('errorID' => '5', 'errorMessage'=>'Given node ID is not recognized in database');
            echo json_encode($errorarray);
            return false;
        }
    }
	
    function updateSettings($userID, $shouldNotify, $whenToNotify, $tasks, $groups, $meetings, $projects){
        $settiIndex = new Index\NodeIndex($this->client, 'UserSettings');
        $settiIndex->save();

        $tempSettingNode= $this->client->makeNode();
        $tempSettingNode->save();

        $tempSettingNode->setProperty('shouldNotify', $shouldNotify)
                        ->setProperty('whenToNotify', $whenToNotify)
                        ->setProperty('type', 'UserSettings')
                        ->setProperty('tasks',$tasks)
                        ->setProperty('groups',$groups)
                        ->setProperty('meetings',$meetings)
                        ->setProperty('projects',$projects);

        setSettingsUserRelation($userID, $tempSettingNode);

        $settiIndex->add($tempSettingNode, 'ID', $tempSettingNode->getID());

        return $tempSettingNode->getID();
    }
	
    function createSettings($userID){
        $settiIndex = new Index\NodeIndex($this->client, 'UserSettings');
        $settiIndex->save();


        $tempSettingNode= $this->client->makeNode();
        $tempSettingNode->save();

        $tempSettingNode->setProperty('shouldNotify', "")
                        ->setProperty('whenToNotify', "")
                        ->setProperty('type', 'UserSettings')
                        ->setProperty('tasks',"")
                        ->setProperty('groups',"")
                        ->setProperty('meetings',"")
                        ->setProperty('projects',"");
        $tempSettingNode->save();

        $this->setSettingsUserRelation($userID, $tempSettingNode);

        $settiIndex->add($tempSettingNode, 'ID', $tempSettingNode->getID());

        return $tempSettingNode->getID();
    }
	
    function deleteSettings($userNode){
        $settiIndex = new Index\NodeIndex($this->client, 'UserSettings');
        $settiIndex->save();

        if(isUserNode($userNode)){
            $relationArray = $userNode->getRelationships(array('SETFOR'), Relationship::DirectionIn);

            //load up the notifications into an array
            $setti = $relationArray[0]->getStartNode();

            //only delete the node if it's a notification
            if($setti != NULL){
                //get the relationships
                $relations = $setti->getRelationships();
                foreach($relations as $rel){
                    //remove all relationships
                    $rel->delete();
                }      
                //delete node and return true
                $setti->delete();
                return json_decode(array('valid'=>'true'));
            } else {
                //return an error otherwise
                $errorArr = array('errorID' => '4', 'errorMessage'=>'Given node ID is not a notification node');
                return json_encode($errorArr);
            }
        }
    }
	
    function setSettingsUserRelation($userID, $settingsNode){
        $user = $this->client->getNode($userID);
        $settingsNode->relateTo($user, 'SETFOR')->save();
    }
}

//================this section is for testing with our test suite without controller==========

$postContent = json_decode(file_get_contents('php://input'));
$client = new Client(new Transport('localhost', 7474));
$User = new User($client);


if (strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'Login')==0) {
    echo $User->login($postContent->email,$postContent->password);
	
}else if (strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0 ){
    echo $User->POST($postContent);
	
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'push')==0){
    echo $User.push($postContent->message);
	
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'Users')==0){
    echo $User->getAllUsers();
	
}else if( strcasecmp($_SERVER['REQUEST_METHOD'],'GET') == 0){
    echo $User->GET($_GET['id']);
	
}else if( strcasecmp($_SERVER['REQUEST_METHOD'],'PUT') == 0){
    echo $User->PUT($postContent);
	
}else if( strcasecmp($_SERVER['REQUEST_METHOD'],'DELETE') == 0){
    echo $User->DELETE($_GET['id']);
}else{
    echo $_SERVER['REQUEST_METHOD'] ." request method not found";
}
 
