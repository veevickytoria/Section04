//
//  RHViewController.m
//  LoginPrototypeSQLite
//
//  Created by Richard Shomer on 10/4/13.
//  Copyright (c) 2013 Richard Shomer. All rights reserved.
//

#import "RHViewController.h"

@interface RHViewController ()

@end

@implementation RHViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    NSString *docsDir;
    NSArray *dirPaths;
    self.passwordTextField.secureTextEntry = YES;
    // Get the documents directory
    dirPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    
    docsDir = dirPaths[0];
    
    // Build the path to the database file
    _databasePath = [[NSString alloc]
                     initWithString: [docsDir stringByAppendingPathComponent:
                                      @"users.db"]];
    
    NSFileManager *filemgr = [NSFileManager defaultManager];
    
    if ([filemgr fileExistsAtPath: _databasePath ] == NO)
    {
        const char *dbpath = [_databasePath UTF8String];
        
        if (sqlite3_open(dbpath, &_contactDB) == SQLITE_OK)
        {
            char *errMsg;
            const char *sql_stmt =
            "CREATE TABLE IF NOT EXISTS USERS (ID INTEGER PRIMARY KEY AUTOINCREMENT, USERNAME TEXT, PASSWORD TEXT)";
            
            if (sqlite3_exec(_contactDB, sql_stmt, NULL, NULL, &errMsg) != SQLITE_OK)
            {
                _status.text = @"Failed to create table";
            } else {
                NSLog(@"Table created");
            }
            sqlite3_close(_contactDB);
            NSLog(@"DB created/opened");
        } else {
            _status.text = @"Failed to open/create database";
        }
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)signInButton:(id)sender {
    const char *dbpath = [_databasePath UTF8String];
    sqlite3_stmt    *statement;
    
    if (sqlite3_open(dbpath, &_contactDB) == SQLITE_OK)
    {
        NSString *querySQL = [NSString stringWithFormat:
                              @"SELECT username, password FROM users WHERE username=\"%@\" AND password=\"%@\"",
                              _usernameTextField.text, _passwordTextField.text];
        
        const char *query_stmt = [querySQL UTF8String];
        
        if (sqlite3_prepare_v2(_contactDB,
                               query_stmt, -1, &statement, NULL) == SQLITE_OK)
        {
            if (sqlite3_step(statement) == SQLITE_ROW)
            {
                _status.text = @"Successfully Logged in.";
            } else {
                _status.text = @"Username/Password is incorrect";
            }
            sqlite3_finalize(statement);
        }
        sqlite3_close(_contactDB);
    }
    
}

- (IBAction)registerButton:(id)sender {
    sqlite3_stmt    *statement;
    const char *dbpath = [_databasePath UTF8String];
    
    if (sqlite3_open(dbpath, &_contactDB) == SQLITE_OK)
    {
        
        NSString *insertSQL = [NSString stringWithFormat:
                               @"INSERT INTO USERS (username, password) VALUES (\"%@\", \"%@\")",
                               self.usernameTextField.text,
                               self.passwordTextField.text];
        
        const char *insert_stmt = [insertSQL UTF8String];
        int prep = sqlite3_prepare_v2(_contactDB, insert_stmt, -1, &statement, NULL);
        if  (prep != SQLITE_OK){
            NSLog(@"Error w/ prepare: %i", prep);
        }
        int step = sqlite3_step(statement);
        if (step == SQLITE_DONE)
        {
            self.status.text = @"Registered Successfully";
            self.usernameTextField.text = @"";
            self.passwordTextField.text = @"";
        } else {
            NSLog(@"%i", step);
            self.status.text = @"Error w/ step";
        }
        sqlite3_finalize(statement);
        sqlite3_close(_contactDB);
    }
}
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self removeKeyboard];
}

- (void)removeKeyboard
{
    [self.view endEditing:YES];
    
}

@end
