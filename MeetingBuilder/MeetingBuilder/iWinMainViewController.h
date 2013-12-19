//
//  iWinMainViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 10/2/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "iWinLoginViewController.h"
#import "iWinRegisterViewController.h"
#import "iWinMeetingViewController.h"
#import "iWinTaskListViewController.h"
#import "iWinNoteListViewController.h"
#import "iWinViewAndAddNotesViewController.h"
#import "iWinViewAndChangeSettingsViewController.h"
#import "iWinViewProfileViewController.h"
#import "iWinOpenEarsModel.h"
//#import <TapkuLibrary/TapkuLibrary.h>
#import "TKCalendarDayView.h"
#import "TKCalendarDayEventView.h"

@interface iWinMainViewController : UIViewController <iWinLoginDelegate, iWinRegisterVCDelegate, NoteListDelegate, ViewAddNoteDelegate, SettingsDelegate, ProfileDelegate, OpenEarsDelegate, TKCalendarDayViewDelegate, TKCalendarDayViewDataSource>

@property (weak, nonatomic) IBOutlet UIView *mainView;
@property (weak, nonatomic) IBOutlet UIView *slideView;
@property (weak, nonatomic) IBOutlet UIView *menuView;
@property (weak, nonatomic) IBOutlet UIButton *voiceCommand;
@property (nonatomic, strong) TKCalendarDayView *dayView;
@property (nonatomic,strong) NSArray *data;

- (IBAction)onClickMenu;
- (IBAction)onClickHome;
- (IBAction)onClickLogOut;
- (IBAction)onClickMeetings;
- (IBAction)onClickNotes;
- (IBAction)onClickTasks;
- (IBAction)onClickSettings;
- (IBAction)onClickProfile;
- (IBAction)onClickSchedule;

- (IBAction)startListening:(id)sender;

@property (weak, nonatomic) IBOutlet UIButton *scheduleButton;
@property (weak, nonatomic) IBOutlet UIView *rightSlideView;
@property (weak, nonatomic) IBOutlet UIButton *menuButton;
@property (weak, nonatomic) IBOutlet UIButton *homeButton;
@property (weak, nonatomic) IBOutlet UIButton *meetingsButton;
@property (weak, nonatomic) IBOutlet UIButton *notesButton;
@property (weak, nonatomic) IBOutlet UIButton *tasksButton;
@property (weak, nonatomic) IBOutlet UIButton *settingsButton;
@property (weak, nonatomic) IBOutlet UIButton *logOutButton;
@property (weak, nonatomic) IBOutlet UIButton *profileButton;

@end