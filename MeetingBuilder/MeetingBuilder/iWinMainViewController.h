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
#import "TKCalendarDayView.h"
#import "TKCalendarDayEventView.h"
#import "iWinMenuViewController.h"

@interface iWinMainViewController : UIViewController <iWinLoginDelegate, iWinRegisterVCDelegate, SettingsDelegate, TKCalendarDayViewDelegate, TKCalendarDayViewDataSource, ReloadScheduleDelegate, MenuDelegate>


@property (weak, nonatomic) IBOutlet UIView *mainView;
@property (weak, nonatomic) IBOutlet UIView *slideView;
@property (weak, nonatomic) IBOutlet UIView *menuView;
@property (weak, nonatomic) IBOutlet UIButton *voiceCommand;
@property (nonatomic, strong) TKCalendarDayView *dayView;
@property (nonatomic,strong) NSArray *data;
@property (weak, nonatomic) IBOutlet UIView *tapView;

- (IBAction)onClickMenu;
- (IBAction)onClickSchedule;

- (IBAction)startListening:(id)sender;

@property (weak, nonatomic) IBOutlet UIButton *scheduleButton;
@property (weak, nonatomic) IBOutlet UIView *rightSlideView;
@property (weak, nonatomic) IBOutlet UIButton *menuButton;

@end
