//
//  iWinAddUsersViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 10/25/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MessageUI/MessageUI.h>
#import "CustomSubtitledCell.h"

@protocol UserDelegate <NSObject>

-(void)selectedUsers:(NSMutableArray *)userList;

@end

@interface iWinAddUsersViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, UIAlertViewDelegate,MFMailComposeViewControllerDelegate, SubtitledCellDelegate>
@property (weak, nonatomic) IBOutlet UITableView *userListTableView;
@property (nonatomic) id<UserDelegate> userDelegate;
- (IBAction)onClickSendInvite:(id)sender;
- (IBAction)onClickSave;
- (IBAction)onClickCancel;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withPageName:(NSString *)pageName inEditMode:(BOOL)isEditing;
@property (weak, nonatomic) IBOutlet UIButton *cancelButton;
@property (weak, nonatomic) IBOutlet UIButton *saveButton;
@property (strong, nonatomic) NSMutableArray *userList;
@property (strong, nonatomic) NSMutableArray *attendeeList;
@property (strong, nonatomic) NSMutableArray *filteredList;
-(void)filterContentForSearchText:(NSString*)searchText scope:(NSString*)scope;
@property (weak, nonatomic) IBOutlet UIButton *inviteButton;
@property (weak, nonatomic) IBOutlet UILabel *orLabel;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
-(void)setUIFor:(NSString*)pageName;
@end
