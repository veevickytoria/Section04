//
//  iWinViewAndCreateProjectViewController.h
//  MeetingBuilder
//
//  Created by Brodie Lockard on 2/2/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "iWinAddUsersViewController.h"
@protocol ViewProjectDelegate <NSObject>

-(void)refreshMeetingList;

@end

@interface iWinViewAndCreateProjectViewController : UIViewController <UserDelegate, UITableViewDelegate, UITableViewDataSource>
- (IBAction)onClickSave:(id)sender;
- (IBAction)onClickCancel:(id)sender;
- (IBAction)onClickAddMembers:(id)sender;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger)userID withProjectID:(NSInteger)projectID;
@property (weak, nonatomic) IBOutlet UITextField *projectTitleField;
@property (weak, nonatomic) IBOutlet UITableView *membersTableView;
@end
