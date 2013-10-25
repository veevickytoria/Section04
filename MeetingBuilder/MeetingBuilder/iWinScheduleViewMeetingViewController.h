//
//  iWinScheduleViewMeetingViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol ScheduleViewMeetingDelegate <NSObject>

-(void)saveClicked;
-(void)cancelClicked;
-(void)addAgendaClicked:(BOOL)isEditing;
-(void)addAttenddesClicked:(BOOL)isEditing;
-(void)viewScheduleClicked;
@end

@interface iWinScheduleViewMeetingViewController : UIViewController
- (IBAction)onAddAgenda;
- (IBAction)onAddAttendees;
- (IBAction)onViewMySchedule;
- (IBAction)onClickSave;
- (IBAction)onClickSaveAndAddMore;
- (IBAction)onClickCancel;
@property (nonatomic) id<ScheduleViewMeetingDelegate> scheduleDelegate;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil inEditMode:(BOOL)isEditing;
@property (weak, nonatomic) IBOutlet UIButton *saveAndAddMoreButton;
@property (weak, nonatomic) IBOutlet UITextField *titleField;
@property (weak, nonatomic) IBOutlet UITextField *startTimeField;
@property (weak, nonatomic) IBOutlet UITextField *endTimeField;
@property (weak, nonatomic) IBOutlet UITextField *durationField;
@property (weak, nonatomic) IBOutlet UITextField *placeField;
@property (weak, nonatomic) IBOutlet UILabel *headerLabel;
@property (weak, nonatomic) IBOutlet UIButton *addAgendaButton;
@end
