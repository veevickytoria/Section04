//
//  iWinTaskListViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinTaskListViewController.h"
#import "iWinAddAndViewTaskViewController.h"
#import <QuartzCore/QuartzCore.h>
#import "iWinAppDelegate.h"

@interface iWinTaskListViewController ()
@property (strong, nonatomic) NSMutableArray *itemList;
@property (strong, nonatomic) NSMutableArray *itemDetail;
@property (nonatomic) NSInteger userID;
@property (strong, nonatomic) iWinAddAndViewTaskViewController *addViewTaskViewController;
@property (nonatomic) NSInteger selectedTask;
@property (strong, nonatomic) NSMutableArray *taskIDs;
@end

@implementation iWinTaskListViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil userID:(NSInteger) userID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    self.userID = userID;
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    self.itemList = [[NSMutableArray alloc] init];
    self.itemDetail = [[NSMutableArray alloc] init];
    
    self.taskIDs = [[NSMutableArray alloc] init];    
    
    self.createTaskButton.layer.cornerRadius = 7;
    self.createTaskButton.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    self.createTaskButton.layer.borderWidth = 1.0f;
    [self populateTaskList];
    
}

-(void)refreshTaskList
{
    [self.addViewTaskViewController dismissViewControllerAnimated:YES completion:nil];
    self.itemList = [[NSMutableArray alloc] init];
    self.itemDetail = [[NSMutableArray alloc] init];
    self.taskIDs = [[NSMutableArray alloc] init];

    [self populateTaskList];
    [self.taskListTable reloadData];
}

-(void)populateTaskList
{
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/User/Tasks/%d", self.userID];
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
        NSDictionary *deserializedDictionary = (NSDictionary *)[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers|NSJSONReadingAllowFragments error:&jsonParsingError];
        jsonArray = [deserializedDictionary objectForKey:@"tasks"];
    }
    if (jsonArray.count > 0)
    {
        for (NSDictionary* tasks in jsonArray)
        {
            NSString *relationship = [tasks objectForKey:@"type"];
            if ([relationship isEqual:@"ASSIGNED_TO"]){
                [self.itemList addObject:[tasks objectForKey:@"title"]];
                [self.taskIDs addObject:[tasks objectForKey:@"id"]];
            }
        }
        [self populateTaskDetails];
    }
    
}

-(void)populateTaskDetails
{
    for (int i = 0; i < [self.taskIDs count]; i++)
    {
        NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Task/%d", [self.taskIDs[i] integerValue]];
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
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Tasks not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
            [alert show];
        }
        else
        {
            NSError *jsonParsingError = nil;
            NSDictionary *deserializedDictionary = (NSDictionary *)[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers|NSJSONReadingAllowFragments error:&jsonParsingError];
            [self.itemDetail addObject:[deserializedDictionary objectForKey:@"deadline"]];
            
            iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
            
            NSManagedObjectContext *context = [appDelegate managedObjectContext];
            
            NSManagedObject *newTask = [NSEntityDescription insertNewObjectForEntityForName:@"Task" inManagedObjectContext:context];
            NSError *error;

            [newTask setValue:[deserializedDictionary objectForKey:@"taskID"] forKey:@"taskID"];
            [newTask setValue:[deserializedDictionary objectForKey:@"title"] forKey:@"title"];
            [newTask setValue:[deserializedDictionary objectForKey:@"isCompleted"] forKey:@"isCompleted"];
            [newTask setValue:[deserializedDictionary objectForKey:@"description"] forKey:@"desc"];
            [newTask setValue:[deserializedDictionary objectForKey:@"deadline"] forKey:@"deadline"];
            [newTask setValue:[deserializedDictionary objectForKey:@"dateCreated"] forKey:@"dateCreated"];
            [newTask setValue:[deserializedDictionary objectForKey:@"dateAssigned"] forKey:@"dateAssigned"];
            [newTask setValue:[NSNumber numberWithInt:self.userID] forKey:@"assignedTo"];
            [newTask setValue:[deserializedDictionary objectForKey:@"assignedFrom"] forKey:@"assignedFrom"];
            [newTask setValue:[deserializedDictionary objectForKey:@"createdBy"] forKey:@"createdBy"];
            [context save:&error];
        }
    }
}

-(NSArray*)getDataFromDatabase
{
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    
    NSManagedObjectContext *context = [appDelegate managedObjectContext];
    
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Task" inManagedObjectContext:context];
    
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
    self.addViewTaskViewController = [[iWinAddAndViewTaskViewController alloc] initWithNibName:@"iWinAddAndViewTaskViewController" bundle:nil withUserID:self.userID withTaskID:-1];
    [self.addViewTaskViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.addViewTaskViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    self.addViewTaskViewController.viewTaskDelegate = self;
    
    [self presentViewController:self.addViewTaskViewController animated:YES completion:nil];
    self.addViewTaskViewController.view.superview.bounds = CGRectMake(0,0,768,1003);
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"TaskCell"];
    if (cell == nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"TaskCell"];
    }
    
    cell.textLabel.text = (NSString *)[self.itemList objectAtIndex:indexPath.row];
    cell.detailTextLabel.text = (NSString *)[self.itemDetail objectAtIndex:indexPath.row];
    
    if (indexPath.row == 0)
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
    self.addViewTaskViewController = [[iWinAddAndViewTaskViewController alloc] initWithNibName:@"iWinAddAndViewTaskViewController" bundle:nil withUserID:self.userID withTaskID:[self.taskIDs[indexPath.row] integerValue]];
    self.addViewTaskViewController.viewTaskDelegate = self;
    [self.addViewTaskViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.addViewTaskViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    [self presentViewController:self.addViewTaskViewController animated:YES completion:nil];
    self.addViewTaskViewController.view.superview.bounds = CGRectMake(0,0,768,1003);
}
@end
