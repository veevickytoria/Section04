<?php

class User{
	$userIndex;
	
	public function __construct($client){	
		$userIndex= new Index\NodeIndex($client, 'Users');
		$userIndex->save();
	}
	
	
	function login($email, $password){
		$userNode=$userIndex->findOne('email', $email)
		if(sizeof($userNode!=0){
			$userNode->getProperties();
			
			if(strcasecmp($password, $properties['password']) == 0){
				echo json_encode(array("userID"=>$email->getId()));
			}else{
				echo json_encode(array("errorID"=>1, "errorMessage"=>"Invalid email or password"));
			}
		}else{
			echo json_encode(array("errorID"=>1, "errorMessage"=>"Invalid email or password"));
		}
	}
	
	function register($email, $password, $phone='', $company='', $title='', $location='', $name=''){
		if($userIndex->findOne('email', $email)){
			$tempUserNode= $client->makeNode();
			
			$tempUserNode->setProperty('email', $email);
			$tempUserNode->setProperty('password',$password);
			$tempUserNode->setProperty('phone',$phone);
			$tempUserNode->setProperty('company',$company);
			$tempUserNode->setProperty('title',$title);
			$tempUserNode->setProperty('location',$location);
			$tempUserNode->setProperty('name',$name);
						
			$tempUserNode->save();
			$userIndex->add($tempUserNode, 'email', $email);
			
			createSettings($tempUserNode->getId());
			
			return echo json_encode(array("userID"=>$tempUserNode->getId()));
			
		}
	}
	
	
	function getAllUsers(){
		$users= $userIndex->query('email:*');
        for($ii=0;$ii<sizeof($users);$ii++){
                $array=$users[$ii]->getProperties();
                $array['userID']=$users[$ii]->getId();
           //     unset($array['nodeType']);
                unset($array['password']);
                $results[$ii]= $array;
        }
        echo json_encode(array("users"=>$results));
	}
	
	function getUserInfo($userId){
		$userNode=$client->getNode($userId);
         
        if($userIndex->findOne('user', $userId) != null){
                        echo json_encode(array('errorID'=>'11', 'errorMessage'=>$userId.' is an not a user node.'));
        }
		
		$array = $userNode->getProperties();
         //unset($array['nodeType']);
         unset($array['password']);
		 
         return json_encode($array);
	}
	
	function updateUser($userId, $field, $value){       
		$userNode=$client->getNode($userId);
		
		if(isValidField($userNode, $field)){	
			$array = array('userID'=>$user->getId());
			
			if(strcasecmp($field, 'email') == 0){
					updateUserEmail($value)
			}
			
			$userNode->setProperty($field, $value);
			$userNode->save();

			$array = array_merge($array, $user->getProperties());
			unset($array['password']);
			//unset($array['nodeType']);
			echo json_encode($array);			
		}        
	}
	
	function deleteUser($userId){
		$userNode = $client->getNode($id);
		if(isUserNode($userNode)){
			deleteSettings($client, $user->getId());
			
			deleteRelationships($userNode);
			
			$userNode->delete();
			$results = array('valid'=>'true');
			return json_encode($results);
		}
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
		if($userIndex->findOne('email', $value) == NULL){
						$userIndex->remove($userNode);
						$userIndex->add($userNode, 'email', $value);
				} else {
						return echo json_encode(array('errorID'=>'11', 'errorMessage'=>'Email already linked to another account'));
				}
	}
	
	function isUserNode($node){
		if($node !=null){
			$node=$node->getProperties();
			$match= $userIndex->findOne('email', $node->getProperty('email'));
			
			if($match!= null){
				return true;
			}else{
				echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a user node.'));
				return false;
			}
		}else{
			$errorarray = array('errorID' => '5', 'errorMessage'=>'Given node ID is not recognized in database');
			echo json_encode($errorarray);
            return false;
		}
	}
	
	//TODO: move to Request class
	function isValidField($node){
		$node= $node->getProperties();
		if(array_key_exists($field, $node->getProperties()){
			//TODO finish this method
			//check indexes for the node. then return the keys in the index
			//also error on invalide node
			return true;
		}else{
			echo json_encode(array('errorID'=>'9', 'errorMessage'=>$field.'is an unknown field'));
			return false;
		}
	}
	
	
	function updateSettings($userID, $shouldNotify, $whenToNotify, $tasks, $groups, $meetings, $projects){
		$settiIndex = new Index\NodeIndex($client, 'UserSettings');
		$settiIndex->save();

		$tempSettingNode= $client->makeNode();
		$tempSettingNode->save();
		
		$settiNode->setProperty('shouldNotify', $shouldNotify)
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
		$settiIndex = new Index\NodeIndex($client, 'UserSettings');
		$settiIndex->save();

		
		$tempSettingNode= $client->makeNode();
		$tempSettingNode->save();
		
		$tempSettingNode->setProperty('shouldNotify', "")
				->setProperty('whenToNotify', "")
				->setProperty('type', 'UserSettings')
				->setProperty('tasks',"")
				->setProperty('groups',"")
				->setProperty('meetings',"")
				->setProperty('projects',"");
		$tempSettingNode->save();
		
		setSettingsUserRelation($userID, $tempSettingNode);
				
		$settiIndex->add($tempSettingNode, 'ID', $tempSettingNode->getID());

		return $tempSettingNode->getID();
	}
	
	function deleteSettings($userID){
        $settiIndex = new Index\NodeIndex($client, 'UserSettings');
        $settiIndex->save();
        
        $userNode = $client->getNode($userID);
        
		if(isUserNode($userNode)){
			$relationArray = $node->getRelationships(array('SETFOR'), Relationship::DirectionIn);
			
			load up the notifications into an array
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
			   $array = array('valid'=>'true');
			} else {
				//return an error otherwise
				$errorArr = array('errorID' => '4', 'errorMessage'=>'Given node ID is not a notification node');
				return echo json_encode($errorArr);
			}
		}
}
	
	function setSettingsUserRelation($userID, $settingsNode){
		$user = $client->getNode($userID);
		$settiRel = $settingsNode->relateTo($user, 'SETFOR')
				->save();
	}
}

//================this section handles parsing the input to call the appropriate method==========
$postContent= json_decode(file_get_contents('php://input'));

if ($_SERVER['REQUEST_METHOD'], 'POST')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'Login')==0) {
    User::login($postContent->email,$postContent->password);
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'push')==0){
	User::push($postContent->message));
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'Users')==0){
	User::getAllUsers());
}else if( strcasecmp($_SERVER['REQUEST_METHOD'],'GET') == 0){
	User::getUserInfo($_GET['id']);
}else if( strcasecmp($_SERVER['REQUEST_METHOD'],'PUT') == 0){
	User::updateUser($_GET['id'], $postContent->field, $postContent->value);
}else if( strcasecmp($_SERVER['REQUEST_METHOD'],'DELETE') == 0){
	User::deleteUser($_GET['id']);
}else{
        echo $_SERVER['REQUEST_METHOD'] ." request method not found";
}
	

?>