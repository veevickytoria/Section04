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
@property (strong, nonatomic) NSString *user;
@property (nonatomic) NSInteger userID;
@property BOOL movedRightView;
@property BOOL movedView;
@property (nonatomic) UISwipeGestureRecognizer *swiperight;
@property (nonatomic) UISwipeGestureRecognizer *swipeleft;
@property (nonatomic) UITapGestureRecognizer *tapGesture;
@property (nonatomic) UIButton *lastClicked;
@property (strong, nonatomic) iWinBackEndUtility *backendUtility;
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
    self.backendUtility = [[iWinBackEndUtility alloc] init];
    self.loginViewController = [[iWinLoginViewController alloc] initWithNibName:@"iWinLoginViewController" bundle:nil];
    self.registerViewController = [[iWinRegisterViewController alloc] initWithNibName:@"iWinRegisterViewController" bundle:nil];
    self.movedView = NO;
    self.movedRightView = NO;
    self.registerViewController.registerDelegate = self;
    
    self.loginViewController.loginDelegate = self;
    [self.mainView  addSubview:self.loginViewController.view];
    [self.loginViewController.view setBounds:self.mainView.bounds];
    
    self.swiperight=[[UISwipeGestureRecognizer alloc]initWithTarget:self action:@selector(swiperight:)];
    self.swiperight.direction=UISwipeGestureRecognizerDirectionRight;
    
    
    self.swipeleft=[[UISwipeGestureRecognizer alloc]initWithTarget:self action:@selector(swipeleft:)];
    self.swipeleft.direction=UISwipeGestureRecognizerDirectionLeft;
    
    self.tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onTap:)];
    
    self.menuButton.hidden = YES;
    self.scheduleButton.hidden = YES;
    self.voiceCommand.hidden = YES;
    
    self.openEars = [[iWinOpenEarsModel alloc] init];
    self.openEars.openEarsDelegate = self;
    [self.openEars initialize];
    
    self.menuView.hidden = YES;
}

-(void)viewDidAppear:(BOOL)animated
{
    CGRect oldFrame = self.menuView.frame;
    self.menuView.frame = CGRectMake(-200, oldFrame.origin.y, oldFrame.size.width, oldFrame.size.height);
    self.menuView.hidden = NO;
}

- (void) viewDidUnload {
	self.dayView = nil;
}

#pragma mark TKCalendarDayViewDelegate
- (NSArray *) calendarDayTimelineView:(TKCalendarDayView*)calendarDayTimeline eventsForDate:(NSDate *)eventDate{
    //if([eventDate compare:[NSDate dateWithTimeIntervalSinceNow:-24*60*60]] == NSOrderedAscending) return @[];
	//if([eventDate compare:[NSDate dateWithTimeIntervalSinceNow:24*60*60]] == NSOrderedDescending) return @[];
    
	NSDateComponents *info = [[NSDate date] dateComponentsWithTimeZone:calendarDayTimeline.timeZone];
	info.second = 0;
	NSMutableArray *ret = [NSMutableArray array];
    
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/User/Schedule/%d", self.userID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    if (!deserializedDictionary)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Schedule not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        NSArray *jsonArray = [deserializedDictionary objectForKey:@"schedule"];
        if (jsonArray.count > 0)
        {
            for (NSDictionary* meetings in jsonArray)
            {
                if ([[meetings objectForKey:@"type"] isEqualToString:@"meeting"]){
                    TKCalendarDayEventView *event = [calendarDayTimeline dequeueReusableEventView];
                    if(event == nil) event = [TKCalendarDayEventView eventView];
                    
                    event.identifier = nil;
                    event.titleLabel.text = [meetings objectForKey:@"title"];
                    event.locationLabel.text = [meetings objectForKey:@"description"];
                    
                    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
                    //Set the AM and PM symbols
                    [dateFormatter setAMSymbol:@"AM"];
                    [dateFormatter setPMSymbol:@"PM"];
                    //Specify only 2 M for month, 2 d for day and 2 h for hour
                    [dateFormatter setDateFormat:@"MM/dd/yyyy hh:mm a"];
                    
                    NSDate *date = [dateFormatter dateFromString:[meetings objectForKey:@"datetimeStart"]];
                    event.startDate = date;
                    
                    date = [dateFormatter dateFromString:[meetings objectForKey:@"datetimeEnd"]];
                    event.endDate = date;
                    
                    [ret addObject:event];
                }

            }
        }
    }
        
		return ret;
}
- (void) calendarDayTimelineView:(TKCalendarDayView*)calendarDayTimeline eventViewWasSelected:(TKCalendarDayEventView *)eventView{
    
}

