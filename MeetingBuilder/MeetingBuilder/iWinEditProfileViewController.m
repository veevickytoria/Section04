//
//  iWinEditProfileViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 12/13/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinEditProfileViewController.h"
#import "iWinViewProfileViewController.h"
#import <QuartzCore/QuartzCore.h>

@interface iWinEditProfileViewController ()

//@property (strong, nonatomic) iWinViewProfileViewController* viewProfileVC;

//@property (strong, nonatomic) NSString *name;
@property (strong, nonatomic) NSString *company;
@property (strong, nonatomic) NSString *email;
@property (strong, nonatomic) NSString *phone;
@property (strong, nonatomic) NSString *position;
@property (strong, nonatomic) NSString *moreAboutMe;
@property (strong, nonatomic) UIImage *profilePic;

@end

@implementation iWinEditProfileViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil /*iWinViewProfileViewController: iWinViewProfileViewController*/
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        /*self.viewProfileVC = iWinViewProfileViewController;*/
        
//        self.company = self.viewProfileVC.company;
//        self.email = self.viewProfileVC.email;
//        self.phone = self.viewProfileVC.phone;
//        self.position = self.viewProfileVC.position;
//        self.moreAboutMe = self.viewProfileVC.moreAboutMe;
//        self.profilePic = self.viewProfileVC.profilePic;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    [self updateButtonUI:self.save];
    [self updateButtonUI:self.cancel];
    [self updateButtonUI:self.changePicture];
    
    //[self updateLabelUI:self.startDateLabel];
    
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) updateButtonUI: (UIButton *)button
{
    button.layer.cornerRadius = 7;
    button.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    button.layer.borderWidth = 1.0f;
}

-(void) updateLabelUI:(UILabel *)label
{
    label.layer.cornerRadius = 7;
    label.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    label.layer.borderWidth = 1.0f;
}

-(IBAction)onChangePicture:(id)sender
{
    
}

-(IBAction)onSave:(id)sender
{
//    self.company = self.companyField.text;
//    self.email = self.emailField.text;
//    self.phone = self.phoneField.text;
//    self.position = self.positionField.text;
//    self.moreAboutMe = self.moreAboutMeField.text;
//    self.profilePic = self.profilePicView.image;
//    
//    self.viewProfileVC.company = self.company;
//    self.viewProfileVC.email = self.email;
//    self.viewProfileVC.phone = self.phone;
//    self.viewProfileVC.position = self.position;
//    self.viewProfileVC.moreAboutMe = self.moreAboutMe;
//    self.viewProfileVC.profilePic = self.profilePic;
    
    
    
    //Trying to have an alert message pop up saying asking user if they are sure they want to cancel
    
    //UIAlertView *alert = [[UIAlertView alloc] initWithTitle: @"Save Successful"
    //                                                message: @"Your changes were successfully saved."
    //                                               delegate: nil];
    //[alert show];
    //[alert release];}
    [self dismissViewControllerAnimated:YES completion:nil];

}
-(IBAction)onCancel:(id)sender
{
    //Trying to have an alert message pop up saying asking user if they are sure they want to cancel
    
    //UIAlertView *alert = [[UIAlertView alloc] initWithTitle: @"Cancel?"
    //                                                message: @"Are you sure you want to cancel? All changes made will be lost"
    //                                               delegate: nil
    //                                      cancelButtonTitle: @"No"
    //                                      otherButtonTitles: @"Yes"];
    //[alert show];
    //[alert release];
    [self dismissViewControllerAnimated:YES completion:nil];

}

@end
