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
/*	
if( strcasecmp($_GET['method'],'login') == 0){
	$user=$userIndex->findOne('user',$_GET['user']);
    
	if (sizeof($user)!=0){
		print "TRUE";
	}else{
		print "FALSE";
	}	
}else*/ if( strcasecmp($_GET['method'],'createMeeting') == 0){
	//get the json string post content
	$postContent = json_decode(@file_get_contents('php://input'));
	
	//create the node
	$meetingNode= $client->makeNode();
	
	//sets the property on the node
	$meetingNode->setProperty('user', $postContent->user)
		->setProperty('title', $postContent->title)
		->setProperty('datetime', $postContent->datetime)
		->setProperty('location', $postContent->location);
	
	//actually add the node in the db
	$meetingNode->save();
	
	//create a relation to the user who made the meeting
	$user = $client->getNode($postContent->user);
	$meetingRel = $user->realteTo($meetingNode, 'MADE_MEETING')
		->save();
	
	//get properties on the node
	$meetingProps= $meetingNode->getProperties();
	
	$response= $meetingIndex->add($meetingNode, 'Meetings', $meetingProps['user']);
	echo $response;
}else if( strcasecmp($_GET['method'],'getMeetingInfo') == 0){
	$meetingNode=$client->getNode($_GET['id']);
	foreach ($meetingNode->getProperties() as $key => $value) {
    echo "$key: $value\n";
}
}else if( strcasecmp($_GET['method'],'updateMeeting') == 0){
	//get the json string post content
	$postContent = json_decode(@file_get_contents('php://input'));
	
	$meeting=$client->getNode($postContent->meetingID);
	if(sizeof($meeting >0){
		if(strcasecmp($postContent->field, 'user') ==0){
			$meeting->setProperty('user', $postContent->value);
			$meeting->save();
			$array = $meeting->getProperties();
			//Update the relationship to reflect the new user
			$user = $client->getNode($postContent->user);
			$meetingRel = $meeting->getRelationships(array('MADE_MEETING'));
			foreach ($meetingRel as $rel){
				$rel->setStartNode($user)
					->save();
			}
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
		}
	}
}

?>