//
//  iWinScheduleViewMeetingViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "OCCalendarViewController.h"

@protocol ViewMeetingDelegate <NSObject>

-(void)refreshMeetingList;

@end

@interface iWinScheduleViewMeetingViewController : UIViewController <OCCalendarDelegate>
- (IBAction)onAddAgenda;
- (IBAction)onAddAttendees;
- (IBAction)onViewMySchedule;
- (IBAction)onClickSave;
- (IBAction)onClickSaveAndAddMore;
- (IBAction)onClickCancel;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil inEditMode:(BOOL)isEditing withID:(NSString*) meetingID withDateTime:(NSString*) dateTime withTitle:(NSString*) title withLocation:(NSString*) location;
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
@property (weak, nonatomic) IBOutlet UIButton *addAgendaButton;
@end
