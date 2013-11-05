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

@interface iWinMainViewController ()
@property (strong, nonatomic) iWinLoginViewController *loginViewController;
@property (strong, nonatomic) iWinRegisterViewController *registerViewController;
@property (strong, nonatomic) iWinMeetingViewController *meetingListViewController;
@property (strong, nonatomic) iWinHomeScreenViewController *homeScreenViewController;
@property (strong, nonatomic) iWinScheduleViewMeetingViewController *scheduleMeetingViewController;
@property (strong, nonatomic) iWinAddAndViewTaskViewController *addViewTaskViewController;
@property (strong, nonatomic) iWinTaskListViewController *taskListViewController;
@property (strong, nonatomic) iWinViewAndAddViewController *agendaController;
@property (strong, nonatomic) iWinAddUsersViewController *userViewController;
@property (strong, nonatomic) iWinNoteListViewController *noteViewController;
@property (strong, nonatomic) iWinViewAndAddNotesViewController *viewAddNoteViewController;
@property (strong, nonatomic) iWinViewAndChangeSettingsViewController *settingsViewController;

@property BOOL movedView;
@property (nonatomic) UISwipeGestureRecognizer * swiperight;
@property (nonatomic) UISwipeGestureRecognizer * swipeleft;
@property (nonatomic) UIButton *lastClicked;
@end

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
}

-(void) disableSliding
{
    [self.slideView removeGestureRecognizer:self.swipeleft];
    [self.slideView removeGestureRecognizer:self.swiperight];
    self.menuButton.hidden = YES;
}

-(void) resetSliding
{
    self.movedView = NO;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.loginViewController = [[iWinLoginViewController alloc] initWithNibName:@"iWinLoginViewController" bundle:nil];
    self.registerViewController = [[iWinRegisterViewController alloc] initWithNibName:@"iWinRegisterViewController" bundle:nil];
    self.movedView = false;
    self.registerViewController.registerDelegate = self;
    self.loginViewController.loginDelegate = self;
    [self.mainView  addSubview:self.loginViewController.view];
    [self.loginViewController.view setBounds:self.mainView.bounds];
    
    self.swiperight=[[UISwipeGestureRecognizer alloc]initWithTarget:self action:@selector(swiperight:)];
    self.swiperight.direction=UISwipeGestureRecognizerDirectionRight;
    
    
    self.swipeleft=[[UISwipeGestureRecognizer alloc]initWithTarget:self action:@selector(swipeleft:)];
    self.swipeleft.direction=UISwipeGestureRecognizerDirectionLeft;
    
    self.menuButton.hidden = YES;
    self.menuButton.layer.cornerRadius = 7;
    self.menuButton.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    self.menuButton.layer.borderWidth = 1.0f;
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void) updateSelectedMenu:(UIButton*)newButton
{
    if (self.lastClicked)
    {
        self.lastClicked.backgroundColor = [UIColor clearColor];
        [self.lastClicked setTitleColor:[UIColor lightGrayColor] forState:UIControlStateNormal];
    }
    newButton.backgroundColor = [UIColor whiteColor];
    [newButton setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    self.lastClicked = newButton;
}

-(void) login:(NSString *)email
{
    [self removeSubViews];
    
    [self enableSliding];
    
    self.homeScreenViewController = [[iWinHomeScreenViewController alloc] initWithNibName:@"iWinHomeScreenViewController" bundle:nil];
    [self.mainView  addSubview:self.homeScreenViewController.view];
    [self.homeScreenViewController.view setBounds:self.mainView.bounds];
    
    [self updateSelectedMenu:self.homeButton];
}

-(void)swipeleft:(UISwipeGestureRecognizer*)gestureRecognizer
{
    if (self.movedView)
    {
        [self animateSlidingMenu:NO];
        self.movedView = !self.movedView;
    }
}

-(void)swiperight:(UISwipeGestureRecognizer*)gestureRecognizer
{
    if (!self.movedView)
    {
        [self animateSlidingMenu:YES];
        self.movedView = !self.movedView;
    }
}


-(void) joinUs
{
    [self removeSubViews];
    [self.mainView  addSubview:self.registerViewController.view];
    [self.registerViewController.view setBounds:self.mainView.bounds];
    [self enableSliding];
}

-(void) onRegister:(NSString *)email
{
    [self removeSubViews];
    self.homeScreenViewController = [[iWinHomeScreenViewController alloc] initWithNibName:@"iWinHomeScreenViewController" bundle:nil];
    [self.mainView  addSubview:self.homeScreenViewController.view];
    [self.homeScreenViewController.view setBounds:self.mainView.bounds];
    
}

-(void) onCancel
{
    [self removeSubViews];
    
    [self.slideView removeGestureRecognizer:self.swipeleft];
    [self.slideView removeGestureRecognizer:self.swiperight];
    self.menuButton.hidden = YES;
    
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
    }
    else
    {
        [self animateSlidingMenu:NO];
    }
    self.movedView = !self.movedView;
}

