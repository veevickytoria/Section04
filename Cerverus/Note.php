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
if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0){
	//createNote method
	
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
	
	//realte the Note to the user who created it
	$user = $client->getNode($postContent->user);
	$userRel = $user->relateTo($noteNode, 'CREATED')
		->save();
	
	//get properties on the node
	$noteProps= $noteNode->getProperties();
	
	$response= $noteIndex->add($noteNode, 'user', $noteProps['user']);
	echo $response;
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'comments')==0){
	//viewComments
	$note = $client->getNode($_GET['id']);
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0){
	//getNoteInfo
	$noteNode=$client->getNode($_GET['id']);
	foreach ($noteNode->getProperties() as $key => $value) {
		echo "$key: $value\n";
	}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'PUT')==0){
	//updateNote
	$postContent = json_decode(@file_get_contents('php://input'));
	
	$note=$client->getNode($postContent->noteID);

	if(sizeof($note) >0){
		if(strcasecmp($postContent->field, 'user') ==0){
			$note->setProperty('user', $postContent->value);
			$note->save();
			$array = $note->getProperties();
			echo json_encode($array);
		}else if(strcasecmp($postContent->field, 'title') ==0){
			$note->setProperty('title', $postContent->value);
			$note->save();
			$array = $note->getProperties();
			echo json_encode($array);
		}else if(strcasecmp($postContent->field, 'description') ==0){
			$note->setProperty('description', $postContent->value);
			$note->save();
			$array = $note->getProperties();
			echo json_encode($array);
		}else if(strcasecmp($postContent->field, 'content') ==0){
			$note->setProperty('content', $postContent->value);
			$note->save();
			$array = $note->getProperties();
			echo json_encode($array);
		}else{
			echo "No node updated.";
		}
	
	}else{
		echo "FALSE node not found";
	}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE')==0){
	//deleteNote
	//get the id
        preg_match("#(\d+)#", $_SERVER['REQUEST_URI'], $id);
        
        //get the node
        $node = $client->getNode($id[0]);
        //make sure the node exists
        if($node != NULL){
                //check if node has note index
                $note = $noteIndex->findOne('ID', ''.$id[0]);
                                
                //only delete the node if it's a note
                if($note != NULL){
                        //get the relationships
                        $relations = $note->getRelationships();
                        foreach($relations as $rel){
                                //remove all relationships
                                $rel->delete();
                        }                
                        
                        //delete node and return true
                        $note->delete();
                  	    $array = array('valid'=>'true');
 						echo json_encode($array);
                } else {
                        //return an error otherwise
                        $errorarray = array('errorID' => '4', 'errorMessage'=>'Given node ID is not a note node');
 				}
		echo json_encode($errorarray);
		} else {
     	//return an error if ID doesn't point to a node
		echo '{"errorID":"5", "errorMessage":"Given node ID is not recognized in database"}';
		$errorarray = array('errorID' => '5', 'errorMessage'=>'Given node ID is not recognized in database');
		echo json_encode($errorarray);
	}
}else{
	echo $_SERVER['REQUEST_METHOD'] ." request method not found";
}
?>