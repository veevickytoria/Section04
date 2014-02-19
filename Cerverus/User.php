<?php
/**
 * Include the API PHP file for neo4j
 */
 
namespace Everyman\Neo4j;
require("phar://neo4jphp.phar");
// These constants may be changed without breaking existing hashes.
define("PBKDF2_HASH_ALGORITHM", "sha256");
define("PBKDF2_ITERATIONS", 1000);
define("PBKDF2_SALT_BYTE_SIZE", 24);
define("PBKDF2_HASH_BYTE_SIZE", 24);

define("HASH_SECTIONS", 4);
define("HASH_ALGORITHM_INDEX", 0);
define("HASH_ITERATION_INDEX", 1);
define("HASH_SALT_INDEX", 2);
define("HASH_PBKDF2_INDEX", 3);

    if (isset($_SERVER['HTTP_ORIGIN'])) {
        header("Access-Control-Allow-Origin: *");
        header('Access-Control-Allow-Credentials: true');
        header('Access-Control-Max-Age: 86400');    // cache for 1 day
    }

    // Access-Control headers are received during OPTIONS requests
    if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
        if (isset($_SERVER['HTTP_ACCESS_CONTROL_REQUEST_METHOD']))
            header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");         

        if (isset($_SERVER['HTTP_ACCESS_CONTROL_REQUEST_HEADERS']))
			header("Access-Control-Allow-Headers: GET, POST, PUT, DELETE, OPTIONS");
        exit(0);
    }
/**
 *        Create a graphDb connection 
 */
$client= new Client();

//get the index
$userIndex = new Index\NodeIndex($client, 'Users');
$userIndex->save();
        
