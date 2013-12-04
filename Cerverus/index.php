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
}else if( strcasecmp($_GET['method'],'getUserInfo') == 0){
	$userNode=$client->getNode($_GET['id']);
	foreach ($userNode->getProperties() as $key => $value) {
    echo "$key: $value\n";
	}
}else if(strcasecmp($_GET['method'], 'updateUser') ==0){
	//get the json string post content
	$postContent = json_decode(@file_get_contents('php://input'));
	
//	$index= new IndexService($graphDb);
	$user=$userIndex->findOne('email',$postContent->email);
	//$nodes= $index->getNodes("Users", "email", $_GET['email'] );

	if(sizeof($user) >0){
		if(strcasecmp($postContent->field, 'password') ==0){
			$user->setProperty('password', $postContent->value);
			$user->save();
			foreach ($user->getProperties() as $key => $value) {
				echo "$key: $value\n";
			}
			//continue this if/else statement for all other fields in the statement
			/*
			localhost?method=updateUser&user=paul
			{"field":"password", "value":"######"}
			*/
		}else if(strcasecmp($postContent->field, 'name') ==0){
			$user->setProperty('name', $postContent->value);
			$user->save();
			echo $user->getProperties();
		}else if(strcasecmp($postContent->field, 'company') ==0){
			$user->setProperty('company', $postContent->value);
			$user->save();
			foreach ($user->getProperties() as $key => $value) {
				echo "$key: $value\n";
			}
		}else if(strcasecmp($postContent->field, 'phone') ==0){
			$user->setProperty('phone', $postContent->value);
			$user->save();
			foreach ($user->getProperties() as $key => $value) {
				echo "$key: $value\n";
			}
		}else if(strcasecmp($postContent->field, 'title') ==0){
			$user->setProperty('title', $postContent->value);
			$user->save();
			foreach ($user->getProperties() as $key => $value) {
				echo "$key: $value\n";
			}
		}else if(strcasecmp($postContent->field, 'location') ==0){
			$user->setProperty('location', $postContent->value);
			$user->save();
			foreach ($user->getProperties() as $key => $value) {
				echo "$key: $value\n";
			}
		}else if(strcasecmp($postContent->field, 'email') ==0){
			$user->setProperty('email', $postContent->value);
			$user->save();
			foreach ($user->getProperties() as $key => $value) {
				echo "$key: $value\n";
			}
		}else{
			echo "no node updated";
		}
		

	}else{
		echo "FALSE node not found";
	}
}
?>