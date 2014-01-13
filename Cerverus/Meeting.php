<?php
/**
 * Include the API PHP file for neo4j
 */
namespace Everyman\Neo4j;
require("phar://neo4jphp.phar");


/**
 *        Create a graphDb connection 
 */
$client= new Client();

        //get the index
        $meetingIndex = new Index\NodeIndex($client, 'Meetings');
        $meetingIndex->save();

if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST') == 0){
	//create meeting POST
        //get the json string post content
        $postContent = json_decode(@file_get_contents('php://input'));
        
        //create the node
        $meetingNode= $client->makeNode();
        
        //sets the property on the node
        $meetingNode->setProperty('user', $postContent->userID)
                ->setProperty('title', $postContent->title)
                ->setProperty('datetime', $postContent->datetime)
				->setProperty('endDatetime', $postContent->datetime)
                ->setProperty('description',$postContent->description)
                ->setProperty('location', $postContent->location);
        
        //actually add the node in the db
        $meetingNode->save();
        
        //create a relation to the user who made the meeting
        $user = $client->getNode($postContent->userID);
        $meetingRel = $meetingNode->relateTo($user, 'MADE_MEETING')
                ->save();
        
        $attendeeArray = $postContent->attendance;
        foreach($attendeeArray as $attendee){
                $user = $client->getNode($attendee->userID);
                $attRel = $meetingNode->relateTo($user, 'ATTEND_MEETING')->save();//->setProperty('ATTENDANCE', $attendee->{$key}[0])
        }
        
        //add the index        
        $response= $meetingIndex->add($meetingNode, 'ID', $meetingNode->getID());
        
        //return the created meeting id
        $array=array();
        $array['meetingID']=$meetingNode->getId();
        echo json_encode($array);
        
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET') == 0){
        //getMeetingInfo
        $meetingNode=$client->getNode($_GET['id']);
        $array=$meetingNode->getProperties();
        echo json_encode($array);
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'PUT') == 0){
        //updateMeeting
        
        //get the json string post content
        $postContent = json_decode(@file_get_contents('php://input'));
        
        $meeting=$client->getNode($postContent->meetingID);
        if(sizeof($meeting > 0)){
                if(strcasecmp($postContent->field, 'user') ==0){
                        $meeting->setProperty('user', $postContent->value);
                        $meeting->save();
                        $array = $meeting->getProperties();
                        $array['meetingID']=$meeting->getId();
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'title') ==0){
                        $meeting->setProperty('title', $postContent->value);
                        $meeting->save();
                        $array = $meeting->getProperties();
                        $array['meetingID']=$meeting->getId();
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'datetime') ==0){
                        $meeting->setProperty('datetime', $postContent->value);
                        $meeting->save();
                        $array = $meeting->getProperties();
                        $array['meetingID']=$meeting->getId();
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'endDatetime') ==0){
                        $meeting->setProperty('endDatetime', $postContent->value);
                        $meeting->save();
                        $array = $meeting->getProperties();
                        $array['meetingID']=$meeting->getId();
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'location') ==0){
                        $meeting->setProperty('location', $postContent->value);
                        $meeting->save();
                        $array = $meeting->getProperties();
                        $array['meetingID']=$meeting->getId();
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'description') ==0){
                        $meeting->setProperty('description', $postContent->value);
                        $meeting->save();
                        $array = $meeting->getProperties();
                        $array['meetingID']=$meeting->getId();
                        echo json_encode($array);
                }else if(strcasecmp($postContent->field, 'attendance') ==0){
                        $attendeeArray = $postContent->value;
                        foreach($attendeeArray as $attendee){
                                $key = key(get_object_vars($attendee));
                                $users = $userIndex->query('name:'.$key[0]);
                                $meetingRels = $meeting->getRelationships('ATTEND_MEETING');
                                foreach($meetingRels as $rel){
                                        if ($users[0]->getID() == $rel->getStartNode->getID()){
                                                $rel->setProperty('ATTENDANCE', $attendee->{$key}[0])
                                                        ->save();
                                        }else{
                                                $attendRel = $users[0]->relateTo($meeting)
                                                        ->setProperty('ATTENDANCE', $attendee->{$key}[0])
                                                        ->save();
                                        }
                                }
                        }
                        $array = $meeting->getProperties();
                        $array['meetingID']=$meeting->getId();
                        echo json_encode($array);
                }
        }
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE') == 0){      
	//delete meeting DELETE
        //get the id
        preg_match("#(\d+)#", $_SERVER['REQUEST_URI'], $id);
        
        //get the node
        $node = $client->getNode($id[0]);
        //make sure the node exists
        if($node != NULL){
                //check if node has meeting index
                $meeting = $meetingIndex->findOne('ID', ''.$id[0]);
                                
                //only delete the node if it's a meeting
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
                        $errorarray = array('errorID' => '4', 'errorMessage'=>'Given node ID is not a meeting node');
			 //return an error otherwise
			echo '"errorID":"4", "errorMessage":"Given node ID is not a meeting node"}';
 		}
		echo json_encode($errorarray);
	} else {
      //return an error if ID doesn't point to a node
		echo '{"errorID":"5", "errorMessage":"Given node ID is not recognized in database"}';
		$errorarray = array('errorID' => '5', 'errorMessage'=>'Given node ID is not recognized in databas');
		echo json_encode($errorarray);
	}
}else{
        echo $_SERVER['REQUEST_METHOD'] ." request method not found in Meeting";
}
?>
