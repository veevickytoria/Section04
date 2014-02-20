//
//  iWinProjectViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/2/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinMeetingViewController.h"
#import "Meeting.h"
#import "iWinBackEndUtility.h"
#import <QuartzCore/QuartzCore.h>
#import "iWinAppDelegate.h"
#import "iWinConstants.h"

@interface iWinMeetingViewController ()
@property (strong, nonatomic) NSMutableArray *meetingList;
@property (strong, nonatomic) NSMutableArray *meetingDetail;
@property (nonatomic) NSInteger userID;
@property (strong, nonatomic) iWinScheduleViewMeetingViewController *scheduleMeetingVC;
@property (strong, nonatomic) NSMutableArray *meetingID;
@property (strong, nonatomic) NSMutableArray *meetingLocations;
@property (nonatomic) NSInteger selectedMeeting;
@property (strong, nonatomic) iWinBackEndUtility *backendUtility;
@end

//constants


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
    self.selectedMeeting = -1;
    self.backendUtility = [[iWinBackEndUtility alloc] init];
    [self.projectTable registerNib:[UINib nibWithNibName:@"CustomSubtitledCell" bundle:nil] forCellReuseIdentifier:@"MeetingCell"];
    [self populateMeetingList];
}

-(void)populateMeetingList
{
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/User/Meetings/%d", self.userID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    if (!deserializedDictionary)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Meetings not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        NSArray *jsonArray = [deserializedDictionary objectForKey:@"meetings"];
        if (jsonArray.count > 0)
        {
            for (NSDictionary* meetings in jsonArray)
            {
                [self.meetingList addObject:[meetings objectForKey:@"title"]];
                [self.meetingID addObject:[meetings objectForKey:@"id"]];
            }
            [self populateMeetingDetails];
            
            
        }
    }
}

-(void)populateMeetingDetails
{
    for (int i = 0; i < [self.meetingID count]; i++)
    {
        NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Meeting/%d", [self.meetingID[i] integerValue]];
        url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
        
        if (!deserializedDictionary)
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Meetings not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
            [alert show];
        }
        else
        {
            [self.meetingDetail addObject:[deserializedDictionary objectForKey:@"datetime"]];
            [self.meetingLocations addObject:[deserializedDictionary objectForKey:@"location"]];
            
            NSArray *jsonArray = [deserializedDictionary objectForKey:@"attendance"];
            NSMutableString *attendeeList = [[NSMutableString alloc] init];
            if (jsonArray.count > 0)
            {
                for (NSDictionary* users in jsonArray)
                {
                    [attendeeList appendFormat:@"%@,", (NSString *)[users objectForKey:@"userID"]];
                }
            }
            
            [attendeeList deleteCharactersInRange:NSMakeRange([attendeeList length]-1, 1)];
            
            //add it to local database so accessing the info for a meeting is faster.
            iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
            
            NSManagedObjectContext *context = [appDelegate managedObjectContext];
            
            NSManagedObject *newMeeting = [NSEntityDescription insertNewObjectForEntityForName:@"Meeting" inManagedObjectContext:context];
            NSError *error;
            [newMeeting setValue:[deserializedDictionary objectForKey:@"title"] forKey:@"title"];
            [newMeeting setValue:[deserializedDictionary objectForKey:@"location"] forKey:@"location"];
            [newMeeting setValue:[deserializedDictionary objectForKey:@"endDatetime"] forKey:@"endDatetime"];
            [newMeeting setValue:self.meetingID[i] forKey:@"meetingID"];
            [newMeeting setValue:[deserializedDictionary objectForKey:@"datetime"] forKey:@"datetime"];
            [newMeeting setValue:[NSNumber numberWithInt:self.userID] forKey:@"userID"];
            [newMeeting setValue:attendeeList forKey:@"attendance"];
            [newMeeting setValue:[deserializedDictionary objectForKey:@"description"] forKey:@"meetingDesc"];
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
    [self populateMeetingList];
    [self.projectTable reloadData];
    [self.reloadScheduleDelegate loadScheduleView];
}

-(IBAction) onScheduleNewMeeting
{
    self.scheduleMeetingVC = [[iWinScheduleViewMeetingViewController alloc] initWithNibName:@"iWinScheduleViewMeetingViewController" bundle:nil withUserID:self.userID withMeetingID:-1];
    self.scheduleMeetingVC.viewMeetingDelegate = self;
    [self.scheduleMeetingVC setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.scheduleMeetingVC setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.scheduleMeetingVC animated:YES completion:nil];
    self.scheduleMeetingVC.view.superview.bounds = CGRectMake(MODAL_XOFFSET,MODAL_YOFFSET,MODAL_WIDTH,MODAL_HEIGHT);
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    CustomSubtitledCell *cell = (CustomSubtitledCell *)[tableView dequeueReusableCellWithIdentifier:@"MeetingCell"];
    if (cell == nil)
    {
        cell = [[CustomSubtitledCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"MeetingCell"];
    }
    [cell initCell];
    cell.subTitledDelegate = self;
    [cell.titleLabel  setText:(NSString *)[self.meetingList objectAtIndex:indexPath.row]];
    [cell.detailLabel setText:(NSString *)[self.meetingDetail objectAtIndex:indexPath.row]];
    [cell.deleteButton setTag:indexPath.row];
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
    self.scheduleMeetingVC.view.superview.bounds = CGRectMake(MODAL_XOFFSET,MODAL_YOFFSET,MODAL_WIDTH,MODAL_HEIGHT);
    
}

-(void)deleteCell:(NSInteger)row
{
    self.selectedMeeting = row;
    UIAlertView *deleteAlertView = [[UIAlertView alloc] initWithTitle:@"Confirm Delete" message:@"Are you sure you want to delete this meeting?" delegate:self cancelButtonTitle:@"No, just kidding!" otherButtonTitles:@"Yes, please", nil];
    [deleteAlertView show];
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 1)
    {
        NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Meeting/%d", [[self.meetingID objectAtIndex:self.selectedMeeting] integerValue]];
        NSError *error = [self.backendUtility deleteRequestForUrl:url];
        if (!error)
        {
            self.selectedMeeting = -1;
            [self refreshMeetingList];
        }
    }
}

@end
