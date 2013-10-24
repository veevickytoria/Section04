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
        NSArray *keys = [NSArray arrayWithObjects:@"user", @"pass", nil];
        NSArray *objects = [NSArray arrayWithObjects:email, password,nil];
        
        NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
        NSData *jsonData;
        NSString *jsonString;
        
        if ([NSJSONSerialization isValidJSONObject:jsonDictionary])
        {
            jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary options:0 error:nil];
            jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
        }
        NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/index.php?method=register"];
        
        NSMutableURLRequest * urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
        [urlRequest setHTTPMethod:@"POST"];
        [urlRequest setValue:@"application/json" forHTTPHeaderField:@"Accept"];
        [urlRequest setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
        [urlRequest setValue:[NSString stringWithFormat:@"%d", [jsonData length]] forHTTPHeaderField:@"Content-length"];
        [urlRequest setHTTPBody:jsonData];
        NSURLResponse * response = nil;
        NSError * error = nil;
        NSData * data = [NSURLConnection sendSynchronousRequest:urlRequest
                                              returningResponse:&response
                                                          error:&error];
        
        NSLog(@"request : %@", urlRequest);
        NSLog(@"request headers : %@", [urlRequest allHTTPHeaderFields]);
        NSLog(@"request body : %@", [[NSString alloc] initWithData:[urlRequest HTTPBody] encoding:NSUTF8StringEncoding]);
//        if (error) {
//            // Handle error.
//        }
//        else
//        {
//            NSError *jsonParsingError = nil;
//            NSArray *jsonArray = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers|NSJSONReadingAllowFragments error:&jsonParsingError];
//        }
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

//- (IBAction)onClickMenu:(id)sender
//{
//    [UIView beginAnimations:nil context:NULL];
//    [UIView setAnimationDuration:0.4];
//    
//    CGRect oldFrame = self.menuView.frame;
//    CGRect oldFrameMain = self.scrollView.frame;
//    
//    if (!self.movedView)
//    {
//        self.menuView.frame = CGRectMake(0, oldFrame.origin.y, oldFrame.size.width, oldFrame.size.height);
//        self.scrollView.frame = CGRectMake(oldFrameMain.origin.x+200,oldFrameMain.origin.y,oldFrameMain.size.width,oldFrameMain.size.height);
//    }
//    else
//    {
//        self.menuView.frame = CGRectMake(-200, oldFrame.origin.y, oldFrame.size.width, oldFrame.size.height);
//        self.scrollView.frame = CGRectMake(0,oldFrameMain.origin.y,oldFrameMain.size.width,oldFrameMain.size.height);
//    }
//    self.movedView = !self.movedView;
//    [UIView commitAnimations];
//}

//- (IBAction)onLogOut:(id)sender
//{
//    [self.registerDelegate logOut];
//}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    NSLog(@"%@", data);
}

@end
