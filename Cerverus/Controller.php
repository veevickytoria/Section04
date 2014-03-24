<?php
require_once("User.php");
/**
 *  Calls the request and returns the request
 */
class Controller {
      
    public static function parse($class, $id, $type, $postContent){
        //parse normal request
        if(class_exists($class))
             echo $class::$type($id, $postContent);
        else
            echo "Error: file not recognized";
    }
    public static function parseSpecial($class1, $class2, $id1, $id2, $type, $postContent){
        //parse special request
        
    }
}


if (isset($_REQUEST['class2'])) {
    Controller::parseSpecial($_GET['class1'], 
                            $_GET['class2'], 
                            $_GET['id1'], 
                            $_GET['id2'],
                            $_SERVER['REQUEST_METHOD'], 
                            json_decode(file_get_contents('php://input')));
} else {
    Controller::parse($_GET['class1'], 
                                $_GET['id1'], 
                                $_SERVER['REQUEST_METHOD'], 
                                json_decode(file_get_contents('php://input')));
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
