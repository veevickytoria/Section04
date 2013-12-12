<?php
/**
 * Include the API PHP file for neo4j
 */
namespace Everyman\Neo4j;
require("phar://neo4jphp.phar");


/**
 *	Create a graphDb connection 
 */
$client = new Client();

	if( strcasecmp( $_GET['method'] , 'getAllUsers' ) == 0 ){
	$queryString = "MATCH (user:User) RETURN user";
	$query = new Cypher\Query\($client, $queryString);
	$results = $query->getResultSet();
	RETURN $results;
	}
?>