//
//  iWinAddAndViewTaskViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/25/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinAddAndViewTaskViewController.h"
#import "iWinAddUsersViewController.h"
#import <QuartzCore/QuartzCore.h>
#import "Contact.h"
#import "Task.h"
#import "iWinAppDelegate.h"
#import "iWinBackEndUtility.h"
#import "Settings.h"
#import "iWinConstants.h"

@interface iWinAddAndViewTaskViewController ()
@property (nonatomic) NSInteger taskID;
@property (nonatomic) NSInteger userID;
@property (strong, nonatomic) Task *task;
@property (nonatomic) BOOL isEditing;
@property (strong, nonatomic) iWinAddUsersViewController *userViewController;
@property (nonatomic) NSDate *endDate;
@property (strong, nonatomic) NSManagedObjectContext *context;
@property (strong, nonatomic) UIPopoverController *popOverController;
@property (strong, nonatomic) OCCalendarViewController* ocCalVC;
@property (strong, nonatomic) UIDatePicker *enddatePicker;
@property (strong, nonatomic) NSDateFormatter *dateFormatter;
@property (strong, nonatomic) NSMutableArray *userList;
@property (nonatomic) iWinBackEndUtility *backendUtility;
@property (nonatomic) UIAlertView *deleteAlertView;

@end

@implementation iWinAddAndViewTaskViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger)userID withTaskID:(NSInteger)taskID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.userID = userID;
        self.taskID = taskID;
        self.isEditing = YES;
        if (taskID == -1){
            self.isEditing = NO;
        }
    }

    return self;
}

- (IBAction)onDeleteTask:(id)sender {
}

- (IBAction)onClickAddAssignees
{
    self.userViewController = [[iWinAddUsersViewController alloc] initWithNibName:@"iWinAddUsersViewController" bundle:nil withPageName:@"Task" inEditMode:self.isEditing];
    [self.userViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.userViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    self.userViewController.userDelegate = self;
    [self presentViewController:self.userViewController animated:YES completion:nil];
    self.userViewController.view.superview.bounds = CGRectMake(0,0,768,1003);
}

- (void)formatTime:(NSDate *)date
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"hh:mm a"];
    NSDate *currentDate = [NSDate date];
    self.endTimeLabel.text = [formatter stringFromDate:currentDate];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.backendUtility = [[iWinBackEndUtility alloc] init];
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    self.context = [appDelegate managedObjectContext];
    // Do any additional setup after loading the view from its nib.
    self.headerLabel.text = @"Add New Task";
    self.saveAndAddMoreButton.hidden = NO;
    self.userList = [[NSMutableArray alloc] init];
    
    self.saveAndAddMoreButton.hidden = NO;
    self.endDateLabel.userInteractionEnabled = YES;
    self.endTimeLabel.userInteractionEnabled = YES;
    self.deleteBtn.hidden = !self.isEditing;
    
    [self setGestureRecognizers];
    
    self.dateFormatter = [[NSDateFormatter alloc] init];
    [self.dateFormatter setDateFormat:@"MM/dd/yyyy"];
    if (self.isEditing)
    {
        [self initForExistingTask];
    } else {

        self.endDateLabel.text = [self.dateFormatter stringFromDate:[NSDate date]];
        self.endDate = [NSDate date];
        [self formatTime:[NSDate date]];
    }

    
}

-(void) initForExistingTask
{
    self.headerLabel.text = @"View/Modify Task";
    self.saveAndAddMoreButton.hidden = YES;
    
    
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Task" inManagedObjectContext:self.context];
    
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDesc];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"taskID = %d", self.taskID];
    [request setPredicate:predicate];
    
    NSError *error;
    NSArray *result = [self.context executeFetchRequest:request
                                                  error:&error];
    self.task = (Task*)[result objectAtIndex:0];
    
    NSString *url = [NSString stringWithFormat:@"%@/Task/%d", DATABASE_URL,self.taskID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    if (!deserializedDictionary) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Failure" message:@"Could not load your task" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        self.titleField.text = [deserializedDictionary objectForKey:@"title"];
        self.descriptionField.text = [deserializedDictionary objectForKey:@"description"];
        self.isCompleted.on = [[deserializedDictionary objectForKey:@"isCompleted"] boolValue];
        NSArray *endDateAndTime = [[deserializedDictionary objectForKey:@"deadline"]  componentsSeparatedByString:@" "];
        NSString *enddate = [endDateAndTime objectAtIndex:0];
        NSString *endtime = [NSString stringWithFormat:@"%@ %@", [endDateAndTime objectAtIndex:1], [endDateAndTime objectAtIndex:2]];
        self.endDateLabel.text = enddate;
        self.endTimeLabel.text = endtime;
        self.endDate = [self.dateFormatter dateFromString:enddate];
        NSString *assignee = [[deserializedDictionary objectForKey:@"assignedTo"] stringValue];
        [self.userList addObject:[self getContactForID:(NSString *)assignee]];
    }
    
