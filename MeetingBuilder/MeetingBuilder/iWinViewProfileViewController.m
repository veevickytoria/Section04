//
//  iWinViewProfileViewController.m
//  MeetingBuilder
//
//  Created by Richard Shomer on 11/6/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

//#import "iWinEditProfileViewController.h"
#import "iWinViewProfileViewController.h"
//#import "iWinViewAndAddViewController.h"
#import "iWinProfile.h"
#import <QuartzCore/QuartzCore.h>

@interface iWinViewProfileViewController ()
@property (nonatomic) NSString *pageName;
@property (nonatomic) BOOL isEditing;
@property (nonatomic) iWinProfile *profile;
//@property (strong, nonatomic) iWinEditProfileViewController *editProfileViewController;
@end

@implementation iWinViewProfileViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withPageName:(NSString *)pageName inEditMode:(BOOL)isEditing
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        //self.pageName = pageName;
        //self.isEditing = isEditing;
    }
    [self viewDidLoad];
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self updateButtonUI:self.editProfile];
    
//    iWinProfile *profile = [[iWinProfile alloc] init];
//    self.profile.displayName = @"Gordon Hazzard";
//    self.profile.email = @"hazzargm@rose-hulman.edu";
//    self.profile.phone = @"(800)866-8866";
//    self.profile.company = @"iWin LLC";
//    self.profile.title = @"Scrum Master";
//    self.profile.location = @"Terre Haute, IN";
    
//    self.displayNameTextView.text =  @"Gordon Hazzard";
//    self.emailTextView.text =  @"hazzargm@rose-hulman.edu";
//    self.phoneTextView.text = @"(800)866-8866";
//    self.companyTextView.text = @"iWin LLC";
//    self.titleTextView.text = @"Scrum Master";
//    self.locationTextView.text = @"Terre Haute, IN";
    self.cancel.hidden = YES;
    [self updateTextUI];
    
    //[self updateButtonUI:self.editProfile];
}
-(void) updateTextUI
{
    
    //Need to make fonts bigger
    
//    UIFont *newFont = [[UIFont alloc] fontWithSize:19.0f];
//    self.titleTextView.font = newFont;
//    self.emailTextView.font = newFont;
//    self.phoneTextView.font = newFont;
//    self.companyTextView.font = newFont;
//    self.titleTextView.font = newFont;
//    self.locationTextView.font = newFont;

    self.displayNameTextView.text =  @"Gordon Hazzard";
    self.emailTextView.text =  @"hazzargm@rose-hulman.edu";
    self.phoneTextView.text = @"(800)866-8866";
    self.companyTextView.text = @"iWin LLC";
    self.titleTextView.text = @"Scrum Master";
    self.locationTextView.text = @"Terre Haute, IN";
    
//    self.displayNameTextView.text = self.profile.displayName;
//    self.emailTextView.text = self.profile.email;
//    self.phoneTextView.text = self.profile.phone;
//    self.companyTextView.text = self.profile.company;
//    self.titleTextView.text = self.profile.title;
//    self.locationTextView.text = self.profile.location;
}

-(void) updateButtonUI: (UIButton *)button
{
    button.layer.cornerRadius = 7;
    button.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    button.layer.borderWidth = 1.0f;
    [button setTintColor:[UIColor blueColor]];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

//-(IBAction)onChangePicture:(id)sender
//{
//    
//}

-(void) saveChanges
{
    
    

}

-(IBAction)onCancel:(id)sender
{
    //    self.displayNameTextView.text = self.profile.displayName;
    //    self.emailTextView.text = self.profile.email;
    //    self.phoneTextView.text = self.profile.phone;
    //    self.companyTextView.text = self.profile.company;
    //    self.titleTextView.text = self.profile.title;
    //    self.locationTextView.text = self.profile.location;
    self.isEditing = NO;
    [self.editProfile setTitle:@"Edit Profile" forState:UIControlStateNormal];
    self.cancel.hidden = YES;
    
}

-(IBAction)onEditProfile:(id)sender
{
    if (self.isEditing) {
        [self saveChanges];
        [self.displayNameTextView setEditable:NO];
        [self.titleTextView setEditable:NO];
        [self.companyTextView setEditable:NO];
        [self.locationTextView setEditable:NO];
        [self.emailTextView setEditable:NO];
        [self.phoneTextView setEditable:NO];
        [self.editProfile setTitle:@"Edit Profile" forState:UIControlStateNormal];
        [self.editProfile setTintColor:[UIColor blueColor]];
        self.cancel.hidden = YES;
        
    } else{
        self.isEditing = YES;
        [self.displayNameTextView setEditable:YES];
        [self.titleTextView setEditable:YES];
        [self.companyTextView setEditable:YES];
        [self.locationTextView setEditable:YES];
        [self.emailTextView setEditable:YES];
        [self.phoneTextView setEditable:YES];
        [self.editProfile setTitle:@"Save" forState:UIControlStateNormal];
        [self.editProfile setTintColor:[UIColor greenColor]];
        self.cancel.hidden = NO;
   
        //Make cancel button visible
        
    }
}
@end