if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'Login')==0){
        // login method
        $postContent = json_decode(@file_get_contents('php://input'));
        $email=$userIndex->findOne('email',$postContent->email);
        if (sizeof($email)!=0){ //check if there is a node with the given email.
                //get the properties
                $properties = $email->getProperties();
                //check given password vs stored password
                if(strcasecmp($postContent->password, $properties['password']) == 0){
                        echo json_encode(array("userID"=>$email->getId()));
                }else{
                        echo json_encode(array("errorID"=>1, "errorMessage"=>"pass invalid email or password"));
                }
        }else{
                echo json_encode(array("errorID"=>1, "errorMessage"=>"email invalid email or password"));
        }        
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'test')==0){
        //testing method to check if mailing is working correctly
        $to = 'rujirasl@rose-hulman.edu';
        $subject = 'the subject';
        $message = 'hello';
        $headers = 'From: webmaster@meetingNinja.com' . "\r\n" .
    'Reply-To: webmaster@meetingNinja.com' . "\r\n" .
    'X-Mailer: PHP/' . phpversion();

        mail($to, $subject, $message, $headers);
        
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'schedule')==0){
        //GET getUserSchedule
        $userNode=$client->getNode($_GET['id']);
        if (sizeof($userNode) > 0){
		$array = $userNode->getProperties();
        if(array_key_exists('nodeType', $array)){
                if(strcasecmp($array['nodeType'], 'User')!=0){
                        echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a user node.'));
                        return 1;
                }
        }
        $relationArray = $userNode->getRelationships(array('ASSIGNED_TO', 'MADE_MEETING'), Relationship::DirectionOut);
        $fullarray=array();
        foreach($relationArray as $rel){
                $booleanFound=0;
                $node = $rel->getEndNode();
                $tempArray=$node->getProperties();
                if(array_key_exists('nodeType', $tempArray)){
                        if(strcasecmp($tempArray['nodeType'], 'Meeting')!=0 && strcasecmp($tempArray['nodeType'], 'Task')!=0){
                                echo json_encode(array('errorID'=>'11', 'errorMessage'=>$node->getId().' is an not a Meeting or Task node.'));
                                return 1;
                            }
                }
                $array=array();
                $array['id']=$node->getId();
                $array['title']=$tempArray['title'];
                                $array['description']=$tempArray['description'];
                //$array['relation']=$rel->getType();
                if(strcasecmp($rel->getType(),'ASSIGNED_TO')==0 || strcasecmp($rel->getType(),'ASSIGNED_FROM')==0 ||strcasecmp($rel->getType(),'CREATED_BY')==0){
                        $array['datetimeEnd']=$tempArray['deadline'];
                                                $array['datetimeStart']=$tempArray['dateCreated'];
                                                $array['type']='task';
                }else if(strcasecmp($rel->getType(),'MADE_MEETING')==0 ||strcasecmp($rel->getType(),'ATTEND_MEETING')==0){
                                                $array['datetimeStart']=$tempArray['datetime'];
                                                $array['datetimeEnd']=$tempArray['endDatetime'];
                                                $array['type']='meeting';
                                }
                                foreach($fullarray as $checkArray){
                                        if($checkArray['id']==$node->getId()){
                                                $booleanFound=1;
                                        }
                                }
                                if($booleanFound==0){
                                        array_push($fullarray,$array);
                                }
        }
        $lastarray=array('schedule'=>$fullarray);
        echo json_encode($lastarray);
		}else{
			$errorarray = array('errorID' => '5', 'errorMessage'=>$_GET['id'].' node ID is not recognized in database');
            echo json_encode($errorarray);
		}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'schedules')==0){
        //GET Multiple Schedules
                $postContent = json_decode(@file_get_contents('php://input'));
                $users=$postContent->users;
                $everything=array();
                foreach($users as $user){
                        $userID=$user->userID;
                        $userNode=$client->getNode($userID);
                        if(sizeof($userNode)==0){
                        echo json_encode(array('errorID' => '5', 'errorMessage'=>$userID.' node ID is not recognized in database'));
                        return 1;
                        }
                        $tempArray = $userNode->getProperties();
                        if(array_key_exists('nodeType', $tempArray)){
                                        if(strcasecmp($tempArray['nodeType'], 'User')!=0){
                                                        echo json_encode(array('errorID'=>'11', 'errorMessage'=>$userID.' is an not a user node.'));
                                                        return 1;
                                        }
                        }
                        $relationArray = $userNode->getRelationships(array('ASSIGNED_TO', 'MADE_MEETING'), Relationship::DirectionOut);
                        $fullarray=array();
                        foreach($relationArray as $rel){
                                        $booleanFound=0;
                                        $node = $rel->getEndNode();
                                        $tempArray=$node->getProperties();
                                        $array=array();
                                        $array['id']=$node->getId();
                                        $array['title']=$tempArray['title'];
                                        $array['description']=$tempArray['description'];
                                        if(strcasecmp($rel->getType(),'ASSIGNED_TO')==0 || strcasecmp($rel->getType(),'ASSIGNED_FROM')==0 ||strcasecmp($rel->getType(),'CREATED_BY')==0){
                                                        $array['datetimeEnd']=$tempArray['deadline'];
                                                        $array['datetimeStart']=$tempArray['dateCreated'];
                                                        $array['type']='task';
                                        }else if(strcasecmp($rel->getType(),'MADE_MEETING')==0 ||strcasecmp($rel->getType(),'ATTEND_MEETING')==0){
                                                        $array['datetimeStart']=$tempArray['datetime'];
                                                        $array['datetimeEnd']=$tempArray['endDatetime'];
                                                        $array['type']='meeting';
                                        }
                                        foreach($fullarray as $checkArray){
                                                if($checkArray['id']==$node->getId()){
                                                        $booleanFound=1;
                                                }
                                        }
                                        if($booleanFound==0){
                                                array_push($fullarray,$array);
                                        }
                        }
                        $lastarray=array('schedule'=>$fullarray,'userID'=>$userID);
                        array_push($everything,$lastarray);
                }
        echo json_encode(array('schedules'=>$everything));
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'Notes')==0){
        //GET userNotes
        $userNode=$client->getNode($_GET['id']);
		if (sizeof($userNode) > 0){
        $array = $userNode->getProperties();
        if(array_key_exists('nodeType', $array)){
                if(strcasecmp($array['nodeType'], 'User')!=0){
                        echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a user node.'));
                        return 1;
                }
        }
        $relationArray = $userNode->getRelationships(array('CREATED', Relationship::DirectionOut));
        $fullarray=array();
        foreach($relationArray as $rel){
                $node = $rel->getEndNode();
                $tempArray=$node->getProperties();
                $array=array();
                $array['noteID']=$node->getId();
                $array['noteTitle']=$tempArray['title'];
                array_push($fullarray,$array);
        }
        $lastarray=array('notes'=>$fullarray);
        echo json_encode($lastarray);
		}else{
			$errorarray = array('errorID' => '5', 'errorMessage'=>$_GET['id'].' node ID is not recognized in database');
            echo json_encode($errorarray);
		}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'tasks')==0){
        //GET getUserTasks
        $userNode=$client->getNode($_GET['id']);
		if (sizeof($userNode) > 0){
        $array = $userNode->getProperties();
        if(array_key_exists('nodeType', $array)){
                if(strcasecmp($array['nodeType'], 'User')!=0){
                        echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a user node.'));
                        return 1;
                }
        }
        $relationArray = $userNode->getRelationships(array('ASSIGNED_TO', 'ASSIGNED_FROM','CREATED_BY'));
        $fullarray=array();
        foreach($relationArray as $rel){
                $node = $rel->getEndNode();
                $tempArray=$node->getProperties();
                if(array_key_exists('nodeType', $tempArray)){
                        if(strcasecmp($tempArray['nodeType'], 'Meeting')!=0 && strcasecmp($tempArray['nodeType'], 'Task')!=0){
                                echo json_encode(array('errorID'=>'11', 'errorMessage'=>$node->getId().' is an not a Meeting or Task node.'));
                                return 1;
                            }
                }
                $array=array();
                $array['id']=$node->getId();
                $array['title']=$tempArray['title'];
                $array['type']=$rel->getType();
                if(strcasecmp($rel->getType(),'ASSIGNED_TO')==0 || strcasecmp($rel->getType(),'ASSIGNED_FROM')==0 ||strcasecmp($rel->getType(),'CREATED_BY')==0){
                        array_push($fullarray,$array);        
                }
        }
        $lastarray=array('tasks'=>$fullarray);
        echo json_encode($lastarray);
		}else{
			$errorarray = array('errorID' => '5', 'errorMessage'=>$_GET['id'].' node ID is not recognized in database');
            echo json_encode($errorarray);
		}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'meetings')==0){
        //GET getUserMeetings
        $userNode=$client->getNode($_GET['id']);
		if (sizeof($userNode) > 0){
		$array = $userNode->getProperties();
		if(array_key_exists('nodeType', $array)){
                if(strcasecmp($array['nodeType'], 'User')!=0){
                        echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a user node.'));
                        return 1;
                }
        }
        $relationArray = $userNode->getRelationships(array('MADE_MEETING','ATTEND_MEETING'));
        $fullarray=array();
        foreach($relationArray as $rel){
                $node = $rel->getEndNode();
                $tempArray=$node->getProperties();
                if(array_key_exists('nodeType', $tempArray)){
                        if(strcasecmp($tempArray['nodeType'], 'Meeting')!=0 && strcasecmp($tempArray['nodeType'], 'Task')!=0){
                                echo json_encode(array('errorID'=>'11', 'errorMessage'=>$node->getId().' is an not a Meeting or Task node.'));
                                return 1;
                            }
                }
                $array=array();
                $array['id']=$node->getId();
                $array['title']=$tempArray['title'];
                $array['type']=$rel->getType();
                if(strcasecmp($rel->getType(),'MADE_MEETING')==0 ||strcasecmp($rel->getType(),'ATTEND_MEETING')==0){
                        array_push($fullarray,$array);        
                }
        }
        $lastarray=array('meetings'=>$fullarray);
        echo json_encode($lastarray);
		}else{
			$errorarray = array('errorID' => '5', 'errorMessage'=>$_GET['id'].' node ID is not recognized in database');
            echo json_encode($errorarray);
		}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET') == 0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'comments') == 0){
        //GET userComments
        $id=$_GET['id'];
        $object = $client->getNode($id);
		if (sizeof($object) > 0){
        $array = $object->getProperties();
        if(array_key_exists('nodeType', $array)){
                if(strcasecmp($array['nodeType'], 'User')!=0){
                        echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a user node.'));
                        return 1;
                }
        }
        $allCommentRels = $object->getRelationships(array('COMMENTED'), Relationship::DirectionOut);
        
        $return = array();
        
        foreach($allCommentRels as $rel){
                //get the comment and it's properties
                $comment = $rel->getEndNode();
                //get the user who posted it
                $postedByRel = $comment->getRelationships(array('COMMENTED_ON'), Relationship::DirectionOut);
                $postedTo = $postedByRel[0]->getStartNode();
                $commentInfo = array("commentID" => $comment->getId(),
                                                        "commentBy" => $id,
                                                        "postedTo" => $postedTo->getId());
                //get the json
                $return[] = array_merge($commentInfo, $comment->getProperties());
                
        }
        echo json_encode(array("comments" => $return));
		}else{
			$errorarray = array('errorID' => '5', 'errorMessage'=>$_GET['id'].' node ID is not recognized in database');
            echo json_encode($errorarray);
		}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET') == 0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'groups') == 0){
	/*=============
	Get User Groups
	===============*/
	
	if (!isset($_GET['id'])) {
		echo json_encode(array('errorID'=>'6', 'errorMessage'=>'No ID provided.'));
	 } else {
	
		$userNode = $client->getNode($_GET['id']);
		
		if ($userNode != NULL) {
			//output new contact list
			//$outputArray['userID']=$userNode->getId();
			$relationArray = $userNode->getRelationships(array(), Relationship::DirectionIn);
			$groupArray = array();
			$g=0;
			foreach($relationArray as $rel){
				$relType = $rel->getType();
				if($relType == 'MEMBER_OF_GROUP') {
					$groupNode=$rel->getStartNode();
					$gArray = array();
					$gArray['groupID']=$groupNode->getId();
					$groupArray[$g++] = $gArray;
				} 
			}
			$outputArray['groups'] = $groupArray;
			
			echo json_encode($outputArray);
			
		} else {
		echo json_encode(array('errorID'=>'5', 'errorMessage'=>$_GET['id']. ' node ID is not recognized in database'));
		}
	}	
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET') == 0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'projects') == 0){
	/*===============
	Get User Projects
	================*/
	
	if (!isset($_GET['id'])) {
		echo json_encode(array('errorID'=>'6', 'errorMessage'=>'No ID provided'));
	 } else {
	
		$userNode = $client->getNode($_GET['id']);
		
		if ($userNode != NULL) {
			//output new contact list
			//$outputArray['userID']=$userNode->getId();
			$relationArray = $userNode->getRelationships(array(), Relationship::DirectionIn);
			$projectArray = array();
			$p=0;
			foreach($relationArray as $rel){
				$relType = $rel->getType();
				if($relType == 'WORKS_ON_PROJECT') {
					$projectNode=$rel->getStartNode();
					$pArray = array();
					$pArray['projectID']=$projectNode->getId();
					$projectArray[$p++] = $pArray;
				} 
			}
			$outputArray['projects'] = $projectArray;
			
			echo json_encode($outputArray);
			
		} else {
		echo json_encode(array('errorID'=>'5', 'errorMessage'=>$_GET['id']. ' node ID is not recognized in database'));
		}
	}	
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'Agendas')==0){
        //GET userAgendas
        //can we require in here?
        
		//include "\CommonFunctions.php";
		//include "Topic.php";

        $userNode=$client->getNode($_GET['id']);
		if (sizeof($userNode) > 0){
        $array = $userNode->getProperties();
        if(array_key_exists('nodeType', $array)){
                if(strcasecmp($array['nodeType'], 'User')!=0){
                        echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a user node.'));
                        return 1;
                }
        }
        $relationArray = $userNode->getRelationships(array('CREATED', Relationship::DirectionOut));
        $fullarray=array();
        foreach($relationArray as $rel){
                $node = $rel->getEndNode();
                $tempArray=$node->getProperties();
                $array = array();
                //check if node is an agenda here
                if(array_key_exists('nodeType', $tempArray)){
                	if(strcasecmp($tempArray['nodeType'], 'Agenda')==0){
                		//here we have the Agenda node.
                		//get the information into an array and put it in fullarray
	
						if ($node == NULL)
							return 1;
						//get properties
						$output = array();
						$output["agendaID"] = $node->getId();
						$output["title"] = $node->getProperty("title");
	
	
						//get relationships
						$meetings = getRelatedNodeIDs($node, "FOLLOWS", "meetingID", "IN");
						$output["meetingID"] = $meetings[0]["meetingID"];
		
						$users = getRelatedNodeIDs($node, "CREATED", "userID", "IN");
						$output["userID"]  = $users[0]["userID"];
			
						//get subtopics
						$topics = getRelatedNodeIDs($node, "HAS_TOPIC", "topicID", "OUT");
						$topicList = array();
						$i = sizeof($topics);
						foreach($topics as $topic){
							$topicList[$i--] = getTopicInfo($topic["topicID"], $client);
						}
						$output["content"] = $topicList;
						
                		array_push($fullarray,$output);
                	}
                }
        }
        echo json_encode($fullarray);
		}else{
			$errorarray = array('errorID' => '5', 'errorMessage'=>$_GET['id'].' node ID is not recognized in database');
            echo json_encode($errorarray);
		}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0){
        // register method
    
        // get the json string post content
        $postContent = json_decode(@file_get_contents('php://input'));
        
        $email=$userIndex->findOne('email',$postContent->email);        
        if($email == NULL){ //make sure no nodes already exist with the given email
                
                // create the node
                $userNode= $client->makeNode();
                
                // sets the property on the node
                $userNode->setProperty('email', $postContent->email);
                $userNode->setProperty('password',$postContent->password);
                $userNode->setProperty('phone',$postContent->phone);
                $userNode->setProperty('company',$postContent->company);
                $userNode->setProperty('title',$postContent->title);
                $userNode->setProperty('location',$postContent->location);
                $userNode->setProperty('name',$postContent->name);
                $userNode->setProperty('nodeType','User');
                // actually add the node in the db
                $userNode->save();
                $userIndex->add($userNode, 'email', $postContent->email);
				
				createSettings($client, $userNode->getId(),"", "", "","","","");

                
                // return the id of the node
                echo json_encode(array("userID"=>$userNode->getId()));
        }else{
                echo json_encode(array("errorID"=>2, "errorMessage"=>"Duplicate email"));
        }
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'Users')==0){
        //GET all users
        $users= $userIndex->query('email:*');
        for($ii=0;$ii<sizeof($users);$ii++){
                $array=$users[$ii]->getProperties();
                $array['userID']=$users[$ii]->getId();
                unset($array['nodeType']);
                unset($array['password']);
                $results[$ii]= $array;
        }
        echo json_encode(array("users"=>$results));
}else if( strcasecmp($_SERVER['REQUEST_METHOD'],'GET') == 0){
        //getUserInfo
         $userNode=$client->getNode($_GET['id']);
         
         
         $array = $userNode->getProperties();
        if(array_key_exists('nodeType', $array)){
                if(strcasecmp($array['nodeType'], 'User')!=0){
                        echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a user node.'));
                        return 1;
                }
        }
         unset($array['nodeType']);
         //hide the password
         unset($array['password']);
         unset($array['nodeType']);
         //return the json string
         echo json_encode($array);
        
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'PUT')==0){
        //update user
        $postContent = json_decode(@file_get_contents('php://input'));
        
        $user=$client->getNode($postContent->userID);
        $field = $postContent->field;
        $value = $postContent->value;
        $array = array('userID'=>$user->getId());

        if(sizeof($user) >0){        
                //all fields
                if(array_key_exists($field, $user->getProperties())){
                        //check if the field is the email
                        if(strcasecmp($field, 'email') == 0){
                                //check if email already exists
                                if($userIndex->findOne('email', $value) == NULL){
                                        //remove index on this email
                                        $old = $user->getProperty('email');
                                        $userIndex->remove($user);
                                        //add the new index
                                        $userIndex->add($user, 'email', $value);
                                } else {
                                        //return error if email exists
                                        echo json_encode(array('errorID'=>'11', 'errorMessage'=>'Email already linked to another account'));
                                        return;
                                }
                        }
                        //change the field
                        $user->setProperty($field, $value);
                        $user->save();
                        //get the return array
                        $array = array_merge($array, $user->getProperties());
                        unset($array['password']);
                        unset($array['nodeType']);
                        echo json_encode($array);
                //invalid field
                }else{
                        echo json_encode(array('errorID'=>'9', 'errorMessage'=>$field.'is an unknown field for User'));
                }
        }else{
                echo json_encode(array('errorID'=>'10', 'errorMessage'=>$postContent->userID.'is an unrecognized node ID in the database'));
        }
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE')==0){
        //delete user DELETE
        //get the id
        $id=$_GET['id'];
        
        //get the node
        $node = $client->getNode($id);
        $array = $node->getProperties();
        //make sure the node exists
        if($node != NULL){
                if(array_key_exists('nodeType', $array)){
                        if(strcasecmp($array['nodeType'], 'User')!=0){
                                echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a user node.'));
                                return 1;
                        }
                }
                //check if node has user index
                $email=$node->getProperty('email');
                $user = $userIndex->findOne('email', $email);
                                                
                //only delete the node if it's a note
                if($user != NULL){
                        //get the relationships
						deleteSettings($client, $user->getId());
                        $relations = $user->getRelationships();
                        foreach($relations as $rel){
                                //remove all relationships
                                $rel->delete();
                        }
                        
                        //delete node and return true
                        $user->delete();
                        $array = array('valid'=>'true');
                        unset($array['nodeType']);
                        echo json_encode($array);
                } else {
                        //return an error otherwise
                        $errorarray = array('errorID' => '4', 'errorMessage'=>'Given node ID is not a user node');
                        echo json_encode($errorarray);
                }
        } else {
                //return an error if ID doesn't point to a node
                $errorarray = array('errorID' => '5', 'errorMessage'=>'Given node ID is not recognized in database');
                echo json_encode($errorarray);
        }
}else{
        echo $_SERVER['REQUEST_METHOD'] ." request method not found";
}