//    self.titleField.text = self.task.title;
//    self.descriptionField.text = self.task.desc;
//    self.isCompleted.enabled = [self.task.isCompleted boolValue];
//    self.isCompleted.on = [self.task.isCompleted boolValue];
    
//    [self initDateTimeLabels];
//    [self initAttendees];
}

-(void) initAttendees
{
    NSString *assignee = [self.task.assignedTo stringValue];
    [self.userList addObject:[self getContactForID:(NSString *)assignee]];
}

-(Contact *)getContactForID:(NSString*)userID
{
    NSString *url = [NSString stringWithFormat:@"%@/User/%@", DATABASE_URL,userID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url] cachePolicy:NSURLRequestReloadIgnoringLocalAndRemoteCacheData timeoutInterval:30];
    [urlRequest setHTTPMethod:@"GET"];
    NSURLResponse * response = nil;
    NSError * error = nil;
    NSData * data = [NSURLConnection sendSynchronousRequest:urlRequest
                                          returningResponse:&response
                                                      error:&error];
    NSArray *jsonArray;
    if (error)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Tasks not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        NSError *jsonParsingError = nil;
        jsonArray = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers|NSJSONReadingAllowFragments error:&jsonParsingError];
    }
    if (jsonArray.count > 0)
    {

        
        NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Contact" inManagedObjectContext:self.context];

        NSDictionary* jsonObj = (NSDictionary*) jsonArray;
        Contact *c = [[Contact alloc] initWithEntity:entityDesc insertIntoManagedObjectContext:self.context];
        
        NSNumberFormatter * f = [[NSNumberFormatter alloc] init];
        [f setNumberStyle:NSNumberFormatterDecimalStyle];
        NSNumber * uid = [f numberFromString:userID];
        [c setUserID:uid];
        
        NSString *name = (NSString *)[jsonObj objectForKey:@"name"];
        [c setName:name];
        
        NSString *email = (NSString *)[jsonObj objectForKey:@"email"];
        [c setEmail:email];
        
        [self.addAssigneeButton setTitle:c.name forState:UIControlStateNormal];
        return c;
    }
    return nil;
}

-(void) initDateTimeLabels
{
    NSArray *endDateAndTime = [self.task.deadline componentsSeparatedByString:@" "];
    NSString *enddate = [endDateAndTime objectAtIndex:0];
    NSString *endtime = [NSString stringWithFormat:@"%@ %@", [endDateAndTime objectAtIndex:1], [endDateAndTime objectAtIndex:2]];
    
    self.endDateLabel.text = enddate;
    self.endTimeLabel.text = endtime;
    self.endDate = [self.dateFormatter dateFromString:enddate];
}

-(void) setGestureRecognizers
{
    UITapGestureRecognizer *tapEndDate = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(endDateClicked)];
    
    UITapGestureRecognizer *tapEndTime = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(endTimeClicked)];
    
    [self.endDateLabel addGestureRecognizer:tapEndDate];
    
    [self.endTimeLabel addGestureRecognizer:tapEndTime];
}

- (IBAction)onClickCancel
{
    [self dismissViewControllerAnimated:YES completion:nil];
}

-(void)selectedUsers:(NSMutableArray *)userList
{
    self.userList = userList;
    if ([self.userList count] > 0){
        Contact *c = (Contact *) self.userList[0];
        [self.addAssigneeButton setTitle:c.name forState:UIControlStateNormal];
    }
}

