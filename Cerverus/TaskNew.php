<?php

class User extends RequestHandler{

	function _construct($client){
		parent::_construct($client,"Tasks","taskID");
		$idName="taskID";
		$userIndex = new Index\NodeIndex($client, 'Users');
    	$userIndex->save();
	}

	protected function nodeToOutput($node) {
        if ($node == NULL) {
        	return false;
        }
        $array = $node->getProperties();
        $relationArray = $node->getRelationships(array(), Relationship::DirectionIn);
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
        $array['taskID']=$node->getId();
        return $array;
     }
    function GET($taskID)){
    	$taskNode=$client->getNode($taskID);
    	$array = $taskNode->getProperties();
    	if (isTaskNode($taskNode)){
            return nodeToOutput($taskNode);
    	}
    }

    function POST($postContent){
		if($index->findOne($indexKey,$node->getProperty($idName))){
			$taskNode= $client->makeNode();
			$taskNode->setProperty('title', $postContent->title);
      		$taskNode->setProperty('isCompleted', $postContent->isCompleted );
        	$taskNode->setProperty('description', $postContent->description );
        	$taskNode->setProperty('deadline', $postContent->deadline );
        	$taskNode->setProperty('dateCreated', $postContent->dateCreated );
        	$taskNode->setProperty('completionCriteria', $postContent->completionCriteria );
            $taskNode->setProperty('dateAssigned', $postContent->dateAssigned);
            NodeUtility::storeNodeInDatabase($taskNode);
   			$index->add($taskNode);
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
			return json_encode(array($idName=>$taskNode->getId()));
		}
	}

	function PUT($putList){
		$taskNode=$client->getNode($putList->$idName);
		if(isTaskNode($taskNode)){
			if(array_key_exists($putList->field, $array)){
				$taskNode->setProperty($putList->field, $putList->value);
            	$taskNode->save();
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
                    $array['taskID']=$taskNode->getId();
                    return json_encode($array);
                } 
			}
		}
	}

	function DELETE($taskID){
		$taskNode = $client->getNode($taskID);
    	if(isTaskNode($taskNode)){
    		$array = $taskNode->getProperties();
           	$relationArray = $taskNode->getRelationships();
            foreach($relationArray as $rel){
               	$rel->delete();
            }
            $taskNode->delete();
            echo json_encode(array('valid'=>'true'));
		}
	}
	function isTaskNode($node){
		if($node !=null){
			if($index->findOne($indexKey,$node->getProperty($idName)!= null){
				return true;
			}else{
				echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a task node.'));
				return false;
			}
		}else{
			echo json_encode(array('errorID' => '5', 'errorMessage'=>'Given node ID is not recognized in database'));
            return false;
		}
	}

//================this section handles parsing the input to call the appropriate method==========
$postContent= json_decode(file_get_contents('php://input'));

if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0 ){
    echo Task::POST($postContent);
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 ){
	echo Task::GET($_GET['id']);
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'PUT')==0){
	echo Task::PUT($postContent);
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE')==0){
	echo Task::DELETE($_GET['id']);
} else {
        echo json_encode(array('errorID'=>'5', 'errorMessage'=>'node ID is not recognized in database.'));
}
}
?>