//
//  iWinViewAndCreateGroupViewController.h
//  MeetingBuilder
//
//  Created by Brodie Lockard on 1/19/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "iWinAddUsersViewController.h"

@interface iWinViewAndCreateGroupViewController : UIViewController <UserDelegate>
- (IBAction)onClickSave:(UIButton *)sender;
- (IBAction)onClickCancel:(UIButton *)sender;
- (IBAction)onClickAddMembers:(UIButton *)sender;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger)userID withGroupID:(NSInteger)groupID;
@property (weak, nonatomic) IBOutlet UITextField *groupTitleField;
@property (weak, nonatomic) IBOutlet UITableView *memberTableView;

@end

