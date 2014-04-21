//
//  iWinMainViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/2/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinMainViewController.h"
#import "iWinHomeScreenViewController.h"
#import <QuartzCore/QuartzCore.h>
#import "iWinPopulateDatabase.h"
#import "NSDate+TKCategory.h"
#import "NSDate+CalendarGrid.h"
#import "iWinBackEndUtility.h"
#import "iWinScheduleViewController.h"
#import "iWinMeetingViewController.h"

@interface iWinMainViewController ()
@property (strong, nonatomic) iWinLoginViewController *loginViewController;
@property (strong, nonatomic) iWinRegisterViewController *registerViewController;
@property (strong, nonatomic) iWinMeetingViewController *meetingListViewController;
@property (strong, nonatomic) iWinHomeScreenViewController *homeScreenViewController;
@property (strong, nonatomic) iWinTaskListViewController *taskListViewController;
@property (strong, nonatomic) iWinNoteListViewController *noteViewController;
@property (strong, nonatomic) iWinViewAndAddNotesViewController *viewAddNoteViewController;
@property (strong, nonatomic) iWinViewAndChangeSettingsViewController *settingsViewController;
@property (strong, nonatomic) iWinViewProfileViewController *profileViewController;
@property (strong, nonatomic) iWinOpenEarsModel *openEars;
@property (nonatomic) NSInteger userID;
@property BOOL movedRightView;
@property BOOL movedView;
@property (nonatomic) UISwipeGestureRecognizer *swiperight;
@property (nonatomic) UISwipeGestureRecognizer *swipeleft;
@property (nonatomic) UITapGestureRecognizer *tapGesture;
@property (strong, nonatomic) iWinBackEndUtility *backendUtility;
@property (strong, nonatomic) iWinMenuViewController *menuViewController;
@property (strong, nonatomic) iWinScheduleViewController *scheduleController;
@end



const int LEFT_MENU_OFFSET = 200;
const int RIGHT_MENU_OFFSET = 350;
const float ANIMATION_DURATION = 0.4;


@implementation iWinMainViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

-(void) enableSliding
{
    [self.slideView addGestureRecognizer:self.swipeleft];
    [self.slideView addGestureRecognizer:self.swiperight];
    self.menuButton.hidden = NO;
    self.scheduleButton.hidden = NO;
    self.voiceCommand.hidden = NO;
}

-(void) disableSliding
{
    [self.slideView removeGestureRecognizer:self.swipeleft];
    [self.slideView removeGestureRecognizer:self.swiperight];
    self.menuButton.hidden = YES;
    self.scheduleButton.hidden = YES;
    self.voiceCommand.hidden = YES;
}

-(void) resetSliding
{
    self.movedView = NO;
    self.movedRightView = NO;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    iWinPopulateDatabase *db = [[iWinPopulateDatabase alloc] init];
    [db populateContacts];
    [db populateSettings];
    [self initializeFields];
    [self initializeRegisterPage];
    [self initializeLogin];
    [self initializeSwipeLeftGesture];
    [self initializeSwipeRightGesture];
    [self initializeTapGesture];
    [self initializeOpenEars];
    [self initializeMenu];
    [self initializeSchedule];
}

-(void)initializeFields
{
    self.backendUtility = [[iWinBackEndUtility alloc] init];
    self.movedView = NO;
    self.movedRightView = NO;
}

-(void)initializeRegisterPage
{
    self.registerViewController = [[iWinRegisterViewController alloc] initWithNibName:REGISTER_VC_NIB_NAME bundle:nil];
    self.registerViewController.registerDelegate = self;
}

-(void)initializeLogin
{
    self.loginViewController = [[iWinLoginViewController alloc] initWithNibName:LOGIN_VC_NIB_NAME bundle:nil];
    self.loginViewController.loginDelegate = self;
    [self.mainView  addSubview:self.loginViewController.view];
    [self.loginViewController.view setBounds:self.mainView.bounds];
    
    self.menuButton.hidden = YES;
    self.scheduleButton.hidden = YES;
    self.voiceCommand.hidden = YES;
}

-(void)initializeSwipeRightGesture
{
    self.swiperight=[[UISwipeGestureRecognizer alloc]initWithTarget:self action:@selector(swiperight:)];
    self.swiperight.direction=UISwipeGestureRecognizerDirectionRight;
}

-(void)initializeSwipeLeftGesture
{
    self.swipeleft=[[UISwipeGestureRecognizer alloc]initWithTarget:self action:@selector(swipeleft:)];
    self.swipeleft.direction=UISwipeGestureRecognizerDirectionLeft;
}

-(void)initializeTapGesture
{
    self.tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onTap:)];
}

-(void)initializeOpenEars
{
    self.openEars = [[iWinOpenEarsModel alloc] init];
    [self.openEars initialize];
    self.openEars.menuDelegate = self;
    self.openEars.voiceCommand = self.voiceCommand;
}

