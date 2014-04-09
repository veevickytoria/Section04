<?php
/**
 * Include the API PHP file for neo4j
 */
namespace Everyman\Neo4j;
//require_once("CommonFunctions.php");
require "phar://neo4jphp.phar";
require_once "RequestHandler.php";

/*
if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST') == 0){
        //create meeting POST
        //get the json string post content
        $postContent = json_decode(@file_get_contents('php://input'));
        
        //create the node
        $meetingNode= $client->makeNode();
        
        //sets the property on the node
        $meetingNode->setProperty('userID', $postContent->userID)
                ->setProperty('title', $postContent->title)
                ->setProperty('datetime', $postContent->datetime)
                ->setProperty('endDatetime', $postContent->endDatetime)
                ->setProperty('description',$postContent->description)
                ->setProperty('location', $postContent->location)
				->setProperty('nodeType','Meeting');
        
        //actually add the node in the db
        $meetingNode->save();
        
        //create a relation to the user who made the meeting
        $user = $client->getNode($postContent->userID);
        $array = $user->getProperties();
		if(array_key_exists('nodeType', $array)){
			if(strcasecmp($array['nodeType'], 'User')!=0){
				echo json_encode(array('errorID'=>'11', 'errorMessage'=>$postContent->userID.' is an not a user node.'));
				return 1;
			}
		} 
        $meetingRel = $user->relateTo($meetingNode, 'MADE_MEETING')
                ->save();
        
        $attendeeArray = $postContent->attendance;
        foreach($attendeeArray as $attendee){
                $user = $client->getNode($attendee->userID);
                $array = $user->getProperties();
		if(array_key_exists('nodeType', $array)){
			if(strcasecmp($array['nodeType'], 'User')!=0){
				echo json_encode(array('errorID'=>'11', 'errorMessage'=>$attendee->userID.' is an not a user node.'));
				return 1;
			}
		} 
                $attRel = $user->relateTo($meetingNode, 'ATTEND_MEETING')->save();//->setProperty('ATTENDANCE', $attendee->{$key}[0])
        }
        
        //add the index        
        $response= $meetingIndex->add($meetingNode, 'ID', $meetingNode->getID());
        
        //return the created meeting id
        $array=array();
        $array['meetingID']=$meetingNode->getId();
        echo json_encode($array);
        
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET') == 0 && ! isset($_REQUEST['cat'])){
        //getMeetingInfo
        $meetingNode=$client->getNode($_GET['id']);
                $array = $meetingNode->getProperties();
				if(array_key_exists('nodeType', $array)){
					if(strcasecmp($array['nodeType'], 'Meeting')!=0){
						echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a meeting node.'));
						return 1;
					}
				}            
                if ($meetingNode != null) {
                
                        $outputArray=$meetingNode->getProperties();
                
                        //get relationships
                        //Arrays are three levels deep
                        // 1-outputArray: overall output array 
                        // 2-userOutputArray: all related users
                        // 3-uArray: individual users
                        $relationArray = $meetingNode->getRelationships(array(), Relationship::DirectionIn);
                        $userOutputArray = array();
                        $u=0;
                        foreach($relationArray as $rel){
                                $relType = $rel->getType();
                                if($relType == 'ATTEND_MEETING') {
                                        $userNode=$rel->getStartNode();
                                        $uArray = array();
                                        $uArray['userID']=$userNode->getId();
                                        $userOutputArray[$u++] = $uArray;
                                } 
                        }
                        $outputArray['attendance'] = $userOutputArray;
                        
                        echo json_encode($outputArray);
                } else {
                        echo "Node not found";
                }
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'PUT') == 0){
        //updateMeeting
        
        //get the json string post content
        $postContent = json_decode(@file_get_contents('php://input'));
        
        $meeting=$client->getNode($postContent->meetingID);
        if(sizeof($meeting) > 0){
                $array = $meeting->getProperties();
		if(array_key_exists('nodeType', $array)){
			if(strcasecmp($array['nodeType'], 'Meeting')!=0){
				echo json_encode(array('errorID'=>'11', 'errorMessage'=>$postContent->meetingID.' is an not a meeting node.'));
				return 1;
			}
		}
                if(strcasecmp($postContent->field, 'user') ==0){
                        $meeting->setProperty('user', $postContent->value);
                        $meeting->save();
                }else if(strcasecmp($postContent->field, 'title') ==0){
                        $meeting->setProperty('title', $postContent->value);
                        $meeting->save();
                }else if(strcasecmp($postContent->field, 'datetime') ==0){
                        $meeting->setProperty('datetime', $postContent->value);
                        $meeting->save();
                }else if(strcasecmp($postContent->field, 'duration') ==0){
                        $meeting->setProperty('duration', $postContent->value);
                        $meeting->save();
                }else if(strcasecmp($postContent->field, 'location') ==0){
                        $meeting->setProperty('location', $postContent->value);
                        $meeting->save();
                }else if(strcasecmp($postContent->field, 'description') ==0){
                        $meeting->setProperty('description', $postContent->value);
                        $meeting->save();
                }else if(strcasecmp($postContent->field, 'attendance') ==0){

                        $attendanceRels = $meeting->getRelationships(array('ATTEND_MEETING'), Relationship::DirectionIn);
                        //remove old relations
                        foreach($attendanceRels as $rel){
                            $rel->delete();
                        }
                        $userArray = $postContent->value;
                        //add new ones
                        foreach($postContent->value as $userInfo){
                            $user = $client->getNode($userInfo->userID);
                            $user->relateTo($meeting, 'ATTEND_MEETING')->save();
                        }
                }
                
                $props = $meeting->getProperties();
                $props['meetingID'] = $meeting->getId();
                unset($props['nodeType']);
                $props['attendance'] = array();
                foreach($meeting->getRelationships(array('ATTEND_MEETING'), Relationship::DirectionIn) as $userRel){
                    $userID = $userRel->getStartNode()->getId();
                    array_push($props['attendance'], array('userID'=>$userID));
                }
                echo json_encode($props);
        }
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE') == 0){      
        //delete meeting DELETE
        //get the id
        $id=$_GET['id'];
        
        //get the node
        $node = $client->getNode($id);
        //make sure the node exists
        if($node != NULL){
                //check if node has meeting index
                $meeting = $meetingIndex->findOne('ID', ''.$id);
                $array = $meeting->getProperties();
		if(array_key_exists('nodeType', $array)){
			if(strcasecmp($array['nodeType'], 'Meeting')!=0){
				echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a meeting node.'));
				return 1;
			}
		}                                
                //only delete the node if it's a note
                if($meeting != NULL){
                        //get the relationships
                        $relations = $meeting->getRelationships();
                        foreach($relations as $rel){
                                //remove all relationships
                                $rel->delete();
                        }
                        
                        //delete node and return true
                        $meeting->delete();
                        $array = array('valid'=>'true');
                        echo json_encode($array);
                } else {
                        //return an error otherwise
                        $errorarray = array('errorID' => '11', 'errorMessage'=>$_GET['id'].' is an not a meeting node.');
                        echo json_encode($errorarray);
                }
        } else {
                //return an error if ID doesn't point to a node
                $errorarray = array('errorID' => '5', 'errorMessage'=>$_GET['id'].' node ID is not recognized in database');
                echo json_encode($errorarray);
        }
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET') ==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'tasks')==0){
    $meeting = $client->getNode($_GET['id']);
    $properties = $meeting->getProperties();
    if(strcasecmp($properties['nodeType'], 'Meeting') != 0){
        echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a meeting node.'));
        return;
    }
    
    $results = array();
    $tasks = $meeting->getRelationships(array('BELONGS_TO'), Relationship::DirectionIn);
    foreach($tasks as $belongs){
        $task = $belongs->getStartNode();
        $properties = $task->getProperties();
        if(strcasecmp($properties['nodeType'], 'Tasks') != 0){ continue; }
        
        array_push(array('taskID'=>$task->getId(), 'taskTitle'=>$properties['title']));
    }
    
    echo json_encode(array("tasks"=>$results));
    
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET') ==0 && isset($_REQUEST['cat']) && strcasecmp($_REQUEST['cat'], 'agendas')==0){
    $meeting = $client->getNode($_GET['id']);
    $properties = $meeting->getProperties();
    if(strcasecmp($properties['nodeType'], 'Meeting') != 0){
        echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a meeting node.'));
        return;
    }
    // find the agenda
    $agendaRels = $meeting->getRelationships(array('FOLLOWS'), Relationship::DirectionOut);
    if(count($agendaRels) <= 0){
        echo json_encode(array('errorID'=>'12', 'errorMessage'=>'Meeting '. $meeting->getId() . ' has no agenda.'));
        return;        
    }
    
    $agenda = $agendaRels[0]->getEndNode();
    $response = array("agendaID"=>$agenda->getId(), "title"=>$agenda->getProperty("title"));
    
    //merge topic content
    $topics = getRelatedNodeIds($agenda, "HAS_TOPIC", "topicID", "OUT");
    $topicContent = array();
    foreach($topics as $topic){
        array_push($topicContent, getTopicInfo($topic["topicID"], $client));
    }
    $response['content'] = $topicContent;
    echo json_encode($response);
    
}else{
        echo $_SERVER['REQUEST_METHOD'] ." request method not found in Meeting";
}*/
/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////
class Meeting extends RequestHandler{

