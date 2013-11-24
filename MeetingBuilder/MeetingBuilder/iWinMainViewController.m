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
@property (strong, nonatomic) iWinTaskListViewController *taskListViewController;
@property (strong, nonatomic) iWinNoteListViewController *noteViewController;
@property (strong, nonatomic) iWinViewAndAddNotesViewController *viewAddNoteViewController;
@property (strong, nonatomic) iWinViewAndChangeSettingsViewController *settingsViewController;
@property (strong, nonatomic) iWinViewProfileViewController *profileViewController;
@property (nonatomic) NSString *lmPath;
@property (nonatomic) NSString *dicPath;
@property (nonatomic) LanguageModelGenerator *lmGenerator;
@property (strong, nonatomic) PocketsphinxController *pocketsphinxController;
@property (strong, nonatomic) OpenEarsEventsObserver *openEarsEventsObserver;@property (strong, nonatomic) NSString *user;
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
    // Do any additional setup after loading the view from its nib.
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
    
    self.pocketsphinxController = [[PocketsphinxController alloc] init];
    self.openEarsEventsObserver = [[OpenEarsEventsObserver alloc] init];
    self.lmGenerator = [[LanguageModelGenerator alloc] init];
    
    NSArray *words = [NSArray arrayWithObjects:@"GO", @"MEETINGS", @"TASK", @"TO", nil];
    NSString *name = @"NameIWantForMyLanguageModelFiles";
    NSError *err = [self.lmGenerator generateLanguageModelFromArray:words withFilesNamed:name forAcousticModelAtPath:[AcousticModel pathToModel:@"AcousticModelEnglish"]];
    
    NSDictionary *languageGeneratorResults = nil;
    self.lmPath = nil;
    self.dicPath = nil;
	
    if([err code] == noErr) {
        
        languageGeneratorResults = [err userInfo];
		
        self.lmPath = [languageGeneratorResults objectForKey:@"LMPath"];
        self.dicPath = [languageGeneratorResults objectForKey:@"DictionaryPath"];
		
    } else {
        NSLog(@"Error: %@",[err localizedDescription]);
    }
    
    [self.openEarsEventsObserver setDelegate:self];
    
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
    self.profileViewController = [[iWinViewProfileViewController alloc] initWithNibName:@"iWinViewProfileViewController" bundle:nil];
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
        self.rightSlideView.frame = CGRectMake(oldFrame.origin.x - 200, oldFrame.origin.y, oldFrame.size.width, oldFrame.size.height);
        self.slideView.frame = CGRectMake(oldFrameMain.origin.x-200,oldFrameMain.origin.y,oldFrameMain.size.width,oldFrameMain.size.height);
    }
    else
    {
        self.rightSlideView.frame = CGRectMake(oldFrame.origin.x + 200, oldFrame.origin.y, oldFrame.size.width, oldFrame.size.height);
        self.slideView.frame = CGRectMake(oldFrameMain.origin.x + 200,oldFrameMain.origin.y,oldFrameMain.size.width,oldFrameMain.size.height);
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

-(void)recognizeSpeech
{
    [self.pocketsphinxController startListeningWithLanguageModelAtPath:self.lmPath dictionaryAtPath:self.dicPath acousticModelAtPath:[AcousticModel pathToModel:@"AcousticModelEnglish"] languageModelIsJSGF:NO];
}
- (void) pocketsphinxDidReceiveHypothesis:(NSString *)hypothesis recognitionScore:(NSString *)recognitionScore utteranceID:(NSString *)utteranceID {
    NSLog(@"The received hypothesis is %@ with a score of %@ and an ID of %@", hypothesis, recognitionScore, utteranceID);
    [self.pocketsphinxController stopListening];
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

- (void) pocketsphinxDidStartCalibration {
	NSLog(@"Pocketsphinx calibration has started.");
    [self.voiceCommand setTitle:@"Wait..." forState:UIControlStateNormal];
}

- (void) pocketsphinxDidCompleteCalibration {
	NSLog(@"Pocketsphinx calibration is complete.");
    [self.voiceCommand setTitle:@"Wait..." forState:UIControlStateNormal];
}

- (void) pocketsphinxDidStartListening {
	NSLog(@"Pocketsphinx is now listening.");
    [self.voiceCommand setTitle:@"Speak Now" forState:UIControlStateNormal];
}

- (void) pocketsphinxDidDetectSpeech {
	NSLog(@"Pocketsphinx has detected speech.");
    [self.voiceCommand setTitle:@"Detecting..." forState:UIControlStateNormal];
}

- (void) pocketsphinxDidDetectFinishedSpeech {
	NSLog(@"Pocketsphinx has detected a period of silence, concluding an utterance.");
    [self.voiceCommand setTitle:@"Detecting..." forState:UIControlStateNormal];
}

- (void) pocketsphinxDidStopListening {
	NSLog(@"Pocketsphinx has stopped listening.");
}

- (void) pocketsphinxDidSuspendRecognition {
	NSLog(@"Pocketsphinx has suspended recognition.");
}

- (void) pocketsphinxDidResumeRecognition {
	NSLog(@"Pocketsphinx has resumed recognition.");
}

- (void) pocketsphinxDidChangeLanguageModelToFile:(NSString *)newLanguageModelPathAsString andDictionary:(NSString *)newDictionaryPathAsString {
	NSLog(@"Pocketsphinx is now using the following language model: \n%@ and the following dictionary: %@",newLanguageModelPathAsString,newDictionaryPathAsString);
}

- (void) pocketSphinxContinuousSetupDidFail { // This can let you know that something went wrong with the recognition loop startup. Turn on OPENEARSLOGGING to learn why.
	NSLog(@"Setting up the continuous recognition loop has failed for some reason, please turn on OpenEarsLogging to learn more.");
}
- (void) testRecognitionCompleted {
	NSLog(@"A test file that was submitted for recognition is now complete.");
}

- (IBAction)startListening:(id)sender {
    [self recognizeSpeech];
}

@end
