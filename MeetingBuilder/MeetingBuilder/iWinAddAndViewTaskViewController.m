//
//  iWinAddAndViewTaskViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/25/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinAddAndViewTaskViewController.h"
#import "iWinAddUsersViewController.h"
#import "iWinScheduleViewMeetingViewController.h"
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

const int ADD_USERS_VC_X_POS = 0;
const int ADD_USERS_VC_Y_POS = 0;
const int ADD_USERS_VC_WIDTH = 768;
const int ADD_USERS_VC_HEIGHT = 1003;
NSString* const ADD_NEW_TASK_TITLE = @"Add New Task";
NSString* const VIEW_TASK_TITLE = @"View/Modify Task";

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
    self.userViewController = [[iWinAddUsersViewController alloc] initWithNibName:ADD_USERS_NIB bundle:nil withPageName:TASK_ENTITY inEditMode:self.isEditing];
    [self.userViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.userViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    self.userViewController.userDelegate = self;
    [self presentViewController:self.userViewController animated:YES completion:nil];
    self.userViewController.view.superview.bounds = CGRectMake(ADD_USERS_VC_X_POS, ADD_USERS_VC_Y_POS, ADD_USERS_VC_WIDTH, ADD_USERS_VC_HEIGHT);
}

- (void)formatTime:(NSDate *)date
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:TIME_FORMAT];
    NSDate *currentDate = [NSDate date];
    self.endTimeLabel.text = [formatter stringFromDate:currentDate];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.backendUtility = [[iWinBackEndUtility alloc] init];
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    self.context = [appDelegate managedObjectContext];

    self.headerLabel.text = ADD_NEW_TASK_TITLE;
    self.saveAndAddMoreButton.hidden = NO;
    self.userList = [[NSMutableArray alloc] init];
    
    self.saveAndAddMoreButton.hidden = NO;
    self.endDateLabel.userInteractionEnabled = YES;
    self.endTimeLabel.userInteractionEnabled = YES;
    self.deleteBtn.hidden = !self.isEditing;
    
    [self setGestureRecognizers];
    
    self.dateFormatter = [[NSDateFormatter alloc] init];
    [self.dateFormatter setDateFormat:DATE_FORMAT];
    if (self.isEditing)
    {
        [self initForExistingTask];
    } else {

        self.endDateLabel.text = [self.dateFormatter stringFromDate:[NSDate date]];
        self.endDate = [NSDate date];
        [self formatTime:[NSDate date]];
    }

    self.descriptionField.layer.borderColor = [[UIColor lightGrayColor] CGColor];
    self.descriptionField.layer.borderWidth = FIELD_BORDER_WIDTH;
    self.descriptionField.layer.cornerRadius = FIELD_CORNER_RADIUS;
    
}

-(void) initForExistingTask
{
    self.headerLabel.text = VIEW_TASK_TITLE;
    self.saveAndAddMoreButton.hidden = YES;
    
    
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:TASK_ENTITY inManagedObjectContext:self.context];
    
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDesc];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"taskID = %d", self.taskID];
    [request setPredicate:predicate];
    
    NSError *error;
    NSArray *result = [self.context executeFetchRequest:request
                                                  error:&error];
    self.task = (Task*)[result objectAtIndex:0];
    
    NSString *url = [NSString stringWithFormat:TASK_URL, DATABASE_URL,self.taskID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    if (!deserializedDictionary) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:ERROR_MESSAGE message:TASK_NOT_FOUND_MESSAGE delegate:self cancelButtonTitle:OK_BUTTON otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        NSDate* endDateAndTime = [NSDate dateWithTimeIntervalSince1970:[[deserializedDictionary objectForKey:DEADLINE_KEY] doubleValue]];
        
        
        NSString *enddate = [iWinScheduleViewMeetingViewController getStringDateFromDate:endDateAndTime];
        NSString *endtime = [iWinScheduleViewMeetingViewController getStringTimeFromDate:endDateAndTime];
        
        self.endDateLabel.text = enddate;
        self.endTimeLabel.text = endtime;
        
        self.endDate = [self.dateFormatter dateFromString:enddate];
        self.titleField.text = [deserializedDictionary objectForKey:TITLE_KEY];
        self.isCompleted.on = [[deserializedDictionary objectForKey:IS_COMPLETED_KEY] boolValue];
        self.endDate = [self.dateFormatter dateFromString:enddate];
        NSInteger assignee = [[deserializedDictionary objectForKey:ASSIGNED_TO_KEY] integerValue];
        [self.userList addObject:[self getContactForID:assignee]];
    }
}

-(void) initAttendees
{
    [self.userList addObject:[self getContactForID:[self.task.assignedTo integerValue]]];
}