/*
 * Password Hashing With PBKDF2 (http://crackstation.net/hashing-security.htm).
 * Copyright (c) 2013, Taylor Hornby
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */



function create_hash($password)
{
    // format: algorithm:iterations:salt:hash
    $salt = base64_encode(mcrypt_create_iv(PBKDF2_SALT_BYTE_SIZE, MCRYPT_DEV_URANDOM));
    return PBKDF2_HASH_ALGORITHM . ":" . PBKDF2_ITERATIONS . ":" .  $salt . ":" .
        base64_encode(pbkdf2(
            PBKDF2_HASH_ALGORITHM,
            $password,
            $salt,
            PBKDF2_ITERATIONS,
            PBKDF2_HASH_BYTE_SIZE,
            true
        ));
}

function validate_password($password, $correct_hash)
{
    $params = explode(":", $correct_hash);
    if(count($params) < HASH_SECTIONS)
       return false;
    $pbkdf2 = base64_decode($params[HASH_PBKDF2_INDEX]);
    return slow_equals(
        $pbkdf2,
        pbkdf2(
            $params[HASH_ALGORITHM_INDEX],
            $password,
            $params[HASH_SALT_INDEX],
            (int)$params[HASH_ITERATION_INDEX],
            strlen($pbkdf2),
            true
        )
    );
}

