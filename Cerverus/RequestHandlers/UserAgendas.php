<?php

namespace Everyman\Neo4j;
require_once 'RequestHandler.php';

class UserAgendas extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "Users", "email");
    }
	protected function nodeToOutput($node) {
        if ($node == NULL) {return false;} //make pretty exception
        $nodeInfo = array_merge(array("agendaID"=>$node->getId()), $node->getProperties());
		$topics=NodeUtility::getNodeRelations($node, 'HAS_TOPIC', 'out');
		$topList=array();
		$i = sizeof($topics);
		foreach($topics as $top){
			$topNode = $top->getEndNode();
			$topInfo = getTopicInfo($topNode->getId(),$this->client);
			$topList[$i--]=$topInfo;
		}
		$returnList = array('content'=>$topList);
        return $nodeInfo+$returnList;
        
    }
	public function GET($id1){
        //GET getUserNotes
        $userNode = NodeUtility::getNodeByID($id1, $this->client);
        if ($userNode == NULL)
            return false;
		$agendaRels = NodeUtility::getNodeRelations($userNode, "createdBy","in");
		$agendaArray = array();
		foreach($agendaRels as $rel){
			$agendaNode = $rel->getStartNode();
			$propertyArray = $this->nodeToOutput($agendaNode);
			array_push($agendaArray, $propertyArray);
		}
		$lastArray = array('agendas'=>$agendaArray);
		return $lastArray;
	}
	public function POST(){
		return "Cannot create or POST on this request. Try GET";
	}
	public function DELETE(){
		return "Cannot delete on this request. Try GET";
	}
	public function PUT(){
		return "Cannot update or PUT on this request. Try GET";
	}
}