-(Contact *)getContactForID:(NSInteger)userID
{
    NSString *url = [NSString stringWithFormat:USER_ID_URL, DATABASE_URL,userID];
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
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:ERROR_MESSAGE message:TASK_NOT_FOUND_MESSAGE delegate:self cancelButtonTitle:OK_BUTTON otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        NSError *jsonParsingError = nil;
        jsonArray = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers|NSJSONReadingAllowFragments error:&jsonParsingError];
    }
    if (jsonArray.count > 0)
    {

        
        NSEntityDescription *entityDesc = [NSEntityDescription entityForName:CONTACT_ENTITY inManagedObjectContext:self.context];

        NSDictionary* jsonObj = (NSDictionary*) jsonArray;
        Contact *c = [[Contact alloc] initWithEntity:entityDesc insertIntoManagedObjectContext:self.context];
        
        [c setUserID:[NSNumber numberWithInt:userID]];
        
        NSString *name = (NSString *)[jsonObj objectForKey:NAME_KEY];
        [c setName:name];
        
        NSString *email = (NSString *)[jsonObj objectForKey:EMAIL_KEY];
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
    NSString *endtime = [NSString stringWithFormat:DATE_TIME_STRING_FORMAT, [endDateAndTime objectAtIndex:1], [endDateAndTime objectAtIndex:2]];
    
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
    Contact *c;
    if (self.userList.count == 0){
        c = [self getContactForID:self.userID];
    }else {
        c = (Contact *) self.userList[0];
    }
    if (self.isEditing)
    {
        NSString *endEpochString = [self makeEpochStringFromDateAndTimeStrings:self.endDateLabel.text timeString:self.endTimeLabel.text];
        [self updateTask:TITLE_KEY :self.titleField.text];
        [self updateTask:IS_COMPLETED_KEY :[NSString stringWithFormat:@"%hhd", self.isCompleted.on]];
        [self updateTask:DESCRIPTION_KEY :self.descriptionField.text];
        [self updateTask:DEADLINE_KEY :endEpochString];
        [self updateTask:ASSIGNED_TO_KEY :[c.userID stringValue]];
        
    }else
    {
        [self saveNewTask];
    }
    [self.viewTaskDelegate refreshTaskList];
    
}



- (void)taskCreationAlert:(BOOL)error
{
    NSString *title;
    NSString *message;
    title = ERROR_MESSAGE;
    message = TASK_NOT_FOUND_MESSAGE;
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:title message:message delegate:self cancelButtonTitle:OK_BUTTON otherButtonTitles: nil];
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
    [NSURLConnection sendSynchronousRequest:urlRequest
                                         returningResponse:&response
                                                     error:&error];
    if (error) {
        [self taskCreationAlert:YES];
    }
}

- (void) saveNewTask{
    Contact *c;
    if (self.userList.count == 0){
        c = [self getContactForID:self.userID];
    }else {
        c = (Contact *) self.userList[0];
    }
    
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
    
    NSString *endEpochString = [self makeEpochStringFromDateAndTimeStrings:self.endDateLabel.text timeString:self.endTimeLabel.text];
    NSArray *objects = [NSArray arrayWithObjects:
                        self.titleField.text,
                        @"False",
                        self.descriptionField.text,
                        endEpochString,
                        endEpochString,
                        endEpochString,
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
    NSDictionary *deserializedDictionary = [self.backendUtility postRequestForUrl:url withDictionary:jsonDictionary];
    self.taskID = [[deserializedDictionary objectForKey:@"taskID"] integerValue];
}

-(NSString *) makeEpochStringFromDateAndTimeStrings:(NSString*)dateString timeString:(NSString *)timeString
{
    NSTimeInterval interval = [[self makeDateFromText:dateString timeText:timeString] timeIntervalSince1970];
    return [NSString stringWithFormat:@"%f", interval];
}
-(NSDate *)makeDateFromText:(NSString *)dateText timeText:(NSString *)timeText
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    dateFormatter.dateFormat = @"MM/dd/yyyy h:mm a";
    NSString *dateTime = [NSString stringWithFormat:@"%@ %@", dateText, timeText];
    return [dateFormatter dateFromString:dateTime];
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
    UIViewController* popoverContent = [[UIViewController alloc] init];
    
    UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(220, 10, 75, 50)];
    [button setTitle:@"Save" forState:UIControlStateNormal];
    [button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [button addTarget:self action:@selector(saveEndTime) forControlEvents:UIControlEventTouchUpInside];
    
    UIView *popoverView = [[UIView alloc] init];
    [popoverView addSubview:button];
    
    
    self.enddatePicker=[[UIDatePicker alloc]init];
    self.enddatePicker.frame=CGRectMake(0,30,320, 216);
    self.enddatePicker.datePickerMode = UIDatePickerModeTime;
    [self.enddatePicker setMinuteInterval:5];
    
    [popoverView addSubview:self.enddatePicker];
    popoverContent.view = popoverView;
    self.popOverController = [[UIPopoverController alloc] initWithContentViewController:popoverContent];
    
    [self.popOverController setPopoverContentSize:CGSizeMake(320, 250) animated:NO];
    [self.popOverController presentPopoverFromRect:CGRectMake(self.endTimeLabel.frame.origin.x, self.endTimeLabel.frame.origin.y+2, self.endTimeLabel.frame.size.width, self.endTimeLabel.frame.size.height)  inView:self.view permittedArrowDirections:UIPopoverArrowDirectionUp animated:YES];
}


-(void) saveEndTime
{
    NSDateFormatter *outputFormatter = [[NSDateFormatter alloc] init];
    [outputFormatter setDateFormat:TIME_FORMAT];
    
    if ([[NSDate date] compare:self.enddatePicker.date] == NSOrderedAscending) {
        [self.endTimeLabel setText:[outputFormatter stringFromDate:self.enddatePicker.date]];
    }
    [self.popOverController dismissPopoverAnimated:YES];
    
    
}

- (void)completedWithDate:(NSDate *)selectedDate
{
    if ([selectedDate compare:[NSDate date]] == NSOrderedDescending)
    {
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:DATE_FORMAT];
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
    self.deleteAlertView = [[UIAlertView alloc] initWithTitle:CONFIRM_DELETE_TITLE message:@"Are you sure you want to delete this task?" delegate:self cancelButtonTitle:NO_DELETE_OPTION otherButtonTitles:YES_DELETE_OPTION, nil];
    [self.deleteAlertView show];
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 1)
    {
        NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Task/%d", self.taskID];
        NSError * error = [self.backendUtility deleteRequestForUrl:url];
        if (!error)
        {
            [self.viewTaskDelegate refreshTaskList];
        }
    }
}

@end
