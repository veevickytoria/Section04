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

//Create a graphDb connection 
$client= new Client();

//get the comment index
$commentIndex = new Index\NodeIndex($client, 'Comment');
$commentIndex->save();

//create a comment
if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST') == 0){
	//get json string content
	$postContent = json_decode(@file_get_contents('php://input'));
	
	//create the node and add the properties and index
	$commentNode = $client->makeNode();
	$commentNode->setProperty('content', $postContent->content)
				->setProperty('datePosted', $postContent->datePosted)
				->setProperty('nodeType','Comment');
    $commentNode->save();
	$response = $commentIndex->add($commentNode, 'ID', $commentNode->getID());
	
	//relate the commentNode to the user who posted it	
	//get the user node
	$postedBy = $client->getNode($postContent->commentBy);
	//TODO check to make sure postedBy is a user
	$postedByRelation = $postedBy->relateTo($commentNode, 'COMMENTED')
									->save();
									
	//relate the commentNode to the object it's posted to	
	//get the user node
	$postedTo = $client->getNode($postContent->commentOn);
	//TODO check to make sure postedTo is a meeting, group, project, or task
	$postedToRelation = $commentNode->relateTo($postedTo, 'COMMENTED_ON')
									->save();
	//set up the return array
	$idArray = array();
	$idArray['commentID'] = $commentNode->getId();
	
	echo json_encode(array_merge($idArray, (array)$postContent));
        
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && !isset($_REQUEST['cat'])){
	//basic get info
	//break up the url
    $id = $_GET['id'];
  
	//get comment properties
	$comment = $client->getNode($id);
	$props = $comment->getProperties();
		if(array_key_exists('nodeType', $props)){
			if(strcasecmp($props['nodeType'], 'Comment')!=0){
				echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a comment node.'));
				return 1;
			}
		} 
	
	//find the user who posted the comment
	$postedByRel = $comment->getRelationships(array('COMMENTED'), Relationship::DirectionIn);
	$postedBy = $postedByRel[0]->getStartNode();
	
	//find the object they posted to
	$postedToRel = $comment->getRelationships(array('COMMENTED_ON'), Relationship::DirectionOut);
	$postedTo = $postedToRel[0]->getEndNode();
	
	//combine the information
	$commentInfo = array("commentID" => $id,
						"commentBy" => $postedBy->getId(),
						"commentOn" => $postedTo->getId());
						
	echo json_encode(array_merge($commentInfo, $props));	
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET') == 0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'all') == 0){
	//TODO check incoming ID for index
	//get the ID
	$id=$_GET['id'];
	$object = $client->getNode($id);
	$allCommentRels = $object->getRelationships(array('COMMENTED_ON'), Relationship::DirectionIn);
	
	$return = array();
	//go through all relations
	foreach($allCommentRels as $rel){
		//get the comment and it's properties
		$comment = $rel->getStartNode();
		//get the user who posted it
		$postedByRel = $comment->getRelationships(array('COMMENTED'), Relationship::DirectionIn);
		$postedBy = $postedByRel[0]->getStartNode();
		$commentInfo = array("commentID" => $comment->getId(),
							"commentBy" => $postedBy->getId(),
							"postedTo" => $id);
		//get the json
		$return[] = array_merge($commentInfo, $comment->getProperties());
		
	}
	echo json_encode(array("comments" => $return));	
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'PUT') == 0){
   echo $_SERVER['REQUEST_METHOD'] ."PUT not supported";
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE') == 0){      
	//delete comment
	//get the id from address
	$id=$_GET['id'];
	
	//get the node
	$node = $client->getNode($id);
	$props = $node->getProperties();
	if(array_key_exists('nodeType', $props)){
		if(strcasecmp($props['nodeType'], 'Comment')!=0){
			echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a comment node.'));
			return 1;
		}
	} 
	//make sure the node exists
	if($node != NULL){
		//check if node has comment index
		$comment = $commentIndex->findOne('ID', ''.$id);
							
		//only delete the node if it's a comment
		if($comment != NULL){
			//get the relationships
				$relations = $comment->getRelationships();
				foreach($relations as $rel){
					//remove all relationships
					$rel->delete();
				}
					
				//delete node and return true
				$comment->delete();
				$array = array('valid'=>'true');
				echo json_encode($array);
		} else {
			//return an error otherwise
			$errorarray = array('errorID' => '11', 'errorMessage'=>$_GET['id'].' is an not a comment node.');
			echo json_encode($errorarray);
		}
	} else {
      //return an error if ID doesn't point to a node
		$errorarray = array('errorID' => '5', 'errorMessage'=>$_GET['id'].' node ID is not recognized in the database');
		echo json_encode($errorarray);
	}
}else{
	echo $_SERVER['REQUEST_METHOD'] ." request method not found in Comment";
}
?>
