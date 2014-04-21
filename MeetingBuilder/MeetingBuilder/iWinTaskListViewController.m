//
//  iWinTaskListViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinTaskListViewController.h"
#import "iWinAddAndViewTaskViewController.h"
#import "iWinScheduleViewMeetingViewController.h"
#import <QuartzCore/QuartzCore.h>
#import "iWinAppDelegate.h"
#import "iWinBackEndUtility.h"
#import "iWinConstants.h"

@interface iWinTaskListViewController ()
@property (strong, nonatomic) NSMutableArray *itemList;
@property (strong, nonatomic) NSMutableArray *itemDetail;
@property (strong, nonatomic) NSMutableArray *itemCompleted;
@property (nonatomic) NSInteger userID;
@property (strong, nonatomic) iWinAddAndViewTaskViewController *addViewTaskViewController;
@property (nonatomic) NSInteger selectedTask;
@property (strong, nonatomic) NSMutableArray *taskIDs;
@property (nonatomic) iWinBackEndUtility *backendUtility;
@end

NSString* const USER_TASK_URL = @"%@/User/Tasks/%d";
NSString* const TASK_NOT_FOUND_MESSAGE = @"Tasks not found";
NSString* const TASKS_KEY = @"tasks";
NSString* const ASSIGEND_TO_STRING = @"ASSIGNED_TO";
NSString* const DEADLINE_KEY = @"deadline";
NSString* const TASK_ENTITY = @"Task";
NSString* const TASK_ID_KEY = @"taskID";
NSString* const DATE_CREATED_KEY = @"dateCreated";
NSString* const DATE_ASSIGNED_KEY = @"dateAssigned";
NSString* const ASSIGNED_TO_KEY = @"assignedTo";
NSString* const ASSIGNED_FROM_KEY = @"assignedFrom";
NSString* const CREATED_BY_KEY = @"createdBy";
NSString* const TASK_CELL_ID = @"TaskCell";
NSString* const DELETE_TASK_MESSAGE = @"Are you sure you want to delete this task?";

@implementation iWinTaskListViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil userID:(NSInteger) userID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    self.userID = userID;
    if (self) {
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.itemList = [[NSMutableArray alloc] init];
    self.itemDetail = [[NSMutableArray alloc] init];
    self.itemCompleted = [[NSMutableArray alloc] init];
    self.taskIDs = [[NSMutableArray alloc] init];
    self.backendUtility = [[iWinBackEndUtility alloc] init];
    
    [self populateTaskList];
    
}

-(void)refreshTaskList
{
    [self.addViewTaskViewController dismissViewControllerAnimated:YES completion:nil];
    self.itemList = [[NSMutableArray alloc] init];
    self.itemDetail = [[NSMutableArray alloc] init];
    self.itemCompleted = [[NSMutableArray alloc] init];
    self.taskIDs = [[NSMutableArray alloc] init];

    [self populateTaskList];
    [self.taskListTable reloadData];
}

-(void)populateTaskList
{
    NSString *url = [NSString stringWithFormat:USER_TASK_URL, DATABASE_URL,self.userID];
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
        NSDictionary *deserializedDictionary = (NSDictionary *)[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers|NSJSONReadingAllowFragments error:&jsonParsingError];
        jsonArray = [deserializedDictionary objectForKey:TASKS_KEY];
    }
    if (jsonArray.count > 0)
    {
        for (NSDictionary* tasks in jsonArray)
        {
            NSString *relationship = [tasks objectForKey:TYPE_KEY];
            if ([relationship isEqual:ASSIGEND_TO_STRING]){
                [self.itemList addObject:[tasks objectForKey:TITLE_KEY]];
                [self.taskIDs addObject:[tasks objectForKey:ID_KEY]];
            }
        }
        [self populateTaskDetails];
    }
    
}

- (NSDictionary *)getDeadLine:(NSData *)data
{
    NSError *jsonParsingError = nil;
    NSDictionary *deserializedDictionary = (NSDictionary *)[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers|NSJSONReadingAllowFragments error:&jsonParsingError];
    NSDate *dateTime = [NSDate dateWithTimeIntervalSince1970:[[deserializedDictionary objectForKey:DEADLINE_KEY] doubleValue]];
    NSString *dateTimeString = [iWinScheduleViewMeetingViewController getStringDateTimeFromDate:dateTime];
    [self.itemDetail addObject:dateTimeString];
    return deserializedDictionary;
}

