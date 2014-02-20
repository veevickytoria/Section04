//
//  iWinProjectListViewController.m
//  MeetingBuilder
//
//  Created by Brodie Lockard on 2/2/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import "iWinProjectListViewController.h"
#import "iWinViewAndCreateProjectViewController.h"
#import "Project.h"
#import "iWinBackEndUtility.h"
#import <QuartzCore/QuartzCore.h>
#import "iWinAppDelegate.h"

@interface iWinProjectListViewController ()
@property (nonatomic) NSInteger userID;
@property (nonatomic) NSInteger selectedProject;
@property (strong, nonatomic) NSMutableArray *projectID;
@property (strong, nonatomic) NSMutableArray *projectList;
@property (strong, nonatomic) NSMutableArray *projectDetail;
@property (strong, nonatomic) iWinBackEndUtility *backendUtility;
@property (strong, nonatomic) iWinViewAndCreateProjectViewController *createProjectVC;
@property (strong, nonatomic) iWinViewAndCreateProjectViewController *viewProjectVC;
@end

//constants
const int XOFFSET = 36;
const int YOFFSET = 15;
const int HEIGHT = 1018;
const int WIDTH = 804;

@implementation iWinProjectListViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger)userID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.userID = userID;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.projectList = [[NSMutableArray alloc] init];
    self.projectDetail = [[NSMutableArray alloc] init];
    self.projectID = [[NSMutableArray alloc] init];
    self.selectedProject = -1;
    self.backendUtility = [[iWinBackEndUtility alloc] init];
    [self.projectTable registerNib:[UINib nibWithNibName:@"CustomSubtitledCell" bundle:nil] forCellReuseIdentifier:@"ProjectCell"];
    [self populateProjectList];
}

-(void)populateProjectList
{
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/User/Projects/%d", self.userID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    if (!deserializedDictionary)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Projects not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        NSArray *jsonArray = [deserializedDictionary objectForKey:@"projects"];
        if (jsonArray.count > 0)
        {
            for (NSDictionary* projects in jsonArray)
            {
                [self.projectID addObject:[projects objectForKey:@"projectID"]];
            }
            [self populateProjectDetails];
        }
    }
}

-(void)populateProjectDetails
{
    for (int i = 0; i < [self.projectID count]; i++)
    {
        NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Project/%d", [self.projectID[i] integerValue]];
        url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
        
        if (!deserializedDictionary)
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Project details not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
            [alert show];
        }
        else
        {
            [self.projectList addObject:[deserializedDictionary objectForKey:@"projectTitle"]];
            iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
            
            NSManagedObjectContext *context = [appDelegate managedObjectContext];
            NSManagedObject * newProject = [NSEntityDescription insertNewObjectForEntityForName:@"Project" inManagedObjectContext:context];
            NSError *error;
            [newProject setValue:[deserializedDictionary objectForKey:@"projectTitle"] forKey:@"projectTitle"];
            [newProject setValue:self.projectID[i] forKey:@"projectID"];
            [newProject setValue:[NSNumber numberWithInt:self.userID] forKey:@"userID"];
            [context save:&error];
        }
    }
}

-(NSArray*)getDataFromDatabase
{
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    
    NSManagedObjectContext *context = [appDelegate managedObjectContext];
    
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Project" inManagedObjectContext:context];
    
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDesc];
    
    NSError *error;
    NSArray *result = [context executeFetchRequest:request
                                             error:&error];
    return result;
}

-(void) refreshProjectList
{
    [self.createProjectVC dismissViewControllerAnimated:YES completion:nil];
    self.projectList = [[NSMutableArray alloc] init];
    self.projectID = [[NSMutableArray alloc] init];
    [self populateProjectList];
    [self.projectTable reloadData];
}


-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    CustomSubtitledCell *cell = (CustomSubtitledCell *)[tableView dequeueReusableCellWithIdentifier:@"AttendeeCell"];
    if (cell == nil)
    {
        cell = [[CustomSubtitledCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"AttendeeCell"];
    }
    [cell initCell];
    //cell.subTitledDelegate = self;
    [cell.textLabel  setText:(NSString *)[self.projectList objectAtIndex:indexPath.row]];
    cell.detailTextLabel.text = @"";
    [cell.deleteButton setTag:indexPath.row];
    return cell;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.projectList.count;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    self.viewProjectVC = [[iWinViewAndCreateProjectViewController alloc] initWithNibName:@"iWinViewAndCreateProjectViewController" bundle:nil withUserID:self.userID withProjectID:[self.projectID[indexPath.row] integerValue]];
    self.viewProjectVC.projectDelegate = self;
    [self.viewProjectVC setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.viewProjectVC setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    
    [self presentViewController:self.viewProjectVC animated:YES completion:nil];
    self.viewProjectVC.view.superview.bounds = CGRectMake(XOFFSET,YOFFSET,WIDTH,HEIGHT);
}



- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    return NO;
}

// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        //add code here for when you hit delete
        
    }
}

-(void)deleteCell:(NSInteger)row
{
    self.selectedProject = row;
    UIAlertView *deleteAlertView = [[UIAlertView alloc] initWithTitle:@"Confirm Delete" message:@"Are you sure you want to delete this project?" delegate:self cancelButtonTitle:@"No, just kidding!" otherButtonTitles:@"Yes, please", nil];
    [deleteAlertView show];
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 1)
    {
        NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Projects/%d", [[self.projectID objectAtIndex:self.selectedProject] integerValue]];
        NSError *error = [self.backendUtility deleteRequestForUrl:url];
        if (!error)
        {
            self.selectedProject = -1;
            [self refreshProjectList];
        }
    }
}

- (IBAction)onClickCreateProject:(id)sender {
    self.createProjectVC = [[iWinViewAndCreateProjectViewController alloc] initWithNibName:@"iWinViewAndCreateProjectViewController" bundle:nil withUserID:self.userID withProjectID:-1];
    
    [self.createProjectVC setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.createProjectVC setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    self.createProjectVC.projectDelegate = self;
    [self presentViewController:self.createProjectVC animated:YES completion:nil];
    self.createProjectVC.view.superview.bounds = CGRectMake(XOFFSET,YOFFSET,WIDTH,HEIGHT);
}

- (IBAction)onClickBackToProfile:(id)sender {
    [self dismissViewControllerAnimated:YES completion:nil];
}
@end
