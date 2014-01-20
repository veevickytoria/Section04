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
        header("Access-Control-Allow-Origin: {$_SERVER['HTTP_ORIGIN']}");
        header('Access-Control-Allow-Credentials: true');
        header('Access-Control-Max-Age: 86400');    // cache for 1 day
    }

    // Access-Control headers are received during OPTIONS requests
    if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {

        if (isset($_SERVER['HTTP_ACCESS_CONTROL_REQUEST_METHOD']))
            header("Access-Control-Allow-Methods: GET, POST, OPTIONS");         

        if (isset($_SERVER['HTTP_ACCESS_CONTROL_REQUEST_HEADERS']))
            header("Access-Control-Allow-Headers:        {$_SERVER['HTTP_ACCESS_CONTROL_REQUEST_HEADERS']}");

        exit(0);
    }
/**
 *	Create a graphDb connection 
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
		echo json_encode(array("errorID"=>2, "errorMessage"=>"email invalid email or password"));
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
        $relationArray = $userNode->getRelationships(array());
        $fullarray=array();
        foreach($relationArray as $rel){
				$booleanFound=0;
                $node = $rel->getStartNode();
                $tempArray=$node->getProperties();
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
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'Notes')==0){
	$userNode=$client->getNode($_GET['id']);
	$relationArray = $userNode->getRelationships(array());
	$fullarray=array();
	foreach($relationArray as $rel){
		$node = $rel->getStartNode();
		$tempArray=$node->getProperties();
		$array=array();
		$array['noteID']=$node->getId();
		$array['noteTitle']=$tempArray['noteTitle'];
	}
	$lastarray=array('notes'=>$fullarray);
	echo json_encode($lastarray);
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'tasks')==0){
	//GET getUserTasks
	$userNode=$client->getNode($_GET['id']);
	$relationArray = $userNode->getRelationships(array());
	$fullarray=array();
	foreach($relationArray as $rel){
		$node = $rel->getStartNode();
		$tempArray=$node->getProperties();
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
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'meetings')==0){
	//GET getUserMeetings
	$userNode=$client->getNode($_GET['id']);
	$relationArray = $userNode->getRelationships(array());
	$fullarray=array();
	foreach($relationArray as $rel){
		$node = $rel->getStartNode();
		$tempArray=$node->getProperties();
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
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET') == 0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'comments') == 0){
	//get the ID
	$id=$_GET['id'];
	$object = $client->getNode($id);
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
		
		// actually add the node in the db
		$userNode->save();
		
		$userIndex->add($userNode, 'email', $postContent->email);
		
		// return the id of the node
		echo json_encode(array("userID"=>$userNode->getId()));
	}else{
		echo json_encode(array("errorID"=>2, "errorMessage"=>"duplicate email"));
	}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'Users')==0){
	//GET all users
	$users= $userIndex->query('email:*');
	for($ii=0;$ii<sizeof($users);$ii++){
		$array=$users[$ii]->getProperties();
		$array['userID']=$users[$ii]->getId();
		unset($array['password']);
		$results[$ii]= $array;
	}
	echo json_encode(array("users"=>$results));
}else if( strcasecmp($_SERVER['REQUEST_METHOD'],'GET') == 0){
	//getUserInfo
	 $userNode=$client->getNode($_GET['id']);
	 $array = $userNode->getProperties();
	
	 //hide the password
	 $array['password']="********";
	
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
			echo json_encode($array);
		//invalid field
		}else{
			echo json_encode(array('errorID'=>'9', 'errorMessage'=>$field.'is an unknown field for User'));
		}
	}else{
		echo json_encode(array('errorID'=>'10', 'errorMessage'=>$postContent->userID.'is an unrecognized node ID in the database'));
	}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE')==0){
	preg_match("#(\d+)#", $_SERVER['REQUEST_URI'], $id);
	$node = $client->getNode($id[0]);
	//check if node is a node
	if ($node != null){
			//check if node has user index
			$userNode = $userIndex->findOne('email', $node->getProperty('email'));
			//check if it is a user
			if($userNode != NULL){
			//delete relationships
			$relationArray = $userNode->getRelationships();
			foreach($relationArray as $rel){
				$rel->delete();
			}
			//delete the node
			$userNode->delete();
			echo json_encode(array('valid'=>'true'));
			}
	} else {
		echo json_encode(array('errorID'=>'8', 'errorMessage'=>'TaskDelete: Specified node does not exist.'));
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



?>