- (void) calendarDayTimelineView:(TKCalendarDayView*)calendarDayTimeline didMoveToDate:(NSDate*)eventDate{
	
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

-(void) login:(NSInteger)userID
{
    [self removeSubViews];
    
    [self enableSliding];
    
    self.userID = userID;
    self.homeScreenViewController = [[iWinHomeScreenViewController alloc] initWithNibName:@"iWinHomeScreenViewController" bundle:nil withUserID:userID];
    [self.mainView  addSubview:self.homeScreenViewController.view];
    [self.homeScreenViewController.view setBounds:self.mainView.bounds];
    
    [self updateSelectedMenu:self.homeButton];
    self.openEars.openEarsDelegate = self;
}

-(void) loadScheduleView {
    CGRect scheduleFrame = CGRectMake(self.rightSlideView.bounds.origin.x, self.rightSlideView.bounds.origin.y + 81, self.rightSlideView.bounds.size.width, self.rightSlideView.bounds.size.height - 95);
    
    self.dayView = [[TKCalendarDayView alloc] initWithFrame:scheduleFrame];
	self.dayView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    self.dayView.backgroundColor = [UIColor blackColor];
	self.dayView.delegate = self;
	self.dayView.dataSource = self;
	[self.rightSlideView addSubview:self.dayView];
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
    [self loadScheduleView];
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
    self.homeScreenViewController = [[iWinHomeScreenViewController alloc] initWithNibName:@"iWinHomeScreenViewController" bundle:nil withUserID:userID];
    [self.mainView  addSubview:self.homeScreenViewController.view];
    [self.homeScreenViewController.view setBounds:self.mainView.bounds];
    [self updateSelectedMenu:self.homeButton];
    self.openEars.openEarsDelegate = self;
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
    [self loadScheduleView];
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

- (IBAction)onClickHome
{
    [self removeSubViews];
    [self enableSliding];
    [self removeTapRecognizer];
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
    [self removeTapRecognizer];
    [self disableSliding];
    self.loginViewController = [[iWinLoginViewController alloc] initWithNibName:@"iWinLoginViewController" bundle:nil];
    [self.mainView  addSubview:self.loginViewController.view];
    [self.loginViewController.view setBounds:self.mainView.bounds];
    self.loginViewController.loginDelegate = self;
    [self resetSliding];
    self.menuButton.hidden = YES;
    self.scheduleButton.hidden = YES;
    self.voiceCommand.hidden = YES;
}

- (IBAction)onClickMeetings
{
    [self removeSubViews];
    [self enableSliding];
    [self removeTapRecognizer];
    self.meetingListViewController = [[iWinMeetingViewController alloc] initWithNibName:@"iWinMeetingViewController" bundle:nil withID:self.userID];
    self.meetingListViewController.reloadScheduleDelegate = self;
    [self.mainView  addSubview:self.meetingListViewController.view];
    [self.meetingListViewController.view setBounds:self.mainView.bounds];
    [self animateSlidingMenu:NO];
    
    [self updateSelectedMenu:self.meetingsButton];
    [self resetSliding];
}

- (IBAction)onClickNotes
{
    [self removeSubViews];
    [self enableSliding];
    [self removeTapRecognizer];
    self.noteViewController = [[iWinNoteListViewController alloc] initWithNibName:@"iWinNoteListViewController" bundle:nil withUserID:self.userID];
    [self.mainView addSubview:self.noteViewController.view];
    [self.noteViewController.view setBounds:self.mainView.bounds];
    [self animateSlidingMenu:NO];
    [self updateSelectedMenu:self.notesButton];
    [self resetSliding];
}

- (IBAction)onClickTasks
{
    [self removeSubViews];
    [self enableSliding];
    [self removeTapRecognizer];
    [self animateSlidingMenu:NO];
    [self updateSelectedMenu:self.tasksButton];
    self.taskListViewController = [[iWinTaskListViewController alloc] initWithNibName:@"iWinTaskListViewController" bundle:nil userID:self.userID];
    [self.mainView  addSubview:self.taskListViewController.view];
    [self.taskListViewController.view setBounds:self.mainView.bounds];
    [self resetSliding];
}

- (IBAction)onClickSettings
{
    [self removeSubViews];
    [self enableSliding];
    [self removeTapRecognizer];
    self.settingsViewController = [[iWinViewAndChangeSettingsViewController alloc] initWithNibName:@"iWinViewAndChangeSettingsViewController" bundle:nil withID:self.userID];
    [self.mainView  addSubview:self.settingsViewController.view];
    [self.settingsViewController.view setBounds:self.mainView.bounds];
    //self.settingsViewController.settingsDelegate = self;
    [self animateSlidingMenu:NO];
    
    [self updateSelectedMenu:self.settingsButton];
    [self resetSliding];
}

- (IBAction)onClickProfile{
    [self removeSubViews];
    [self enableSliding];
    [self removeTapRecognizer];
    self.profileViewController = [[iWinViewProfileViewController alloc] initWithNibName:@"iWinViewProfileViewController" bundle:nil withID: self.userID];
    [self.mainView  addSubview:self.profileViewController.view];
    [self.profileViewController.view setBounds:self.mainView.bounds];
   //self.profileViewController.profileDelegate = self;
    [self animateSlidingMenu:NO];
    
    [self updateSelectedMenu:self.profileButton];
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

-(void)animateRightSlidingMenu:(BOOL)moveLeft
{
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.4];
    
    CGRect oldFrame = self.rightSlideView.frame;
    CGRect oldFrameMain = self.slideView.frame;
    
    if (moveLeft)
    {
        self.rightSlideView.frame = CGRectMake(oldFrame.origin.x - 350, oldFrame.origin.y, oldFrame.size.width, oldFrame.size.height);
        self.slideView.frame = CGRectMake(oldFrameMain.origin.x-350,oldFrameMain.origin.y,oldFrameMain.size.width,oldFrameMain.size.height);
        
    }
    else
    {
        self.rightSlideView.frame = CGRectMake(oldFrame.origin.x + 350, oldFrame.origin.y, oldFrame.size.width, oldFrame.size.height);
        self.slideView.frame = CGRectMake(oldFrameMain.origin.x + 350,oldFrameMain.origin.y,oldFrameMain.size.width,oldFrameMain.size.height);
        
    }
    [UIView commitAnimations];
}


-(void) saveClicked
{
    [self onClickMeetings];
}

-(void) cancelClicked
{
    [self onClickMeetings];
}

-(void)viewScheduleClicked
{
    
}

-(void) goToTaskList
{
    [self onClickTasks];
}

-(void) onDeleteAccount
{
    [self onClickLogOut];
}

-(void)speechToText:(NSString *)hypothesis
{
    if (([hypothesis rangeOfString:@"GO"].location != NSNotFound) && ([hypothesis rangeOfString:@"HOME"].location != NSNotFound))
    {
        [self onClickHome];
    }
    else if (([hypothesis rangeOfString:@"GO"].location != NSNotFound) && ([hypothesis rangeOfString:@"MEETINGS"].location != NSNotFound))
    {
        [self onClickMeetings];
    }
    else if (([hypothesis rangeOfString:@"GO"].location != NSNotFound) && ([hypothesis rangeOfString:@"PROFILE"].location != NSNotFound))
    {
        [self onClickProfile];
    }
    else if (([hypothesis rangeOfString:@"GO"].location != NSNotFound) && ([hypothesis rangeOfString:@"TASK"].location != NSNotFound))
    {
        [self onClickTasks];
    }
    else if (([hypothesis rangeOfString:@"GO"].location != NSNotFound) && ([hypothesis rangeOfString:@"NOTES"].location != NSNotFound))
    {
        [self onClickNotes];
    }
    else if (([hypothesis rangeOfString:@"GO"].location != NSNotFound) && ([hypothesis rangeOfString:@"SETTINGS"].location != NSNotFound))
    {
        [self onClickSettings];
    }
    else if (([hypothesis rangeOfString:@"LOG"].location != NSNotFound) && ([hypothesis rangeOfString:@"OUT"].location != NSNotFound))
    {
        [self onClickLogOut];
    }
    [self.voiceCommand setTitle:@"Voice Command" forState:UIControlStateNormal];
}

-(void) detecting
{
    [self.voiceCommand setTitle:@"Detecting" forState:UIControlStateNormal];
}

-(void) speakNow
{
    [self.voiceCommand setTitle:@"Speak Now" forState:UIControlStateNormal];
}

-(void) loading
{
    [self.voiceCommand setTitle:@"Wait..." forState:UIControlStateNormal];
}

- (IBAction)startListening:(id)sender
{
    [self loading];
    [self.openEars startListening];
}

@end
