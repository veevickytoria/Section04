<?php
    /** 
     * Examples of creating classes, subclasses, methods, etc.
     */

    // Use these methods to ensure that the classes you reference from different
    // files are are loaded. Simply list the namespaces in the first method.
    spl_autoload_extensions(".aNamespace, anotherNamespace");
    spl_autoload_register();

    class BasicClass
    {
        // private, public, protected modifiers for properties and functions
        public $visibleValue = "Hi there";
        private $myValue;

        //constant values are declared using 'const' and use no '$'
        //they are public and cannot be changed
        //reference constants using "self::myPi" or "BasicClass::myPi"
        const myPi = 3.14159;
        
        //a constructor method is not necessary
        //to create a constructor, create a function called "__construct" (two underscores)
        //using the name of the class as a constructor is a deprecated practice in php
        function __construct($bar){
            //when accessing a property, do not use "$" before the name of the property
            $this->myValue = $bar;
        }
        
        // functions do not specify a return type
        // functions do not specify the type of their paramaters
        //returns are optional for all functions
        protected function setMyValue($value){
            $this->myValue = $value;
        }
        
        //use "$this" just "like" this in Java
        //call a function on an object using "->" (instead of a dot)
        public function makeMyValuePi(){
            $this->setMyValue(BasicClass::myPi);
            echo ($this->myValue . "\n");
            return $this->myValue;
        }
        
        
    }

    // extend a class using the "extends" keyword
    class SubClass extends BasicClass
    {
        
        //to override a parent function, just redeclare it
        public function makeMyValuePi(){
            $this->setMyValue(BasicClass::myPi);
            echo ("utter nonsense\n");
        }
        
    }
    
    
    abstract class basicAbstract
    {
        
        
    }
    
    // implement an interface with "implements"
    interface basicInterface
    {
        
    }
    
    class Main
    {
        public static function go(){
            $myClass = new BasicClass(22);
            $myClass->makeMyValuePi();
            
            $mySubClass = new SubClass(18);
            $mySubClass->makeMyValuePi();
        }

}
   //call a static function using the name of the class and "::"
    Main::go();
?>