// Compares two strings $a and $b in length-constant time.
function slow_equals($a, $b)
{
    $diff = strlen($a) ^ strlen($b);
    for($i = 0; $i < strlen($a) && $i < strlen($b); $i++)
    {
        $diff |= ord($a[$i]) ^ ord($b[$i]);
    }
    return $diff === 0;
}

/*
 * PBKDF2 key derivation function as defined by RSA's PKCS #5: https://www.ietf.org/rfc/rfc2898.txt
 * $algorithm - The hash algorithm to use. Recommended: SHA256
 * $password - The password.
 * $salt - A salt that is unique to the password.
 * $count - Iteration count. Higher is better, but slower. Recommended: At least 1000.
 * $key_length - The length of the derived key in bytes.
 * $raw_output - If true, the key is returned in raw binary format. Hex encoded otherwise.
 * Returns: A $key_length-byte key derived from the password and salt.
 *
 * Test vectors can be found here: https://www.ietf.org/rfc/rfc6070.txt
 *
 * This implementation of PBKDF2 was originally created by https://defuse.ca
 * With improvements by http://www.variations-of-shadow.com
 */
function pbkdf2($algorithm, $password, $salt, $count, $key_length, $raw_output = false)
{
    $algorithm = strtolower($algorithm);
    if(!in_array($algorithm, hash_algos(), true))
        trigger_error('PBKDF2 ERROR: Invalid hash algorithm.', E_USER_ERROR);
    if($count <= 0 || $key_length <= 0)
        trigger_error('PBKDF2 ERROR: Invalid parameters.', E_USER_ERROR);

    if (function_exists("hash_pbkdf2")) {
        // The output length is in NIBBLES (4-bits) if $raw_output is false!
        if (!$raw_output) {
            $key_length = $key_length * 2;
        }
        return hash_pbkdf2($algorithm, $password, $salt, $count, $key_length, $raw_output);
    }

    $hash_length = strlen(hash($algorithm, "", true));
    $block_count = ceil($key_length / $hash_length);

    $output = "";
    for($i = 1; $i <= $block_count; $i++) {
        // $i encoded as 4 bytes, big endian.
        $last = $salt . pack("N", $i);
        // first iteration
        $last = $xorsum = hash_hmac($algorithm, $last, $password, true);
        // perform the other $count - 1 iterations
        for ($j = 1; $j < $count; $j++) {
            $xorsum ^= ($last = hash_hmac($algorithm, $last, $password, true));
        }
        $output .= $xorsum;
    }

    if($raw_output)
        return substr($output, 0, $key_length);
    else
        return bin2hex(substr($output, 0, $key_length));
}

