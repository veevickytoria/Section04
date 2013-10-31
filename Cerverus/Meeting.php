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
	
	//get properties on the node
	$meetingProps= $meetingNode->getProperties();
	
	$response= $meetingIndex->add($meetingNode, 'Meetings', $meetingProps['user']);
	echo $response;
}
?>