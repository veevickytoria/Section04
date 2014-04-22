//
//  iWinAddUsersViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/25/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinAddUsersViewController.h"
#import "Contact.h"
#import <QuartzCore/QuartzCore.h>
#import "iWinAppDelegate.h"
#import "iWinConstants.h"
#import "iWinBackEndUtility.h"

@interface iWinAddUsersViewController ()
@property (nonatomic) NSString *pageName;
@property (nonatomic) BOOL isEditing;
@property (nonatomic) NSUInteger rowToDelete;
@property (nonatomic) UIAlertView *deleteAlertView;
@property (nonatomic) UIAlertView *inviteAlertView;
@property (nonatomic) iWinBackEndUtility *backendUtility;
@end

NSString* const USER_ATTENDEE_CELL_ID = @"UserAttendeeCell";
NSString* const NO_EMAIL_MESSAGE = @"You're device does not have email set up.";
NSString* const EMAIL_SUBJECT = @"Invitation to join Meeting Ninja";
NSString* const EMAIL_BODY = @"Hello! \n \nYou have been invited to join the Meeting Ninja community. Meeting Ninja as an iPad application that simplfies the management of meetings, tasks, & projects. \n\nTo accept the invitation and install the Meeting Ninja application, please visit: www.apple.com/appStore/Downloads/MeetingNinja\n\nAndroid and Web versions of Meeting Ninja are also available.";
NSString* const USER_LIST_URL = @"%@/User/Users";

@implementation iWinAddUsersViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withPageName:(NSString *)pageName inEditMode:(BOOL)isEditing
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.pageName = pageName;
        self.isEditing = isEditing;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self.userListTableView registerNib:[UINib nibWithNibName:CUSTOM_SUBTITLED_CELL bundle:nil] forCellReuseIdentifier:USER_ATTENDEE_CELL_ID];
    [self.searchDisplayController.searchResultsTableView registerNib:[UINib nibWithNibName:CUSTOM_SUBTITLED_CELL bundle:nil] forCellReuseIdentifier:USER_ATTENDEE_CELL_ID];
    self.attendeeList = [[NSMutableArray alloc] init];
    self.filteredList = [[NSMutableArray alloc] init];
    self.userList = [[NSMutableArray alloc] init];
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    NSManagedObjectContext *context = [appDelegate managedObjectContext];
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:CONTACT_ENTITY inManagedObjectContext:context];
    
    self.backendUtility = [[iWinBackEndUtility alloc] init];

    //for backend connection
    NSString *url = [NSString stringWithFormat:USER_LIST_URL, DATABASE_URL];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    NSArray *jsonArray;
    if (!deserializedDictionary)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:ERROR_MESSAGE message:USER_NOT_FOUND_MESSAGE delegate:self cancelButtonTitle:OK_BUTTON otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        jsonArray = [deserializedDictionary objectForKey:USERS_KEY];
    }
    if (jsonArray.count > 0)
    {
        for (NSDictionary* users in jsonArray)
        {
            Contact *c = [[Contact alloc] initWithEntity:entityDesc insertIntoManagedObjectContext:context];
            c.userID = [users objectForKey:USER_ID_KEY];

            c.name = (NSString *)[users objectForKey:NAME_KEY];
            c.email = (NSString *)[users objectForKey:EMAIL_KEY];
            c.phone = (NSString *)[users objectForKey:@"phone"];
            c.company = (NSString *)[users objectForKey:@"companyc"];
            c.title = (NSString *)[users objectForKey:TITLE_KEY];
            c.location = (NSString *)[users objectForKey:LOCATION_KEY];
            
            [self.userList addObject:c];
        }
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)sendInvitation:(MFMailComposeViewController *)mc
{
    mc.mailComposeDelegate = self;
    [mc setSubject:EMAIL_SUBJECT];
    [mc setMessageBody:EMAIL_BODY isHTML:NO];
    [mc setToRecipients:[NSArray arrayWithObject:@"[Enter Invitee Email Here!]"]];
    [self presentViewController:mc animated:YES completion:NULL];
}

- (void)alertEmailNotSetUp
{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:ERROR_MESSAGE message:NO_EMAIL_MESSAGE delegate:nil cancelButtonTitle:OK_BUTTON otherButtonTitles:nil, nil];
    [alert show];
}

- (IBAction)onClickSendInvite: (id) sender
{
    MFMailComposeViewController *mc = [[MFMailComposeViewController alloc] init];
    if ([MFMailComposeViewController canSendMail]){
        [self sendInvitation:mc];
    } else {
        [self alertEmailNotSetUp];
    }
}

