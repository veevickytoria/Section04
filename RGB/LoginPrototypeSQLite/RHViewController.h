//
//  RHViewController.h
//  LoginPrototypeSQLite
//
//  Created by Richard Shomer on 10/4/13.
//  Copyright (c) 2013 Richard Shomer. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <sqlite3.h>

@interface RHViewController : UIViewController
@property (weak, nonatomic) IBOutlet UITextField *usernameTextField;
@property (weak, nonatomic) IBOutlet UITextField *passwordTextField;
- (IBAction)signInButton:(id)sender;
- (IBAction)registerButton:(id)sender;

@property (strong, nonatomic) NSString *databasePath;
@property (nonatomic) sqlite3 *contactDB;
@property (weak, nonatomic) IBOutlet UILabel *status;

@end