function createSettings($client, $userID, $shouldNotify, $whenToNotify, $tasks, $groups, $meetings, $projects){
	//create notification POST
	//get the json string post content
	$settiIndex = new Index\NodeIndex($client, 'UserSettings');
	$settiIndex->save();
	$userIndex = new Index\NodeIndex($client, 'Users');
	$userIndex->save();
	//create the node
	$settiNode= $client->makeNode();
	$settiNode->save();
	//sets the property on the node
	$settiNode->setProperty('shouldNotify', $shouldNotify)->setProperty('whenToNotify', $whenToNotify)->
	setProperty('type', 'UserSettings')
			->setProperty('tasks',$tasks)
			->setProperty('groups',$groups)
			->setProperty('meetings',$meetings)
			->setProperty('projects',$projects);
	
	//actually add the node in the db
	$settiNode->save();
	
	//create a relation to the user who made the meeting
	$user = $client->getNode($userID);
	$settiRel = $settiNode->relateTo($user, 'SETFOR')
			->save();
	
	//add the index        
	$response= $settiIndex->add($settiNode, 'ID', $settiNode->getID());
	
	//return the created meeting id
	$array=array();
	$array['settingsID']=$settiNode->getId();
	return $settiNode->getID();
}
function deleteSettings($client, $userID){
        $settiIndex = new Index\NodeIndex($client, 'UserSettings');
        $settiIndex->save();
        //get the node
        $node = $client->getNode($userID);
        //make sure the node exists
        if($node != NULL){
                //check if node has notification index
            $relationArray = $node->getRelationships(array('SETFOR'), Relationship::DirectionIn);
			
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
			   $array = array('valid'=>'true');
			} else {
				//return an error otherwise
				$errorarray = array('errorID' => '4', 'errorMessage'=>'Given node ID is not a notification node');
			//return an error otherwise
			}
	} else {
      //return an error if ID doesn't point to a node
	}
}

