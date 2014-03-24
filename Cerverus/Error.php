<?php

public class Error{
	public function __construct(){
		
	}
		
	function getErrorMessage($errorCode){
		return "Error: undefined error";
	}
	
	function getErrorJson($errorCode){
		echo json_encode(array("errorID"=>$errorCode, getErrorMessage($errorCode)));
	}
}

public class LoginError extends Error{
	function getErrorMessage($errorCode){
		switch ($errorCode){
			case 1:
				$errorMessage="Invalid email or password";
			default:
				$errorMessage="Error: undefined error";
		}
		return "LoginError: ". $errorMessage;
	}
}

?>