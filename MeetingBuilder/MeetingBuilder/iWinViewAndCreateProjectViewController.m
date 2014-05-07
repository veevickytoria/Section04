//
//  iWinViewAndCreateProjectViewController.m
//  MeetingBuilder
//
//  Created by Brodie Lockard on 2/2/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import "iWinViewAndCreateProjectViewController.h"
#import "iWinProjectListViewController.h"
#import "iWinAddUsersViewController.h"
#import "iWinAppDelegate.h"
#import <QuartzCore/QuartzCore.h>
#import "Contact.h"
#import "Settings.h"
#import "Group.h"
#import "Project.h"
#import "iWinBackEndUtility.h"

@interface iWinViewAndCreateProjectViewController ()
@property (nonatomic) BOOL isEditing;
@property (strong, nonatomic) iWinAddUsersViewController *userViewController;
@property (strong, nonatomic) NSMutableArray *userList;
@property (nonatomic) NSInteger userID;
@property (nonatomic) NSInteger projectID;
@property (nonatomic) NSUInteger rowToDelete;
@property (nonatomic) NSDictionary *existProject;
@property (strong, nonatomic) iWinBackEndUtility *backendUtility;
@property (strong, nonatomic) NSManagedObjectContext *context;

@end

@implementation iWinViewAndCreateProjectViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger)userID withProjectID:(NSInteger)projectID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.isEditing = TRUE;
        self.userID = userID;
        self.projectID = projectID;
        if(projectID == -1) {
            self.isEditing = FALSE;
        }
    }
    return self;
}

-(void)selectedUsers:(NSMutableArray *)userList
{
    self.userList = userList;
    [self.userList addObject:[self getContactForID:self.userID]];
    [self.membersTableView reloadData];
    
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    self.backendUtility = [[iWinBackEndUtility alloc] init];
    self.userList = [[NSMutableArray alloc] init];
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    self.context = [appDelegate managedObjectContext];
    [self.userList addObject:[self getContactForID:self.userID]];
    if (self.isEditing)
    {
        
        [self initForExistingProject];
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) saveNewProject
{
    NSMutableArray *meetingIDJsonDictionary = [[NSMutableArray alloc] init];
    NSMutableArray *notesIDJsonDictionary = [[NSMutableArray alloc] init];
    NSMutableArray *userIDJsonDictionary = [[NSMutableArray alloc] init];

    for (int i = 0; i<[self.userList count]; i++)
    {
        Contact *c = (Contact *)self.userList[i];
        NSArray *userIDKeys = [[NSArray alloc] initWithObjects:@"userID", nil];
        NSArray *userIDObjects = [[NSArray alloc] initWithObjects:[c.userID stringValue], nil];
        NSDictionary *dict = [NSDictionary dictionaryWithObjects:userIDObjects forKeys:userIDKeys];
        [userIDJsonDictionary addObject:dict];
    }
    
    
    NSArray *keys = [NSArray arrayWithObjects:@"projectTitle", @"meetings", @"notes", @"members", nil];
    NSArray *objects = [NSArray arrayWithObjects: self.projectTitleField.text, meetingIDJsonDictionary, notesIDJsonDictionary, userIDJsonDictionary, nil];
    
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    NSData *jsonData;
    NSString *jsonString;
    
    if ([NSJSONSerialization isValidJSONObject:jsonDictionary])
    {
        jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary options:0 error:nil];
        jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Project/"];
    
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
    self.projectID = [[deserializedDictionary objectForKey:@"projectID"] integerValue];
    NSLog(@"%d", self.projectID);    
}

-(void) updateProject
{
    NSMutableArray *userIDJsonDictionary = [[NSMutableArray alloc] init];
    
    for (int i = 0; i<[self.userList count]; i++)
    {
        Contact *c = (Contact *)self.userList[i];
        
        NSArray *userIDKeys = [[NSArray alloc] initWithObjects:@"userID", nil];
        NSArray *userIDObjects = [[NSArray alloc] initWithObjects:[c.userID stringValue], nil];
        NSDictionary *dict = [NSDictionary dictionaryWithObjects:userIDObjects forKeys:userIDKeys];
        [userIDJsonDictionary addObject:dict];
    }
    
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Project/"];
    
    NSArray *keys = [NSArray arrayWithObjects:@"projectID", @"field", @"value", nil];
    NSArray *objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.projectID] stringValue], @"projectTitle", self.projectTitleField.text,nil];
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    [self.backendUtility putRequestForUrl:url withDictionary:jsonDictionary];
    
    
    objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.projectID] stringValue], @"members", userIDJsonDictionary,nil];
    jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    [self.backendUtility putRequestForUrl:url withDictionary:jsonDictionary];
}

