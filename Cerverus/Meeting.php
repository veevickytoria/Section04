<?php
/**
 * Include the API PHP file for neo4j
 */
namespace Everyman\Neo4j;
require("phar://neo4jphp.phar");


/**
 *	Create a graphDb connection 
 */
$client= new Client();

	//get the index
	$meetingIndex = new Index\NodeIndex($client, 'Meetings');
	$meetingIndex->save();

//create meeting POST
if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST') == 0){
	//get the json string post content
	$postContent = json_decode(@file_get_contents('php://input'));
	
	//create the node
	$meetingNode= $client->makeNode();
	
	//sets the property on the node
	$meetingNode->setProperty('user', $postContent->userID)
		->setProperty('title', $postContent->title)
		->setProperty('datetime', $postContent->datetime)
		->setProperty('location', $postContent->location);
	
	//actually add the node in the db
	$meetingNode->save();
	
	//create a relation to the user who made the meeting
	$user = $client->getNode($postContent->userID);
	$meetingRel = $user->relateTo($meetingNode, 'MADE_MEETING')
		->save();
	
	$attendeeArray = $postContent->attendance;
	foreach($attendeeArray as $attendee){
		$key = key(get_object_vars($attendee));
		$users = $userIndex->query('name:'.$key[0]);
		$user = $users[0];
		$attRel = $user->relateTo($meeting, 'ATTEND_MEETING')
			->setProperty('ATTENDANCE', $attendee->{$key}[0])
			->save();
	}
	
	//add the index	
	$response= $meetingIndex->add($meetingNode, 'ID', $meetingNode->getID());
	
	//return the created meeting id
	echo '{"meetingID":"'.$meetingNode->getID().'"}';
	
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET') == 0){
	//getMeetingInfo
	$meetingNode=$client->getNode($_GET['id']);
	foreach ($meetingNode->getProperties() as $key => $value) {
		echo "$key: $value\n";
	}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'PUT') == 0){
	//updateMeeting
	
	//get the json string post content
	$postContent = json_decode(@file_get_contents('php://input'));
	
	$meeting=$client->getNode($postContent->meetingID);
	if(sizeof($meeting > 0)){
		if(strcasecmp($postContent->field, 'user') ==0){
			$meeting->setProperty('user', $postContent->value);
			$meeting->save();
			$array = $meeting->getProperties();
			echo json_encode($array);
		}else if(strcasecmp($postContent->field, 'title') ==0){
			$meeting->setProperty('title', $postContent->value);
			$meeting->save();
			$array = $meeting->getProperties();
			echo json_encode($array);
		}else if(strcasecmp($postContent->field, 'datetime') ==0){
			$meeting->setProperty('datetime', $postContent->value);
			$meeting->save();
			$array = $meeting->getProperties();
			echo json_encode($array);
		}else if(strcasecmp($postContent->field, 'location') ==0){
			$meeting->setProperty('location', $postContent->value);
			$meeting->save();
			$array = $meeting->getProperties();
			echo json_encode($array);
		}else if(strcasecmp($postContent->field, 'attendance') ==0){
			$attendeeArray = $postContent->value;
			foreach($attendeeArray as $attendee){
				$key = key(get_object_vars($attendee));
				$users = $userIndex->query('name:'.$key[0]);
				$meetingRels = $meeting->getRelationships('ATTEND_MEETING')
				foreach($meetingRels as $rel){
					if ($users[0]->getID() == $rel->getStartNode->getID()){
						$rel->setProperty('ATTENDANCE', $attendee->{$key}[0])
							->save();
					}else{
						$attendRel = $users[0]->relateTo($meeting)
							->setProperty('ATTENDANCE', $attendee->{$key}[0])
							->save();
					}
				}
			}
			$array = $meeting->getProperties();
			echo json_encode($array);
		}
	}
//delete meeting DELETE
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE') == 0){	
	//get the id
	preg_match("#(\d+)#", $_SERVER['REQUEST_URI'], $id);
	
	//get the node
	$node = $client->getNode($id[0]);
	//make sure the node exists
	if($node != NULL){
		//check if node has meeting index
		$meeting = $meetingIndex->findOne('ID', ''.$id[0]);
				
		//only delete the node if it's a meeting
		if($meeting != NULL){
			//get the relationships
			$relations = $meeting->getRelationships();
			foreach($relations as $rel){
				//remove all relationships
				$rel->delete();
			}		
			
			//delete node and return true
			$meeting->delete();
			echo '{"valid":"true"}';
		} else {
			//return an error otherwise
			echo '"errorID":"4", "errorMessage":"Given node ID is not a meeting node"}';
		}
	} else {
		//return an error if ID doesn't point to a node
		echo '{"errorID":"5", "errorMessage":"Given node ID is not recognized in database"}';
	}
}else{
	echo $_SERVER['REQUEST_METHOD'] ." request method not found in Meeting";
}
?>
