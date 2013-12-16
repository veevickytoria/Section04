//
//  iWinViewProfileViewController.h
//  MeetingBuilder
//
//  Created by Richard Shomer on 11/6/13.
//  Modified by Gordon Hazzard and Brodie Lockard.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol ProfileDelegate <NSObject>
-(void) onClickEditProfile;
//-(void) onClickChangePicture;


@end

@interface iWinViewProfileViewController : UIViewController
//- (IBAction)onChangePicture:(id)sender;
- (IBAction)onEditProfile:(id)sender;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withPageName:(NSString *)pageName inEditMode:(BOOL)isEditing;

@property (weak, nonatomic) IBOutlet UIButton *editProfile;

@property (weak, nonatomic) IBOutlet UITextView *displayNameTextView;
@property (weak, nonatomic) IBOutlet UITextView *companyTextView;
@property (weak, nonatomic) IBOutlet UITextView *emailTextView;
@property (weak, nonatomic) IBOutlet UITextView *phoneTextView;
@property (weak, nonatomic) IBOutlet UITextView *titleTextView;
@property (weak, nonatomic) IBOutlet UITextView *location;

@property (nonatomic) id<ProfileDelegate> profileDelegate;

//@property (strong, nonatomic) NSString *company;
//@property (strong, nonatomic) NSString *email;
//@property (strong, nonatomic) NSString *phone;
//@property (strong, nonatomic) NSString *position;
//@property (strong, nonatomic) NSString *moreAboutMe;
//@property (strong, nonatomic) UIImage *profilePic;

//-(NSString *) getCompany;
//-(NSString *) getEmail;
//-(NSString *) getPhone;
//-(NSString *) getPosition;
//-(NSString *) getMoreAboutMe;
//-(UIImage *) getProfilePic;
//-(void) setCompany: (NSString *)company;
//-(void) setEmail: (NSString *)email;
//-(void) setPhone: (NSString *)phone;
//-(void) setPosition: (NSString *)position;
//-(void) setMoreAboutMe: (NSString *)moreAboutMe;
//-(void) setProfilePic: (UIImage *)profilePic;


@end
