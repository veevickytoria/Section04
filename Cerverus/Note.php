<?php
/**
 * Include the API PHP file for neo4j
 */
require_once 'Neo4j.php';

/**
 *	Create a graphDb connection 
 */

$client= new Client();

	//get the index
	$noteIndex = new Index\NodeIndex($client, 'Note');
	$noteIndex->save();
if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0){
	//createNote method
	
	//get the json string post content
	$postContent = json_decode(@file_get_contents('php://input'));

	//create the node
	$noteNode = $client->makeNode();
	//sets the property on the node
	$noteNode->setProperty('title', $postContent->title)
		->setProperty('description', $postContent->description)
		->setProperty('dateCreated', $postContent->dateCreated)
		->setProperty('content', $postContent->content)
		->save();
	
	//relate the Note to the user who created it
	$user = $client->getNode($postContent->createdBy);
	$userRel = $user->relateTo($noteNode, 'CREATED')->save();
	$response= $noteIndex->add($noteNode, 'ID', $noteNode->getId());
	
	//TODO make this relate to a group, project, or meeting
	
	//get properties on the node
	$noteProps= $noteNode->getProperties();	
	echo json_encode(array_merge(array("noteID"=>$noteNode->getID()), $noteProps));
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0){
	//getNoteInfo
	$noteNode=$client->getNode($_GET['noteID']);			
	$createdByRel = $noteNode->getRelationships(array('CREATED'), Relationship::DirectionIn);
	$createdBy = $createdByRel[0]->getStartNode();	
	echo json_encode(array_merge(array("noteID"=>$noteNode->getId()), $noteNode->getProperties()));
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'PUT')==0){
	//updateNote
	$postContent = json_decode(@file_get_contents('php://input'));
	
	$note=$client->getNode($postContent->noteID);

	if(sizeof($note) >0){
		//get userID of who created the node
		$createdByRel = $note->getRelationships(array('CREATED'), Relationship::DirectionIn);
		$createdBy = $createdByRel[0]->getStartNode();	
		$array = array("noteID"=>$note->getId(),"createdBy"=>$createdBy->getId());
		
		//edit the title property
		if (strcasecmp($postContent->field, 'title') ==0){
			$note->setProperty('title', $postContent->value);
			$note->save();
			echo json_encode(array_merge($array, $note->getProperties()));
		//edit the title property
		}else if(strcasecmp($postContent->field, 'description') ==0){
			$note->setProperty('description', $postContent->value);
			$note->save();
			echo json_encode(array_merge($array, $note->getProperties()));
		//edit the title property
		}else if(strcasecmp($postContent->field, 'content') ==0){
			$note->setProperty('content', $postContent->value);
			$note->save();
			echo json_encode(array_merge($array, $note->getProperties()));
		//tell the user they can't change the person who created the note
		}else if(strcasecmp($postContent->field, 'createdBy') ==0){
			echo json_encode(array("errorID"=>"8", "errorMessage"=>"createdBy field is immutable."));
		//tell the user they can't change the date it was created
		}else if(strcasecmp($postContent->field, 'dateCreated') ==0){
			echo json_encode(array("errorID"=>"8", "errorMessage"=>"dateCreated field is immutable."));
		//unrecognized paramater
		}else{
			echo json_encode(array("errorID"=>"9", "errorMessage"=>$postContent->field." is an unrecognized field."));
		}
	//general 'you done fucked up' error message
	}else{
		echo json_encode(array('errorID' => '5', 'errorMessage'=>'Given node ID is not recognized in database'));
	}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE')==0){
	//deleteNote
	//get the id
	$id=$_GET['noteID'];
        
	//get the node
	$node = $client->getNode($id);
	//make sure the node exists
	if($node != NULL){
		//check if node has note index
		$note = $noteIndex->findOne('ID', ''.$id);
						
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
			echo json_encode($errorarray);
		}
	} else {
		//return an error if ID doesn't point to a node
		$errorarray = array('errorID' => '5', 'errorMessage'=>'Given node ID is not recognized in database');
		echo json_encode($errorarray);
	}
}else{
	echo $_SERVER['REQUEST_METHOD'] ." request method not found";
}
?>
