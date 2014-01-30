<?php
/**
 * Include the API PHP file for neo4j
 */
namespace Everyman\Neo4j;
require("phar://neo4jphp.phar");

//Make each if-else here a function. Then require Topic.php in Agenda and call the functions from there.


/**
 *	Create a graphDb connection 
 */
$client= new Client();

	//get the index
	$topicIndex = new Index\NodeIndex($client, 'agendas');
	$topicIndex->save();

function createTopic($title, $time, $subtopic){
	//createTopic method
	//paramas: title, time, subtopics
	
	/*get the json string post content
	$postContent = json_decode(@file_get_contents('php://input'));*/
	
	//create the node
	$topicNode= $client->makeNode();
	
	//sets the property on the node
	$topicNode->setProperty('title', $title)
		->setProperty('time', $time);
		//and calls to create more topics to create the subtopics
		
	$topicNode->save();
	
	foreach($subtopic as $topic){
		//pass the topic to Topic.php's create Topic method
		$subtopicID = createTopic($topic->title, $topic->time, $topic->subtopic);
		$node = $client->getNode($subtopicID);
		//make a relation to the topic 'HAS_TOPIC'
		$topicRel = $agendaNode->relateTo($node, 'HAS_TOPIC') // actually should get node associated with $topNode
			->save();
	}
	
	//get properties on the node
	$topicProps= $topicNode->getProperties();
	
	$response= $topicIndex->add($topicNode, 'user', $topicProps['user']);
	echo $response;
	return $topicNode->getID();
}

function deleteTopic($id){
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
                        {
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
                        $errorarray = array('errorID' => '4', 'errorMessage'=>'Given node ID is not a topic node');
                        return 4;
 				}
		echo json_encode($errorarray);
		} else {
     	//return an error if ID doesn't point to a node
		echo '{"errorID":"5", "errorMessage":"Given node ID is not recognized in database"}';
		$errorarray = array('errorID' => '5', 'errorMessage'=>'Given node ID is not recognized in database');
		echo json_encode($errorarray);
		return 5;
	}
}

function getTopicInfo($id){
	//getTopicInfo
	//params: id
	$topicNode=$client->getNode($id);
	$result = array();
	foreach ($topicNode->getProperties() as $key => $value) {
		$result[] = $key => $value;
	}
    $relations = $topic->getRelationships(array('HAS_TOPIC'));
    foreach ($relations as $rel){
    	$toGetID = $rel->getEndNode()->getID();
    	$ret = getTopicInfo($toGetID);
        $result[] = 'subtopic' => $ret;
    }
	echo json_encode($result);
	return $result
}
?>