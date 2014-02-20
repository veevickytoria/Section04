//
//  iWinViewProfileViewController.h
//  MeetingBuilder
//
//  Created by Richard Shomer on 11/6/13.
//  Modified by Gordon Hazzard and Brodie Lockard.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

//@protocol ProfileDelegate <NSObject>
//-(void) onClickEditProfile;
//-(void) onCancel;
//
//
//@end

@interface iWinViewProfileViewController : UIViewController
//- (IBAction)onChangePicture:(id)sender;
- (IBAction)onEditProfile:(id)sender;
- (IBAction)onCancel:(id)sender;
- (IBAction)onCreateGroup:(id)sender;
- (IBAction)onViewProjects:(id)sender;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withID:(NSInteger) userID;

@property (weak, nonatomic) IBOutlet UIButton *editProfile;
@property (weak, nonatomic) IBOutlet UIButton *cancel;
@property (weak, nonatomic) IBOutlet UIButton *createGroup;
@property (weak, nonatomic) IBOutlet UIButton *viewProjects;

@property (weak, nonatomic) IBOutlet UITextField *displayNameTextField;
@property (weak, nonatomic) IBOutlet UITextField *companyTextField;
@property (weak, nonatomic) IBOutlet UITextField *emailTextField;
@property (weak, nonatomic) IBOutlet UITextField *phoneTextField;
@property (weak, nonatomic) IBOutlet UITextField *titleTextField;
@property (weak, nonatomic) IBOutlet UITextField *locationTextField;
@property (weak, nonatomic) IBOutlet UITableView *groupsTableView;

//@property (nonatomic) id<ProfileDelegate> profileDelegate;


@end
