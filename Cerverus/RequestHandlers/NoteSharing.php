<?php

namespace Everyman\Neo4j;
require_once "RequestHandler.php";

/**
 * This class handles Note requests. It specifies the properties and relations
 * of Notes.
 *
 * @author millerns
 */
class NoteSharing extends RequestHandler {
    
    function __construct($client){
        parent::__construct($client, "Note", "ID");
        
        $this->idName = "noteID";
        
        array_push($this->propertyList, "title");
        array_push($this->propertyList, "description");
        array_push($this->propertyList, "dateCreated");
        array_push($this->propertyList, "content");
        
        $this->relationList["createdBy"] = "createdBy";
        
        $this->relationName = "sharedNote";
    }
    
    public function GET($id) {
        //find note
        $matchedNote = $this->index->findOne($this->indexKey, $id);
        if ($matchedNote == null){
            return "Note with given ID does not exist";
        }
        //get users
        $relations = NodeUtility::getNodeRelations($matchedNote, $this->relationName, Relationship::DirectionOut);
        $relatedUsers = NodeUtility::getNodesFromRelations($relations, "DirectionOut");
        //echo json_encode($relatedUsers);
        $usersArray = array();
        foreach ($relatedUsers as $relatedUser){
            $userArray = array();
            $userArray["userID"] = $relatedUser->getId();
            array_push($usersArray, $userArray);
        }
        
        $outputArray = array();
        $outputArray["users"] = $usersArray;
        return $outputArray;
        
    }
    
    public function POST($postContents){
        //find note
        $noteID = $postContents[$this->idName];
        $matchedNote = $this->index->findOne($this->indexKey, $noteID);
        if ($matchedNote == null){
            return "Note with given ID does not exist";
        }
        //relate to users
        $users = $postContents["users"];
        foreach ($users as $user){
            $relatedNode = NodeUtility::getNodeByID($user->userID, $this->client);
            $matchedNote->relateTo($relatedNode, $this->relationName)->save();
        }
        
        return "success";        
    }
    
    public function PUT($postContents){
        return "ERROR: PUT is not allowed for this URL";
    }
    
    public function DELETE($noteID, $userID){
        //find note
        $matchedNote = $this->index->findOne($this->indexKey, $noteID);
        if ($matchedNote == null){
            return "Note with given ID does not exist";
        }
        
        //get relatedUsers
        $relations = NodeUtility::getNodeRelations($matchedNote, $this->relationName, Relationship::DirectionOut);
        $relatedUsers = NodeUtility::getNodesFromRelations($relations, "DirectionOut");
        
        //delete specified user
        foreach ($relations as $relation){
            $userNode = $relation->getEndNode();
            $relatedUserID = $userNode->getId();
            if ($relatedUserID == $userID){
                //delete this relation
                $relation->delete();
                return "success";
            }
        }
        return "User not related to note";
    }


}
