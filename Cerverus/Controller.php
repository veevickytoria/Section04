<?php

namespace Everyman\Neo4j;
require "phar://neo4jphp.phar";

require_once "RequestHandlers\Contact.php";
require_once "RequestHandlers\Note.php";
require_once "RequestHandlers\Meeting.php";
require_once "RequestHandlers\Group.php";
require_once "RequestHandlers\Project.php";
require_once "RequestHandlers\User.php";
require_once "RequestHandlers\Task.php";
require_once "RequestHandlers\UserLogin.php";
require_once "RequestHandlers\UserMeetings.php";
require_once "RequestHandlers\UserUsers.php";
require_once "RequestHandlers\UserGroups.php";


/**
 *  Directs a request to the proper class and returns the result
 */
class Controller {
    
    /**
     * Parse a regular GET/POST/PUT/DELETE request
     * @param type $class
     * @param type $id
     * @param type $type
     * @param type $postContent
     * @return type
     */
    public static function parseStandard($class, $id, $type, $postContent){
    
        $aClient = new Client();
	$c = '\\Everyman\\Neo4j\\'.$class;
        $handler= new $c($aClient);
        
        if ($type == "GET" || $type == "DELETE") {
             if ($id == NULL){
                 echo json_encode("ERROR: ID cannot be null!");
                 return;
             }
             $requestResult =  $handler->$type($id);
        } else {
            $requestResult =  $handler->$type((array)$postContent);
        }
        if (!$requestResult) {echo "ERROR: Something went wrong";} else {
            echo json_encode($requestResult);
        }
         
    }
    
    /**
     * Parse a non-standard request
     * @param type $class1
     * @param type $class2
     * @param type $id1
     * @param type $id2
     * @param type $type
     * @param type $postContent
     */
    public static function parseSpecial($class1, $class2, $id1, $id2, $type, $postContent){
        //parse special request
        $aClient = new Client();
        
        $c = '\\Everyman\\Neo4j\\'.$class1.$class2;
        $handler = new $c($aClient);
        
         if ($type == "GET" || $type == "DELETE") {
             $requestResult =  $handler->$type($id1, $id2);
        } else {
            $requestResult =  $handler->$type((array)$postContent);
        }
        if (!$requestResult) {echo "REQUEST RESULT WAS FALSE";} else {
            echo json_encode($requestResult);
        }
        
    }        
    /*
    *This function initializes the headers needed for the application
    */
    public static function initHeaders(){
            define("PBKDF2_HASH_ALGORITHM", "sha256");
            define("PBKDF2_ITERATIONS", 1000);
            define("PBKDF2_SALT_BYTE_SIZE", 24);
            define("PBKDF2_HASH_BYTE_SIZE", 24);

            define("HASH_SECTIONS", 4);
            define("HASH_ALGORITHM_INDEX", 0);
            define("HASH_ITERATION_INDEX", 1);
            define("HASH_SALT_INDEX", 2);
            define("HASH_PBKDF2_INDEX", 3);

                    if (isset($_SERVER['HTTP_ORIGIN'])) {
                            header("Access-Control-Allow-Origin: *");
                            header('Access-Control-Allow-Credentials: true');
                            header('Access-Control-Max-Age: 86400');    // cache for 1 day
                    }

                    // Access-Control headers are received during OPTIONS requests
                    if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
                            if (isset($_SERVER['HTTP_ACCESS_CONTROL_REQUEST_METHOD']))
                                    header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");         

                            if (isset($_SERVER['HTTP_ACCESS_CONTROL_REQUEST_HEADERS']))
                                    header("Access-Control-Allow-Headers: GET, POST, PUT, DELETE, OPTIONS");
                            exit(0);
                    }
    }
}


Controller::initHeaders();

if (isset($_REQUEST['class2'])) {
    Controller::parseSpecial($_GET['class1'],//.'Handler', 
                            $_GET['class2'], 
                            $_GET['id1'], 
                            $_GET['id2'],
                            $_SERVER['REQUEST_METHOD'], 
                            json_decode(file_get_contents('php://input')));
} else {
    Controller::parseStandard($_GET['class1'],//.'Handler', 
                    (isset($_GET['id1']) ? $_GET['id1'] : null), 
                    $_SERVER['REQUEST_METHOD'], 
                    json_decode(file_get_contents('php://input')));
}

