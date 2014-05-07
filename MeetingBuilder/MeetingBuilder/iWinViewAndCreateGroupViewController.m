//
//  iWinViewAndCreateGroupViewController.m
//  MeetingBuilder
//
//  Created by Brodie Lockard on 1/19/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import "iWinViewAndCreateGroupViewController.h"
#import "iWinAddUsersViewController.h"
#import "Contact.h"
#import "iWinBackEndUtility.h"
#import "iWinAppDelegate.h"
#import "Settings.h"
#import "Group.h"
#import "iWinConstants.h"

@interface iWinViewAndCreateGroupViewController ()
@property (nonatomic) BOOL isEditing;
@property (strong, nonatomic) iWinAddUsersViewController *userViewController;
@property (strong, nonatomic) NSMutableArray *userList;
@property (strong, nonatomic) NSMutableArray *userIDList;
@property (nonatomic) iWinBackEndUtility *backEndUtility;
@property (nonatomic) NSInteger userID;
@property (nonatomic) NSInteger groupID;
@property (nonatomic) NSUInteger rowToDelete;
@property (strong, nonatomic) NSManagedObjectContext *context;
@property (nonatomic) UIAlertView *deleteAlertView;
@end

@implementation iWinViewAndCreateGroupViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger)userID withGroupID:(NSInteger)groupID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.isEditing = YES;
        self.userID = userID;
        self.groupID = groupID;
        self.groupTitleField.text = @"Enter Your Group Name";
    }
    return self;
}

-(void)selectedUsers:(NSMutableArray *)userList
{
    self.userList = userList;
    [self.userList addObject:[self getContactForID:self.userID]];
    [self.memberTableView reloadData];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.backEndUtility = [[iWinBackEndUtility alloc] init];
    self.userList = [[NSMutableArray alloc] init];
    self.userIDList = [[NSMutableArray alloc] init];
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    self.context = [appDelegate managedObjectContext];
    [self.userIDList addObject:[NSNumber numberWithInt:self.userID]];
    [self.userList addObject:[self getContactForID:self.userID]];
    self.titleLabel.text = @"Create a Group";
    self.deleteButton.hidden = YES;
    if(self.groupID != -1){
        [self initView];
    }
}

-(void) saveNewGroup
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
    
    NSArray *keys = [NSArray arrayWithObjects:@"groupTitle", @"members", nil];
    NSArray *objects = [NSArray arrayWithObjects: self.groupTitleField.text, userIDJsonDictionary, nil];
    
    NSDictionary *deserializedDictionary;
    
    if(self.groupID != -1)
    {
        NSArray *putKeys = [NSArray arrayWithObjects:@"groupID", @"field", @"value", nil];
        
        for (int i = 0; i < keys.count; i++) {
            
            NSArray *putObjects = [NSArray arrayWithObjects:[NSNumber numberWithInt:self.groupID], keys[i], objects[i], nil];
            NSString *url = [NSString stringWithFormat:@"%@/Group/", DATABASE_URL];
            NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:putObjects forKeys:putKeys];
            [self.backEndUtility putRequestForUrl:url withDictionary:jsonDictionary];
        }
    }
    else
    {
        NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
        NSString *url = [NSString stringWithFormat:@"%@/Group/", DATABASE_URL];
        deserializedDictionary = [self.backEndUtility postRequestForUrl:url withDictionary:jsonDictionary];
    }
    
    if (deserializedDictionary)
        self.groupID = [[deserializedDictionary objectForKey:@"groupID"] integerValue];
}