- (IBAction)onClickSave
{
    Contact *c = (Contact *) self.userList[0];
    if (self.isEditing)
    {
        [self updateTask:@"title" :self.titleField.text];
        [self updateTask:@"isCompleted" :@"False"];
        [self updateTask:@"description" :self.descriptionField.text];
        [self updateTask:@"deadline" :[NSString stringWithFormat:@"%@ %@", self.endDateLabel.text, self.endTimeLabel.text]];
        [self updateTask:@"assignedTo" :[c.userID stringValue]];
        
    }else
    {
        [self saveNewTask];
    }

    [self scheduleNotification];
    [self.viewTaskDelegate refreshTaskList];
    
}

-(void) scheduleNotification
{
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Settings" inManagedObjectContext:self.context];
    
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDesc];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"userID = %d", self.userID];
    [request setPredicate:predicate];
    
    NSError *error;
    NSArray *result = [self.context executeFetchRequest:request
                                                  error:&error];
    
    Settings *settings = (Settings *)[result objectAtIndex:0];
    if ([settings.shouldNotify boolValue])
    {
        [self removeOldNotifications];
        
        NSString *meetingStartDate = [NSString stringWithFormat:@"%@ %@", self.endDateLabel.text, self.endTimeLabel.text];
        NSDateFormatter *formatter = [[NSDateFormatter alloc]init];
        [formatter setDateFormat:@"MM/dd/yyyy hh:mm a"];
        NSDate *dateTimeOfMeeting = [formatter dateFromString:meetingStartDate];
        
        UILocalNotification* localNotification = [[UILocalNotification alloc] init];
        localNotification.timeZone = [NSTimeZone defaultTimeZone];
        
        NSNumber *taskID = [NSNumber numberWithInt:self.taskID];
        NSArray *key = [[NSArray alloc] initWithObjects:@"taskID", nil];
        NSArray *object = [[NSArray alloc] initWithObjects:taskID, nil];
        NSDictionary *userInfo = [[NSDictionary alloc] initWithObjects:object forKeys:key];
        localNotification.userInfo = userInfo;
        
        NSDateComponents *dateComponents = [[NSDateComponents alloc] init];
        NSDate *fireDate;
        switch ([settings.whenToNotify integerValue]) {
            case 0:
                fireDate = dateTimeOfMeeting;
                localNotification.alertBody = [NSString stringWithFormat:@"%@ Task due now", self.titleField.text];
                break;
            case 1:
                [dateComponents setMinute:-5];
                fireDate = [[NSCalendar currentCalendar] dateByAddingComponents:dateComponents toDate:dateTimeOfMeeting options:0];
                localNotification.alertBody = [NSString stringWithFormat:@"%@ Task due in 5 minutes", self.titleField.text];
                break;
            case 2:
                [dateComponents setMinute:-15];
                fireDate = [[NSCalendar currentCalendar] dateByAddingComponents:dateComponents toDate:dateTimeOfMeeting options:0];
                localNotification.alertBody = [NSString stringWithFormat:@"%@ Task due in 15 minutes", self.titleField.text];
                break;
            case 3:
                [dateComponents setMinute:-30];
                fireDate = [[NSCalendar currentCalendar] dateByAddingComponents:dateComponents toDate:dateTimeOfMeeting options:0];
                localNotification.alertBody = [NSString stringWithFormat:@"%@ Task due in 30 minutes", self.titleField.text];
                break;
            case 4:
                [dateComponents setHour:-1];
                fireDate = [[NSCalendar currentCalendar] dateByAddingComponents:dateComponents toDate:dateTimeOfMeeting options:0];
                localNotification.alertBody = [NSString stringWithFormat:@"%@ Task due in 1 hour", self.titleField.text];
                break;
            case 5:
                [dateComponents setHour:-2];
                fireDate = [[NSCalendar currentCalendar] dateByAddingComponents:dateComponents toDate:dateTimeOfMeeting options:0];
                localNotification.alertBody = [NSString stringWithFormat:@"%@ Task due in 2 hours", self.titleField.text];
                break;
            case 6:
                [dateComponents setDay:-1];
                fireDate = [[NSCalendar currentCalendar] dateByAddingComponents:dateComponents toDate:dateTimeOfMeeting options:0];
                localNotification.alertBody = [NSString stringWithFormat:@"%@ Task due in 1 day", self.titleField.text];
                break;
            case 7:
                [dateComponents setDay:-2];
                fireDate = [[NSCalendar currentCalendar] dateByAddingComponents:dateComponents toDate:dateTimeOfMeeting options:0];
                localNotification.alertBody = [NSString stringWithFormat:@"%@ Task due in 2 days", self.titleField.text];
                break;
            default:
                break;
        }
        localNotification.fireDate = fireDate;
        [[NSNotificationCenter defaultCenter] postNotificationName:@"reloadData" object:self];
        localNotification.applicationIconBadgeNumber = [[UIApplication sharedApplication] applicationIconBadgeNumber] + 1;
        [[UIApplication sharedApplication] scheduleLocalNotification:localNotification];
    }
}
-(void) removeOldNotifications
{
    NSArray *notifications = [[UIApplication sharedApplication] scheduledLocalNotifications];
    for (int i=0; i<[notifications count]; i++)
    {
        UILocalNotification* notification = [notifications objectAtIndex:i];
        NSDictionary *userInfo = notification.userInfo;
        NSInteger taskID=[[userInfo valueForKey:@"taskID"] integerValue];
        if (taskID == self.taskID)
        {
            [[UIApplication sharedApplication] cancelLocalNotification:notification];
            break;
        }
    }
}


