<?php
/**
 * Include the API PHP file for neo4j
 */
namespace Everyman\Neo4j;
require("phar://neo4jphp.phar");


/**
 *	Create a graphDb connection 
 */
$client= new Client();

	//get the index
	$topicIndex = new Index\NodeIndex($client, 'agendas');
	$topicIndex->save();
if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0){
	//createTopic method
	//title, time, subtopics
	
	//get the json string post content
	$postContent = json_decode(@file_get_contents('php://input'));
	
	//create the node
	$topicNode= $client->makeNode();
	
	//sets the property on the node
	$topicNode->setProperty('title', $postContent->title)
		->setProperty('time', $postContent->time);
		//and calls to create more topics to create the subtopics
		
	$topicNode->save();
	
	foreach($postContent->subtopic as $topic){
		//pass the topic to Topic.php's create Topic method
		$request = new HttpRequest('http://csse371-04.csse.rose-hulman.edu/Topic/', HttpRequest:METH_POST);
		$request->addPostFields(array('title' => $topic->title, 'time' => $topic->time, 'suptopic' => $topic->subtopic));
		$result = $request->send();
		$topicNode = $client->getNode($result);
		//make a relation to the topic 'HAS_TOPIC'
		$topicRel = $agendaNode->relateTo($topicNode, 'HAS_TOPIC')
			->save();
	}
	
	//get properties on the node
	$topicProps= $topicNode->getProperties();
	
	$response= $topicIndex->add($topicNode, 'user', $topicProps['user']);
	echo $topicNode->getID();
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE')==0){
	//deleteTopic
	//get the id
        preg_match("#(\d+)#", $_SERVER['REQUEST_URI'], $id);
        
        //get the node
        $node = $client->getNode($id[0]);
        //make sure the node exists
        if($node != NULL){
                //check if node has topic index
                $topic = $topicIndex->findOne('ID', ''.$id[0]);
                                
                //only delete the node if it's a topic
                if($topic != NULL){
                        //get the relationships
                        $relations2 = $topic->getRelationships(array('HAS_TOPIC'));
                        foreach($relations2 as $rel){
                        	$id = $rel->getEndNode()->getID();
                        	$request = new HttpRequest('http://csse371-04.csse.rose-hulman.edu/Topic/'.$id, HttpRequest:METH_DELETE);
                			$result = $request->send();
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
                } else {
                        //return an error otherwise
                        $errorarray = array('errorID' => '4', 'errorMessage'=>'Given node ID is not a topic node');
 				}
		echo json_encode($errorarray);
		} else {
     	//return an error if ID doesn't point to a node
		echo '{"errorID":"5", "errorMessage":"Given node ID is not recognized in database"}';
		$errorarray = array('errorID' => '5', 'errorMessage'=>'Given node ID is not recognized in database');
		echo json_encode($errorarray);
	}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0){
	//getTopicInfo
	$topicNode=$client->getNode($_GET['id']);
	$result = array();
	foreach ($topicNode->getProperties() as $key => $value) {
		$result[] = $key => $value;
	}
    $relations = $topic->getRelationships(array('HAS_TOPIC'));
    foreach ($relations as $rel){
    	$request = new HttpRequest('http://csse371-04.csse.rose-hulman.edu/Topic/'.$id, HttpRequest:METH_GET);
        $return = $request->send();
        $result[] = 'subtopic' => json_decode($return);
    }
	echo json_encode($result);
}else{
        echo $_SERVER['REQUEST_METHOD'] ." request method not found in Topic";
}
?>