function getRelatedNodeIDs($node, $relationValue, $relationName, $direction) {
	$outputArray = array();
	if ($node != NULL) {
		//$outputArray['nodeID']=$node->getId();
		if ($direction == "IN") {
			$relationArray = $node->getRelationships(array(), Relationship::DirectionIn);
		} else if ($direction == "OUT") {
			$relationArray = $node->getRelationships(array(), Relationship::DirectionOut);
		} else {
			return null;
		}
		$nodeOutputArray = array();
		$i = 0;
		foreach($relationArray as $rel){
			$relType = $rel->getType();
			if($relType == $relationValue) {
				if ($direction == "IN") {
					$relatedNode=$rel->getStartNode();
				} else if ($direction == "OUT") {
					$relatedNode=$rel->getEndNode();
				} else {
					return null;
				}
				$nArray = array();
				$nArray[$relationName]=$relatedNode->getId();
				$nodeOutputArray[$i++] = $nArray;
			}
		}
		return $nodeOutputArray;
		$outputArray['relatedNodes'] = $nodeOutputArray;
		
	} else {
		$outputArray['relatedNodes'] = "";
	}
	
	return $outputArray;
}

function getNodeByID($id, $client) {
	$node = $client->getNode($id);
	if ($node == NULL)  {
		echo json_encode(array('errorID'=>'XX', 'errorMessage'=>$id . ' is an unrecognized node ID in the database'));
		return NULL;
	} else {
		return $node;
	}
}

function getTopicInfo($id, $client){
	$topicNode = getNodeByID($id, $client);
	$result = array();
	$result["title"] = $topicNode->getProperty("title");
	$result["time"] = $topicNode->getProperty("time");
	$result["description"] = $topicNode->getProperty("description");
	
	//get subtopics
	$subtopics = getRelatedNodeIDs($topicNode, "HAS_TOPIC", "topicID", "OUT");
	$topicList = array();
	$i = sizeof($subtopics);
	foreach($subtopics as $subtopic){
		$topicList[$i--] = getTopicInfo($subtopic["topicID"], $client);
	}
	$result["content"] = $topicList;
	
	return $result;
}

?>
