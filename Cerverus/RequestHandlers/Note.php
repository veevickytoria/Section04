<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace Everyman\Neo4j;
require_once "RequestHandler.php";

/**
 * Description of NoteHandler
 *
 * @author millerns
 */
class Note extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "Note", "ID");
    }
    
    protected function nodeToOutput($node) {
        if ($node == NULL) {return false;} //make pretty exception
        $nodeInfo = array();
        $nodeInfo['title'] = $node->getProperty('title');
        $nodeInfo['description'] = $node->getProperty('description');
        $nodeInfo['dateCreated'] = $node->getProperty('dateCreated');
        $nodeInfo['content'] = $node->getProperty('content');
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

    protected function setNodeRelationships($node, $postList) {        
        $creatorNode = NodeUtility::getNodeByID($postList['createdBy'], $this->client);                
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
                NodeUtility::updateRelation($node, "created", "DirectionIn", $value, $this->client);
                return nodeToOutput($node);
            } else {
                return false;
            }
        }
    }
}