	function __construct($client){
		parent::__construct("Meeting","meetingID");
		$idName="meetingID";
		$userIndex = new Index\NodeIndex($client, 'Users');
    	$userIndex->save();
	}

	protected function nodeToOutput($node) {
        if ($node == NULL) {
        	return false;
        }
        $array = $node->getProperties();
        $relationArray = $node->getRelationships(array(), Relationship::DirectionIn);
		$userOutputArray = array();
		$u = 0;
     	foreach($relationArray as $rel){
                    $relType = $rel->getType();
                    $userNode = $rel->getStartNode();
                    if ($relType == 'MADE_MEETING') {
                            $array['userID']=$userNode->getId();
                    } else if ($relType == 'ATTEND_MEETING') {
                            $userNode=$rel->getStartNode();
							$uArray = array();
							$uArray['userID']=$userNode->getId();
							$userOutputArray[$u++] = $uArray;
                    } 
        }
		$array['attendance'] = $userOutputArray;
         
        unset($array['nodeType']);
        return json_encode($array);
     }
    function GET($meetingID){
    	$meetingNode=$this->client->getNode($meetingID);
    	$array = $meetingNode->getProperties();
        return $this::nodeToOutput($meetingNode);
    }

    function POST($postContent){
		$meetingNode= $this->client->makeNode();
		$meetingNode->setProperty('title', $postContent->title);
		$meetingNode->setProperty('location', $postContent->location );
		$meetingNode->setProperty('description', $postContent->description );
		$meetingNode->setProperty('endDatetime', $postContent->endDatetime );
		$meetingNode->setProperty('datetime', $postContent->datetime );

		NodeUtility::storeNodeInDatabase($meetingNode);
		if(array_key_exists("userID", $postContent)){
			$UserCreator = $this->client->getNode($postContent-> userID );
			$madeMeeting = $UserCreator->relateTo( $meetingNode,  "MADE_MEETING" )->save();
		}
	
		if(array_key_exists("attendance", $postContent)){
			$attendeeArray = $postContent->attendance;
			foreach($attendeeArray as $attendee){
			$attendeeNode = $this->client->getNode($attendee->userID);
			$attendsMeeting = $attendeeNode->relateTo( $meetingNode, "ATTEND_MEETING" )->save();
			$array = $attendeeNode->getProperties();
			}
		
		return json_encode(array('meetingID'=>$meetingNode->getId()));
		}
	}

