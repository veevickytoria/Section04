//
//  iWinMainViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/2/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinMainViewController.h"
#import "iWinHomeScreenViewController.h"

@interface iWinMainViewController ()
@property (strong, nonatomic) iWinLoginViewController *loginViewController;
@property (strong, nonatomic) iWinRegisterViewController *registerViewController;
@property (strong, nonatomic) iWinMeetingViewController *meetingListViewController;
@property (strong, nonatomic) iWinHomeScreenViewController *homeScreenViewController;
@property (strong, nonatomic) iWinScheduleViewMeetingViewController *scheduleMeetingViewController;
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
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) login:(NSString *)email
{
    [self removeSubViews];
    
    [self enableSliding];
     
//    self.projectViewController = [[iWinMeetingViewController alloc] initWithNibName:@"iWinProjectViewController" bundle:nil withEmail:email];
//    [self.mainView  addSubview:self.projectViewController.view];
//    [self.projectViewController.view setBounds:self.mainView.bounds];
    
    self.homeScreenViewController = [[iWinHomeScreenViewController alloc] initWithNibName:@"iWinHomeScreenViewController" bundle:nil];
    [self.mainView  addSubview:self.homeScreenViewController.view];
    [self.homeScreenViewController.view setBounds:self.mainView.bounds];
    
    self.homeButton.backgroundColor = [UIColor whiteColor];
    self.lastClicked = self.homeButton;
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
    self.homeButton.backgroundColor = [UIColor whiteColor];
    self.lastClicked.backgroundColor = [UIColor clearColor];
    self.lastClicked = self.homeButton;
}

- (IBAction)onClickLogOut
{
    [self animateSlidingMenu:NO];
    self.loginViewController = [[iWinLoginViewController alloc] initWithNibName:@"iWinLoginViewController" bundle:nil];
    [self.mainView  addSubview:self.loginViewController.view];
    [self.loginViewController.view setBounds:self.mainView.bounds];
    
    [self disableSliding];
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
    
    self.meetingsButton.backgroundColor = [UIColor whiteColor];
    self.lastClicked.backgroundColor = [UIColor clearColor];
    self.lastClicked = self.meetingsButton;
}

- (IBAction)onClickNotes
{
    [self removeSubViews];
    [self enableSliding];
    [self animateSlidingMenu:NO];
    self.notesButton.backgroundColor = [UIColor whiteColor];
    self.lastClicked.backgroundColor = [UIColor clearColor];
    self.lastClicked = self.notesButton;
}

- (IBAction)onClickTasks
{
    [self removeSubViews];
    [self enableSliding];
    [self animateSlidingMenu:NO];
    self.tasksButton.backgroundColor = [UIColor whiteColor];
    self.lastClicked.backgroundColor = [UIColor clearColor];
    self.lastClicked = self.tasksButton;
}

- (IBAction)onClickSettings
{
    [self removeSubViews];
    [self enableSliding];
    [self animateSlidingMenu:NO];
    self.settingsButton.backgroundColor = [UIColor whiteColor];
    self.lastClicked.backgroundColor = [UIColor clearColor];
    self.lastClicked = self.settingsButton;
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

-(void) scheduleMeetingClicked
{
    [self removeSubViews];
    [self enableSliding];
    [self animateSlidingMenu:NO];
    
    self.scheduleMeetingViewController = [[iWinScheduleViewMeetingViewController alloc] initWithNibName:@"iWinScheduleViewMeetingViewController" bundle:nil inEditMode:NO];
    [self.mainView  addSubview:self.scheduleMeetingViewController.view];
    [self.scheduleMeetingViewController.view setBounds:self.mainView.bounds];
    self.scheduleMeetingViewController.scheduleDelegate = self;
}

-(void) meetingSelected
{
    [self removeSubViews];
    [self enableSliding];
    [self animateSlidingMenu:NO];
    
    self.scheduleMeetingViewController = [[iWinScheduleViewMeetingViewController alloc] initWithNibName:@"iWinScheduleViewMeetingViewController" bundle:nil inEditMode:YES];
    [self.mainView  addSubview:self.scheduleMeetingViewController.view];
    [self.scheduleMeetingViewController.view setBounds:self.mainView.bounds];
    self.scheduleMeetingViewController.scheduleDelegate = self;
}

-(void) saveClicked
{
    [self onClickMeetings];
}

-(void) cancelClicked
{
    [self onClickMeetings];
}

-(void)addAgendaClicked
{
    
}

-(void)addAttenddesClicked
{
    
}

-(void)viewScheduleClicked
{
    
}

@end
