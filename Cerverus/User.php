<?php
namespace Everyman\Neo4j;
require "phar://neo4jphp.phar";
require_once "RequestHandler.php";

class User extends RequestHandler{
		
    function __construct(){	
            parent::__construct("Users", "email");
    }
	
    protected function nodeToOutput($node) {
        if ($node == NULL) {return false;} //make pretty exception
        return json_encode(array_merge(array("userID"=>$node->getId()), $node->getProperties()));
    }
	
    function login($email, $password){
        $userNode=$this->index->findOne('email', $email);
        if(sizeof($userNode!=0)){
            $properties = $userNode->getProperties();

            if(strcasecmp($password, $properties['password']) == 0){
                    return json_encode(array("userID"=>$email->getId()));
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
            $tempUserNode= $this->client->makeNode();

            $tempUserNode->setProperty('email', $postContent->email);
            $tempUserNode->setProperty('password',$postContent->password);
            $tempUserNode->setProperty('phone',$postContent->phone);
            $tempUserNode->setProperty('company',$postContent->company);
            $tempUserNode->setProperty('title',$postContent->title);
            $tempUserNode->setProperty('location',$postContent->location);
            $tempUserNode->setProperty('name',$postContent->name);

            NodeUtility::storeNodeInDatabase($tempUserNode);        
            $this->index->add($tempUserNode, 'email', $postContent->email);
            $this->createSettings($tempUserNode->getId());
            return json_encode(array("userID"=>$tempUserNode->getId()));
        } else {
            return json_encode(array('errorID'=>'13', 'errorMessage'=>$postContent->email.' already exists for a user.'));            
        }
    }
	
    function getAllUsers(){
        $users= $this->index->query('email:*');
        for($ii=0;$ii<sizeof($users);$ii++){
            $array=$users[$ii]->getProperties();
            $array['userID']=$users[$ii]->getId();
            unset($array['password']);
            $results[$ii]= $array;
        }
        return json_encode(array("users"=>$results));
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
        $userId= $putList["userID"];
        $field= $putList["field"];
        $value= $putList["value"];

        $userNode=$this->client->getNode($userId);

        if(NodeUtility::isValidField($userNode, $field)){	
            if(strcasecmp($field, 'email') == 0){
                updateUserEmail($value);
            }

            $userNode->setProperty($field, $value);
            NodeUtility::storeNodeInDatabase($userNode);        

            $resultsArr = array_merge(array('userID'=>$userNode->getId()), $userNode->getProperties());
            unset($resultsArr['password']);
            return json_encode($resultsArr);		
        }        
    }
    
    public function DELETE($id){
        $node = NodeUtility::getNodeByID($id, $this->client);
        if (!NodeUtility::checkInIndex($node, $this->index, $this->indexKey, $node->getProperty('email')))
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
	
    function updateUserEmail($newEmail){
        $user = $this->index->findOne('email', $newEmail);
        if($user != NULL){
            $user->setProperty('email', $newEmail)->save();
            $this->index->remove($user);
            $this->index->add($user, 'email', $newEmail);
        } else {
            return json_encode(array('errorID'=>'11', 'errorMessage'=>'Email already linked to another account'));
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

        $userNode = $this->client->getNode($userID);
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
/*
$postContent = json_decode(file_get_contents('php://input'));
$client = new Client();
$User = new User($client);


if (strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'Login')==0) {
    echo $User.login($postContent->email,$postContent->password);
	
}else if (strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0 ){
    echo $User->POST($postContent);
	
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'push')==0){
    echo $User.push($postContent->message);
	
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'Users')==0){
    echo $User.getAllUsers();
	
}else if( strcasecmp($_SERVER['REQUEST_METHOD'],'GET') == 0){
    echo $User->GET($_GET['id']);
	
}else if( strcasecmp($_SERVER['REQUEST_METHOD'],'PUT') == 0){
    echo $User.updateUser($_GET['id'], $postContent->field, $postContent->value);
	
}else if( strcasecmp($_SERVER['REQUEST_METHOD'],'DELETE') == 0){
    echo $User->DELETE($_GET['id']);
}else{
    echo $_SERVER['REQUEST_METHOD'] ." request method not found";
}
 
 */