- (IBAction)onClickHome
{
    [self removeSubViews];
    [self enableSliding];
    self.homeScreenViewController = [[iWinHomeScreenViewController alloc] initWithNibName:@"iWinHomeScreenViewController" bundle:nil];
    [self.mainView  addSubview:self.homeScreenViewController.view];
    [self.homeScreenViewController.view setBounds:self.mainView.bounds];
    [self animateSlidingMenu:NO];
    [self updateSelectedMenu:self.homeButton];
    [self resetSliding];
}

- (IBAction)onClickLogOut
{
    [self animateSlidingMenu:NO];
    [self removeSubViews];
    [self disableSliding];
    self.loginViewController = [[iWinLoginViewController alloc] initWithNibName:@"iWinLoginViewController" bundle:nil];
    [self.mainView  addSubview:self.loginViewController.view];
    [self.loginViewController.view setBounds:self.mainView.bounds];
    self.loginViewController.loginDelegate = self;
    [self resetSliding];
}

- (IBAction)onClickMeetings
{
    [self removeSubViews];
    [self enableSliding];
    self.meetingListViewController = [[iWinMeetingViewController alloc] initWithNibName:@"iWinMeetingViewController" bundle:nil];
    [self.mainView  addSubview:self.meetingListViewController.view];
    [self.meetingListViewController.view setBounds:self.mainView.bounds];
    self.meetingListViewController.meetingListDelegate = self;
    [self animateSlidingMenu:NO];
    
    [self updateSelectedMenu:self.meetingsButton];
    [self resetSliding];
}

- (IBAction)onClickNotes
{
    [self removeSubViews];
    [self enableSliding];
    self.noteViewController = [[iWinNoteListViewController alloc] initWithNibName:@"iWinNoteListViewController" bundle:nil];
    [self.mainView addSubview:self.noteViewController.view];
    [self.noteViewController.view setBounds:self.mainView.bounds];
    self.noteViewController.noteListDelegate = self;
    [self animateSlidingMenu:NO];
    [self updateSelectedMenu:self.notesButton];
}

- (IBAction)onClickTasks
{
    [self removeSubViews];
    [self enableSliding];
    [self animateSlidingMenu:NO];
    [self updateSelectedMenu:self.tasksButton];
    
    self.taskListViewController = [[iWinTaskListViewController alloc] initWithNibName:@"iWinTaskListViewController" bundle:nil];
    [self.mainView  addSubview:self.taskListViewController.view];
    [self.taskListViewController.view setBounds:self.mainView.bounds];
    self.taskListViewController.taskListDelegate = self;
    [self resetSliding];
}

- (IBAction)onClickSettings
{
    [self removeSubViews];
    [self enableSliding];
    self.settingsViewController = [[iWinViewAndChangeSettingsViewController alloc] initWithNibName:@"iWinViewAndChangeSettingsViewController" bundle:nil];
    [self.mainView  addSubview:self.settingsViewController.view];
    [self.settingsViewController.view setBounds:self.mainView.bounds];
    self.settingsViewController.settingsDelegate = self;
    [self animateSlidingMenu:NO];
    
    [self updateSelectedMenu:self.meetingsButton];
    [self resetSliding];
}

-(void)animateSlidingMenu:(BOOL)moveRight
{
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.4];
    
    CGRect oldFrame = self.menuView.frame;
    CGRect oldFrameMain = self.slideView.frame;
    
    if (moveRight)
    {
        self.menuView.frame = CGRectMake(0, oldFrame.origin.y, oldFrame.size.width, oldFrame.size.height);
        self.slideView.frame = CGRectMake(oldFrameMain.origin.x+200,oldFrameMain.origin.y,oldFrameMain.size.width,oldFrameMain.size.height);
    }
    else
    {
        self.menuView.frame = CGRectMake(-200, oldFrame.origin.y, oldFrame.size.width, oldFrame.size.height);
        self.slideView.frame = CGRectMake(0,oldFrameMain.origin.y,oldFrameMain.size.width,oldFrameMain.size.height);
    }
    [UIView commitAnimations];
}