- (IBAction)onClickSave:(UIButton *)sender {
    if(self.isEditing) {
        [self updateProject];
    }
    else {
        [self saveNewProject];
    }
    [self.projectDelegate refreshProjectList];
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)onClickCancel:(UIButton *)sender {
    [self dismissViewControllerAnimated:YES completion:nil];
}
- (IBAction)onClickAddMembers:(UIButton *)sender {
    self.userViewController = [[iWinAddUsersViewController alloc] initWithNibName:@"iWinAddUsersViewController" bundle:nil withPageName:@"Project" inEditMode:self.isEditing];
    [self.userViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.userViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    self.userViewController.userDelegate = self;
    [self presentViewController:self.userViewController animated:YES completion:nil];
    self.userViewController.view.superview.bounds = CGRectMake(0,0,768,1003);
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"AttendeeCell"];
    if (cell == nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"AttendeeCell"];
    }
    
    Contact *c = (Contact *)[self.userList objectAtIndex:indexPath.row];
    
    cell.textLabel.text =  c.name;
    if (c.name.length == 0){
        cell.textLabel.text = c.email;
    }
    cell.detailTextLabel.text = c.email;
    return cell;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.userList.count;
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    return YES;
}

// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        self.rowToDelete = indexPath.row;
        UIAlertView *deleteAlertView = [[UIAlertView alloc] initWithTitle:@"Confirm Delete" message:@"Are you sure you want to delete this?" delegate:self cancelButtonTitle:@"No, just kidding!" otherButtonTitles:@"Yes, please", nil];
        [deleteAlertView show];
    }
}


-(void) initForExistingProject
{
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Project/%d", self.projectID];
    
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    if (!deserializedDictionary)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Project not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        self.existProject  = deserializedDictionary;
        
    }
    
    self.projectTitleField.text = [self.existProject objectForKey:@"projectTitle"];

    [self initAttendees];
}


-(void) initAttendees
{
    self.userList = [[NSMutableArray alloc] init];
    NSMutableArray *attendeeArray = [[NSMutableArray alloc]init];
    NSArray* attendeeFromDatabase = [self.existProject objectForKey:@"members"];
    for(int i = 0; i < [attendeeFromDatabase count]; i++){
        [attendeeArray addObject:[(NSDictionary*)[attendeeFromDatabase objectAtIndex:i] objectForKey:@"userID"]];
    }
    
    for (int i=0; i<[attendeeArray count]; i++)
    {
        [self.userList addObject:[self getContactForID:[attendeeArray[i] integerValue]]];
    }
}


-(Contact *)getContactForID:(NSInteger)userID
{
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/User/%d", userID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    if (!deserializedDictionary)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Project not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        if (deserializedDictionary.count > 0)
        {
            
            NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Contact" inManagedObjectContext:self.context];
            Contact *c = [[Contact alloc] initWithEntity:entityDesc insertIntoManagedObjectContext:self.context];
            c.userID = [NSNumber numberWithInt:userID];
            c.name = (NSString *)[deserializedDictionary objectForKey:@"name"];
            c.email = (NSString *)[deserializedDictionary objectForKey:@"email"];
            return c;
            
        }
    }
    
    return nil;
}

@end

