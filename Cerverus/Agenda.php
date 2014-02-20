<?php
/**
 * Include the API PHP file for neo4j
 */
namespace Everyman\Neo4j;
require_once("CommonFunctions.php");
require_once("Topic.php");

/**
 *	Create a graphDb connection 
 */
$client= new Client();

	//get the index
	$agendaIndex = new Index\NodeIndex($client, 'agendas');
	$userIndex = new Index\NodeIndex($client, 'Users');
	$agendaIndex->save();
if(strcasecmp($_SERVER['REQUEST_METHOD'], 'POST')==0){
	/* 
	/ =====================
	/ ===Create Agenda=====
	/ =====================
	*/
	
	//get the json string post content
	$postContent = json_decode(@file_get_contents('php://input'));
	
	//create the node
	$agendaNode = $client->makeNode();
	$agendaNode->setProperty('nodeType','Agenda');
	
	//sets the properties on the node
	$title;
	if (isset($postContent->title))
		$title = $postContent->title;
	else
		$title = "No Title";
	$agendaNode->setProperty('title', $title);
		
	//ensure that if meeting or user are specified, they exist
	if (isset($postContent->meeting)){
		$meeting = $postContent->meeting;
		$meetingNode = getNodeByID($meeting, $client);
		if ($meetingNode == NULL) {
			return;
		} else if (strcmp($meetingNode->getProperty("nodeType"), "Meeting")){
			echo json_encode(array('errorID'=>'??', 'errorMessage'=>$meeting. ' is not a meetingID in the database'));
			return;
		}
	} else {
		$meeting = "none";
	}
	
	if (isset($postContent->user)){
		$user = $postContent->user;
		$userNode = getNodeByID($user, $client);
		if ($userNode == NULL) {
			return;
		} else if (strcmp($userNode->getProperty("nodeType"), "User")){
			echo json_encode(array('errorID'=>'??', 'errorMessage'=>$user. ' is not a userID in the database'));
			return;
		}
	} else {
		$user = "none";
	}
	
	//add the node in the db
	$agendaNode->save();
	
	//topics
	//get the index
	$topicIndex = new Index\NodeIndex($client, 'agendas');
	$topicIndex->save();
	
	$topicList = array();
	
	if ( isset($postContent->content) and !empty($postContent->content) ){
		$topics = $postContent->content;
		foreach($postContent->content as $topic){
			//pass the topic to Topic.php's create Topic method
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
				$content = $topic->content;
			else
				$content = array();
			$topicCreation = createTopic($subTitle, $subTime, $subDescription, $content, $client, $topicIndex);
			//make a relation to the topic 'HAS_TOPIC'
			$topicRel = $agendaNode->relateTo($topicCreation[0], 'HAS_TOPIC')
				->save();
			array_push($topicList, $topicCreation[1]);
		}
	} else {
		$topics = "none";
	}
	
	//relate agenda to meeting
	if ($meeting != "none") {
		$meetingNode = $client->getNode($postContent->meeting);
		$meetingRel = $meetingNode->relateTo($agendaNode, 'FOLLOWS')
			->save();
		if ($meetingRel == null){
			echo json_encode(array('errorID'=>'??', 'errorMessage'=>'Relationship to meeting could not be made'));
			return;
		}
	}

	//relate agenda to user
	if ($user != "none") {
		$userNode = $client->getNode($postContent->user);
		$userRel = $userNode->relateTo($agendaNode, 'CREATED')
					->save();
		if ($userRel == null){
			echo json_encode(array('errorID'=>'??', 'errorMessage'=>'Relationship to user could not be made'));
			return;
		}
	}
				
	//get properties on the node
	$output = array();
	$output["agendaID"] = $agendaNode->getId();
	$output["title"] = $title;
	$output["meetingID"] = $meeting;
	$output["userID"] = $user;
	$output["content"] = $topicList;
	
	//return information about the node
	echo json_encode($output);
	
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'GET')==0){
	/* 
	/ =====================
	/ =====Get Agenda======
	/ =====================
	*/
	
	$agendaNode = getNodeFromRequest($client);
	
	if ($agendaNode == NULL)
		return 1;
	
	if ($agendaNode->getProperty("nodeType") != "Agenda"){
		echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a agenda node.'));
		return 1;
	}
	//get properties
	$output = array();
	$output["agendaID"] = $agendaNode->getId();
	$output["title"] = $agendaNode->getProperty("title");
	
	
	//get relationships
	$meetings = getRelatedNodeIDs($agendaNode, "FOLLOWS", "meetingID", "IN");
	if(!empty($meetings)){
		$output["meetingID"] = $meetings[0]["meetingID"];
	}else{
		$output["meetingID"] = "none";
	}
	$users = getRelatedNodeIDs($agendaNode, "CREATED", "userID", "IN");
	$output["userID"]  = $users[0]["userID"];
	
	//get subtopics
	$topics = getRelatedNodeIDs($agendaNode, "HAS_TOPIC", "topicID", "OUT");
	$topicList = array();
	$i = sizeof($topics);
	foreach($topics as $topic){
		$topicList[$i--] = getTopicInfo($topic["topicID"], $client);
	}
	$output["content"] = $topicList;
	
	echo json_encode($output);

}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'PUT')==0){
	/* 
	/ =====================
	/ =====Edit Agenda=====
	/ =====================
	*/

	$postContent = json_decode(@file_get_contents('php://input'));
	$agendaNode = getNodeByID($postContent->agendaID, $client);
	
	//ensure node is an agenda
	if ($agendaNode->getProperty("nodeType") != "Agenda"){
		echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a agenda node.'));
		return 1;
	}
	
	$title;
	if (isset($postContent->title)){
		$title = $postContent->title;
		$agendaNode->setProperty("title", $title);
	} else {
		//get current title
		$title = $agendNode->getProperty(title);
	}
	
	$relations = $agendaNode->getRelationships();
	
	$meeting;
	if (isset($postContent->meeting)){
		//ensure new meeting exists and is a meeting
		$meeting = $postContent->meeting;
		$meetingNode = getNodeByID($meeting, $client);
		if (strcmp($meetingNode->getProperty("nodeType"), "Meeting")){
			echo json_encode(array('errorID'=>'??', 'errorMessage'=>$meeting. ' is not a meetingID in the database'));
			return;
		}
		//delete old relation
		foreach ($relations as $rel){
			if ($rel->getType() == "FOLLOWS")
				$rel->delete();
		}
		
		//add new relation
		$meetingNode->relateTo($agendaNode, "FOLLOWS")->save();		
	} else {
		//get current meeting
		foreach ($relations as $rel){
			if ($rel->getType() == "FOLLOWS")
				$meeting = $rel->getStartNode()->getId();
		}
	}
	
	$user;
	if (isset($postContent->user)){
		//ensure new user exists and is a user
		$user = $postContent->user;
		$userNode = getNodeByID($user, $client);
		if (strcmp($userNode->getProperty("nodeType"), "User")){
			echo json_encode(array('errorID'=>'??', 'errorMessage'=>$postContent->user. ' is not a userID in the database'));
			return;
		}
		//delete old relation
		foreach ($relations as $rel){
			if ($rel->getType() == "CREATED")
				$rel->delete();
		}
		
		//add new relation
		$userNode->relateTo($agendaNode, "CREATED")->save();		
	} else {
		//get current user
		foreach ($relations as $rel){
			if ($rel->getType() == "CREATED")
				$user = $rel->getStartNode()->getId();
		}
	}
	
	$topicList = array();
	if (isset($postContent->content)){
		//delete topics
		$topics = getRelatedNodes($agendaNode, "HAS_TOPIC", "OUT");
		foreach($topics as $topic){
			deleteTopic($topic, $client);
		}
		//add new content
		foreach($postContent->content as $topic){
			//pass the topic to Topic.php's create Topic method
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
				$content = $topic->content;
			else
				$content = array();
			$topicCreation = createTopic($subTitle, $subTime, $subDescription, $content, $client, $topicIndex);
			//make a relation to the topic 'HAS_TOPIC'
			$topicRel = $agendaNode->relateTo($topicCreation[0], 'HAS_TOPIC')
				->save();
			array_push($topicList, $topicCreation[1]);
		}
	} else {
		//get current subtopics
		$topics = getRelatedNodeIDs($agendaNode, "HAS_TOPIC", "topicID", "OUT");
		$topicList = array();
		$i = sizeof($topics);
		foreach($topics as $topic){
			$topicList[$i--] = getTopicInfo($topic["topicID"], $client);
		}
	}
	
	$agendaNode->save();
	
	//get properties on the node
	$output = array();
	$output["agendaID"] = $agendaNode->getId();
	$output["title"] = $title;
	$output["meetingID"] = $meeting;
	$output["userID"] = $user;
	$output["content"] = $topicList;
	
	//return information about the node
	echo json_encode($output);
	
}else if(strcasecmp($_SERVER['REQUEST_METHOD'], 'DELETE')==0){
	/* 
	/ =====================
	/ ====Delete Agenda====
	/ =====================
	*/
	
	//get node
	$agendaNode = getNodeFromRequest($client);
	
	if ($agendaNode == NULL)
		return 1;
	
	//ensure node is an agenda
	if ($agendaNode->getProperty("nodeType") != "Agenda"){
		echo json_encode(array('errorID'=>'11', 'errorMessage'=>$_GET['id'].' is an not a agenda node.'));
		return 1;
	}
	
	//get related topics
	$topics = getRelatedNodes($agendaNode, "HAS_TOPIC", "OUT");
	
	//delete relations
	$relations = $agendaNode->getRelationships();
	foreach($relations as $rel){
		$rel->delete();
	}
	
	//delete topics
	foreach($topics as $topic){
		deleteTopic($topic, $client);
	}
	
	//delete the node
	$agendaNode->delete();
	
	//return result
	echo(json_encode(array('valid'=>'true')));
	
}else{
	echo $_SERVER['REQUEST_METHOD'] ." request method not found";
}
?>
