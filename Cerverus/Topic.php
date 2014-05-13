<?php
/**
 * Include the API PHP file for neo4j
 */
namespace Everyman\Neo4j;
require_once "NodeUtility.php";
//require("phar://neo4jphp.phar");

//Make each if-else here a function. Then require Topic.php in Agenda and call the functions from there.


/**
 *	Create a graphDb connection 
 */
$client= new Client();

	//get the index
	$topicIndex = new Index\NodeIndex($client, 'agendas');
	$topicIndex->save();

function createTopic($title, $time, $description, $content, $client, $index){
	//createTopic method
	//params: title, time, subtopics
	
	//create the node
	$topicNode = $client->makeNode();
	
	//sets the properties on the node
	$topicNode->setProperty('title', $title)
		->setProperty('time', $time)
		->setProperty('description', $description)
		->setProperty('nodeType', "Topic");
		
	$topicNode->save();
	
	//relate to parent
//	$parentNode->relateTo($topicNode, "HAS_TOPIC");
	
	$topicList = array();
	
	//calls to create more topics to create the subtopics
	if (isset($content) and !empty($content)) {
		foreach($content as $topic){
		
			/*
			echo(sizeof($topic));
			echo(json_encode($topic));
			*/
			
			/*
			if (empty($topic[0])){
				echo("empty");
				continue;
			} else {
				echo("not empty");
				echo(sizeof($topic[0]));
			}
			*/
			
			if (isset($topic->title))
				$subTitle = $topic->title;
			else
				$subTitle = "NO_TITLE";
			if (isset($topic->time))
				$subTime = $topic->time;
			else
				$subTime = "NO_TIME";
			if (isset($topic->description))
				$subDescription = $topic->description;
			else
				$subDescription = "NO_DESCRIPTION";
			if (isset($topic->content))
				$subcontent = $topic->content;
			else
				$subcontent = array();
			$subTopicCreation = createTopic($subTitle, $subTime, $subDescription, $subcontent, $client, $index);
			//make a relation to the topic 'HAS_TOPIC'
			$topicNode->relateTo($subTopicCreation[0], "HAS_TOPIC")
				->save();
			array_push($topicList, $subTopicCreation[1]);
		}
	}
	//get properties on the node
	//$topicProps= $topicNode->getProperties();
	$output = array();
	$output["topicID"] = $topicNode->getId();
	$output["title"] = $title;
	$output["time"] = $time;
	$output["description"] = $description;
	$output["content"] = $topicList;
	
	$return = array();
	$return[0] = $topicNode;
	$return[1] = $output;
	
	//$response= $index->add($topicNode, 'user', $topicProps['user']);
	return $return;
}

function deleteTopicOLD($id){
	//deleteTopic
	//parmas: id
        
    //get the node
    $node = $client->getNode($id);
    //make sure the node exists
    if($node != NULL){
             //check if node has topic index
             $topic = $topicIndex->findOne('ID', ''.$id);
                              
             //only delete the node if it's a topic
    		if($topic != NULL){
                //get the relationships
                $relations2 = $topic->getRelationships(array('HAS_TOPIC'));
            	foreach($relations2 as $rel){
                   	$toDelID = $rel->getEndNode()->getID();
                   	deleteTopic($toDelID);
                   	$rel->delete();
                }
                $relations = $topic->getRelationships();
                foreach($relations as $rel){
                    //remove all relationships
                    $rel->delete();
                }                
                        
                //delete node and return true
                $topic->delete();
                $array = array('valid'=>'true');
 				echo json_encode($array);
 				return 'true';
            } else {
                //return an error otherwise
                $errorarray = array('errorID' => '11', 'errorMessage'=>$id.' node ID is not a topic node');
                return 4;
 			}
	echo json_encode($errorarray);
	} else {
     	//return an error if ID doesn't point to a node
		$errorarray = array('errorID' => '5', 'errorMessage'=>$id.' node ID is not recognized in database');
		echo json_encode($errorarray);
		return 5;
	}
}

function getTopicInfo($id, $client){
	if($id!=NULL){
		$topicNode = NodeUtility::getNodeByID($id, $client);
		$result = array();
		$result["title"] = $topicNode->getProperty("title");
		$result["time"] = $topicNode->getProperty("time");
		$result["description"] = $topicNode->getProperty("description");
		
		//get subtopics
		$topics=NodeUtility::getNodeRelations($topicNode, 'HAS_TOPIC', 'out');
		$subtopics=array();
		foreach($topics as $top){
			$topNode = $top->getEndNode();
			array_push($subtopics,$topNode->getID());
		}
		$topicList = array();
		$i = sizeof($subtopics);
		foreach($subtopics as $subtopic){
			$topicList[$i--] = getTopicInfo($subtopic, $client);
		}
		$result["content"] = $topicList;
		
		return $result;
	}
	return array();
}

function deleteTopic($topic, $client){
	//ensure node is a topic
	if ($topic->getProperty('nodeType') != 'Topic'){
		echo json_encode(array('errorID'=>'??', 'errorMessage'=>$topic->getId().' is not a topic node(delete topic).'));		
		return 1;
	}

	//get subtopics
	$rels = NodeUtility::getNodeRelations($topic, "HAS_TOPIC", "in");
	$subTopics = NodeUtility::getNodesFromRelations($rels, "OUT");
	//delete relations
	$relations = $topic->getRelationships();
	foreach($relations as $rel){
		$rel->delete();
	}
	
	//delete subtopics
	if($subTopics!=NULL){
		foreach ($subTopics as $subTopic){
			deleteTopic($subTopic, $client);
		}
	}
	
	//delete topic
	$topic->delete();
}
?>
