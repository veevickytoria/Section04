//
//  iWinTaskListViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "iWinAddAndViewTaskViewController.h"

@interface iWinTaskListViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, ViewTaskDelegate>
- (IBAction)onClickCreateNewTask;
@property (weak, nonatomic) IBOutlet UITableView *taskListTable;
@property (weak, nonatomic) IBOutlet UIButton *createTaskButton;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil userID:(NSInteger) userID;
@end
