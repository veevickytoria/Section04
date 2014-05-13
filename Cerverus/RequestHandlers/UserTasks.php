<?php

namespace Everyman\Neo4j;
require_once 'RequestHandler.php';

class UserTasks extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "Users", "email");
    }
    
    public function GET($id){
        $user = NodeUtility::getNodeByID($id, $this->client);
        if ($user == NULL) { return false; }
        $taskRels = $user->getRelationships(array('assignedTo', 'assignedFrom', 'createdBy'), Relationship::DirectionIn);
        $response = array();
        $visitedTasks = array();
        foreach($taskRels as $rel){
            $task = $rel->getStartNode();
            if(!in_array($task->getId(), $visitedTasks)){
                array_push($response, array('id'=>$task->getId(), 'title'=>$task->getProperty('title')));
                array_push($visitedTasks, $task->getId());
            }
        }
        return array("tasks"=>$response);
    }    
    
    protected function nodeToOutput($node) {
       //do nothing
    }
    
    function PUT($putList) {
        //do nothing
    }
    
    function DELETE($id) {
        //do nothing
    }
    function POST($postList) {
        //do nothing
    }
}