<?php
/**
 * Include the API PHP file for neo4j
 */
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

	//get the project index
	$projectIndex = new Index\NodeIndex( $client , 'projects' );
	$projectIndex->save();
	
	//get the User index
	$userIndex = new Index\NodeIndex($client, 'Users');
	$userIndex->save();
	 
if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0 ){
	//createProject
	//get the json string post content
	$postContent = json_decode( @file_get_contents( 'php://input' ));
	
	//create the node
	$projectNode = $client->makeNode();
	
	//sets the property on the node
	$projectNode->setProperty( 'projectTitle', $postContent->projectTitle );
	
	//add the node in the db
	$projectNode->save();
	
	//add meeting relationships
	$meetingArray = $postContent->meetings;
    foreach($meetingArray as $item){
		$meeting = $client->getNode($item->meetingID);
        $attRel = $projectNode->relateTo($meeting, 'MEETING_FOR_PROJECT')->save();
    }
	//add note relationships
	$noteArray = $postContent->notes;
    foreach($noteArray as $item){
		$note = $client->getNode($item->noteID);
        $attRel = $projectNode->relateTo($note, 'NOTE_ABOUT_PROJECT')->save();
    }
	//add user relationships
	$userArray = $postContent->members;
    foreach($userArray as $item){
		$user = $client->getNode($item->userID);
        $attRel = $projectNode->relateTo($user, 'WORKS_ON_PROJECT')->save();
    }	
		
	//add the index        
    $response= $projectIndex->add($projectNode, 'ID', $projectNode->getID());
	
	//return info on new projectNode
	//$outputArray=array();
	$outputArray=$projectNode->getProperties();
	$outputArray['projectID']=$projectNode->getId();
    //$outputArray['projectTitle']=$projectNode->projectTitle;
	
	//get relationships
	//Arrays are three levels deep
	// 1-outputArray: overall output array 
	// 2-meeting/note/userOutputArray: all related meetings, notes, and users
	// 3-m/n/uArray: individual meetings, notes, and users
	$relationArray = $projectNode->getRelationships(array(), Relationship::DirectionOut);
	$meetingOutputArray = array();
	$noteOutputArray = array();
	$userOutputArray = array();
	$m=0;
	$n=0;
	$u=0;
	foreach($relationArray as $rel){
		$relType = $rel->getType();
		$userNode = $rel->getEndNode();
		if ($relType == 'MEETING_FOR_PROJECT') {
			$meetingNode=$rel->getEndNode();
			$mArray = array();
			$mArray['meetingID']=$meetingNode->getId();
			$meetingOutputArray[$m++] = $mArray;
		} else if ($relType == 'NOTE_ABOUT_PROJECT') {
			$noteNode=$rel->getEndNode();
			$nArray = array();
			$nArray['noteID']=$noteNode->getId();
			$noteOutputArray[$n++] = $nArray;
		} else if($relType == 'WORKS_ON_PROJECT') {
			$userNode=$rel->getEndNode();
			$uArray = array();
			$uArray['userID']=$userNode->getId();
			$userOutputArray[$u++] = $uArray;
		} 
	}
	$outputArray['meetings'] = $meetingOutputArray;
	$outputArray['notes'] = $noteOutputArray;
	$outputArray['members'] = $userOutputArray;
	
	echo json_encode($outputArray);
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 ){
	//get projectInfo
	 $projectNode=$client->getNode($_GET['id']);
	 
	if ($projectNode != null){
	
		//return info on specified projectNode
		//$outputArray=array();
		$outputArray=$projectNode->getProperties();
		$outputArray['projectID']=$projectNode->getId();
		//$outputArray['projectTitle']=$projectNode->projectTitle;
		
		//get relationships
		//Arrays are three levels deep
		// 1-outputArray: overall output array 
		// 2-meeting/note/userOutputArray: all related meetings, notes, and users
		// 3-m/n/uArray: individual meetings, notes, and users
		$relationArray = $projectNode->getRelationships(array(), Relationship::DirectionOut);
		$meetingOutputArray = array();
		$noteOutputArray = array();
		$userOutputArray = array();
		$m=0;
		$n=0;
		$u=0;
		foreach($relationArray as $rel){
			$relType = $rel->getType();
			$userNode = $rel->getEndNode();
			if ($relType == 'MEETING_FOR_PROJECT') {
				$meetingNode=$rel->getEndNode();
				$mArray = array();
				$mArray['meetingID']=$meetingNode->getId();
				$meetingOutputArray[$m++] = $mArray;
			} else if ($relType == 'NOTE_ABOUT_PROJECT') {
				$noteNode=$rel->getEndNode();
				$nArray = array();
				$nArray['noteID']=$noteNode->getId();
				$noteOutputArray[$n++] = $nArray;
			} else if($relType == 'WORKS_ON_PROJECT') {
				$userNode=$rel->getEndNode();
				$uArray = array();
				$uArray['userID']=$userNode->getId();
				$userOutputArray[$u++] = $uArray;
			} 
		}
		$outputArray['meetings'] = $meetingOutputArray;
		$outputArray['notes'] = $noteOutputArray;
		$outputArray['members'] = $userOutputArray;
		
		echo json_encode($outputArray);
	} else {
		echo "Node not found.";
	}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'PUT')==0){
	//updateProject
	$postContent = json_decode(@file_get_contents('php://input'));
	$projectNode=$client->getNode($postContent->projectID);
	$updated = 0;
	
	if (sizeof($projectNode) > 0){
		if(strcasecmp($postContent->field, 'projectTitle')==0){
			$projectNode->setProperty('projectTitle', $postContent->value);
			$projectNode->save();
			$updated = 1;
		}else if (strcasecmp($postContent->field, 'meetings')==0){
			$relationArray = $projectNode->getRelationships(array('MEETING_FOR_PROJECT'), Relationship::DirectionOut);
			foreach($relationArray as $rel) {
				$rel->delete();
			}
			$meetingArray = $postContent->value;
			foreach($meetingArray as $item){
				$meeting = $client->getNode($item->meetingID);
				$attRel = $projectNode->relateTo($meeting, 'MEETING_FOR_PROJECT')->save();
			}			
			$updated = 1;
		}else if (strcasecmp($postContent->field, 'notes')==0){
			$relationArray = $projectNode->getRelationships(array('NOTE_ABOUT_PROJECT'), Relationship::DirectionOut);
			foreach($relationArray as $rel) {
				$rel->delete();
			}
			$noteArray = $postContent->value;
			foreach($noteArray as $item){
				$note = $client->getNode($item->noteID);
				$attRel = $projectNode->relateTo($note, 'NOTE_ABOUT_PROJECT')->save();
			}
			$updated = 1;
		}else if (strcasecmp($postContent->field, 'members')==0){
			$relationArray = $projectNode->getRelationships(array('WORKS_ON_PROJECT'), Relationship::DirectionOut);
			foreach($relationArray as $rel) {
				$rel->delete();
			}
			$userArray = $postContent->value;
			foreach($userArray as $item){
				$user = $client->getNode($item->userID);
				$attRel = $projectNode->relateTo($user, 'WORKS_ON_PROJECT')->save();
			}
			$updated = 1;
		}
		
		if ($updated==1){
			//return info on specified projectNode
			//$outputArray=array();
			$outputArray=$projectNode->getProperties();
			$outputArray['projectID']=$projectNode->getId();
			//$outputArray['projectTitle']=$projectNode->projectTitle;
			
			//get relationships
			//Arrays are three levels deep
			// 1-outputArray: overall output array 
			// 2-meeting/note/userOutputArray: all related meetings, notes, and users
			// 3-m/n/uArray: individual meetings, notes, and users
			$relationArray = $projectNode->getRelationships(array(), Relationship::DirectionOut);
			$meetingOutputArray = array();
			$noteOutputArray = array();
			$userOutputArray = array();
			$m=0;
			$n=0;
			$u=0;
			foreach($relationArray as $rel){
				$relType = $rel->getType();
				$userNode = $rel->getEndNode();
				if ($relType == 'MEETING_FOR_PROJECT') {
					$meetingNode=$rel->getEndNode();
					$mArray = array();
					$mArray['meetingID']=$meetingNode->getId();
					$meetingOutputArray[$m++] = $mArray;
				} else if ($relType == 'NOTE_ABOUT_PROJECT') {
					$noteNode=$rel->getEndNode();
					$nArray = array();
					$nArray['noteID']=$noteNode->getId();
					$noteOutputArray[$n++] = $nArray;
				} else if($relType == 'WORKS_ON_PROJECT') {
					$userNode=$rel->getEndNode();
					$uArray = array();
					$uArray['userID']=$userNode->getId();
					$userOutputArray[$u++] = $uArray;
				} 
			}
			$outputArray['meetings'] = $meetingOutputArray;
			$outputArray['notes'] = $noteOutputArray;
			$outputArray['members'] = $userOutputArray;
			
			echo json_encode($outputArray);
		} else{
			echo "No node updated";
		}
	}else{
		echo "FALSE node not found";
	}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE')==0){
	//delete project DELETE
	//get the id
	$id=$_GET['id'];
        
	//get the node
	$node = $client->getNode($id);
	//make sure the node exists
	if($node != NULL){
		//check if node has project index
		$project = $projectIndex->findOne('ID', ''.$id);
						
		//only delete the node if it's a note
		if($project != NULL){
			//get the relationships
			$relations = $project->getRelationships();
			foreach($relations as $rel){
				//remove all relationships
				$rel->delete();
			}
			
			//delete node and return true
			$project->delete();
			$array = array('valid'=>'true');
			echo json_encode($array);
		} else {
			//return an error otherwise
			$errorarray = array('errorID' => '4', 'errorMessage'=>'Given node ID is not a project node');
			echo json_encode($errorarray);
		}
	} else {
		//return an error if ID doesn't point to a node
		$errorarray = array('errorID' => '5', 'errorMessage'=>'Given node ID is not recognized in database');
		echo json_encode($errorarray);
	}
}
?>
