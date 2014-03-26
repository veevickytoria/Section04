<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of NoteHandler
 *
 * @author millerns
 */
class NoteHandler extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "Note", "ID");
    }
    
    protected function nodeToOutput($node) {
        if ($node == NULL) {return false;} //make pretty exception
        $nodeInfo = array();
        $nodeInfo['title'] = $node['title'];
        $nodeInfo['description'] = $node['description'];
        $nodeInfo['dateCreated'] = $node['dateCreated'];
        $nodeInfo['content'] = $node['content'];
        $nodeInfo['noteID'] = $node->getId();
        return $nodeInfo;
    }

    protected function setNodeProperties($node, $postList) {
        $node->setProperty('title', $postList->title)
                ->setProperty('description', $postList->description)
                ->setProperty('dateCreated', $postList->dateCreated)
                ->setProperty('content', $postList->content);
        //relate the Note to the user who created it        
    }

    protected function setNodeRelationships($node, $postList, $client) {        
        $creatorNode = NodeUtility::getNodeByID($postList['createdBy'], $client);                
        $creatorNode->relateTo($node, 'CREATED')->save();
    }

    public function PUT($putList) {
        $id = $putList["id"];
        $field = $putList["field"];
        $value = $putList["value"];
        $node = NodeUtility::getNodeByID($id);
        
        if (NodeUtility::isValidField($node, $field)){
            if (($field == "title") || ($field == "description") || ($field == 
                    "dateCreated") || ($field == "content")){
                $node->setProperty($field, $value);
                NodeUtility::storeNodeInDatabase($node);        
                return nodeToOutput($node);   
            } else if ($field == "created") {
                $relationArray = $node->getRelationships(array('CREATED'), Relationship::DirectionIn);
                foreach($relationArray as $rel) {
                    $rel->delete();
		}
                $creator = NodeUtility::getNodeByID($value, $this->client);
                $creator->relateTo($node, "CREATED")->save();
                return nodeToOutput($node);
            } else {
                return false;
            }
        }
    }
}
