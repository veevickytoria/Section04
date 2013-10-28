<?php
/**
 * Include the API PHP file for neo4j
 */
require_once 'Neo4j.php';

/**
 *	Create a graphDb connection 
 */
$graphDb = new GraphDatabaseService('http://localhost:7474/');

/**
 * Deliver HTTP Response
 * @param string $format The desired HTTP response content type: [json, html, xml]
 * @param string $api_response The desired HTTP response data
 * @return void
 **/
function deliver_response($api_response){
    // Define HTTP responses
    $http_response_code = array(
        200 => 'OK',
        400 => 'Bad Request',
        401 => 'Unauthorized',
        403 => 'Forbidden',
        404 => 'Not Found'
    );
 
    // Set HTTP Response
    header('HTTP/1.1 '.$api_response['status'].' '.$http_response_code[ $api_response['status'] ]);
	// Set HTTP Response Content Type
        header('Content-Type: application/json; charset=utf-8');
 
        // Format data into a JSON response
        $json_response = json_encode($api_response);
 
        // Deliver formatted data
        //echo $json_response;
 /*
    // Process different content types
    if( strcasecmp($format,'json') == 0 ){
 
        // Set HTTP Response Content Type
        header('Content-Type: application/json; charset=utf-8');
 
        // Format data into a JSON response
        $json_response = json_encode($api_response);
 
        // Deliver formatted data
        echo $json_response;
 
    }elseif( strcasecmp($format,'xml') == 0 ){
 
        // Set HTTP Response Content Type
        header('Content-Type: application/xml; charset=utf-8');
 
        // Format data into an XML response (This is only good at handling string data, not arrays)
        $xml_response = '<?xml version="1.0" encoding="UTF-8"?>'."\n".
            '<response>'."\n".
            "\t".'<code>'.$api_response['code'].'</code>'."\n".
            "\t".'<data>'.$api_response['data'].'</data>'."\n".
            '</response>';
 
        // Deliver formatted data
        echo $xml_response;
 
    }else{
 
        // Set HTTP Response Content Type (This is only good at handling string data, not arrays)
        header('Content-Type: text/html; charset=utf-8');
 
        // Deliver formatted data
        echo $api_response['data'];
 
    }
 */
    // End script process
    exit;
 
}
 
//// Define whether an HTTPS connection is required
// $HTTPS_required = FALSE;
 
//// Define whether user authentication is required
// $authentication_required = FALSE;
 
// Define API response codes and their related HTTP response
$api_response_code = array(
    0 => array('HTTP Response' => 400, 'Message' => 'Unknown Error'),
    1 => array('HTTP Response' => 200, 'Message' => 'Success'),
    2 => array('HTTP Response' => 403, 'Message' => 'HTTPS Required'),
    3 => array('HTTP Response' => 401, 'Message' => 'Authentication Required'),
    4 => array('HTTP Response' => 401, 'Message' => 'Authentication Failed'),
    5 => array('HTTP Response' => 404, 'Message' => 'Invalid Request'),
    6 => array('HTTP Response' => 400, 'Message' => 'Invalid Response Format')
);
 
// Set default HTTP response of 'ok'
$response['code'] = 0;
$response['status'] = 404;
$response['data'] = NULL;
// echo "</br>response: " .print_r($response) . "</br>";
// --- Step 2: Authorization
/* 
// Optionally require connections to be made via HTTPS
if( $HTTPS_required && $_SERVER['HTTPS'] != 'on' ){
    $response['code'] = 2;
    $response['status'] = $api_response_code[ $response['code'] ]['HTTP Response'];
    $response['data'] = $api_response_code[ $response['code'] ]['Message'];
 
    // Return Response to browser. This will exit the script.
    deliver_response($_GET['format'], $response);
}
 
// Optionally require user authentication
if( $authentication_required ){
 
    if( empty($_POST['username']) || empty($_POST['password']) ){
        $response['code'] = 3;
        $response['status'] = $api_response_code[ $response['code'] ]['HTTP Response'];
        $response['data'] = $api_response_code[ $response['code'] ]['Message'];
    }
 
    // Return an error response if user fails authentication. This is a very simplistic example
    // that should be modified for security in a production environment
    elseif( $_POST['username'] != 'foo' && $_POST['password'] != 'bar' ){
        $response['code'] = 4;
        $response['status'] = $api_response_code[ $response['code'] ]['HTTP Response'];
        $response['data'] = $api_response_code[ $response['code'] ]['Message'];
    }
 
}
*/


