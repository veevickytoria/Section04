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
#import "iWinScheduleViewMeetingViewController.h"
#import "iWinViewAndAddViewController.h"
#import "iWinTaskListViewController.h"
#import "iWinAddAndViewTaskViewController.h"
#import "iWinAddUsersViewController.h"
#import "iWinNoteListViewController.h"
#import "iWinViewAndAddNotesViewController.h"
#import "iWinViewAndChangeSettingsViewController.h"

@interface iWinMainViewController : UIViewController <iWinLoginDelegate, iWinRegisterVCDelegate, ScheduleViewMeetingDelegate, MeetingListDelegate, AgendaDelegate, TaskListDelegate, TaskDelegate, UserDelegate, NoteListDelegate, ViewAddNoteDelegate,
    SettingsDelegate>
@property (weak, nonatomic) IBOutlet UIView *mainView;
@property (weak, nonatomic) IBOutlet UIView *slideView;
@property (weak, nonatomic) IBOutlet UIView *menuView;

- (IBAction)onClickMenu;
- (IBAction)onClickHome;
- (IBAction)onClickLogOut;
- (IBAction)onClickMeetings;
- (IBAction)onClickNotes;
- (IBAction)onClickTasks;
- (IBAction)onClickSettings;

@property (weak, nonatomic) IBOutlet UIButton *menuButton;
@property (weak, nonatomic) IBOutlet UIButton *homeButton;
@property (weak, nonatomic) IBOutlet UIButton *meetingsButton;
@property (weak, nonatomic) IBOutlet UIButton *notesButton;
@property (weak, nonatomic) IBOutlet UIButton *tasksButton;
@property (weak, nonatomic) IBOutlet UIButton *settingsButton;
@property (weak, nonatomic) IBOutlet UIButton *logOutButton;

@end