- (void)taskCreationAlert:(BOOL)error
{
    NSString *title;
    NSString *message;
    title = @"Error";
    message = @"Failed to save task";
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:title message:message delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
    [alert show];
}

- (void) updateTask: (NSString*)field : (NSString *)value{
    NSArray *keys = [NSArray arrayWithObjects:@"taskID", @"field", @"value", nil];
    NSArray *objects = [NSArray arrayWithObjects:[NSNumber numberWithInt:self.taskID], field, value, nil];
    
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    NSData *jsonData;
    NSString *jsonString;
    
    
    if ([NSJSONSerialization isValidJSONObject:jsonDictionary])
    {
        jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary options:0 error:nil];
        jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    NSString *url = [NSString stringWithFormat:@"%@/Task/", DATABASE_URL];
    
    NSMutableURLRequest * urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
    [urlRequest setHTTPMethod:@"PUT"];
    [urlRequest setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    [urlRequest setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [urlRequest setValue:[NSString stringWithFormat:@"%d", [jsonData length]] forHTTPHeaderField:@"Content-length"];
    [urlRequest setHTTPBody:jsonData];
    NSURLResponse * response = nil;
    NSError * error = nil;
    NSData * data =[NSURLConnection sendSynchronousRequest:urlRequest
                                         returningResponse:&response
                                                     error:&error];
    if (error) {
        [self taskCreationAlert:YES];
    }
}

- (void) saveNewTask{
    Contact *c = (Contact *) self.userList[0];
    
    NSArray *keys = [NSArray arrayWithObjects:
                     @"title",
                     @"isCompleted",
                     @"description",
                     @"deadline",
                     @"dateCreated",
                     @"dateAssigned",
                     @"completionCriteria",
                     @"assignedTo",
                     @"assignedFrom",
                     @"createdBy",
                     nil];
    
    NSArray *objects = [NSArray arrayWithObjects:
                        self.titleField.text,
                        @"False",
                        self.descriptionField.text,
                        [NSString stringWithFormat:@"%@ %@", self.endDateLabel.text, self.endTimeLabel.text],
                        [NSString stringWithFormat:@"%@ %@", self.endDateLabel.text, self.endTimeLabel.text],
                        [NSString stringWithFormat:@"%@ %@", self.endDateLabel.text, self.endTimeLabel.text],
                        @"nothing",
                        [c.userID stringValue],
                        [[NSNumber numberWithInt:self.userID] stringValue],
                        [[NSNumber numberWithInt:self.userID] stringValue],
                        nil];
    
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    NSData *jsonData;
    NSString *jsonString;
    
    if ([NSJSONSerialization isValidJSONObject:jsonDictionary])
    {
        jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary options:0 error:nil];
        jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    NSString *url = [NSString stringWithFormat:@"%@/Task/", DATABASE_URL];
    
    NSMutableURLRequest * urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
    [urlRequest setHTTPMethod:@"POST"];
    [urlRequest setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    [urlRequest setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [urlRequest setValue:[NSString stringWithFormat:@"%d", [jsonData length]] forHTTPHeaderField:@"Content-length"];
    [urlRequest setHTTPBody:jsonData];
    NSURLResponse * response = nil;
    NSError * error = nil;
    NSData * data =[NSURLConnection sendSynchronousRequest:urlRequest
                                         returningResponse:&response
                                                     error:&error];
    NSError *jsonParsingError = nil;
    NSDictionary *deserializedDictionary = (NSDictionary *)[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingAllowFragments|NSJSONReadingMutableContainers error:&jsonParsingError];
    self.taskID = [[deserializedDictionary objectForKey:@"taskID"] integerValue];
}

- (IBAction)onClickSaveAndAddMore
{
    [self saveNewTask];
    //save and clear textfields
    self.headerLabel.text = @"Add New Task";
    self.saveAndAddMoreButton.hidden = NO;
    self.titleField.text = @"";
    self.dueField.text = @"";
    self.descriptionField.text = @"";
    self.createdByField.text = @"";
}


- (void)endDateClicked
{
    self.ocCalVC = [[OCCalendarViewController alloc] initAtPoint:CGPointMake(self.endDateLabel.frame.origin.x+84, self.endDateLabel.frame.origin.y+32) inView:self.view];
    self.ocCalVC.selectionMode = OCSelectionSingleDate;
    self.ocCalVC.delegate = self;
    [self.ocCalVC setStartDate:self.endDate];
    [self.ocCalVC setEndDate:self.endDate];
    [self.view addSubview:self.ocCalVC.view];
}

-(void) endTimeClicked
{
    UIViewController* popoverContent = [[UIViewController alloc] init]; //ViewController
    
    UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(220, 10, 75, 50)];
    [button setTitle:@"Save" forState:UIControlStateNormal];
    [button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [button addTarget:self action:@selector(saveEndTime) forControlEvents:UIControlEventTouchUpInside];
    
    UIView *popoverView = [[UIView alloc] init];   //view
    [popoverView addSubview:button];
    
    //UIDatePicker *enddatePicker=[[UIDatePicker alloc]init];//Date picker
    self.enddatePicker=[[UIDatePicker alloc]init];//Date picker
    self.enddatePicker.frame=CGRectMake(0,30,320, 216);
    self.enddatePicker.datePickerMode = UIDatePickerModeTime;
    [self.enddatePicker setMinuteInterval:5];
    //[datePicker addTarget:self action:@selector(Result) forControlEvents:UIControlEventValueChanged];
    [popoverView addSubview:self.enddatePicker];
    
    popoverContent.view = popoverView;
    self.popOverController = [[UIPopoverController alloc] initWithContentViewController:popoverContent];
    //popoverController.delegate=self;
    
    [self.popOverController setPopoverContentSize:CGSizeMake(320, 250) animated:NO];
    [self.popOverController presentPopoverFromRect:CGRectMake(self.endTimeLabel.frame.origin.x, self.endTimeLabel.frame.origin.y+2, self.endTimeLabel.frame.size.width, self.endTimeLabel.frame.size.height)  inView:self.view permittedArrowDirections:UIPopoverArrowDirectionUp animated:YES];
}


-(void) saveEndTime
{
    NSDateFormatter *outputFormatter = [[NSDateFormatter alloc] init];
    [outputFormatter setDateFormat:@"hh:mm a"];
    
    [self.endTimeLabel setText:[outputFormatter stringFromDate:self.enddatePicker.date]];
    [self.popOverController dismissPopoverAnimated:YES];
    
    
}

- (void)completedWithDate:(NSDate *)selectedDate
{
    if ([selectedDate compare:[NSDate date]] == NSOrderedDescending)
    {
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"MM/dd/yyyy"];
        self.endDate = selectedDate;
        self.endDateLabel.text = [dateFormatter stringFromDate:selectedDate];

    }
    [self.ocCalVC.view removeFromSuperview];
}

-(void)completedWithNoSelection
{
    [self.ocCalVC.view removeFromSuperview];
}

- (IBAction)onDeleteTask
{
    self.deleteAlertView = [[UIAlertView alloc] initWithTitle:@"Confirm Delete" message:@"Are you sure you want to delete this task?" delegate:self cancelButtonTitle:@"No, just kidding!" otherButtonTitles:@"Yes, please", nil];
    [self.deleteAlertView show];
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 1)
    {
        //Perform deletion
        NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Task/%d", self.taskID];
        NSError * error = [self.backendUtility deleteRequestForUrl:url];
        if (!error)
        {
            [self.viewTaskDelegate refreshTaskList];
        }
    }
}

@end
