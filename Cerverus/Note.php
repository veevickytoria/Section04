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
	$noteIndex = new Index\NodeIndex($client, 'notes');
	$noteIndex->save();
	/*
if( strcasecmp($_GET['method'],'login') == 0){
	$note=$noteIndex->findOne('note',$_GET['note']);
    
	if (sizeof($note)!=0){
		print "TRUE";
	}else{
		print "FALSE";
	}	
}else */if( strcasecmp($_GET['method'],'createNote') == 0){
	//get the json string post content
	$postContent = json_decode(@file_get_contents('php://input'));
	
	//create the node
	$noteNode= $client->makeNode();
	
	//sets the property on the node
	$noteNode->setProperty('user', $postContent->user)
		->setProperty('title', $postContent->title)
		->setProperty('location', $postContent->location)
		->setProperty('date', $postContent->date);
	
	//actually add the node in the db
	$noteNode->save();
	
	//get properties on the node
	$noteProps= $noteNode->getProperties();
	
	$response= $noteIndex->add($noteNode, 'user', $noteProps['user']);
	echo $response;
}
?>