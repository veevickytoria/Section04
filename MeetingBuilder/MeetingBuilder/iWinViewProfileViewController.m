//
//  iWinViewProfileViewController.m
//  MeetingBuilder
//
//  Created by Richard Shomer on 11/6/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinViewProfileViewController.h"
#import "iWinViewAndCreateGroupViewController.h"
#import "iWinProjectListViewController.h"
#import "iWinViewAndCreateProjectViewController.h"
#import "iWinBackEndUtility.h"
#import "iWinAppDelegate.h"
#import "Contact.h"
#import <QuartzCore/QuartzCore.h>

@interface iWinViewProfileViewController ()
@property (nonatomic) BOOL isEditing;
@property (nonatomic) Contact *contact;
@property (nonatomic) NSInteger userID;
@property (strong, nonatomic) iWinViewAndCreateGroupViewController *createGroupVC;
@property (strong, nonatomic) iWinProjectListViewController *viewProjectsVC;
@property (nonatomic) iWinBackEndUtility *backEndUtility;
@end

@implementation iWinViewProfileViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withID:(NSInteger)userID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.userID = userID;
    }
    [self viewDidLoad];
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.backEndUtility = [[iWinBackEndUtility alloc] init];
    self.isEditing = NO;
    self.cancel.hidden = YES;
    [self updateTextUI];
    [self withBorders:NO];
}
-(void)updateTextUI
{
    NSString *url = [NSString stringWithFormat: @"http://csse371-04.csse.rose-hulman.edu/User/%d", self.userID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];

    NSDictionary *userInfo = [self.backEndUtility getRequestForUrl:url];
   
    
    if (!userInfo)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Meetings not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        if (userInfo.count > 0)
        {
            self.displayNameTextField.text = (NSString *) [userInfo objectForKey:@"name"];
            self.emailTextField.text = (NSString *) [userInfo objectForKey:@"email"];
            self.phoneTextField.text = (NSString *) [userInfo objectForKey:@"phone"];
            self.companyTextField.text = (NSString *) [userInfo objectForKey:@"company"];
            self.titleTextField.text = (NSString *) [userInfo objectForKey:@"title"];
            self.locationTextField.text = (NSString *) [userInfo objectForKey:@"location"];
        }
    }
    
}

-(void) saveChanges
{
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/User/"];
    NSArray *fields = [NSArray arrayWithObjects:@"name", @"email", @"phone", @"company", @"title", @"location",nil];
    NSArray *values = [NSArray arrayWithObjects:self.displayNameTextField.text, self.emailTextField.text, self.phoneTextField.text, self.companyTextField.text, self.titleTextField.text, self.locationTextField.text,nil];
    NSArray *keys = [NSArray arrayWithObjects:@"userID", @"field", @"value", nil];

    for (int i = 0; i < fields.count; i++) {
    
        NSArray *objects = [NSArray arrayWithObjects:[NSNumber numberWithInt:self.userID], fields[i], values[i], nil];

        NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
        [self.backEndUtility putRequestForUrl:url withDictionary:jsonDictionary];
    }
}

-(IBAction)onCancel:(id)sender
{
    [self userInteraction:NO];
    [self withBorders:NO];
    [self updateTextUI];
    [self.editProfile setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
    self.isEditing = NO;
    [self.editProfile setTitle:@"Edit Profile" forState:UIControlStateNormal];
    self.cancel.hidden = YES;
}

- (IBAction)onCreateGroup:(id)sender {
    self.createGroupVC = [[iWinViewAndCreateGroupViewController alloc] initWithNibName:@"iWinViewAndCreateGroupViewController" bundle:nil withUserID:self.userID withGroupID:-1];
    
    [self.createGroupVC setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.createGroupVC setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.createGroupVC animated:YES completion:nil];
    self.createGroupVC.view.superview.bounds = CGRectMake(0,0,768,1003);
}

- (IBAction)onViewProjects:(id)sender {
    self.viewProjectsVC = [[iWinProjectListViewController alloc] initWithNibName:@"iWinProjectListViewController" bundle:nil withUserID:self.userID];
    
    [self.viewProjectsVC setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.viewProjectsVC setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.viewProjectsVC animated:YES completion:nil];
    self.viewProjectsVC.view.superview.bounds = CGRectMake(0,0,768,1003);
}

-(IBAction)onEditProfile:(id)sender
{
    if (self.isEditing) {
        
        [self saveChanges];
        
        self.isEditing = NO;
        [self userInteraction:NO];
        [self withBorders:NO];
        [self.editProfile setTitle:@"Edit Profile" forState:UIControlStateNormal];
        [self.editProfile setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        self.cancel.hidden = YES;
        
    } else{
        
        self.isEditing = YES;
        [self userInteraction:YES];
        [self withBorders:YES];
        [self.editProfile setTitle:@"Save" forState:UIControlStateNormal];
        [self.editProfile setTitleColor:[UIColor greenColor] forState:UIControlStateNormal];
        self.cancel.hidden = NO;
        
    }
}

- (void) userInteraction: (BOOL) enable
{
    if (enable) {
        
        self.displayNameTextField.userInteractionEnabled = YES;
        self.companyTextField.userInteractionEnabled = YES;
        self.titleTextField.userInteractionEnabled = YES;
        self.emailTextField.userInteractionEnabled = YES;
        self.phoneTextField.userInteractionEnabled = YES;
        self.locationTextField.userInteractionEnabled = YES;
        
    } else {
        
        self.displayNameTextField.userInteractionEnabled = NO;
        self.companyTextField.userInteractionEnabled = NO;
        self.titleTextField.userInteractionEnabled = NO;
        self.emailTextField.userInteractionEnabled = NO;
        self.phoneTextField.userInteractionEnabled = NO;
        self.locationTextField.userInteractionEnabled = NO;
        
    }
}

- (void) withBorders: (BOOL) enable
{
    if (enable) {
        
        [self.displayNameTextField setBorderStyle:UITextBorderStyleRoundedRect];
        [self.companyTextField setBorderStyle:UITextBorderStyleRoundedRect];
        [self.titleTextField setBorderStyle:UITextBorderStyleRoundedRect];
        [self.emailTextField setBorderStyle:UITextBorderStyleRoundedRect];
        [self.phoneTextField setBorderStyle:UITextBorderStyleRoundedRect];
        [self.locationTextField setBorderStyle:UITextBorderStyleRoundedRect];
        
    } else {
        
        [self.displayNameTextField setBorderStyle:UITextBorderStyleNone];
        [self.companyTextField setBorderStyle:UITextBorderStyleNone];
        [self.titleTextField setBorderStyle:UITextBorderStyleNone];
        [self.emailTextField setBorderStyle:UITextBorderStyleNone];
        [self.phoneTextField setBorderStyle:UITextBorderStyleNone];
        [self.locationTextField setBorderStyle:UITextBorderStyleNone];
        
    }
}



@end
