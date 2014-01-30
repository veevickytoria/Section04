<?php
/**
 * Include the API PHP file for neo4j
 */
namespace Everyman\Neo4j;
require("phar://neo4jphp.phar");
require_once(Topic.php);

/**
 *	Create a graphDb connection 
 */
$client= new Client();

	//get the index
	$agendaIndex = new Index\NodeIndex($client, 'agendas');
	$agendaIndex->save();
if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0){
	//createAgenda method
	
	//get the json string post content
	$postContent = json_decode(@file_get_contents('php://input'));
	
	//create the node
	$agendaNode= $client->makeNode();
	
	//sets the property on the node
	$agendaNode->setProperty('title', $postContent->title)
		->setProperty('meeting', $postContent->meeting)
		->setProperty('noteType','Agenda');
	//actually add the node in the db
	$agendaNode->save();
	
	foreach($postContent->content as $topic){
		//pass the topic to Topic.php's create Topic method
		$creTopNode = createTopic($topic->title, $topic->time, $topic->subtopic);
		$topicNode = $client->getNode($creTopNode);
		//make a relation to the topic 'HAS_TOPIC'
		$topicRel = $agendaNode->relateTo($topicNode, 'HAS_TOPIC')
			->save();
	}
	
	//relate agenda to meeting
	$meeting = $client->getNode($postContent->meeting);
    $meetingRel = $meeting->relateTo($agenda, 'FOLLOWS')
                ->save();
                
	//get properties on the node
	$agendaProps= $agendaNode->getProperties();
	
	$response= $agendaIndex->add($agendaNode, 'user', $agendaProps['user']);
	echo json_encode($response);
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0){
	//getAgendaInfo
	$agendaNode=$client->getNode($_GET['id']);
	$array = $agendaNode->getProperties();
	if(array_key_exists('nodeType', $array)){
		if(strcasecmp($array['nodeType'], 'Agenda')!=0){
			echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a agenda node.'));
			return 1;
		}
	} 
	$result = $agendaNode->getProperties();
	
	$relations = $topic->getRelationships(array('HAS_TOPIC'));
    foreach ($relations as $rel){
    	$info = getTopicInfo($rel->getEndNode()->getID());
        $result['subtopic'] = $info; //so I got rid of the => error, but I need to check if this actually works
    } //find out how to get this code form github to the server so you can test this properly
	echo json_encode($result);
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'PUT')==0){
	//updateAgenda
	$postContent = json_decode(@file_get_contents('php://input'));
	
	$agenda=$client->getNode($postContent->agendaID);
	if(sizeof($note) >0){
		$array = $agenda->getProperties();
		if(array_key_exists('nodeType', $array)){
			if(strcasecmp($array['nodeType'], 'Agenda')!=0){
				echo json_encode(array('errorID'=>'11', 'errorMessage'=>$postContent->agendaID.' is an not a agenda node.'));
				return 1;
			}
		} 
		if(strcasecmp($postContent->field, 'title') ==0){
			$agenda->setProperty('title', $postContent->value);
			$agenda->save();
			$array = $agenda->getProperties();
			echo json_encode($array);
		}else if(strcasecmp($postContent->field, 'meeting') ==0){
			$agenda->setProperty('meeting', $postContent->value);
			$meeting = $client->getNode($postContent->value);
			$relations = $agenda->getRelationships(array('FOLLOWS'));
			$relations[0]->setEndNode($meeting)
				->save();
			$agenda->save();
			$array = $agenda->getProperties();
			echo json_encode($array);
		}else if(strcasecmp($postContent->field, 'content') ==0){
			//delete all the topics and replace them with the new topics in content. TODO
			
			$relations2 = $agenda->getRelationships(array('HAS_TOPIC'));
            foreach($relations2 as $rel){
                //remove the relation and delete the topic it's associated with
                //delete Topic
                $ret = deleteTopic($rel->getEndNode()->getID());
            	$rel->delete();
            }
			
			foreach($postContent->value as $topic){
				//pass the topic to Topic.php's create Topic method
				$ret = createTopic($topic->title, $topic->time, $topic->subtopic);
				$topicNode = $client->getNode($ret);
				//make a relation to the topic 'HAS_TOPIC'
				$topicRel = $agendaNode->relateTo($topicNode, 'HAS_TOPIC')
					->save();
			}
			$array = $agenda->getProperties();
			echo json_encode($array);
		}else{
			echo "No node updated.";
		}
	
	}else{
		echo "FALSE node not found";
	}
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE')==0){
	//deleteAgenda
	//get the id
        preg_match("#(\d+)#", $_SERVER['REQUEST_URI'], $id);
        
        //get the node
        $node = $client->getNode($id[0]);
        //make sure the node exists
        if($node != NULL){
        	$array = $node->getProperties();
		if(array_key_exists('nodeType', $array)){
			if(strcasecmp($array['nodeType'], 'Agenda')!=0){
				echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a agenda node.'));
				return 1;
			}
		} 
                //check if node has agenda index
                $agenda = $agendaIndex->findOne('ID', ''.$id[0]);
                                
                //only delete the node if it's an agenda
                if($agenda != NULL){
                		//get the relationships which are 'HAS_TOPIC'
                		$relations2 = $agenda->getRelationships(array('HAS_TOPIC'));
                		foreach($relations2 as $rel){
                			//remove the relation and delete the topic it's associated with
                			//delete Topic
                			$ret = deleteTopic($rel->getEndNode()->getID());
                			$rel->delete();
                		}
                        //get the relationships
                        $relations = $agenda->getRelationships();
                        foreach($relations as $rel){
                                //remove all relationships
                                $rel->delete();
                        }                
                        
                        //delete node and return true
                        $agenda->delete();
                  	    $array = array('valid'=>'true');
 						echo json_encode($array);
                } else {
                        //return an error otherwise
                        $errorarray = array('errorID' => '4', 'errorMessage'=>'Given node ID is not an agenda node');
 				}
		echo json_encode($errorarray);
		} else {
     	//return an error if ID doesn't point to a node
		echo '{"errorID":"5", "errorMessage":"Given node ID is not recognized in database"}';
		$errorarray = array('errorID' => '5', 'errorMessage'=>'Given node ID is not recognized in database');
		echo json_encode($errorarray);
	}
}else{
	echo $_SERVER['REQUEST_METHOD'] ." request method not found";
}
?>
