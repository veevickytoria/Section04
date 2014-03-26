//
//  iWinViewProfileViewController.m
//  MeetingBuilder
//
//  Created by Richard Shomer on 11/6/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinViewProfileViewController.h"
#import "iWinViewAndCreateGroupViewController.h"
#import "iWinProjectListViewController.h"
#import "iWinViewAndCreateProjectViewController.h"
#import "iWinBackEndUtility.h"
#import "iWinAppDelegate.h"
#import "Contact.h"
#import "Group.h"
#import "iWinConstants.h"
#import <QuartzCore/QuartzCore.h>

@interface iWinViewProfileViewController ()
@property (nonatomic) BOOL isEditing;
@property (nonatomic) Contact *contact;
@property (nonatomic) NSInteger userID;
@property (strong, nonatomic) iWinViewAndCreateGroupViewController *createGroupVC;
@property (strong, nonatomic) iWinProjectListViewController *viewProjectsVC;
@property (nonatomic) iWinBackEndUtility *backEndUtility;
@property (nonatomic) NSInteger selectedGroup;
@property (strong, nonatomic) NSMutableArray *groupID;
@property (strong, nonatomic) NSMutableArray *groupList;
@property (strong, nonatomic) NSMutableArray *groupDetail;
@end

@implementation iWinViewProfileViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withID:(NSInteger)userID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.userID = userID;
    }
    [self viewDidLoad];
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.backEndUtility = [[iWinBackEndUtility alloc] init];
    self.isEditing = NO;
    self.cancel.hidden = YES;
    [self updateTextUI];
    [self.displayNameTextField setBorderStyle:UITextBorderStyleNone];
    [self.companyTextField setBorderStyle:UITextBorderStyleNone];
    [self.titleTextField setBorderStyle:UITextBorderStyleNone];
    [self.emailTextField setBorderStyle:UITextBorderStyleNone];
    [self.phoneTextField setBorderStyle:UITextBorderStyleNone];
    [self.locationTextField setBorderStyle:UITextBorderStyleNone];
    
    self.groupList = [[NSMutableArray alloc] init];
    self.groupDetail = [[NSMutableArray alloc] init];
    self.groupID = [[NSMutableArray alloc] init];
    self.selectedGroup = -1;
    [self.groupsTableView registerNib:[UINib nibWithNibName:@"CustomSubtitledCell" bundle:nil] forCellReuseIdentifier:@"ProjectCell"];
    
    [self populateGroupList];
    
    [self withBorders:NO];
}
-(void)updateTextUI
{
    NSString *url = [NSString stringWithFormat: @"%@/User/%d", DATABASE_URL,self.userID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];

    NSDictionary *userInfo = [self.backEndUtility getRequestForUrl:url];
   
    
    if (!userInfo)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Meetings not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        if (userInfo.count > 0)
        {
            self.displayNameTextField.text = (NSString *) [userInfo objectForKey:@"name"];
            self.emailTextField.text = (NSString *) [userInfo objectForKey:@"email"];
            self.phoneTextField.text = (NSString *) [userInfo objectForKey:@"phone"];
            self.companyTextField.text = (NSString *) [userInfo objectForKey:@"company"];
            self.titleTextField.text = (NSString *) [userInfo objectForKey:@"title"];
            self.locationTextField.text = (NSString *) [userInfo objectForKey:@"location"];
        }
    }
    
}

-(void) saveChanges
{
    NSString *url = [NSString stringWithFormat:@"%@/User/", DATABASE_URL];
    NSArray *fields = [NSArray arrayWithObjects:@"name", @"email", @"phone", @"company", @"title", @"location",nil];
    NSArray *values = [NSArray arrayWithObjects:self.displayNameTextField.text, self.emailTextField.text, self.phoneTextField.text, self.companyTextField.text, self.titleTextField.text, self.locationTextField.text,nil];
    NSArray *keys = [NSArray arrayWithObjects:@"userID", @"field", @"value", nil];

    for (int i = 0; i < fields.count; i++) {
    
        NSArray *objects = [NSArray arrayWithObjects:[NSNumber numberWithInt:self.userID], fields[i], values[i], nil];

        NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
        [self.backEndUtility putRequestForUrl:url withDictionary:jsonDictionary];
    }
}

