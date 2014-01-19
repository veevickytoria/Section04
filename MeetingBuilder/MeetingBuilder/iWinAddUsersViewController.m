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

@interface iWinAddUsersViewController ()
@property (nonatomic) NSString *pageName;
@property (nonatomic) BOOL isEditing;
@property (nonatomic) NSUInteger rowToDelete;
@property (nonatomic) UIAlertView *deleteAlertView;
@property (nonatomic) UIAlertView *inviteAlertView;
@end

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
    // Do any additional setup after loading the view from its nib.
    
    self.attendeeList = [[NSMutableArray alloc] init];
    self.filteredList = [[NSMutableArray alloc] init];
    self.userList = [[NSMutableArray alloc] init];

    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    
    NSManagedObjectContext *context = [appDelegate managedObjectContext];
    
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Contact" inManagedObjectContext:context];
    
    //for local database
//
//    NSFetchRequest *request = [[NSFetchRequest alloc] init];
//    [request setEntity:entityDesc];
//    
//    NSError *error;
//    NSArray *result = [context executeFetchRequest:request
//                                              error:&error];
//    
//    self.userList = [[NSMutableArray alloc] initWithArray:result];
    
    
    //for backend connection
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/User/Users"];
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
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Users not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        NSError *jsonParsingError = nil;
         NSDictionary *deserializedDictionary = (NSDictionary *)[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers|NSJSONReadingAllowFragments error:&jsonParsingError];
        //jsonArray = [[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers|NSJSONReadingAllowFragments error:&jsonParsingError] objectAtIndex:0];
        jsonArray = [deserializedDictionary objectForKey:@"users"];
    }
    if (jsonArray.count > 0)
    {
        for (NSDictionary* users in jsonArray)
        {
            Contact *c = [[Contact alloc] initWithEntity:entityDesc insertIntoManagedObjectContext:context];
            c.userID = [users objectForKey:@"userID"];

            c.name = (NSString *)[users objectForKey:@"name"];
            c.email = (NSString *)[users objectForKey:@"email"];
            c.phone = (NSString *)[users objectForKey:@"phone"];
            c.company = (NSString *)[users objectForKey:@"companyc"];
            c.title = (NSString *)[users objectForKey:@"title"];
            c.location = (NSString *)[users objectForKey:@"location"];
            
            [self.userList addObject:c];
        }
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)onClickSendInvite
{
    
    self.inviteAlertView = [[UIAlertView alloc] initWithTitle:@"Invite User" message:@"Enter user email" delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"Invite", nil];
    [self.inviteAlertView setAlertViewStyle:UIAlertViewStylePlainTextInput];
    [self.inviteAlertView show];
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
    UITableViewCell *cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"UserCell"];
    
    Contact *c = nil;
    
    if ([tableView isEqual:self.searchDisplayController.searchResultsTableView])
    {
        c = (Contact *)[self.filteredList objectAtIndex:indexPath.row];
    }
    else
    {
        c = (Contact *)[self.attendeeList objectAtIndex:indexPath.row];
    }
    
    cell.textLabel.text =  c.name;
    if (c.name.length == 0){
        cell.textLabel.text = c.email;
    }
    cell.detailTextLabel.text = c.email;
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
//            if ([self.pageName isEqualToString:@"Task"]){
//                self.attendeeList[0] = c;
//            }else{
                [self.attendeeList addObject:c];
                [self.userListTableView reloadData];
//            }
        }
        [self.searchDisplayController setActive:NO];
    }
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    return YES;
}

// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        self.rowToDelete = indexPath.row;
        self.deleteAlertView = [[UIAlertView alloc] initWithTitle:@"Confirm Delete" message:@"Are you sure you want to delete this contact?" delegate:self cancelButtonTitle:@"No, just kidding!" otherButtonTitles:@"Yes, please", nil];
        [self.deleteAlertView show];
    }
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

@end
