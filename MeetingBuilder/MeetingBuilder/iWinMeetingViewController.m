//
//  iWinProjectViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/2/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinMeetingViewController.h"
#import "Meeting.h"
#import <QuartzCore/QuartzCore.h>
#import "iWinAppDelegate.h"

@interface iWinMeetingViewController ()
@property (strong, nonatomic) NSMutableArray *meetingList;
@property (strong, nonatomic) NSMutableArray *meetingDetail;
@property (nonatomic) NSInteger userID;
@property (strong, nonatomic) iWinScheduleViewMeetingViewController *scheduleMeetingVC;
@property (strong, nonatomic) NSMutableArray *meetingID;
@property (strong, nonatomic) NSMutableArray *meetingLocations;

@end

@implementation iWinMeetingViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withID:(NSInteger)userID
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
    self.meetingList = [[NSMutableArray alloc] init];
    self.meetingDetail = [[NSMutableArray alloc] init];
    self.meetingID = [[NSMutableArray alloc] init];
    self.meetingLocations = [[NSMutableArray alloc] init];
    
    [self populateMeetingList];
//    NSArray *result = [self getDataFromDatabase];
//    for (Meeting *m in result)
//    {
//        [self.meetingList addObject:m.title];
//        [self.meetingDetail addObject:m.datetime];
//        [self.meetingID addObject:m.userID];
//        [self.meetingLocations addObject:m.location];
//    }

    
    //for local database
//    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
//    
//    NSManagedObjectContext *context = [appDelegate managedObjectContext];
//    
//    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Meeting" inManagedObjectContext:context];
//    
//    NSFetchRequest *request = [[NSFetchRequest alloc] init];
//    [request setEntity:entityDesc];
//    
//    NSError *error;
//    NSArray *result = [context executeFetchRequest:request
//                                             error:&error];
//    for (Meeting *m in result)
//    {
//        [self.meetingList addObject:m.title];
//        [self.meetingDetail addObject:m.datetime];
//        [self.meetingID addObject:m.userID];
//        [self.meetingLocations addObject:m.location];
//    }
    
    
//    self.scheduleMeetingButton.layer.cornerRadius = 7;
//    self.scheduleMeetingButton.layer.borderColor = [[UIColor darkGrayColor] CGColor];
//    self.scheduleMeetingButton.layer.borderWidth = 2.0f;
    
}

-(void)populateMeetingList
{
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/User/Meetings/%d", self.userID];
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
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Meetings not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        NSError *jsonParsingError = nil;
        NSDictionary *deserializedDictionary = (NSDictionary *)[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers|NSJSONReadingAllowFragments error:&jsonParsingError];
        jsonArray = [deserializedDictionary objectForKey:@"meetings"];
    }
    if (jsonArray.count > 0)
    {
        for (NSDictionary* meetings in jsonArray)
        {
            [self.meetingList addObject:[meetings objectForKey:@"title"]];
            [self.meetingID addObject:[meetings objectForKey:@"meetingID"]];
        }
        [self populateMeetingDetails];
    }
    
}

-(void)populateMeetingDetails
{
    for (int i = 0; i < [self.meetingID count]; i++)
    {
        NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Meeting/%d", [self.meetingID[i] integerValue]];
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
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Meetings not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
            [alert show];
        }
        else
        {
            NSError *jsonParsingError = nil;
            NSDictionary *deserializedDictionary = (NSDictionary *)[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers|NSJSONReadingAllowFragments error:&jsonParsingError];
            [self.meetingDetail addObject:[deserializedDictionary objectForKey:@"datetime"]];
            [self.meetingLocations addObject:[deserializedDictionary objectForKey:@"location"]];
            
            iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
            
            NSManagedObjectContext *context = [appDelegate managedObjectContext];
            
            NSManagedObject *newMeeting = [NSEntityDescription insertNewObjectForEntityForName:@"Meeting" inManagedObjectContext:context];
            NSError *error;
            [newMeeting setValue:[deserializedDictionary objectForKey:@"title"] forKey:@"title"];
            [newMeeting setValue:[deserializedDictionary objectForKey:@"location"] forKey:@"location"];
            [newMeeting setValue:[deserializedDictionary objectForKey:@"endDatetime"] forKey:@"endDatetime"];
            [newMeeting setValue:[deserializedDictionary objectForKey:@"meetingID"] forKey:@"meetingID"];
            [newMeeting setValue:[deserializedDictionary objectForKey:@"datetime"] forKey:@"datetime"];
            [newMeeting setValue:[NSNumber numberWithInt:self.userID] forKey:@"userID"];
            [newMeeting setValue:[deserializedDictionary objectForKey:@"attendance"] forKey:@"attendance"];
            [newMeeting setValue:[deserializedDictionary objectForKey:@"description"] forKey:@"description"];
            [context save:&error];
        }
    }
}