-(void) mailComposeController:(MFMailComposeViewController *)controller didFinishWithResult:(MFMailComposeResult)result error:(NSError *)error
{
    switch(result)
    {
        case MFMailComposeResultCancelled:
            NSLog(@"Mail cancelled");
            break;
        case MFMailComposeResultSaved:
            NSLog(@"Mail saved");
            break;
        case MFMailComposeResultSent:
            NSLog(@"Mail sent");
            break;
        case MFMailComposeResultFailed:
            NSLog(@"Mail failed");
            break;
        default:
            break;
    }
    [self dismissViewControllerAnimated:YES completion:NULL];
}


- (IBAction)onClickSave
{
    //save
    [self.userDelegate selectedUsers:self.attendeeList];
    [self dismissViewControllerAnimated:YES completion:nil];
}   

- (IBAction)onClickCancel
{
    //[self.userDelegate returnToPreviousView:self.pageName inEditMode:self.isEditing];
    [self dismissViewControllerAnimated:YES completion:nil];
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    CustomSubtitledCell *cell = (CustomSubtitledCell *)[tableView dequeueReusableCellWithIdentifier:USER_ATTENDEE_CELL_ID];
    if (cell == nil)
    {
        cell = [[CustomSubtitledCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:USER_ATTENDEE_CELL_ID];
    }
    [cell initCell];
    cell.subTitledDelegate = self;
    
    Contact *c = nil;
    
    if ([tableView isEqual:self.searchDisplayController.searchResultsTableView])
    {
        c = (Contact *)[self.filteredList objectAtIndex:indexPath.row];
        cell.deleteButton.hidden = YES;
    }
    else
    {
        c = (Contact *)[self.attendeeList objectAtIndex:indexPath.row];
        cell.deleteButton.hidden = NO;
    }
    cell.deleteButton.tag = indexPath.row;
    cell.titleLabel.text =  c.name;
    if (c.name.length == 0){
        cell.titleLabel.text = c.email;
    }
    cell.detailLabel.text = c.email;
    return cell;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if ([tableView isEqual:self.searchDisplayController.searchResultsTableView])
    {
        return self.filteredList.count;
    }
    return self.attendeeList.count;
}

#pragma mark Content Filtering
-(void)filterContentForSearchText:(NSString*)searchText scope:(NSString*)scope {
    [self.filteredList removeAllObjects];
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"SELF.name contains[c] %@ OR SELF.email contains[c] %@", searchText, searchText];
    self.filteredList = [NSMutableArray arrayWithArray:[self.userList filteredArrayUsingPredicate:predicate]];
}

#pragma mark - UISearchDisplayController Delegate Methods
-(BOOL)searchDisplayController:(UISearchDisplayController *)controller
shouldReloadTableForSearchString:(NSString *)searchString
{
    [self filterContentForSearchText:searchString
                               scope:[[self.searchDisplayController.searchBar scopeButtonTitles]
                                      objectAtIndex:[self.searchDisplayController.searchBar
                                                     selectedScopeButtonIndex]]];
    
    return YES;
}


-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    if ([tableView isEqual:self.searchDisplayController.searchResultsTableView])
    {
        Contact *c = (Contact *)[self.filteredList objectAtIndex:indexPath.row];
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"SELF.email contains[c] %@", c.email];
        NSArray *checkArray = [self.attendeeList filteredArrayUsingPredicate:predicate];
        if (checkArray.count == 0)
        {
            [self.attendeeList addObject:c];
            [self.userListTableView reloadData];
        }
        [self.searchDisplayController setActive:NO];
    }
}

-(void)deleteCell:(NSInteger)row
{
    self.rowToDelete = row;
    self.deleteAlertView = [[UIAlertView alloc] initWithTitle:CONFIRM_DELETE_TITLE message:DELETE_CONTACT_MESSAGE delegate:self cancelButtonTitle:NO_DELETE_OPTION otherButtonTitles:YES_DELETE_OPTION, nil];
    [self.deleteAlertView show];
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if ([alertView isEqual:self.deleteAlertView])
    {
        if (buttonIndex == 1)
        {
            [self.attendeeList removeObjectAtIndex:self.rowToDelete];
            [self.userListTableView reloadData];
        }
        else
        {
            self.rowToDelete = -1;
        }
    }
    else
    {
        NSString *email = [alertView textFieldAtIndex:0].text;
        NSLog(@"%@", email);
    }
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    
    if ([self.pageName isEqualToString:@"ShareNotes"])
    {
        self.orLabel.hidden = YES;
        self.inviteButton.hidden = YES;
        self.titleLabel.text = @"Share With";
        
        CGRect barFrame = self.searchDisplayController.searchBar.frame;
        barFrame.size.width = 728;
        self.searchDisplayController.searchBar.frame = barFrame;
    }
    
}

-(void)initAttendeesList:(NSMutableArray*)attendees
{
    for (int i=0; i<attendees.count; i++)
    {
        [self.attendeeList addObject:attendees[i]];
    }
    [self.userListTableView reloadData];
}

@end
