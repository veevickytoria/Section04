//
//  iWinRegisterViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/2/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinRegisterViewController.h"

@interface iWinRegisterViewController ()

@end

@implementation iWinRegisterViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)onClickRegister:(id)sender
{
    
    NSString *name = [[self.nameField text] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    NSString *email = [[self.emailField text] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    NSString *password = [self.passwordField text];
    NSString *confirmPassword = [self.confirmPasswordField text];
    if ([password isEqualToString:confirmPassword] && name.length > 0 && email.length > 0 && password.length>0 && confirmPassword.length>0)
    {
        //register
        NSString *url = [NSString stringWithFormat:@"http://localhost:8888/db_api.php?action=write&table=User&name=%@&password=%@&email=%@", name, password, email];
        url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:url]];
        NSURLResponse * response = nil;
        NSError * error = nil;
        NSData * data = [NSURLConnection sendSynchronousRequest:urlRequest
                                              returningResponse:&response
                                                          error:&error];
        [self.registerDelegate onRegister:email];
    }
    else
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Enter valid values" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }

}

- (IBAction)onClickCancel:(id)sender
{
    [self.registerDelegate onCancel];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    NSLog(@"%@", data);
}

@end
