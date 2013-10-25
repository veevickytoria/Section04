//
//  iWinTaskListViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol TaskListDelegate <NSObject>

-(void)createNewTaskClicked:(BOOL)isEditing;

@end

@interface iWinTaskListViewController : UIViewController <UITableViewDataSource, UITableViewDelegate>
- (IBAction)onClickCreateNewTask;
@property (weak, nonatomic) IBOutlet UITableView *taskListTable;
@property (weak, nonatomic) IBOutlet UIButton *createTaskButton;
@property (nonatomic) id<TaskListDelegate> taskListDelegate;
@end
