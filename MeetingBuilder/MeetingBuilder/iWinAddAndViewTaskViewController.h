//
//  iWinAddAndViewTaskViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 10/25/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "OCCalendarViewController.h"
#import "iWinAddUsersViewController.h"

@protocol ViewTaskDelegate <NSObject>

-(void)refreshTaskList;

@end


@interface iWinAddAndViewTaskViewController : UIViewController <OCCalendarDelegate, UserDelegate>
- (IBAction)onClickCancel;
- (IBAction)onClickSave;
- (IBAction)onClickSaveAndAddMore;
@property (weak, nonatomic) IBOutlet UIButton *saveAndAddMoreButton;
@property (weak, nonatomic) IBOutlet UIButton *saveButton;
@property (weak, nonatomic) IBOutlet UIButton *addAssigneeButton;
@property (weak, nonatomic) IBOutlet UIButton *cancelButton;
@property (weak, nonatomic) IBOutlet UILabel *headerLabel;
@property (weak, nonatomic) IBOutlet UITextField *titleField;
@property (weak, nonatomic) IBOutlet UITextField *dueField;
@property (weak, nonatomic) IBOutlet UITextView *descriptionField;
@property (weak, nonatomic) IBOutlet UITextField *createdByField;
@property (weak, nonatomic) IBOutlet UISwitch *isCompleted;
@property (weak, nonatomic) IBOutlet UILabel *endDateLabel;
@property (weak, nonatomic) IBOutlet UILabel *endTimeLabel;
@property (nonatomic) id<ViewTaskDelegate> viewTaskDelegate;
- (IBAction)onClickAddAssignees;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger)userID withTaskID:(NSInteger)taskID;

@end
