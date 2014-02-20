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
		->setProperty( 'completionCriteria', $postContent->completionCriteria )
                ->setProperty('dateAssigned', $postContent->dateAssigned)
		->setProperty( 'nodeType', 'Task');
	//actually add the node in the db
	$taskNode->save();
	
	//sets the relationships on the node
        //make sure the assignedTo person is there
        if(array_key_exists("assignedTo", $postContent)){
            $UserAssigned = $client->getNode($postContent-> assignedTo );
            $assignedTo = $UserAssigned->relateTo( $taskNode,  "ASSIGNED_TO" )->save();
        }
        
        if(array_key_exists("assignedFrom", $postContent)){
            $Assigner = $client->getNode($postContent-> assignedFrom );
            $assignedFrom = $Assigner->relateTo( $taskNode, "ASSIGNED_FROM" )->save();
        }
        
        if(array_key_exists("createdBy", $postContent)){
            $Creator = $client->getNode($postContent-> createdBy );
            $createdBy = $Creator->relateTo( $taskNode, "CREATED_BY" )->save();
        }
        
        if(array_key_exists("meetingID", $postContent)){
            $Meeting = $client->getNode($postContent->meetingID);
            $taskNode->relateTo($Meeting, "BELONGS_TO");
        }
        
	//get node id
	echo json_encode(array("taskID"=>$taskNode->getId())); //output was revised?
        
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 ){
	//viewTaskInfo
	$taskNode=$client->getNode($_GET['id']);
	$array = $taskNode->getProperties();
	if(array_key_exists('nodeType', $array)){
		if(strcasecmp($array['nodeType'], 'Task')!=0){
			echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a task node.'));
			return 1;
		}
	}   
	if ($taskNode != null){
            $array = $taskNode->getProperties();

            //display relationships
            $relationArray = $taskNode->getRelationships(array(), Relationship::DirectionIn);
            foreach($relationArray as $rel){
                    $relType = $rel->getType();
                    $userNode = $rel->getStartNode();
                    if ($relType == 'ASSIGNED_TO') {
                            $userNode=$rel->getStartNode();
                            $array['assignedTo']=$userNode->getId();
                    } else if ($relType == 'ASSIGNED_FROM') {
                            $userNode=$rel->getStartNode();
                            $array['assignedFrom']=$userNode->getId();
                    } else if($relType == 'CREATED_BY') {
                            $userNode=$rel->getStartNode();
                            $array['createdBy']=$userNode->getId();
                    } 
            }

            $array['taskID']=$taskNode->getId();
            echo json_encode($array);
	} else {
            echo "Node not found.";
	}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'PUT')==0){
	//updateTask
	$postContent = json_decode(@file_get_contents('php://input'));
	$taskNode=$client->getNode($postContent->taskID);
	$array = $taskNode->getProperties();
	if(array_key_exists('nodeType', $array)){
		if(strcasecmp($array['nodeType'], 'Task')!=0){
			echo json_encode(array('errorID'=>'11', 'errorMessage'=>$postContent->taskID.' is an not a task node.'));
			return 1;
		}
	}   
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
                }else if(strcasecmp($postContent->field, 'meetingID') == 0){
                        $taskNode->setProperty('meetingID', $postContent->value);
                        $taskNode->save();
                        $update = 1;
		}else if (strcasecmp($postContent->field, 'assignedTo')==0){
			$relationArray = $taskNode->getRelationships(array('ASSIGNED_TO'), Relationship::DirectionIn);
			foreach($relationArray as $rel) {
				$rel->delete();
			}
			$UserAssigned = $client->getNode($postContent-> value );
			$assignedTo = $UserAssigned->relateTo( $taskNode,  "ASSIGNED_TO" )->save();
			$updated = 1;
		}else if (strcasecmp($postContent->field, 'assignedFrom')==0){
			$relationArray = $taskNode->getRelationships(array('ASSIGNED_FROM'), Relationship::DirectionIn);
			foreach($relationArray as $rel) {
				$rel->delete();
			}
			$Assigner = $client->getNode($postContent-> value );
			$assignedFrom = $Assigner->relateTo( $taskNode, "ASSIGNED_FROM" )->save();
			$updated = 1;
		}else if (strcasecmp($postContent->field, 'createdBy')==0){
			echo json_encode(array("errorID"=>"16", "errorMessage"=>"A Task's createdBy field cannot be modified."));
                        return;
		}
		
		if ($updated==1){
			$array = $taskNode->getProperties();
			$relationArray = $taskNode->getRelationships(array(), Relationship::DirectionOut);
			foreach($relationArray as $rel){
				$relType = $rel->getType();
				$userNode = $rel->getStartNode();
				if ($relType == 'ASSIGNED_TO') {
					$userNode=$rel->getStartNode();
					$array['assignedTo']=$userNode->getId();
				} else if ($relType == 'ASSIGNED_FROM') {
					$userNode=$rel->getStartNode();
					$array['assignedFrom']=$userNode->getId();
				} else if($relType == 'CREATED_BY') {
					$userNode=$rel->getStartNode();
					$array['createdBy']=$userNode->getId();
				} 
			}
			unset($array['nodeType']);
			echo json_encode($array);
		} else{
			echo json_encode(array("errorID"=>"17", "errorMessage"=>$postContent->field." is an invalid field for Task."));
                        return;
		}
	}else{
		echo json_encode(array("errorID"=>"5", "errorMessage"=>$postContent->taskID." is not recognized in the database"));
	}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE')==0){
	//deleteTask
	//$postContent = json_decode(@file_get_contents('php://input'));
	//$taskNode=$client->getNode($_DELETE['id']);
	//$taskNode=$client->getNode($_GET['id']);
	//$taskNode=$client->getNode($postContent->taskID);
	preg_match("#(\d+)#", $_SERVER['REQUEST_URI'], $id);
	$taskNode = $client->getNode($id[0]);
	$array = $taskNode->getProperties();
	if(array_key_exists('nodeType', $array)){
		if(strcasecmp($array['nodeType'], 'Task')!=0){
			echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a task node.'));
			return 1;
		}
	}   
	if ($taskNode != null){
		//only delete the node if it's a task
		//$task = $taskIndex->findOne('ID', ''.$id[0]);
		//if($task != null) {

			//delete relationships
			$relationArray = $taskNode->getRelationships();
			foreach($relationArray as $rel){
				$rel->delete();
			}
			//delete the node
			$taskNode->delete();
			echo json_encode(array('valid'=>'true'));
		//} else {
		//	echo json_encode(array('errorID'=>'11', 'errorMessage'=>'TaskDelete: Specified node is not a task.'));
		//}	
	} else {
		echo json_encode(array('errorID'=>'5', 'errorMessage'=>'node ID is not recognized in database.'));
	}
}
?>