-(void) scheduleMeetingClicked:(BOOL)isEditing
{
    [self removeSubViews];
    [self enableSliding];
    [self animateSlidingMenu:NO];
    [self resetSliding];
    self.scheduleMeetingViewController = [[iWinScheduleViewMeetingViewController alloc] initWithNibName:@"iWinScheduleViewMeetingViewController" bundle:nil inEditMode:isEditing];
    [self.mainView  addSubview:self.scheduleMeetingViewController.view];
    [self.scheduleMeetingViewController.view setBounds:self.mainView.bounds];
    self.scheduleMeetingViewController.scheduleDelegate = self;
}

-(void) addViewNoteClicked:(BOOL)isEditing
{
    [self removeSubViews];
    [self enableSliding];
    [self animateSlidingMenu:NO];
    [self resetSliding];
    self.viewAddNoteViewController = [[iWinViewAndAddNotesViewController alloc] initWithNibName:@"iWinViewAndAddNotesViewController" bundle:nil inEditMode:isEditing];
    [self.mainView  addSubview:self.viewAddNoteViewController.view];
    [self.viewAddNoteViewController.view setBounds:self.mainView.bounds];
    self.viewAddNoteViewController.addNoteDelegate = self;
}


-(void) goToViewMeeting
{
    [self scheduleMeetingClicked:YES];
}

-(void) saveClicked
{
    [self onClickMeetings];
}

-(void) cancelClicked
{
    [self onClickMeetings];
}

-(void)saveNoteClicked{
    [self onClickNotes];
}
-(void)cancelNoteClicked{
    [self onClickNotes];
}
-(void)mergeNoteClicked{
    [self onClickNotes];
}

-(void)addAgendaClicked:(BOOL)isEditing
{
    [self removeSubViews];
    [self enableSliding];
    [self animateSlidingMenu:NO];
    [self resetSliding];
    self.agendaController = [[iWinViewAndAddViewController alloc] initWithNibName:@"iWinViewAndAddViewController" bundle:nil inEditMode:isEditing];
    [self.mainView  addSubview:self.agendaController.view];
    [self.agendaController.view setBounds:self.mainView.bounds];
    self.agendaController.agendaDelegate = self;
}

-(void)addAttenddesClicked:(BOOL)isEditing
{
    //meeting
    self.userViewController = [[iWinAddUsersViewController alloc] initWithNibName:@"iWinAddUsersViewController" bundle:nil withPageName:@"Meeting" inEditMode:isEditing];
    [self.mainView  addSubview:self.userViewController.view];
    [self.userViewController.view setBounds:self.mainView.bounds];
    self.userViewController.userDelegate = self;
    [self resetSliding];
}

-(void) addAttendeesForAgenda:(BOOL)isEditing
{
    //agenda
    self.userViewController = [[iWinAddUsersViewController alloc] initWithNibName:@"iWinAddUsersViewController" bundle:nil withPageName:@"Agenda" inEditMode:isEditing];
    [self.mainView  addSubview:self.userViewController.view];
    [self.userViewController.view setBounds:self.mainView.bounds];
    self.userViewController.userDelegate = self;
    [self resetSliding];
}

-(void) addAssigneesForTask:(BOOL)isEditing
{
    //task
    self.userViewController = [[iWinAddUsersViewController alloc] initWithNibName:@"iWinAddUsersViewController" bundle:nil withPageName:@"Task" inEditMode:isEditing];
    [self.mainView  addSubview:self.userViewController.view];
    [self.userViewController.view setBounds:self.mainView.bounds];
    self.userViewController.userDelegate = self;
    [self resetSliding];
}

-(void) returnToPreviousView:(NSString *)pageName inEditMode:(BOOL)isEditing
{
    if ([pageName isEqualToString:@"Meeting"])
    {
        [self scheduleMeetingClicked:isEditing];
    }
    else if ([pageName isEqualToString:@"Agenda"])
    {
        [self addAgendaClicked:isEditing];
    }
    else
    {
        [self createNewTaskClicked:isEditing];
    }
}

-(void)viewScheduleClicked
{
    
}

-(void) createNewTaskClicked:(BOOL)isEditing
{
    [self removeSubViews];
    [self enableSliding];
    [self animateSlidingMenu:NO];
    [self resetSliding];
    self.addViewTaskViewController = [[iWinAddAndViewTaskViewController alloc] initWithNibName:@"iWinAddAndViewTaskViewController" bundle:nil inEditMode:isEditing];
    [self.mainView  addSubview:self.addViewTaskViewController.view];
    [self.addViewTaskViewController.view setBounds:self.mainView.bounds];
    self.addViewTaskViewController.taskDelegate = self;
}

-(void) goToTaskList
{
    [self onClickTasks];
}

-(void) onClickSaveSettings{
    
}
-(void) onclickCancelSettings{
    
}

@end
