<?php
/**
 * Include the API PHP file for neo4j
 */
namespace Everyman\Neo4j;
require("phar://neo4jphp.phar");


/**
 *	Create a graphDb connection 
 */
$client = new Client();

	//get the Task index
	$taskIndex = new Index\NodeIndex( $client , 'tasks' );
	$taskIndex->save();
	
	//get the User index
	$userIndex = new Index\NodeIndex($client, 'Users');
	$userIndex->save();
	 
if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0 ){
	//get the json string post content
	$postContent = json_decode( @file_get_contents( 'php://input' ));
	
	//create the node
	$taskNode = $client->makeNode();
	
	//sets the property on the node
	$taskNode->setProperty( 'title', $postContent->title )
		->setProperty( 'isCompleted', $postContent->isCompleted )
		->setProperty( 'description', $postContent->description )
		->setProperty( 'deadline', $postContent->deadline )
		->setProperty( 'dateCreated', $postContent->dateCreated )
		->setProperty( 'dateAssigned', $postContent->dateAssigned )
		->setProperty( 'completionCriteria', $postContent->completionCriteria );
	
	//actually add the node in the db
	$taskNode->save();
	
	//sets the relationships on the node
	$UserAssigned = $client->getNode($postContent-> assignedTo );
	$assignedTo = $taskNode->relateTo( $UserAssigned,  "ASSIGNED_TO" )->save();
	$Assigner = $client->getNode($postContent-> assignedBy );
	$assignedBy = $taskNode->relateTo( $Assigner, "ASSIGNED_BY" )->save();
	$Creator = $client->getNode($postContent-> createdBy );
	$createdBy = $taskNode->relateTo( $Creator, "CREATED_BY" )->save();
	
	//get node id
	echo $taskNode->getId(); //revise output
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 ){
	$taskNode=$client->getNode($_GET['id']);
	$array = $taskNode->getProperties();
	echo json_encode($array);
}
?>
