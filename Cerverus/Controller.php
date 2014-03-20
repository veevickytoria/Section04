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

