<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace Everyman\Neo4j;

class NodeUtility{
    
    public static function getNodeByID($id, $client){
        $node = $client->getNode($id);
	if ($node == NULL)  {
            /* Should we handle errors elsewhere? */
            echo json_encode(array('errorID'=>'12', 'errorMessage'=>$id . ' is an unrecognized node ID in the database'));
            return NULL;
	} else {
            return $node;
	}
    }
    
    public static function storeNodeInDatabase($node){
        if ($node == NULL) {return false;}
        $node->save();
    }
    
    public static function deleteNodeFromDatabase($node){
        if ($node == NULL) {return false;}
        NodeUtility::deleteAllNodeRelations($node); 
        $node->delete();
        return true;
    }
    
    public static function getAllNodeRelations($node){
        if ($node == NULL) {return false;}
        $relations = array();
        foreach($node->getRelations() as $rel){
            $relations->add($rel);
        }
        return $relations;        
    }
    
    public static function getNodeRelations($node, $relationName, $direction){
        /*
        $relationArray = array();
        if ($direction == "IN") {
            $relationArray = $node->getRelationships(array(), Relationship::DirectionIn);
        } else if ($direction == "OUT") {
            $relationArray = $node->getRelationships(array(), Relationship::DirectionOut);
        } else {return false;}
        */
        
        $relationArray = $node->getRelationships(array(), $direction);
        $specifiedRelation = array();
        
        foreach ($relationArray as $rel){
            if ($relationName == $rel->getType()) {
                array_push($specifiedRelation, $rel);
                //$specifiedRelation->add($rel);
            }
        }
        
        return $specifiedRelation;
   }
    
    public static function getNodesFromRelations($relations, $direction){
        $nodeToRetrieve;
        if ($direction == "DirectionIn") {
            $nodeToRetrieve = getStartNode();
        } else if ($direction == "DirectionOut"){
            $nodeToRetrieve = getEndNode();
        } else {return false;}
        
        $relatedNodes = array();
        foreach ($relations as $rel){
            $relatedNodes->add($rel->nodeToRetrieve);
        }
        return $relatedNodes;
    }
    
    public static function deleteAllNodeRelations($node){
        $relationArray = $node->getRelationships();
	foreach($relationArray as $rel) {
			$rel->delete();
	}
    }
    public static function deleteSpecificNodeRelations($node, $relationshipTypes, $direction){
        $relationArray = $node->getRelationships($relationshipTypes, $direction);
	foreach($relationArray as $rel) {
			$rel->delete();
	}
    }
    
    public static function nodeTypeCheck($node, $nodeTypeShouldBe){
        return ($node == $nodeTypeShouldBe);
    }
    
    public static function setPropertyFromList($node, $nodeProperty, $listProperty, $list){
        $node->setProperty($nodeProperty, $list->$listProperty);
    }
    
    public static function checkInIndex($node, $index, $indexKey="ID", $checkValue=null){
        if($checkValue == null){ $checkValue = $node->getId(); }
        $match= $index->findOne($indexKey, $checkValue);
        return ($match != null);
    }
    
    /**
     * Updates the specified relation type of a given node. Deletes the old
     * relations of the given type, then adds new relations of the given type
     * with the given values
     * 
     * @param node $node    Node to update
     * @param String $relation  Type of relation to update
     * @param String $direction DirectionIn or DirectionOut
     * @param array $newValues  Array of nodes to be related with the given node
     * @return Boolean  True if successful
     */
    public static function updateRelation($node, $relationName, $direction, $newValues, $client){
        //delete old relations
        $relationArray = $node->getRelationships(array('relation'), Relationship::$direction);
        foreach($relationArray as $rel) {
            $rel->delete();
        }        
        //turn new values into an array if it is not already
        if (!is_array($newValues)){
            $newValues = array($newValues);
        }
        //add new relations
        foreach($newValues as $newVal){
          $relatedNode = NodeUtility::getNodeByID($newVal, $client);
          if ($direction == "DirectionIn"){              
              $relatedNode->relateTo($node, $relationName)->save();
          } else if ($direction == "DirectionOut"){
              $node->relateTo($relatedNode, $relationName)->save();
          }
        }
        return true;
    }
}