- (IBAction)onClickSave:(UIButton *)sender {
    [self saveNewGroup];
    [self.groupDelegate refreshGroupList];
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)onClickCancel:(UIButton *)sender {
    [self dismissViewControllerAnimated:YES completion:nil];
}
- (IBAction)onClickAddMembers:(UIButton *)sender {
    self.userViewController = [[iWinAddUsersViewController alloc] initWithNibName:@"iWinAddUsersViewController" bundle:nil withPageName:@"Group" inEditMode:self.isEditing];
    [self.userViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.userViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    self.userViewController.userDelegate = self;
    [self presentViewController:self.userViewController animated:YES completion:nil];
    self.userViewController.view.superview.bounds = CGRectMake(MODAL_XOFFSET, MODAL_YOFFSET, MODAL_WIDTH, MODAL_HEIGHT);
}

-(void)initView
{
    self.saveButton.hidden = NO;
    self.addMembersButton.hidden = NO;
    self.titleLabel.text = @"Edit Group";
    self.deleteButton.hidden = NO;
    [self populateMembers];
}

-(void)populateMembers
{
    self.userList = [[NSMutableArray alloc] init];
    self.userIDList = [[NSMutableArray alloc] init];
    NSString *url = [NSString stringWithFormat:@"%@/Group/%d", DATABASE_URL,self.groupID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backEndUtility getRequestForUrl:url];
    
    if (!deserializedDictionary)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Group not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        self.groupTitleField.text = [deserializedDictionary objectForKey:@"groupTitle"];
        NSArray *jsonArray = [deserializedDictionary objectForKey:@"members"];
        if (jsonArray.count > 0)
        {
            for (NSDictionary* users in jsonArray)
            {
                [self.userIDList addObject:[users objectForKey:@"userID"]];
            }
            [self populateMemberDetails];
        }
    }
}

-(void)populateMemberDetails
{
    for (int i = 0; i < [self.userIDList count]; i++)
    {
        NSString *url = [NSString stringWithFormat:@"%@/User/%d",DATABASE_URL ,[self.userIDList[i] integerValue]];
        url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        NSDictionary *deserializedDictionary = [self.backEndUtility getRequestForUrl:url];
        
        if (!deserializedDictionary)
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Group details not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
            [alert show];
        }
        else
        {
            [self.userList addObject:[self getContactForID:[self.userIDList[i] integerValue]]];
            iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
            
            NSManagedObjectContext *context = [appDelegate managedObjectContext];
            NSManagedObject * newUser = [NSEntityDescription insertNewObjectForEntityForName:@"Group" inManagedObjectContext:context];
            NSError *error;
            [newUser setValue:[deserializedDictionary objectForKey:@"name"] forKey:@"groupTitle"];
            [newUser setValue:self.userIDList[i] forKey:@"groupID"];
            [context save:&error];
        }
    }
}

-(NSArray*)getDataFromDatabase
{
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    
    NSManagedObjectContext *context = [appDelegate managedObjectContext];
    
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Group" inManagedObjectContext:context];
    
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDesc];
    
    NSError *error;
    NSArray *result = [context executeFetchRequest:request
                                             error:&error];
    return result;
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
    Contact * c = (Contact *)[self.userList objectAtIndex:indexPath.row];
    cell.textLabel.text =  c.name;
    cell.detailTextLabel.text = @"";
    [cell.deleteButton setTag:indexPath.row];
    return cell;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.userList.count;
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    return NO;
}


-(Contact *)getContactForID:(NSInteger)userID
{
    NSString *url = [NSString stringWithFormat:@"%@/User/%d", DATABASE_URL,userID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backEndUtility getRequestForUrl:url];
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
            c.userID = [NSNumber numberWithInt: userID];
            c.name = (NSString *)[deserializedDictionary objectForKey:@"name"];
            c.email = (NSString *)[deserializedDictionary objectForKey:@"email"];
            c.title = (NSString *)[deserializedDictionary objectForKey:@"title"];
            c.company = (NSString *)[deserializedDictionary objectForKey:@"company"];
            c.location = (NSString *)[deserializedDictionary objectForKey:@"location"];
            c.phone = (NSString *)[deserializedDictionary objectForKey:@"phone"];
            return c;
            
        }
    }
    
    return nil;
}
- (IBAction)onClickDelete
{
    self.deleteAlertView = [[UIAlertView alloc] initWithTitle:@"Confirm Delete" message:@"Are you sure you want to delete this contact?" delegate:self cancelButtonTitle:@"No, just kidding!" otherButtonTitles:@"Yes, please", nil];
    [self.deleteAlertView show];
}


-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 1)
    {
        //Perform deletion
        NSString *url = [NSString stringWithFormat:@"%@/Group/%d", DATABASE_URL,self.groupID];
        NSError * error = [self.backEndUtility deleteRequestForUrl:url];
        
        if (!error){
            [self.groupDelegate refreshGroupList];
            [self dismissViewControllerAnimated:YES completion:nil];
        }
        
    }
    
}

@end
