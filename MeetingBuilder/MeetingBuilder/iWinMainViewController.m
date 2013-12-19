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
//#import <TapkuLibrary/TKCalendarDayView.h>
//#import <TapkuLibrary/TKCalendarDayEventView.h>
#import "iWinPopulateDatabase.h"
#import "NSDate+TKCategory.h"
#import "NSDate+CalendarGrid.h"


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
    self.scheduleButton.hidden = NO;
}

-(void) disableSliding
{
    [self.slideView removeGestureRecognizer:self.swipeleft];
    [self.slideView removeGestureRecognizer:self.swiperight];
    self.menuButton.hidden = YES;
    self.scheduleButton.hidden = YES;
}

-(void) resetSliding
{
    self.movedView = NO;
    self.movedRightView = NO;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.voiceCommand.hidden = YES;
    iWinPopulateDatabase *db = [[iWinPopulateDatabase alloc] init];
    [db populateContacts];
    
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
    
    self.menuButton.hidden = YES;
    self.menuButton.layer.cornerRadius = 7;
    self.menuButton.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    self.menuButton.layer.borderWidth = 1.0f;
    
    self.scheduleButton.hidden = YES;
    self.scheduleButton.layer.cornerRadius = 7;
    self.scheduleButton.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    self.scheduleButton.layer.borderWidth = 1.0f;
    
    self.openEars = [[iWinOpenEarsModel alloc] init];
    self.openEars.openEarsDelegate = self;
    [self.openEars initialize];
    
    
    self.data = @[
                  @[@"Meeting with five random dudes", @"Five Guys", @5, @0, @5, @30],
                  @[@"Unlimited bread rolls got me sprung", @"Olive Garden", @7, @0, @12, @0],
                  @[@"Appointment", @"Dennys", @15, @0, @18, @0],
                  @[@"Hamburger Bliss", @"Wendys", @15, @0, @18, @0],
                  @[@"Fishy Fishy Fishfelayyyyyyyy", @"McDonalds", @5, @30, @6, @0],
                  @[@"Turkey Time...... oh wait", @"Chick-fela", @14, @0, @19, @0],
                  @[@"Greet the king at the castle", @"Burger King", @19, @30, @30, @0]];
    
    self.dayView = [[TKCalendarDayView alloc] initWithFrame:self.rightSlideView.bounds];
	self.dayView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
	self.dayView.delegate = self;
	self.dayView.dataSource = self;
	[self.rightSlideView addSubview:self.dayView];
}
- (void) viewDidUnload {
	self.dayView = nil;
}

#pragma mark TKCalendarDayViewDelegate
- (NSArray *) calendarDayTimelineView:(TKCalendarDayView*)calendarDayTimeline eventsForDate:(NSDate *)eventDate{
    if([eventDate compare:[NSDate dateWithTimeIntervalSinceNow:-24*60*60]] == NSOrderedAscending) return @[];
	if([eventDate compare:[NSDate dateWithTimeIntervalSinceNow:24*60*60]] == NSOrderedDescending) return @[];
    
	NSDateComponents *info = [[NSDate date] dateComponentsWithTimeZone:calendarDayTimeline.timeZone];
	info.second = 0;
	NSMutableArray *ret = [NSMutableArray array];
	
	for(NSArray *ar in self.data){
		
		TKCalendarDayEventView *event = [calendarDayTimeline dequeueReusableEventView];
		if(event == nil) event = [TKCalendarDayEventView eventView];
        
		event.identifier = nil;
		event.titleLabel.text = ar[0];
		event.locationLabel.text = ar[1];
		
		info.hour = [ar[2] intValue];
		info.minute = [ar[3] intValue];
		event.startDate = [NSDate dateWithDateComponents:info];
		
		info.hour = [ar[4] intValue];
		info.minute = [ar[5] intValue];
		event.endDate = [NSDate dateWithDateComponents:info];
        
		[ret addObject:event];
		
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

-(void) login:(NSString *)email
{
    [self removeSubViews];
    
    [self enableSliding];
    
    self.user = email;
    self.homeScreenViewController = [[iWinHomeScreenViewController alloc] initWithNibName:@"iWinHomeScreenViewController" bundle:nil];
    [self.mainView  addSubview:self.homeScreenViewController.view];
    [self.homeScreenViewController.view setBounds:self.mainView.bounds];
    
    [self updateSelectedMenu:self.homeButton];
    self.openEars.openEarsDelegate = self;
}

-(void)swipeleft:(UISwipeGestureRecognizer*)gestureRecognizer
{
    if (self.movedView)
    {
        [self animateSlidingMenu:NO];
        self.movedView = !self.movedView;
    }
    else if (!self.movedRightView && !self.movedView)
    {
        [self animateRightSlidingMenu:YES];
        self.movedRightView = !self.movedRightView;
    }
    
}

-(void)swiperight:(UISwipeGestureRecognizer*)gestureRecognizer
{
    if (!self.movedView && !self.movedRightView)
    {
        [self animateSlidingMenu:YES];
        self.movedView = !self.movedView;
    }
    else if (self.movedRightView)
    {
        [self animateRightSlidingMenu:NO];
        self.movedRightView = !self.movedRightView;
    }
}


-(void) joinUs
{
    [self removeSubViews];
    [self.mainView  addSubview:self.registerViewController.view];
    [self.registerViewController.view setBounds:self.mainView.bounds];
}

-(void) onRegister:(NSString *)email
{
    [self removeSubViews];
    [self enableSliding];
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

- (IBAction)onClickSchedule
{
    if (!self.movedRightView)
    {
        [self animateRightSlidingMenu:YES];
    }
    else
    {
        [self animateRightSlidingMenu:NO];
    }
    self.movedRightView = !self.movedRightView;
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
    self.meetingListViewController = [[iWinMeetingViewController alloc] initWithNibName:@"iWinMeetingViewController" bundle:nil withEmail:self.user];
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
    
    [self updateSelectedMenu:self.settingsButton];
    [self resetSliding];
}

- (IBAction)onClickProfile{
    [self removeSubViews];
    [self enableSliding];
    self.profileViewController = [[iWinViewProfileViewController alloc] initWithNibName:@"iWinViewProfileViewController" bundle:nil withID: self.userID];
    [self.mainView  addSubview:self.profileViewController.view];
    [self.profileViewController.view setBounds:self.mainView.bounds];
    self.profileViewController.profileDelegate = self;
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


-(void)viewScheduleClicked
{
    
}

-(void) goToTaskList
{
    [self onClickTasks];
}

-(void) onClickSaveSettings{
    
}
-(void) onclickCancelSettings{
    
}

-(void)speechToText:(NSString *)hypothesis
{
    if (([hypothesis rangeOfString:@"GO"].location != NSNotFound) && ([hypothesis rangeOfString:@"MEETINGS"].location != NSNotFound))
    {
        [self onClickMeetings];
    }
    else if (([hypothesis rangeOfString:@"GO"].location != NSNotFound) && ([hypothesis rangeOfString:@"TASK"].location != NSNotFound))
    {
        [self onClickTasks];
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
    [self.openEars startListening];
}

@end
