//
//  iWinScheduleViewMeetingViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "OCCalendarViewController.h"
#import "iWinAddUsersViewController.h"
#import "iWinViewAndAddViewController.h"

@protocol ViewMeetingDelegate <NSObject>

-(void)refreshMeetingList;

@end

@interface iWinScheduleViewMeetingViewController : UIViewController <OCCalendarDelegate, UserDelegate, UITableViewDataSource, UITableViewDelegate, UIAlertViewDelegate, UITextFieldDelegate, AgendaDelegate>
- (IBAction)onAddAgenda;
- (IBAction)onAddAttendees;
- (IBAction)onViewMySchedule;
- (IBAction)onClickSave;
- (IBAction)onClickSaveAndAddMore;
- (IBAction)onDeleteMeeting;
- (IBAction)onClickCancel;

//- (IBAction)viewAgendas:(id)sender;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger)userID withMeetingID:(NSInteger)meetingID;
@property (weak, nonatomic) IBOutlet UIButton *saveAndAddMoreButton;
@property (weak, nonatomic) IBOutlet UITextField *titleField;
@property (weak, nonatomic) IBOutlet UILabel *startDateLabel;
@property (weak, nonatomic) IBOutlet UILabel *endDateLabel;
@property (weak, nonatomic) IBOutlet UILabel *startTimeLabel;
@property (weak, nonatomic) IBOutlet UILabel *endTimeLabel;
@property (nonatomic) id<ViewMeetingDelegate> viewMeetingDelegate;
//@property (weak, nonatomic) IBOutlet UITextField *durationField;
@property (weak, nonatomic) IBOutlet UITextField *placeField;
@property (weak, nonatomic) IBOutlet UILabel *headerLabel;
@property (weak, nonatomic) IBOutlet UIButton *saveButton;
@property (weak, nonatomic) IBOutlet UIButton *cancelButton;
@property (weak, nonatomic) IBOutlet UIButton *addAttendeesButton;
@property (weak, nonatomic) IBOutlet UIButton *visitScheduleButton;
@property (weak, nonatomic) IBOutlet UITableView *attendeeTableView;
@property (weak, nonatomic) IBOutlet UIButton *deleteMeetingButton;
@property (weak, nonatomic) IBOutlet UIButton *addAgendaButton;
@property (nonatomic) BOOL isAgendaCreated;
@end
