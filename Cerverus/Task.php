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
	//createTask
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
	echo json_encode(array("taskID"=>$taskNode->getId())); //output was revised?
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 ){
	//viewTaskInfo
	$taskNode=$client->getNode($_GET['id']);
	$array = $taskNode->getProperties();
	
	//display relationships
	$relationArray = $taskNode->getRelationships(array(), Relationship::DirectionOut);
	foreach($relationArray as $rel){
		$relType = $rel->getType();
		$userNode = $rel->getEndNode();
		if ($relType == 'ASSIGNED_TO') {
			$userNode=$rel->getEndNode();
			$array['assignedTo']=$userNode->getId();
		} else if ($relType == 'ASSIGNED_BY') {
			$userNode=$rel->getEndNode();
			$array['assignedBy']=$userNode->getId();
		} else if($relType == 'CREATED_BY') {
			$userNode=$rel->getEndNode();
			$array['createdBy']=$userNode->getId();
		} 
	}
	
	
	/*
	$array['assignedTo']=$taskNode->getRelationships(array('ASSIGNED_TO'), Relationship::DirectionOut)->getEndNode()->getId();
	$array['assignedBy']=$taskNode->getRelationships(array('ASSIGNED_BY'), Relationship::DirectionOut);
	$array['createdBy']=$taskNode->getRelationships(array('CREATED_BY'), Relationship::DirectionOut);
	*/
	
	$array['taskID']=$taskNode->getId();
	echo json_encode($array);
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'PUT')==0){
	//updateTask
	$postContent = json_decode(@file_get_contents('php://input'));
	$taskNode=$client->getNode($postContent->taskID);
	$updated = 0;
	
	if (sizeof($taskNode) > 0){
		if(strcasecmp($postContent->field, 'title')==0){
			$taskNode->setProperty('title', $postContent->value);
			$taskNode->save();
			$updated = 1;
		}else if (strcasecmp($postContent->field, 'isCompleted')==0){
			$taskNode->setProperty('isCompleted', $postContent->value);
			$taskNode->save();
			$updated = 1;
		}else if (strcasecmp($postContent->field, 'description')==0){			
			$taskNode->setProperty('description', $postContent->value);
			$taskNode->save();
			$updated = 1;
		}else if (strcasecmp($postContent->field, 'deadline')==0){
			$taskNode->setProperty('deadline', $postContent->value);
			$taskNode->save();
			$updated = 1;
		}else if (strcasecmp($postContent->field, 'dateCreated')==0){
			$taskNode->setProperty('dateCreated', $postContent->value);
			$taskNode->save();
			$updated = 1;
		}else if (strcasecmp($postContent->field, 'dateAssigned')==0){
			$taskNode->setProperty('dateAssigned', $postContent->value);
			$taskNode->save();
			$updated = 1;
		}else if (strcasecmp($postContent->field, 'completionCriteria')==0){
			$taskNode->setProperty('completionCriteria', $postContent->value);
			$taskNode->save();
			$updated = 1;
		}else if (strcasecmp($postContent->field, 'assignedTo')==0){
			$relationArray = $taskNode->getRelationships(array('ASSIGNED_TO'), Relationship::DirectionOut);
			foreach($relationArray as $rel) {
				$rel->delete();
			}
			$UserAssigned = $client->getNode($postContent-> value );
			$assignedTo = $taskNode->relateTo( $UserAssigned,  "ASSIGNED_TO" )->save();
			$updated = 1;
		}else if (strcasecmp($postContent->field, 'assignedBy')==0){
			$relationArray = $taskNode->getRelationships(array('ASSIGNED_BY'), Relationship::DirectionOut);
			foreach($relationArray as $rel) {
				$rel->delete();
			}
			$Assigner = $client->getNode($postContent-> value );
			$assignedBy = $taskNode->relateTo( $Assigner, "ASSIGNED_BY" )->save();
			$updated = 1;
		}else if (strcasecmp($postContent->field, 'createdBy')==0){
			$relationArray = $taskNode->getRelationships(array('CREATED_BY'), Relationship::DirectionOut);
			foreach($relationArray as $rel) {
				$rel->delete();
			}
			$Creator = $client->getNode($postContent-> value );
			$createdBy = $taskNode->relateTo( $Creator, "CREATED_BY" )->save();
			$updated = 1;
		}
		
		if ($updated==1){
			$array = $taskNode->getProperties();
			$relationArray = $taskNode->getRelationships(array(), Relationship::DirectionOut);
			foreach($relationArray as $rel){
				$relType = $rel->getType();
				$userNode = $rel->getEndNode();
				if ($relType == 'ASSIGNED_TO') {
					$userNode=$rel->getEndNode();
					$array['assignedTo']=$userNode->getId();
				} else if ($relType == 'ASSIGNED_BY') {
					$userNode=$rel->getEndNode();
					$array['assignedBy']=$userNode->getId();
				} else if($relType == 'CREATED_BY') {
					$userNode=$rel->getEndNode();
					$array['createdBy']=$userNode->getId();
				} 
			}
			echo json_encode($array);
		} else{
			echo "No node updated";
		}
	}else{
		echo "FALSE node not found";
	}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE')==0){
	//deleteTask
	$postContent = json_decode(@file_get_contents('php://input'));
	$taskNode=$client->getNode($_DELETE['id']);
	//$taskNode=$client->getNode($_GET['id']);
	//$taskNode=$client->getNode($postContent->taskID);
	
	if (sizeof($taskNode) > 0){
		//delete relationships
		$relationArray = $taskNode->getRelationships();
		foreach($relationArray as $rel){
			$rel->delete();
		}
		//delete the node
		$taskNode->delete();
		
		echo "The node was deleted";
	} else {
		echo "FALSE node not found";
	}
}
?>
