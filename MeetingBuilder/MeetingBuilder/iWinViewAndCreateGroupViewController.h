//
//  iWinViewAndCreateGroupViewController.h
//  MeetingBuilder
//
//  Created by Brodie Lockard on 1/19/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "iWinAddUsersViewController.h"

@protocol GroupDelegate <NSObject>

-(void) refreshGroupList;

@end

@interface iWinViewAndCreateGroupViewController : UIViewController <UserDelegate, UITableViewDelegate, UITableViewDataSource>
@property (nonatomic) id<GroupDelegate> groupDelegate;
- (IBAction)onClickSave:(UIButton *)sender;
- (IBAction)onClickCancel:(UIButton *)sender;
- (IBAction)onClickAddMembers:(UIButton *)sender;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger)userID withGroupID:(NSInteger)groupID;
@property (weak, nonatomic) IBOutlet UITextField *groupTitleField;
@property (weak, nonatomic) IBOutlet UITableView *memberTableView;
@property (weak, nonatomic) IBOutlet UIButton *saveButton;
@property (weak, nonatomic) IBOutlet UILabel *viewTitle;
@property (weak, nonatomic) IBOutlet UITextField *groupName;
@property (weak, nonatomic) IBOutlet UIButton *addMembersButton;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UIButton *deleteButton;
- (IBAction)onClickDelete;

@end

