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

// --- Step 2: Authorization (this will come later)


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
}else if( strcasecmp($_GET['method'],'getUserInfo') == 0){
	$response['code'] = 1;
	$response['status'] = $api_response_code[ $response['code'] ]['HTTP Response'];
	$response['data'] = 'Logged in'; 

	$index= new IndexService($graphDb);

	$userNodes= $index->getNodes("Userss", "name", $_GET['user'] );
	$userNodeInfo = array();
	foreach($userNodes as &$node){
		$userNodeInfo = $node->_data;
	}
	echo json_encode($userNodeInfo);
}else if(strcasecmp($_GET['method'], 'register') ==0){
	$postContent = json_decode(@file_get_contents('php://input'));
	$debugStr= "  postContent: " . "user-".$postContent->user . "    pass-". $postContent->pass; //gets the username from json content
	
	$response['code'] = 1;
    $response['status'] = $api_response_code[ $response['code'] ]['HTTP Response'];
    $response['data'] = 'registering-------  ' . $debugStr; 
	
	
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
		//get single node
		if(strcasecmp($postContent->field, 'password') ==0){
			$node->password = "". $postContent->value;
			$node->save();
			echo $node->getProperties();
			$postContent = json_decode($node->getProperties());
			//continue this if/else statement for all other fields in the statement
			/*
			localhost?method=updateUser&user=paul
			{"field":"password", "value":"######"}
			*/
		}else if(strcasecmp($postContent->field, 'name') ==0){
			$node->name = "". $postContent->value;
			$node->save();
			echo $node->getProperties();
		}else if(strcasecmp($postContent->field, 'company') ==0){
			$node->company = "". $postContent->value;
			$node->save();
			echo $node->getProperties();
		}else if(strcasecmp($postContent->field, 'phone') ==0){
			$node->phone = "". $postContent->value;
			$node->save();
			echo $node->getProperties();
		}else if(strcasecmp($postContent->field, 'username') ==0){
			$node->username = "". $postContent->value;
			$node->save();
			echo $node->getProperties();
		}else{
			echo "no node updated";
		}
		

	}else{
		echo "FALSE node not found";
	}
	
}
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