// --- Step 3: Process Request
 
// Method A: Say Hello to the API
if( strcasecmp($_GET['method'],'login') == 0){
    $response['code'] = 1;
    $response['status'] = $api_response_code[ $response['code'] ]['HTTP Response'];
    $response['data'] = 'Logged in'; 
	
	$index= new IndexService($graphDb);

	$nodes= $index->getNodes("Userss", "name", $_GET['user'] );
	if(sizeof($nodes) >0){
		echo "TRUE";
	}else{
		echo "FALSE invalid login";
	}
	// foreach($nodes as $node){
		// echo "Node : " . $node->username; 
	// }
	

}else if(strcasecmp($_GET['method'], 'register') ==0){
	$postContent = json_decode(@file_get_contents('php://input'));
	$debugStr= "  postContent: " . "user-".$postContent->user . "    pass-". $postContent->pass; //gets the username from json content
	
	$response['code'] = 1;
    $response['status'] = $api_response_code[ $response['code'] ]['HTTP Response'];
    $response['data'] = 'registering-------  ' . $debugStr; 
	
	//echo "user----".$postContent->user;
	//$indexService= new IndexService($graphDb);
	
	 $userNode = $graphDb->createNode();
	 $userNode->username = "". $postContent->user;
	 $userNode->save();

	 $index= new IndexService($graphDb);
	 $index->index("name", "Userss");
	 
	 $index->addNode($userNode, "Userss","name", $postContent->user);
}else if(strcasecmp($_GET['method'], 'updateUser') ==0){
	$postContent = json_decode(@file_get_contents('php://input'));
	$response['code'] = 1;
    $response['status'] = $api_response_code[ $response['code'] ]['HTTP Response'];
  	$response['data'] = 'updateUser'; 
	
	$index= new IndexService($graphDb);

	$nodes= $index->getNodes("Userss", "name", $_GET['user'] );
	if(sizeof($nodes) >0){
		$node = $nodes[0];
		if(strcasecmp($postContent->field, 'pass') ==0){
			$node->pass = "". $postContent->value;
			$node->save();
			echo $node->getProperties();
			$postContent = json_decode($node->getProperties());
			echo $postContent->username;
			//continue this if/else statement for all other fields in the statement
			/*
			localhost?method=updateUser&user=paul
			{"field":"pass", "value":"######"}
			//the field will be any field available for a user, 
			//like username, password, title, only 1 at a time
			*/
		}else{
			echo "no node updated";
		}
		

	}else{
		echo "FALSE node not found";
	}
}
 
// --- Step 4: Deliver Response
 
// Return Response to browser
deliver_response($response);
 


/**
 *	A little utility function to display a node
 */
function dump_node($node)
{
	$rels = $node->getRelationships();
	
	echo 'Node '.$node->getId()."\t\t\t\t\t\t\t\t".json_encode($node->getProperties())."<br />\n";
	
	foreach($rels as $rel)
	{
		$start = $rel->getStartNode();
		$end = $rel->getEndNode();
		
		echo 	"  Relationship ".$rel->getId()."  :  Node ".$start->getId()." ---".$rel->getType()."---> Node ".$end->getId(),
				"\t\t\t\t\t\t\t\t".json_encode($rel->getProperties())."<br />\n";
	}
}



?>