	function PUT($putList){
		$meetingNode=$this->client->getNode($putList->meetingID);
		$array = array();
		if(array_key_exists($putList->field, $meetingNode->getProperties())){
			$meetingNode->setProperty($putList->field, $putList->value);
			$meetingNode->save();
			$array = $meetingNode->getProperties();
			$relationArray = $meetingNode->getRelationships(array(), Relationship::DirectionOut);
			$userOutputArray = array();
			foreach($relationArray as $rel){
			   $relType = $rel->getType();
			   $userNode = $rel->getStartNode();
				if ($relType == 'MADE_MEETING') {
					$userNode=$rel->getStartNode();
					$array['userID']=$userNode->getId();
				} else if ($relType == 'ATTEND_MEETING') {
					$userNode=$rel->getStartNode();
					$uArray = array();
					$uArray['userID']=$userNode->getId();
					$userOutputArray[$u++] = $uArray;
				} 
			} 
			$array['attendance'] = $userOutputArray;
           
				unset($array['nodeType']);
				return json_encode($array);
		}
	}

	function DELETE($meetingID){
		$meetingNode = $this->client->getNode($meetingID);
		$array = $meetingNode->getProperties();
		$relationArray = $meetingNode->getRelationships();
		foreach($relationArray as $rel){
			$rel->delete();
		}
		$meetingNode->delete();
		echo json_encode(array('valid'=>'true'));
	}
}
//================this section handles parsing the input to call the appropriate method==========
$postContent = json_decode(file_get_contents('php://input'));
$client = new Client();
$meeting = new Meeting($client);
if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0 ){
    echo $meeting->POST($postContent);
}
else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0 ){
	echo $meeting->GET($_GET['id']);
}
else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'PUT')==0){
	echo $meeting->PUT($postContent);
}
else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE')==0){
	echo $meeting->DELETE($_GET['id']);
} else {
        echo json_encode(array('errorID'=>'5', 'errorMessage'=>'node ID is not recognized in database.'));
}
