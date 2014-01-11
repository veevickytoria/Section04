//
//  iWinViewAndChangeSettingsViewController.m
//  MeetingBuilder
//
//  Created by Richard Shomer on 11/4/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinViewAndChangeSettingsViewController.h"
#import "iWinAppDelegate.h"
#import "Contact.h"
#import <QuartzCore/QuartzCore.h>

@interface iWinViewAndChangeSettingsViewController ()
@property (nonatomic) BOOL isEditing;
@property (nonatomic) Contact *contact;
@property (nonatomic) NSInteger userID;@end

@implementation iWinViewAndChangeSettingsViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withID:(NSInteger) userID;
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.userID = userID;
        [self viewDidLoad];
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

-(IBAction)onCancel:(id)sender
{
    //PULL FROM DB
    
    
//    self.displayNameTextField.userInteractionEnabled = NO;
//    self.companyTextField.userInteractionEnabled = NO;
//    self.titleTextField.userInteractionEnabled = NO;
//    self.emailTextField.userInteractionEnabled = NO;
//    self.phoneTextField.userInteractionEnabled = NO;
//    self.locationTextField.userInteractionEnabled = NO;
//    
//    [self updateTextUI];
    
    //    [self unUpdateTextFieldUI:self.displayNameTextField];
    //    [self unUpdateTextFieldUI:self.companyTextField];
    //    [self unUpdateTextFieldUI:self.titleTextField];
    //    [self unUpdateTextFieldUI:self.emailTextField];
    //    [self unUpdateTextFieldUI:self.phoneTextField];
    //    [self unUpdateTextFieldUI:self.locationTextField];
    
    
    //    self.displayNameTextField.text =  [NSString stringWithFormat:@"%@ %@", self.contact.firstName, self.contact.lastName];
    //    self.emailTextField.text =  self.contact.email;
    //    self.phoneTextField.text = self.contact.phone;
    //    self.companyTextField.text = self.contact.company;
    //    self.titleTextField.text = self.contact.title;
    //    self.locationTextField.text = self.contact.location;
    
//    [self.editProfile setTintColor:[UIColor blueColor]];
//    self.isEditing = NO;
//    [self.editProfile setTitle:@"Edit Profile" forState:UIControlStateNormal];
//    self.cancel.hidden = YES;
//    
}

-(IBAction)onEditProfile:(id)sender
{
//    if (self.isEditing) {
//        [self saveChanges];
//        self.displayNameTextField.userInteractionEnabled = NO;
//        self.companyTextField.userInteractionEnabled = NO;
//        self.titleTextField.userInteractionEnabled = NO;
//        self.emailTextField.userInteractionEnabled = NO;
//        self.phoneTextField.userInteractionEnabled = NO;
//        self.locationTextField.userInteractionEnabled = NO;
//        
//        [self unUpdateTextFieldUI:self.displayNameTextField];
//        [self unUpdateTextFieldUI:self.companyTextField];
//        [self unUpdateTextFieldUI:self.titleTextField];
//        [self unUpdateTextFieldUI:self.emailTextField];
//        [self unUpdateTextFieldUI:self.phoneTextField];
//        [self unUpdateTextFieldUI:self.locationTextField];
//        
//        
//        [self.editProfile setTitle:@"Edit Profile" forState:UIControlStateNormal];
//        [self.editProfile setTintColor:[UIColor blueColor]];
//        [self saveChanges];
//        self.cancel.hidden = YES;
//        self.isEditing = NO;
//        
//    } else{
//        self.isEditing = YES;
//        self.displayNameTextField.userInteractionEnabled = YES;
//        self.companyTextField.userInteractionEnabled = YES;
//        self.titleTextField.userInteractionEnabled = YES;
//        self.emailTextField.userInteractionEnabled = YES;
//        self.phoneTextField.userInteractionEnabled = YES;
//        self.locationTextField.userInteractionEnabled = YES;
//        
//        [self updateTextFieldUI:self.displayNameTextField];
//        [self updateTextFieldUI:self.companyTextField];
//        [self updateTextFieldUI:self.titleTextField];
//        [self updateTextFieldUI:self.emailTextField];
//        [self updateTextFieldUI:self.phoneTextField];
//        [self updateTextFieldUI:self.locationTextField];
//        
//        [self.editProfile setTitle:@"Save" forState:UIControlStateNormal];
//        [self.editProfile setTintColor:[UIColor greenColor]];
//        self.cancel.hidden = NO;
//        
//    }
}

-(void)saveChanges
{
    //blah
}

@end
