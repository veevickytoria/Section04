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
//@property (strong, nonatomic) iWinEditProfileViewController *editProfileViewController;
@end

@implementation iWinViewProfileViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withPageName:(NSString *)pageName inEditMode:(BOOL)isEditing
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.pageName = pageName;
        self.isEditing = isEditing;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self updateButtonUI:self.editProfile];
    
    iWinProfile *profile = [[iWinProfile alloc] init];
    profile.displayName = @"Gordon Hazzard";
    profile.email = @"hazzargm@rose-hulman.edu";
    profile.phone = @"(800)866-8866";
    profile.company = @"iWin LLC";
    profile.title = @"Scrum Master";
    profile.location = @"Terre Haute, IN";
    
    //[self updateButtonUI:self.editProfile];
}

-(void) updateButtonUI: (UIButton *)button
{
    button.layer.cornerRadius = 7;
    button.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    button.layer.borderWidth = 1.0f;
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

-(IBAction)onEditProfile:(id)sender
{
    if (self.isEditing) {
        [self saveChanges];
    } else{
        self.isEditing = YES;
   
        //Make cancel button visible
        
    }
//    self.editProfileViewController = [[iWinEditProfileViewController alloc] initWithNibName:@"iWinEditProfileViewController" bundle: nil /*iWinProfileViewController:self*/];
    
//    [self.editProfileViewController setModalPresentationStyle:UIModalPresentationPageSheet];
//    [self.editProfileViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
//    
//    [self presentViewController:self.editProfileViewController animated:YES completion:nil];
//    self.editProfileViewController.view.superview.bounds = CGRectMake(0,0,768,1003);
}


//    self.profilePicImageView.image = nil;
//    self.companyTextView.text = self.company;
//    self.emailTextView.text = self.email;
//    self.phoneTextView.text = self.phone;
//    self.positionTextView.text =self.position;
//    self.moreAboutMeTextView.text = self.moreAboutMe;
//    self.profilePicImageView.image = nil;
//    self.companyTextView.text =  @"Garmin";
//    self.emailTextView.text = @"hazzargm@garmin.com";
//    self.phoneTextView.text = @"(555) 123-7654";
//    self.positionTextView.text = @"Intern";
//    self.moreAboutMeTextView.text = @"I enjoy long walks on the beach... etc. etc. etc. And now to make this really long just to see what it does..............................................................................................................";
//
//-(NSString *) getCompany
//{
//    return self.company;
//}
//-(NSString *) getEmail
//{
//    return self.email;
//}
//-(NSString *) getPhone
//{
//    return self.phone;
//}
//-(NSString *) getPosition
//{
//    return self.position;
//}
//-(NSString *) getMoreAboutMe
//{
//    return self.moreAboutMe;
//}
//-(UIImage *) getProfilePic
//{
//    return self.profilePic;
//}
//-(void) setCompany: (NSString *)company
//{
//    self.company = company;
//}
//-(void) setEmail: (NSString *)email
//{
//    self.email = email;
//}
//-(void) setPhone: (NSString *)phone
//{
//    self.phone = phone;
//}
//-(void) setPosition: (NSString *)position
//{
//    self.position = position;
//}
//-(void) setMoreAboutMe: (NSString *)moreAboutMe
//{
//    self.moreAboutMe = moreAboutMe;
//}
//-(void) setProfilePic: (UIImage *)profilePic
//{
//    self.profilePic = profilePic;
//}
//@property (strong, nonatomic) NSString *name;
//@property (strong, nonatomic) NSString *company;
//@property (strong, nonatomic) NSString *email;
//@property (strong, nonatomic) NSString *phone;
//@property (strong, nonatomic) NSString *position;
//@property (strong, nonatomic) NSString *moreAboutMe;
//@property (strong, nonatomic) UIImage *profilePic;
//        self.company = @"Garmin";
//        self.email = @"hazzargm@garmin.com";
//        self.phone = @"(555) 123-7654";
//        self.position = @"Intern";
//        self.moreAboutMe = @"I enjoy long walks on the beach... etc. etc. etc. And now to make this really long just to see what it does..............................................................................................................";
//        self.editProfileViewController = [[iWinEditProfileViewController alloc] initWithNibName:@"iWinEditProfileViewController" bundle: nil company:self.company email:self.email phone:self.phone position:self.position moreAboutMe:self.moreAboutMe profilePic:self.profilePic];


@end