-(IBAction)onCancel:(id)sender
{
    [self userInteraction:NO];
    [self withBorders:NO];
    [self updateTextUI];
    [self.editProfile setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
    self.isEditing = NO;
    [self.editProfile setTitle:@"Edit Profile" forState:UIControlStateNormal];
    self.cancel.hidden = YES;
}

- (IBAction)onCreateGroup:(id)sender {
    self.createGroupVC = [[iWinViewAndCreateGroupViewController alloc] initWithNibName:@"iWinViewAndCreateGroupViewController" bundle:nil withUserID:self.userID withGroupID:-1];
    
    [self.createGroupVC setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.createGroupVC setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    self.createGroupVC.groupDelegate = self;
    [self presentViewController:self.createGroupVC animated:YES completion:nil];
    self.createGroupVC.view.superview.bounds = CGRectMake(MODAL_XOFFSET, MODAL_YOFFSET, MODAL_WIDTH, MODAL_HEIGHT);
}

- (IBAction)onViewProjects:(id)sender {
    self.viewProjectsVC = [[iWinProjectListViewController alloc] initWithNibName:@"iWinProjectListViewController" bundle:nil withUserID:self.userID];
    
    [self.viewProjectsVC setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.viewProjectsVC setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.viewProjectsVC animated:YES completion:nil];
    self.viewProjectsVC.view.superview.bounds = CGRectMake(MODAL_XOFFSET, MODAL_YOFFSET, MODAL_WIDTH, MODAL_HEIGHT);
}

-(IBAction)onEditProfile:(id)sender
{
    if (self.isEditing) {
        
        [self saveChanges];
        
        self.isEditing = NO;
        [self userInteraction:NO];
        [self withBorders:NO];
        [self.editProfile setTitle:@"Edit Profile" forState:UIControlStateNormal];
        [self.editProfile setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        self.cancel.hidden = YES;
        
    } else{
        
        self.isEditing = YES;
        [self userInteraction:YES];
        [self withBorders:YES];
        [self.editProfile setTitle:@"Save" forState:UIControlStateNormal];
        [self.editProfile setTitleColor:[UIColor greenColor] forState:UIControlStateNormal];
        self.cancel.hidden = NO;
        
    }
}

- (void) userInteraction: (BOOL) enable
{
    if (enable) {
        
        self.displayNameTextField.userInteractionEnabled = YES;
        self.companyTextField.userInteractionEnabled = YES;
        self.titleTextField.userInteractionEnabled = YES;
        self.emailTextField.userInteractionEnabled = YES;
        self.phoneTextField.userInteractionEnabled = YES;
        self.locationTextField.userInteractionEnabled = YES;
        
    } else {
        
        self.displayNameTextField.userInteractionEnabled = NO;
        self.companyTextField.userInteractionEnabled = NO;
        self.titleTextField.userInteractionEnabled = NO;
        self.emailTextField.userInteractionEnabled = NO;
        self.phoneTextField.userInteractionEnabled = NO;
        self.locationTextField.userInteractionEnabled = NO;
        
    }
}

- (void) withBorders: (BOOL) enable
{
    if (enable) {
        
        [self.displayNameTextField setBorderStyle:UITextBorderStyleRoundedRect];
        [self.companyTextField setBorderStyle:UITextBorderStyleRoundedRect];
        [self.titleTextField setBorderStyle:UITextBorderStyleRoundedRect];
        [self.emailTextField setBorderStyle:UITextBorderStyleRoundedRect];
        [self.phoneTextField setBorderStyle:UITextBorderStyleRoundedRect];
        [self.locationTextField setBorderStyle:UITextBorderStyleRoundedRect];
        
    } else {
        
        [self.displayNameTextField setBorderStyle:UITextBorderStyleNone];
        [self.companyTextField setBorderStyle:UITextBorderStyleNone];
        [self.titleTextField setBorderStyle:UITextBorderStyleNone];
        [self.emailTextField setBorderStyle:UITextBorderStyleNone];
        [self.phoneTextField setBorderStyle:UITextBorderStyleNone];
        [self.locationTextField setBorderStyle:UITextBorderStyleNone];
        
    }
}

-(void)populateGroupList
{
    NSString *url = [NSString stringWithFormat:@"%@/User/Groups/%d", DATABASE_URL,self.userID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backEndUtility getRequestForUrl:url];
    
    if (!deserializedDictionary)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Group not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        NSArray *jsonArray = [deserializedDictionary objectForKey:@"groups"];
        if (jsonArray.count > 0)
        {
            for (NSDictionary* groups in jsonArray)
            {
                [self.groupID addObject:[groups objectForKey:@"groupID"]];
            }
            [self populateGroupDetails];
        }
    }
}

-(void)populateGroupDetails
{
    for (int i = 0; i < [self.groupID count]; i++)
    {
        NSString *url = [NSString stringWithFormat:@"%@/Group/%d", DATABASE_URL,[self.groupID[i] integerValue]];
        url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        NSDictionary *deserializedDictionary = [self.backEndUtility getRequestForUrl:url];
        
        if (!deserializedDictionary)
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Group details not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
            [alert show];
        }
        else
        {
            [self.groupList addObject:[deserializedDictionary objectForKey:@"groupTitle"]];
            iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
            
            NSManagedObjectContext *context = [appDelegate managedObjectContext];
            NSManagedObject * newGroup = [NSEntityDescription insertNewObjectForEntityForName:@"Group" inManagedObjectContext:context];
            NSError *error;
            [newGroup setValue:[deserializedDictionary objectForKey:@"groupTitle"] forKey:@"groupTitle"];
            [newGroup setValue:self.groupID[i] forKey:@"groupID"];
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

-(void) refreshGroupList
{
    [self.createGroupVC dismissViewControllerAnimated:YES completion:nil];
    self.groupList = [[NSMutableArray alloc] init];
    self.groupID = [[NSMutableArray alloc] init];
    [self populateGroupList];
    [self.groupsTableView reloadData];
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
    cell.textLabel.text =  (NSString *)[self.groupList objectAtIndex:indexPath.row];
    cell.detailTextLabel.text = @"";
    [cell.deleteButton setTag:indexPath.row];
    return cell;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.groupList.count;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    self.createGroupVC = [[iWinViewAndCreateGroupViewController alloc] initWithNibName:@"iWinViewAndCreateGroupViewController" bundle:nil withUserID:self.userID withGroupID:[self.groupID[indexPath.row] integerValue]];
    //self.viewProjectVC.viewProjectDelegate = self;
    [self.createGroupVC setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.createGroupVC setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.createGroupVC animated:YES completion:nil];
    self.createGroupVC.view.superview.bounds = CGRectMake(MODAL_XOFFSET,MODAL_YOFFSET,MODAL_WIDTH,MODAL_HEIGHT);
}



- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    return NO;
}


@end
