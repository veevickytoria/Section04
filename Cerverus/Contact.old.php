<?php

namespace Everyman\Neo4j;
require("phar://neo4jphp.phar");


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
 *	Create a graphDb connection 
 */
$client = new Client();
	
	//get the User index
	$userIndex = new Index\NodeIndex($client, 'Users');
	$userIndex->save();
if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'relations') == 0){		
	/*============================
	Delete Specific Contact Method
	==============================*/
	if (!isset($_GET['id'])) {
		echo json_encode(array('errorID'=>'6', 'errorMessage'=>'No ID provided'));
	} else {
		$id = $_GET['id'];
		$contactRelationship = $client->getRelationship($_GET['id']);
	
		if ($contactRelationship == null) {
			echo json_encode(array('errorID'=>'7', 'errorMessage'=>$id. ' is an unrecognized relationship ID in the database'));
		} else {
			$contactRelationship->delete();
		
			$contactRelationship = $client->getRelationship($_GET['id']);
			if ($contactRelationship == null) {
				$outputArray['valid'] = 'true';
				echo json_encode($outputArray);
			} else {
				echo json_encode(array('errorID'=>'8', 'errorMessage'=>'Relationship was not deleted.'));
			}
		}
	}
} else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET') == 0){
	/*=================
	Get Contacts Method 
	==================*/
	
	if (!isset($_GET['id'])) {
		echo json_encode(array('errorID'=>'6', 'errorMessage'=>'ID was not set. Usage: contact<forwardslash>@id.'));
	 } else {
	
		$userNode = $client->getNode($_GET['id']);
		
		if ($userNode != NULL) {
			//output new contact list
			$outputArray['userID']=$userNode->getId();
			$relationArray = $userNode->getRelationships(array(), Relationship::DirectionOut);
			$contactOutputArray = array();
			$u=0;
			foreach($relationArray as $rel){
				$relType = $rel->getType();
				if($relType == 'CONTACT') {
					$contactNode=$rel->getEndNode();
					$uArray = array();
					$uArray['contactID']=$contactNode->getId();
					$uArray['relationID']=$rel->getId();
					$contactOutputArray[$u++] = $uArray;
				} 
			}
			$outputArray['contacts'] = $contactOutputArray;
			
			echo json_encode($outputArray);
		} else {
		echo json_encode(array('errorID'=>'5', 'errorMessage'=>$_GET['id']. ' is an unrecognized node ID in the database'));
		}
	}

}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0){
	/*=================
	Set Contacts Method
	=================*/
    $postContent = json_decode(@file_get_contents('php://input'));
    $userNode = $client->getNode($postContent->userID);
    if ($userNode == null) {
        echo json_encode(array('errorID'=>'5', 'errorMessage'=>$postContent->userID. ' is an unrecognized node ID in the database'));
    } else {
        //remove old contact relationships
        $relationArray = $userNode->getRelationships(array('CONTACT'), Relationship::DirectionOut);
        foreach($relationArray as $rel) {
            $rel->delete();
        }

        //add new contact relationships
        $contactArray = $postContent->contacts;

        $badNode = false;
        foreach($contactArray as $item){
            $user = $client->getNode($item->contactID);
            if ($user == null) {
                $badNode = $item->contactID;
            }
        }

        if ($badNode){
            echo json_encode(array('errorID'=>'5', 'errorMessage'=>$badNode. ' is an unrecognized node ID in the database'));
        } else {

            foreach($contactArray as $item){
                $user = $client->getNode($item->contactID);
                $attRel = $userNode->relateTo($user, 'CONTACT')->save();
            }

            //output new contact list
            $outputArray['userID']=$userNode->getId();
            $relationArray = $userNode->getRelationships(array(), Relationship::DirectionOut);
            $contactOutputArray = array();
            $u=0;
            foreach($relationArray as $rel){
                $relType = $rel->getType();
                if($relType == 'CONTACT') {
                    $contactNode=$rel->getEndNode();
                    $uArray = array();
                    $uArray['contactID']=$contactNode->getId();
                    $uArray['relationID']=$rel->getId();
                    $contactOutputArray[$u++] = $uArray;
                } 
            }
            $outputArray['contacts'] = $contactOutputArray;

            echo json_encode($outputArray);
        }
    }
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'PUT')==0){		
	/*====================
	Append Contacts Method
	=====================*/
	$postContent = json_decode(@file_get_contents('php://input'));
	$userNode = $client->getNode($postContent->userID);
	if ($userNode == null) {
		echo json_encode(array('errorID'=>'5', 'errorMessage'=>$postContent->userID. 'is an unrecognized node ID in the database'));
	} else {
		//add new contact relationships
		$contactArray = $postContent->contacts;
		
		$badNode = false;
		foreach($contactArray as $item){
			$user = $client->getNode($item->contactID);
			if ($user == null) {
				$badNode = $item->contactID;
			}
		}
		
		if ($badNode){
			echo json_encode(array('errorID'=>'5', 'errorMessage'=>$badNode. ' is an unrecognized node ID in the database'));
		} else {
		
			foreach($contactArray as $item){
				$user = $client->getNode($item->contactID);
				$attRel = $userNode->relateTo($user, 'CONTACT')->save();
			}
			
			//output new contact list
			$outputArray['userID']=$userNode->getId();
			$relationArray = $userNode->getRelationships(array(), Relationship::DirectionOut);
			$contactOutputArray = array();
			$u=0;
			foreach($relationArray as $rel){
				$relType = $rel->getType();
				if($relType == 'CONTACT') {
					$contactNode=$rel->getEndNode();
					$uArray = array();
					$uArray['contactID']=$contactNode->getId();
					$uArray['relationID']=$rel->getId();
					$contactOutputArray[$u++] = $uArray;
				} 
			}
			$outputArray['contacts'] = $contactOutputArray;
		
			echo json_encode($outputArray);		
		}
	}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE')==0){		
	/*====================
	Delete Contacts Method
	=====================*/
	if (!isset($_GET['id'])) {
		echo json_encode(array('errorID'=>'6', 'errorMessage'=>'ID was not set. Usage: contact<forwardslash>@id.'));
	} else {
		$id = $_GET['id'];
		$userNode = $client->getNode($_GET['id']);
	
		if ($userNode == null) {
			echo json_encode(array('errorID'=>'5', 'errorMessage'=>$id. ' is an unrecognized node ID in the database'));
		} else {
			//remove all contact relationships
			$relationArray = $userNode->getRelationships(array('CONTACT'), Relationship::DirectionOut);
			foreach($relationArray as $rel) {
				$rel->delete();
			}
			
			//output new contact list
			$outputArray['userID']=$userNode->getId();
			$relationArray = $userNode->getRelationships(array(), Relationship::DirectionOut);
			$contactOutputArray = array();
			$u=0;
			foreach($relationArray as $rel){
				$relType = $rel->getType();
				if($relType == 'CONTACT') {
					$contactNode=$rel->getEndNode();
					$uArray = array();
					$uArray['contactID']=$contactNode->getId();
					$uArray['relationID']=$rel->getId();
					$contactOutputArray[$u++] = $uArray;
				} 
			}
			$outputArray['contacts'] = $contactOutputArray;
		
			echo json_encode($outputArray);		
		}
	}
} 

?>
