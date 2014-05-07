<?php

namespace Everyman\Neo4j;

/**
 * This class contains many functions which are useful when dealing with nodes.
 */
class NodeUtility {

    /**
     * Returns a node from the given client with the specified ID.
     * 
     * @param int $id ID of Node to retrieve
     * @param client $client Client to retrieve node from
     * @return Node The specified node or null if node was not found
     */
    public static function getNodeByID($id, $client) {
        $node = $client->getNode($id);
        if ($node == NULL) {
            /* TODO update error handling */
            echo json_encode(array('errorID' => 'XX', 'errorMessage' => $id . ' is an unrecognized node ID in the database'));
            return NULL;
        } else {
            return $node;
        }
    }

    /**
     * Saves a node in the database.
     * 
     * @param Node $node node to store in the database
     * @return boolean returns true if successful, otherwise false
     */
    public static function storeNodeInDatabase($node) {
        if ($node == NULL) {
            return false;
        }
        $node->save();
        return true;
    }

    /**
     * Removes a node from the database. First deletes all relationships with
     * the node, then deletes the node itself.
     * 
     * @param Node $node Node to delete
     * @return boolean True if successful, otherwise false
     */
    public static function deleteNodeFromDatabase($node) {
        if ($node == NULL) {
            return false;
        }
        NodeUtility::deleteAllNodeRelations($node);
        $node->delete();
        return true;
    }

    /**
     * Returns all relations on a given node.
     * 
     * @param Node $node Node 
     * @return boolean|array Array of relations, or false if node is null
     */
    public static function getAllNodeRelations($node) {
        if ($node == NULL) {
            return false;
        }
        $relations = array();
        foreach ($node->getRelationships() as $rel) {
            array_push($relations, $rel);
        }
        return $relations;
    }

    /**
     * Returns all relations of a specifed type and direction on a node
     * 
     * @param Node $node Node to get relations from
     * @param String $relationName Name of relation to retrieve
     * @param String $direction Direction of relation to retrieve
     * @return array Array of the specified relation
     */
    public static function getNodeRelations($node, $relationName, $direction) {
        /*
          $relationArray = array();
          if ($direction == "IN") {
          $relationArray = $node->getRelationships(array(), Relationship::DirectionIn);
          } else if ($direction == "OUT") {
          $relationArray = $node->getRelationships(array(), Relationship::DirectionOut);
          } else {return false;}
         */
        $relationArray = $node->getRelationships($relationName, $direction);

        $specifiedRelation = array();
        foreach ($relationArray as $rel) {
            if ($relationName == $rel->getType()) {
                array_push($specifiedRelation, $rel);
                //$specifiedRelation->add($rel);
            }
        }

        return $specifiedRelation;
    }

    /**
     * Retrieve the nodes on the specified end of a list of relations.
     * "DirectionIn" -> get the starting node
     * "DirectionOut" -> get the end node
     * @param Relation[] $relations Relations to retrieve nodes from
     * @param String $direction Direction to get nodes from
     * @return boolean|array List of nodes, or false if direction is invalid
     */
    public static function getNodesFromRelations($relations, $direction) {
        $nodeToRetrieve = null;
        if ($direction == "DirectionIn") {
            $nodeToRetrieve = getStartNode();
        } else if ($direction == "DirectionOut") {
            $nodeToRetrieve = getEndNode();
        } else {
            return false;
        }

        $relatedNodes = array();
        foreach ($relations as $rel) {
            $relatedNodes->add($rel->nodeToRetrieve);
        }
        return $relatedNodes;
    }

    /**
     * Delete all relations on a node.
     * 
     * @param Node $node Node to delete relations from.
     */
    public static function deleteAllNodeRelations($node) {
        $relationArray = $node->getRelationships();
        foreach ($relationArray as $rel) {
            $rel->delete();
        }
    }

    /**
     * Delete the specified relations on a node.
     * 
     * @param Node $node Node to delete relations from.
     * @param String $relationshipTypes Name of relation to remove.
     * @param String $direction Direction of relation to remove.
     */
    public static function deleteSpecificNodeRelations($node, $relationshipTypes, $direction) {
        $relationArray = $node->getRelationships($relationshipTypes, $direction);
        foreach ($relationArray as $rel) {
            $rel->delete();
        }
    }

