//
//  iWinViewAndCreateProjectViewController.m
//  MeetingBuilder
//
//  Created by Brodie Lockard on 2/2/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import "iWinViewAndCreateProjectViewController.h"
#import "iWinAddUsersViewController.h"
#import "Contact.h"
#import "Settings.h"
#import "Group.h"
#import "Project.h"

@interface iWinViewAndCreateProjectViewController ()
@property (nonatomic) BOOL isEditing;
@property (strong, nonatomic) iWinAddUsersViewController *userViewController;
@property (strong, nonatomic) NSMutableArray *userList;
@property (nonatomic) NSInteger userID;
@property (nonatomic) NSInteger projectID;
@property (nonatomic) NSUInteger rowToDelete;

@end

@implementation iWinViewAndCreateProjectViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger)userID withProjectID:(NSInteger)projectID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.isEditing = YES;
        self.userID = userID;
        self.projectID = projectID;
    }
    return self;
}

-(void)selectedUsers:(NSMutableArray *)userList
{
    self.userList = userList;
    [self.membersTableView reloadData];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    self.userList = [[NSMutableArray alloc] init];
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
    
}

- (IBAction)onClickSave:(UIButton *)sender {
    [self saveNewProject];
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

@end

