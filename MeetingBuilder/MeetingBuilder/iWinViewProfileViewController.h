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
-(void) onCancel;


@end

@interface iWinViewProfileViewController : UIViewController
//- (IBAction)onChangePicture:(id)sender;
- (IBAction)onEditProfile:(id)sender;
- (IBAction)onCancel:(id)sender;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withPageName:(NSString *)pageName inEditMode:(BOOL)isEditing;

@property (weak, nonatomic) IBOutlet UIButton *editProfile;
@property (weak, nonatomic) IBOutlet UIButton *cancel;

@property (weak, nonatomic) IBOutlet UITextView *displayNameTextView;
@property (weak, nonatomic) IBOutlet UITextView *companyTextView;
@property (weak, nonatomic) IBOutlet UITextView *emailTextView;
@property (weak, nonatomic) IBOutlet UITextView *phoneTextView;
@property (weak, nonatomic) IBOutlet UITextView *titleTextView;
@property (weak, nonatomic) IBOutlet UITextView *locationTextView;

@property (nonatomic) id<ProfileDelegate> profileDelegate;


@end