-(NSArray*)getDataFromDatabase
{
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    
    NSManagedObjectContext *context = [appDelegate managedObjectContext];
    
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Meeting" inManagedObjectContext:context];
    
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDesc];
    
    NSError *error;
    NSArray *result = [context executeFetchRequest:request
                                             error:&error];
    return result;
}

-(void) refreshMeetingList
{
    [self.scheduleMeetingVC dismissViewControllerAnimated:YES completion:nil];
    self.meetingList = [[NSMutableArray alloc] init];
    self.meetingDetail = [[NSMutableArray alloc] init];
    self.meetingID = [[NSMutableArray alloc] init];
    self.meetingLocations = [[NSMutableArray alloc] init];
    
//    NSArray *result = [self getDataFromDatabase];
//    for (Meeting *m in result)
//    {
//        [self.meetingList addObject:m.title];
//        [self.meetingDetail addObject:m.datetime];
//        [self.meetingID addObject:m.userID];
//        [self.meetingLocations addObject:m.location];
//    }
    [self populateMeetingList];
    [self.projectTable reloadData];
}

-(IBAction) onScheduleNewMeeting
{
    self.scheduleMeetingVC = [[iWinScheduleViewMeetingViewController alloc] initWithNibName:@"iWinScheduleViewMeetingViewController" bundle:nil withUserID:self.userID withMeetingID:-1];
    self.scheduleMeetingVC.viewMeetingDelegate = self;
    [self.scheduleMeetingVC setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.scheduleMeetingVC setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.scheduleMeetingVC animated:YES completion:nil];
    self.scheduleMeetingVC.view.superview.bounds = CGRectMake(0,0,768,1003);
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    
    NSManagedObjectContext *context = [appDelegate managedObjectContext];
    
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Meeting" inManagedObjectContext:context];
    
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDesc];
    
    NSError *error;
    NSArray *result = [context executeFetchRequest:request
                                             error:&error];
    for (Meeting *m in result)
    {
        [self.meetingList addObject:m.title];
        [self.meetingDetail addObject:m.datetime];
        [self.meetingID addObject:m.userID];
        [self.meetingLocations addObject:m.location];
    }
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"MeetingCell"];
    if (cell == nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"MeetingCell"];
    }
    
    cell.textLabel.text = (NSString *)[self.meetingList objectAtIndex:indexPath.row];
    cell.detailTextLabel.text = (NSString *)[self.meetingDetail objectAtIndex:indexPath.row];
    return cell;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.meetingList.count;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    self.scheduleMeetingVC = [[iWinScheduleViewMeetingViewController alloc] initWithNibName:@"iWinScheduleViewMeetingViewController" bundle:nil withUserID:self.userID withMeetingID:[self.meetingID[indexPath.row] integerValue]];
    self.scheduleMeetingVC.viewMeetingDelegate = self;
    [self.scheduleMeetingVC setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.scheduleMeetingVC setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.scheduleMeetingVC animated:YES completion:nil];
    self.scheduleMeetingVC.view.superview.bounds = CGRectMake(0,0,768,1003);
    
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    return YES;
}

// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        //add code here for when you hit delete
    }
}

@end
