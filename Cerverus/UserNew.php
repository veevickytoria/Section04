<?php

class User{
	$userIndex;
	
	public function __construct($client){	
		$userIndex= new Index\NodeIndex($client, 'Users');
		$userIndex->save();
		//set_error_handler("
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
	
	function getAllUsers(){
		$users= $userIndex->query('email:*');
        for($ii=0;$ii<sizeof($users);$ii++){
                $array=$users[$ii]->getProperties();
                $array['userID']=$users[$ii]->getId();
                unset($array['nodeType']);
                unset($array['password']);
                $results[$ii]= $array;
        }
        echo json_encode(array("users"=>$results));
	}
	
	function getUserInfo($userId){
		 $userNode=$client->getNode($_GET['id']);
         
         
         $array = $userNode->getProperties();
        if(array_key_exists('nodeType', $array)){
                if(strcasecmp($array['nodeType'], 'User')!=0){
                        echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a user node.'));
                        return 1;
                }
        }
         unset($array['nodeType']);
         unset($array['password']);
         unset($array['nodeType']);
		 
         echo json_encode($array);
	}
}


$postContent= json_decode(file_get_contents('php://input'));

if ($_SERVER['REQUEST_METHOD'], 'POST')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'Login')==0) {
    User::login($postContent->email,$postContent->password);
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'push')==0){
	User::push($postContent->message));
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'Users')==0){
	User::getAllUsers());
}else if( strcasecmp($_SERVER['REQUEST_METHOD'],'GET') == 0){
	User::getUserInfo($_GET['id']);
}
	

?>