    /**
     * Check if a node is the proper type.
     * 
     * @param String $nodeTypeActual The actual type of the node.
     * @param String $nodeTypeShouldBe The expected type of the node.
     * @return Boolean True if the types are the same, otherwise false
     */
    public static function nodeTypeCheck($nodeTypeActual, $nodeTypeShouldBe) {
        return ($nodeTypeActual == $nodeTypeShouldBe);
    }

    /**
     * Sets the specified property from a list on a node.
     * 
     * @param Node $node Node to set property of.
     * @param String $nodeProperty Name of property on the node.
     * @param String $listProperty Name of property in the array.
     * @param Array $list List to get value from.
     */
    public static function setPropertyFromList($node, $nodeProperty, $listProperty, $list) {
        $node->setProperty($nodeProperty, $list->$listProperty);
    }

    /**
     * Checks if a given node exists in a given index.
     * 
     * @param Node $node Node to check the index for
     * @param Index $index Index to check for the node
     * @param String $indexKey Key used by the index; default value is ID
     * @return Boolean True if node exists in the index, otherwise false
     */
    public static function checkInIndex($node, $index, $indexKey = "ID") {
        $match = $index->findOne($indexKey, $node->getId());
        return ($match != null);
    }

    /**
     * Updates the specified relation type of a given node. Deletes the old
     * relations of the given type, then adds new relations of the given type
     * with the given values
     * 
     * @param node $node Node to update
     * @param String $relationName  Type of relation to update
     * @param String $direction DirectionIn or DirectionOut
     * @param array $newValues  Array of nodes to be related with the given node
     * @param Client $client client to use
     * @return Boolean  True if successful
     */
    public static function updateRelation($node, $relationName, $direction, $newValues, $client) {
        //delete old relations
        //$relationArray = $node->getRelationships(array('relation'), Relationship::$direction);
        $relationArray = $node->getRelationships($relationName, Relationship::$direction);
        foreach ($relationArray as $rel) {
            $rel->delete();
        }
        //turn new values into an array if they are not already
        if (!is_array($newValues)) {
            $newValues = array($newValues);
        }
        //add new relations
        foreach ($newValues as $newVal) {
            $relatedNode = NodeUtility::getNodeByID($newVal, $client);
            if ($direction == "DirectionIn") {
                $relatedNode->relateTo($node, $relationName)->save();
            } else if ($direction == "DirectionOut") {
                $node->relateTo($relatedNode, $relationName)->save();
            }
        }
        return true;
    }

    /**
     * Updates the specified nested relation type of a given node. Deletes the
     * old nested relations of the given type, then adds new nested relations of
     * the given type with the given values.
     * 
     * @param node $node Node to update
     * @param String $relationName  Type of nested relation to update
     * @param String $direction DirectionIn or DirectionOut
     * @param array $newValues  Array of nodes to be related with the given node
     * @param Client $client client to use
     * @return Boolean  True if successful
     */
    public static function updateNestedRelation($node, $relationName, $direction, $newValues, $client, $individualName) {
        //delete old relations of the specified type
        $relationArray = $node->getRelationships($relationName, Relationship::DirectionOut);
        foreach ($relationArray as $rel) {
            $rel->delete();
        }
        //turn new values into an array if they are not already
        if (!is_array($newValues)) {
            $newValues = array($newValues);
        }
        
        $errorsOccurred = false;
        
        //add new relations
        foreach ($newValues as $relatedPair){
            //check node exists
            $relatedID = $relatedPair->$individualName;
            $relatedNode = NodeUtility::getNodeByID($relatedID, $client);
            if ($relatedNode == null){
                $errorsOccurred = true;
            }            
            if ($direction == "DirectionIn") {
                $relatedNode->relateTo($node, $relationName)->save();
            } else if ($direction == "DirectionOut") {
                $node->relateTo($relatedNode, $relationName)->save();
            }
        }
        return $errorsOccurred;
    }

}