- (void)setTaskInfo:(NSDictionary *)deserializedDictionary
{
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    
    NSManagedObjectContext *context = [appDelegate managedObjectContext];
    
    NSManagedObject *newTask = [NSEntityDescription insertNewObjectForEntityForName:TASK_ENTITY inManagedObjectContext:context];
    NSError *error;
    
    [newTask setValue:[deserializedDictionary objectForKey:TASK_ID_KEY] forKey:TASK_ID_KEY];
    [newTask setValue:[deserializedDictionary objectForKey:TITLE_KEY] forKey:TITLE_KEY];
    [newTask setValue:[deserializedDictionary objectForKey:IS_COMPLETED_KEY] forKey:IS_COMPLETED_KEY];
    [newTask setValue:[deserializedDictionary objectForKey:DESCRIPTION_KEY] forKey:@"desc"];
    [newTask setValue:[deserializedDictionary objectForKey:DEADLINE_KEY] forKey:DEADLINE_KEY];
    [newTask setValue:[deserializedDictionary objectForKey:DATE_CREATED_KEY] forKey:DATE_CREATED_KEY];
    [newTask setValue:[deserializedDictionary objectForKey:DATE_ASSIGNED_KEY] forKey:DATE_ASSIGNED_KEY];
    [newTask setValue:[NSNumber numberWithInt:self.userID] forKey:ASSIGNED_TO_KEY];
    [newTask setValue:[deserializedDictionary objectForKey:ASSIGNED_FROM_KEY] forKey:ASSIGNED_FROM_KEY];
    [newTask setValue:[deserializedDictionary objectForKey:CREATED_BY_KEY] forKey:CREATED_BY_KEY];
    [context save:&error];
    
    [self.itemCompleted addObject:[deserializedDictionary objectForKey:IS_COMPLETED_KEY]];
}


-(void)populateTaskDetails
{
    for (int i = 0; i < [self.taskIDs count]; i++)
    {
        NSString *url = [NSString stringWithFormat:TASK_URL, DATABASE_URL,[self.taskIDs[i] integerValue]];
        url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url] cachePolicy:NSURLRequestReloadIgnoringLocalAndRemoteCacheData timeoutInterval:30];
        [urlRequest setHTTPMethod:@"GET"];
        NSURLResponse * response = nil;
        NSError * error = nil;
        NSData * data = [NSURLConnection sendSynchronousRequest:urlRequest
                                              returningResponse:&response
                                                          error:&error];
        if (error)
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:ERROR_MESSAGE message:TASK_NOT_FOUND_MESSAGE delegate:self cancelButtonTitle:OK_BUTTON otherButtonTitles: nil];
            [alert show];
        }
        else
        {
            NSDictionary *deserializedDictionary;
            deserializedDictionary = [self getDeadLine:data];
            [self setTaskInfo:deserializedDictionary];
        }
    }
}

-(NSArray*)getDataFromDatabase
{
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    
    NSManagedObjectContext *context = [appDelegate managedObjectContext];
    
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:TASK_ENTITY inManagedObjectContext:context];
    
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDesc];
    
    NSError *error;
    NSArray *result = [context executeFetchRequest:request
                                             error:&error];
    return result;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)onClickCreateNewTask
{
    //[self.taskListDelegate createNewTaskClicked:NO];
    self.addViewTaskViewController = [[iWinAddAndViewTaskViewController alloc] initWithNibName:ADD_AND_VIEW_TASK_NIB bundle:nil withUserID:self.userID withTaskID:-1];
    [self.addViewTaskViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.addViewTaskViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    self.addViewTaskViewController.viewTaskDelegate = self;
    
    [self presentViewController:self.addViewTaskViewController animated:YES completion:nil];
    self.addViewTaskViewController.view.superview.bounds = CGRectMake(MODAL_XOFFSET, MODAL_YOFFSET, MODAL_WIDTH, MODAL_HEIGHT);
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:TASK_CELL_ID];
    if (cell == nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:TASK_CELL_ID];
    }
    
    cell.textLabel.text = (NSString *)[self.itemList objectAtIndex:indexPath.row];
    cell.detailTextLabel.text = (NSString *)[self.itemDetail objectAtIndex:indexPath.row];
    
    if ([self.itemCompleted[indexPath.row] boolValue])
    {
        cell.accessoryType = UITableViewCellAccessoryCheckmark;
    }
    return cell;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.itemList.count;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    self.addViewTaskViewController = [[iWinAddAndViewTaskViewController alloc] initWithNibName:ADD_AND_VIEW_TASK_NIB bundle:nil withUserID:self.userID withTaskID:[self.taskIDs[indexPath.row] integerValue]];
    self.addViewTaskViewController.viewTaskDelegate = self;
    [self.addViewTaskViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.addViewTaskViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    [self presentViewController:self.addViewTaskViewController animated:YES completion:nil];
    self.addViewTaskViewController.view.superview.bounds = CGRectMake(MODAL_XOFFSET,MODAL_YOFFSET,MODAL_WIDTH,MODAL_HEIGHT);
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    return YES;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {

        self.selectedTask = indexPath.row;
        UIAlertView *deleteAlertView = [[UIAlertView alloc] initWithTitle:CONFIRM_DELETE_TITLE message:DELETE_TASK_MESSAGE delegate:self cancelButtonTitle:NO_DELETE_OPTION otherButtonTitles:YES_DELETE_OPTION, nil];
        [deleteAlertView show];
    }
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 1)
    {
        NSString *url = [NSString stringWithFormat:TASK_URL, DATABASE_URL,[[self.taskIDs objectAtIndex:self.selectedTask] integerValue]];
        NSError *error = [self.backendUtility deleteRequestForUrl:url];
        if (!error)
        {
            self.selectedTask = -1;
            [self refreshTaskList];
        }
    }
}
@end
