<?php 

class IndexService {

	var $_neo_db;
	
	/**
	 * JSON HTTP client
	 *
	 * @var JSONClient
	 */
	protected $jsonClient;
	
	public function __construct( GraphDatabaseService $neo_db, $jsonClient=null)
	{
		$this->_neo_db = $neo_db;
		
		if (!is_null($jsonClient)) {
		    $this->jsonClient = $jsonClient;
		} else {
		    $this->jsonClient = new JsonClient;
		}
	}
	
	public function index($key, $value ) {
		$uri = $this->_neo_db->getBaseUri() . "db/data/index/node/";
		$data= array($key=>$value);

		list($response, $http_code) = $this->jsonClient->jsonPostRequest($uri, $data);
		if ($http_code!=201) throw new Exception($http_code);
	}
	public function addNode(Node $node, $indexName, $key, $value){
		$data= array("value"=>$value, "uri"=> $node->getUri(), "key"=>$key);
		echo "------". print_r($data);
		list($response, $http_code) = $this->jsonClient->jsonPostRequest( $this->_neo_db->getBaseUri() ."db/data/index/node/". $indexName, $data);
		if ($http_code!=201) throw new Exception($http_code);
	}
	
	public function removeIndex(Node $node, $key, $value)
	{
		$uri = $this->_neo_db->getBaseUri().'index/node/'.$key.'/'.$value.'/'.$node->getId();
		list($response, $http_code) = $this->jsonClient->deleteRequest($uri);
		if ($http_code!=204) throw new HttpException($http_code);
	}

	public function getNodes($indexName, $key, $value ) {
		
		$uri = $this->_neo_db->getBaseUri().'db/data/index/node/'. $indexName. "/".$key ."/".$value;
		//echo "</br>".$uri. "</br>";
		list($response, $http_code) = $this->jsonClient->jsonGetRequest($uri);
		if ($http_code!=200) throw new HttpException("http code: " . $http_code . ", response: " . print_r($response, true));
		$nodes = array();
		foreach($response as $nodeData) {
			$nodes[] = Node::inflateFromResponse( $this->_neo_db, $nodeData );
		}
		return $nodes;
		
	}
	
	public function getNode($key, $value) {
		$nodes = $this->getNodes($key, $value);
		return $nodes[0];
	}
	
}
