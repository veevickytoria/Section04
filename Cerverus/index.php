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
	$userIndex = new Index\NodeIndex($client, 'Users');
	$userIndex->save();
	
if( strcasecmp($_GET['method'],'login') == 0){
	$user=$userIndex->findOne('user',$_GET['user']);
    
	if (sizeof($user)!=0){
		print "TRUE";
	}else{
		print "FALSE";
	}	
}else if( strcasecmp($_GET['method'],'register') == 0){
	//get the json string post content
	$postContent = json_decode(@file_get_contents('php://input'));
	
	//create the node
	$userNode= $client->makeNode();
	
	//sets the property on the node
	$userNode->setProperty('name', $postContent->user)->setProperty('email', '1234@gmail.com');
	
	//actually add the node in the db
	$userNode->save();
	
	//get properties on the node
	$userProps= $userNode->getProperties();
	
	$response= $userIndex->add($userNode, 'user', $userProps['name']);
	echo $response;
}
?>