-(void)initializeMenu
{
    self.menuViewController = [[iWinMenuViewController alloc] initWithNibName:MENU_VC_NIB_NAME bundle:nil];
    self.menuViewController.menuDelegate = self;
    [self.menuView addSubview:self.menuViewController.view];
    [self.menuViewController.view setBounds:self.menuView.bounds];
}

-(void)initializeSchedule
{
    self.scheduleController = [[iWinScheduleViewController alloc] initWithNibName:SCHEDULE_VC_NIB_NAME bundle:nil withUserID:self.userID];
    [self.rightSlideView addSubview:self.scheduleController.view];
    [self.scheduleController.view setBounds:self.rightSlideView.bounds];
}

-(void) login:(NSInteger)userID
{
    [self removeSubViews];
    [self enableSliding];
    self.userID = userID;
    [self.scheduleController setUserID:self.userID];
    [self.scheduleController loadScheduleView];
    self.homeScreenViewController = [[iWinHomeScreenViewController alloc] initWithNibName:HOME_SCREEN_VC_NIB_NAME bundle:nil withUserID:userID];
    [self.mainView  addSubview:self.homeScreenViewController.view];
    [self.homeScreenViewController.view setBounds:self.mainView.bounds];
    
}

-(void)swipeleft:(UISwipeGestureRecognizer*)gestureRecognizer
{
    if (self.movedView)
    {
        [self animateSlidingMenu:NO];
        self.movedView = !self.movedView;
        [self removeTapRecognizer];
    }
    else if (!self.movedRightView && !self.movedView)
    {
        [self animateRightSlidingMenu:YES];
        self.movedRightView = !self.movedRightView;
        [self addTapRecognizer];
    }
    
}

-(void)swiperight:(UISwipeGestureRecognizer*)gestureRecognizer
{
    [self.scheduleController loadScheduleView];
    if (!self.movedView && !self.movedRightView)
    {
        [self animateSlidingMenu:YES];
        self.movedView = !self.movedView;
        [self addTapRecognizer];
    }
    else if (self.movedRightView)
    {
        [self animateRightSlidingMenu:NO];
        self.movedRightView = !self.movedRightView;
        [self removeTapRecognizer];
    }
}

-(void)removeTapRecognizer
{
    self.tapView.hidden = YES;
    [self.tapView removeGestureRecognizer:self.tapGesture];
    [self.tapView removeGestureRecognizer:self.swipeleft];
    [self.tapView removeGestureRecognizer:self.swiperight];
    [self enableSliding];
}

-(void) addTapRecognizer
{
    self.tapView.hidden = NO;
    [self.tapView addGestureRecognizer:self.tapGesture];
    [self.tapView addGestureRecognizer:self.swipeleft];
    [self.tapView addGestureRecognizer:self.swiperight];
}

-(void) onTap:(UITapGestureRecognizer *)gestureRecognizer
{
    if (self.movedView)
    {
        [self swipeleft:nil];
        [self removeTapRecognizer];
    }
    else if(self.movedRightView)
    {
        [self swiperight:nil];
        [self removeTapRecognizer];
    }
}

-(void) joinUs
{
    [self removeSubViews];
    [self.mainView  addSubview:self.registerViewController.view];
    [self.registerViewController.view setBounds:self.mainView.bounds];
}

-(void) onRegister:(NSInteger)userID
{
    [self removeSubViews];
    [self enableSliding];
    [self removeTapRecognizer];
    self.userID = userID;
    self.homeScreenViewController = [[iWinHomeScreenViewController alloc] initWithNibName:HOME_SCREEN_VC_NIB_NAME bundle:nil withUserID:userID];
    [self.mainView  addSubview:self.homeScreenViewController.view];
    [self.homeScreenViewController.view setBounds:self.mainView.bounds];
}

-(void) onCancel
{
    [self removeSubViews];
    
    [self.slideView removeGestureRecognizer:self.swipeleft];
    [self.slideView removeGestureRecognizer:self.swiperight];
    self.menuButton.hidden = YES;
    self.scheduleButton.hidden = YES;
    self.voiceCommand.hidden = YES;
    [self.mainView  addSubview:self.loginViewController.view];
    [self.loginViewController.view setBounds:self.mainView.bounds];
    
    [self disableSliding];
}

-(void)removeSubViews
{
    NSArray *subViews = [self.mainView subviews];
    for (int i=0; i<subViews.count; i++)
    {
        [subViews[i] removeFromSuperview];
    }
}

- (IBAction)onClickMenu
{
    if (!self.movedView)
    {
        [self animateSlidingMenu:YES];
        [self addTapRecognizer];
    }
    else
    {
        [self animateSlidingMenu:NO];
        [self removeTapRecognizer];
    }
    self.movedView = !self.movedView;
}

