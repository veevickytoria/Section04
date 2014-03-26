<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of MeetingHandler
 *
 * @author harrissa
 */
class MeetingHandler extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "Meeting", "ID");
    }
    
    protected function nodeToOutput($node) {
        if ($node == NULL) {return false;} //make pretty exception
        $nodeInfo = array();
        $nodeInfo['userID'] = $node['userID'];
        $nodeInfo['title'] = $node['title'];
        $nodeInfo['datetime'] = $node['datetime'];
        $nodeInfo['endDatetime'] = $node['endDatetime'];
		$nodeInfo['description'] = $node['description'];
        $nodeInfo['locaiton'] = $node['location'];
        $nodeInfo['nodeType'] = $node['nodeType'];
        $nodeInfo['noteID'] = $node->getId();
        return $nodeInfo;
    }

    protected function setNodeProperties($node, $postList) {
        $node->setProperty('userID', $postContent->userID)
                ->setProperty('title', $postContent->title)
                ->setProperty('datetime', $postContent->datetime)
                ->setProperty('endDatetime', $postContent->endDatetime)
                ->setProperty('description',$postContent->description)
                ->setProperty('location', $postContent->location)
				->setProperty('nodeType','Meeting');
        //relate the Note to the user who created it        
    }

    protected function setNodeRelationships($node, $postList, $client) {        
		$user = $client->getNode($postContent->userID);
		
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
    }

    public function PUT($putList) {
        $id = $putList["id"];
        $field = $putList["field"];
        $value = $putList["value"];
        $node = NodeUtility::getNodeByID($id);
        
        if (NodeUtility::isValidField($node, $field)){
            if (($field == "title") || ($field == "description") || ($field == 
                    "dateTime") || ($field == "location") || ($field == "endDatetime")){
                $node->setProperty($field, $value);
                NodeUtility::storeNodeInDatabase($node);        
                return nodeToOutput($node);   
            } else if ($field == "created") {
                NodeUtility::updateRelation($node, "MADE_MEETING", "DirectionIn", $value, $this->client);
                return nodeToOutput($node);
            } else {
                return false;
            }
        }
    }
}
