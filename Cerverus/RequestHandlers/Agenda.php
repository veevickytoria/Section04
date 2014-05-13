<?php

namespace Everyman\Neo4j;
require_once "RequestHandler.php";
require_once "Topic.php";
/**
 * This class handles Note requests. It specifies the properties and relations
 * of Notes.
 *
 * @author millerns
 */
class Agenda extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "Agenda", "ID");
        
        $this->idName = "agendaID";
        
        array_push($this->propertyList, "title");
		$this->relationList["meeting"] = "meeting";
        $this->relationList["createdBy"] = "createdBy";
    }
	public function POST($postList){
        $node = $this->client->makeNode();
        // make sure methods pass $node by reference
        $this->setNodeProperties($node, $postList);
		NodeUtility::storeNodeInDatabase($node);
		
		
		$topicList = array();
		if ( isset($postList['content']) and !empty($postList['content']) ){
			foreach($postList['content'] as $topic){
				//$keys = array_keys($topic);
				foreach($topic as $key){
				//pass the topic to Topic.php's create Topic method
				if (isset($key->title))
					$subTitle = $key->title;
				else
					$subTitle = "NO_TITLE";
				if (isset($key->time)){
					echo $key->time;
					$subTime = $key->time;}
				else
					$subTime = "NO_TIME";
				if (isset($key->description))
					$subDescription = $key->description;
				else
					$subDescription = "NO_DESCRIPTION";
				if (isset($key->content))
					$content = $key->content;
				else
					$content = array();
				$topicCreation = createTopic($subTitle, $subTime, $subDescription, $content, $this->client, $this->index);
				//make a relation to the topic 'HAS_TOPIC'
				$topicRel = $node->relateTo($topicCreation[0], 'HAS_TOPIC')
					->save();
				array_push($topicList, $topicCreation[1]);
				
				}

			}
		} else {
		$topicList = array();
		}
		$returnTopic = array('content'=>$topicList);
        $this->setNodeRelationships($node, $postList);
        $this->index->add($node, 'ID', $node->getId());
		return $this->nodeToOutput($node)+$returnTopic;
	}
	
	public function GET($id){
	    $node = NodeUtility::getNodeByID($id, $this->client);
        if ($node == NULL) {
            return false;
        } if (!NodeUtility::checkInIndex($node, $this->index, $this->indexKey)) {
            return false;
        } //TODO Use fancier error message
		$topics=NodeUtility::getNodeRelations($node, 'HAS_TOPIC', 'out');
		$topList=array();
		$i = sizeof($topics);
		foreach($topics as $top){
			$topNode = $top->getEndNode();
			$topInfo = getTopicInfo($topNode->getId(),$this->client);
			$topList[$i--]=$topInfo;
		}
		$returnList = array('content'=>$topList);
        return $this->nodeToOutput($node)+$returnList;
	}
	
	public function PUT($putList){
	    $id = $putList[$this->idName];
        $field = $putList["field"];
        $value = $putList["value"];
        $node = NodeUtility::getNodeByID($id, $this->client);

        if (in_array($field, $this->propertyList)) {
            $node->setProperty($field, $value);
            NodeUtility::storeNodeInDatabase($node);
        } else if (in_array($field, $this->relationList)) {
            NodeUtility::updateRelation($node, $field, "DirectionOut", $value, $this->client);
        } else if ($field == 'content'){
			echo 'sadness';
		}
		else{
            echo ("notInArrays");
            return false;
        }
        return $this->nodeToOutput($node);
	}
	public function DELETE($id){
	    $node = NodeUtility::getNodeByID($id, $this->client);
        if (!NodeUtility::checkInIndex($node, $this->index, $this->indexKey)) {
            return false;
        } //!!Use fancier error message  
		$topics=NodeUtility::getNodeRelations($node, 'HAS_TOPIC', 'out');
		$topList=array();
		foreach($topics as $top){
			$topNode = $top->getEndNode();
			deleteTopic($topNode,$this->client);
		}
		
        $this->index->remove($node);
        //NodeUtility::deleteNodeFromDatabase(NodeUtility::getNodeByID($id));
        NodeUtility::deleteNodeFromDatabase($node);
        return true; //make fancy delete message
	}
}