- (IBAction)onClickSchedule
{
    [self.scheduleController loadScheduleView];
    if (!self.movedRightView)
    {
        [self animateRightSlidingMenu:YES];
        [self addTapRecognizer];
    }
    else
    {
        [self animateRightSlidingMenu:NO];
        [self removeTapRecognizer];
    }
    self.movedRightView = !self.movedRightView;
}

- (void)goToHomePage
{
    [self prepareForControllerChange];
    self.homeScreenViewController = [[iWinHomeScreenViewController alloc] initWithNibName:HOME_SCREEN_VC_NIB_NAME bundle:nil withUserID:self.userID];
    [self.mainView  addSubview:self.homeScreenViewController.view];
    [self.homeScreenViewController.view setBounds:self.mainView.bounds];
    [self.scheduleController setUserID:self.userID];
}

- (void)goToLogout
{
    [self animateSlidingMenu:NO];
    [self removeSubViews];
    [self removeTapRecognizer];
    [self disableSliding];
    self.loginViewController = [[iWinLoginViewController alloc] initWithNibName:LOGIN_VC_NIB_NAME bundle:nil];
    [self.mainView  addSubview:self.loginViewController.view];
    [self.loginViewController.view setBounds:self.mainView.bounds];
    self.loginViewController.loginDelegate = self;
    [self resetSliding];
    self.menuButton.hidden = YES;
    self.scheduleButton.hidden = YES;
    self.voiceCommand.hidden = YES;
}

-(void)prepareForControllerChange
{
    [self removeSubViews];
    [self enableSliding];
    [self removeTapRecognizer];
    [self animateSlidingMenu:NO];
    [self resetSliding];
}

- (void)goToMeetings
{
    [self prepareForControllerChange];
    self.meetingListViewController = [[iWinMeetingViewController alloc] initWithNibName:MEETING_VC_NIB_NAME bundle:nil withID:self.userID];
    [self.mainView  addSubview:self.meetingListViewController.view];
    [self.meetingListViewController.view setBounds:self.mainView.bounds];
    self.meetingListViewController.reloadScheduleDelegate = self.scheduleController;
}

- (void)goToNotes
{
    [self prepareForControllerChange];
    self.noteViewController = [[iWinNoteListViewController alloc] initWithNibName:NOTES_VC_NIB_NAME bundle:nil withUserID:self.userID];
    [self.mainView addSubview:self.noteViewController.view];
    [self.noteViewController.view setBounds:self.mainView.bounds];
}

- (void)goToTasks
{
    [self prepareForControllerChange];
    self.taskListViewController = [[iWinTaskListViewController alloc] initWithNibName:TASK_VC_NIB_NAME bundle:nil userID:self.userID];
    [self.mainView  addSubview:self.taskListViewController.view];
    [self.taskListViewController.view setBounds:self.mainView.bounds];
}

- (void)goToSettings
{
    [self prepareForControllerChange];
    self.settingsViewController = [[iWinViewAndChangeSettingsViewController alloc] initWithNibName:SETTINGS_VC_NIB_NAME bundle:nil withID:self.userID];
    [self.mainView  addSubview:self.settingsViewController.view];
    [self.settingsViewController.view setBounds:self.mainView.bounds];
    self.settingsViewController.settingsDelegate = self;
}

- (void)goToProfile{
    [self prepareForControllerChange];
    self.profileViewController = [[iWinViewProfileViewController alloc] initWithNibName:PROFILE_VC_NIB_NAME bundle:nil withID: self.userID];
    [self.mainView  addSubview:self.profileViewController.view];
    [self.profileViewController.view setBounds:self.mainView.bounds];
}

-(void)animateSlidingMenu:(BOOL)moveRight
{
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:ANIMATION_DURATION];
    if (moveRight)
        [self slideForView:self.menuView withOffset:LEFT_MENU_OFFSET];
    else
        [self slideForView:self.menuView withOffset:-LEFT_MENU_OFFSET];
    [UIView commitAnimations];
}

-(void)animateRightSlidingMenu:(BOOL)moveLeft
{
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:ANIMATION_DURATION];
    if (moveLeft)
        [self slideForView:self.rightSlideView withOffset:-RIGHT_MENU_OFFSET];
    else
        [self slideForView:self.rightSlideView withOffset:RIGHT_MENU_OFFSET];
    [UIView commitAnimations];
}

-(void) slideForView:(UIView*)view withOffset:(NSInteger)offset
{
    CGRect oldFrame = view.frame;
    CGRect oldFrameMain = self.slideView.frame;
    
    view.frame = CGRectMake(oldFrame.origin.x + offset, oldFrame.origin.y, oldFrame.size.width, oldFrame.size.height);
    self.slideView.frame = CGRectMake(oldFrameMain.origin.x + offset,oldFrameMain.origin.y,oldFrameMain.size.width,oldFrameMain.size.height);
}

-(void) onDeleteAccount
{
    [self goToLogout];
}

- (IBAction)startListening:(id)sender
{
    [self.openEars startListening];
}

@end
