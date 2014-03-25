//
//  iWinViewAndCreateProjectViewController.h
//  MeetingBuilder
//
//  Created by Brodie Lockard on 2/2/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "iWinAddUsersViewController.h"

@protocol ProjectDelegate <NSObject>

-(void) refreshProjectList;

@end


@interface iWinViewAndCreateProjectViewController : UIViewController <UITableViewDelegate, UITableViewDataSource, UserDelegate>
@property (nonatomic) id<ProjectDelegate> projectDelegate;
- (IBAction)onClickSave:(id)sender;
- (IBAction)onClickCancel:(id)sender;
- (IBAction)onClickAddMembers:(id)sender;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger)userID withProjectID:(NSInteger)projectID;
@property (weak, nonatomic) IBOutlet UITextField *projectTitleField;
@property (weak, nonatomic) IBOutlet UITableView *membersTableView;
@end
