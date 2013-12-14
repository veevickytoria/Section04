//
//  iWinLoginViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/2/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinLoginViewController.h"
#import <QuartzCore/QuartzCore.h>

@interface iWinLoginViewController ()
@property (strong, nonatomic) NSString *loggedIn;
@end

@implementation iWinLoginViewController

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
    self.loginButton.layer.cornerRadius = 7;
    self.loginButton.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    self.loginButton.layer.borderWidth = 1.0f;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)onClickLogin:(id)sender
{
    
    NSString *email = [[self.userNameField text] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
//    NSString *password = [self.passwordField text];
    
    [self.loginDelegate login:email];
    
//    if (password.length > 0 && email.length>0)
//    {
//        NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/index.php?method=login&user=%@", email];
//        url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
//        NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url] cachePolicy:NSURLRequestReloadIgnoringLocalAndRemoteCacheData timeoutInterval:30];
//        [urlRequest setHTTPMethod:@"GET"];
//        NSURLResponse * response = nil;
//        NSError * error = nil;
//        NSData * data = [NSURLConnection sendSynchronousRequest:urlRequest
//                                              returningResponse:&response
//                                                          error:&error];
//        self.loggedIn = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
//        //check login
//        if (([self.loggedIn rangeOfString:@"TRUE"].location != NSNotFound) || ([self.loggedIn rangeOfString:@"true"].location != NSNotFound))
//        {
//            [self.loginDelegate login:email];
//        }
//        else
//        {
//            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Username/Password not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
//            [alert show];
//        }
//    }
//    else
//    {
//        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Enter valid values" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
//        [alert show];
//    }
    
}

- (IBAction)onClickJoinUs:(id)sender
{
    [self.loginDelegate joinUs];
}



@end
