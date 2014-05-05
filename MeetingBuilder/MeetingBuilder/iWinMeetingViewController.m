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
NSString* const MEETING_CELL_ID = @"MeetingCell";
NSString* const USER_MEETING_URL = @"%@/User/Meetings/%ld";
NSString* const MEETINGS_KEY = @"meetings";
NSString* const ATTENDEE_LIST_FORMAT = @"%@,";
NSString* const MEETING_DESCRIPTION_KEY = @"meetingDesc";

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
    [self.projectTable registerNib:[UINib nibWithNibName:CUSTOM_SUBTITLED_CELL bundle:nil] forCellReuseIdentifier: MEETING_CELL_ID];
    [self populateMeetingList];
}

-(void)populateMeetingList
{
    NSString *url = [NSString stringWithFormat:USER_MEETING_URL, DATABASE_URL, (long)self.userID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    if (!deserializedDictionary)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:ERROR_MESSAGE message:MEETING_NOT_FOUND_MESSAGE delegate:self cancelButtonTitle:OK_BUTTON otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        NSArray *jsonArray = [deserializedDictionary objectForKey:MEETINGS_KEY];
        if (jsonArray.count > 0)
        {
            for (NSDictionary* meetings in jsonArray)
            {
                if(![self.meetingID containsObject:[meetings objectForKey:ID_KEY]]){
                    [self.meetingList addObject:[meetings objectForKey:TITLE_KEY]];
                    [self.meetingID addObject:[meetings objectForKey:ID_KEY]];
                }
            }
            [self populateMeetingListDetails];
        }
    }
}

-(NSDictionary *)getMeetingDetailsFromStringId:(NSString*)meetingId
{
    NSString *url = [[NSString stringWithFormat:MEETING_URL, DATABASE_URL,[meetingId integerValue]] stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    return [self.backendUtility getRequestForUrl:url];
}


-(NSMutableString*)getAttendeeListFromJsonArray:(NSArray*)jsonArray
{
    NSMutableString *attendeeList = [[NSMutableString alloc] init];
    for (NSDictionary* users in jsonArray) {
        [attendeeList appendFormat:ATTENDEE_LIST_FORMAT, (NSString *)[users objectForKey:USER_ID_KEY]];
    }
    
    if (attendeeList.length > 0) {
        [attendeeList deleteCharactersInRange:NSMakeRange([attendeeList length]-1, 1)];
    }
    
    return attendeeList;
}

-(void)addMeetingDetailsToLocalDatabase:(NSDictionary*)deserializedDictionary
{
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    NSManagedObjectContext *context = [appDelegate managedObjectContext];
    NSManagedObject *newMeeting = [NSEntityDescription insertNewObjectForEntityForName:MEETING_HEADER inManagedObjectContext:context];
    NSError *error;
    [newMeeting setValue:[deserializedDictionary objectForKey:TITLE_KEY] forKey:TITLE_KEY];
    [newMeeting setValue:[deserializedDictionary objectForKey:LOCATION_KEY] forKey:LOCATION_KEY];
    [newMeeting setValue:[deserializedDictionary objectForKey:END_DATE_TIME_KEY] forKey:END_DATE_TIME_KEY];
    [newMeeting setValue:[deserializedDictionary objectForKey:MEETING_ID_KEY]forKey:MEETING_ID_KEY];
    [newMeeting setValue:[deserializedDictionary objectForKey:DATE_TIME_KEY] forKey:DATE_TIME_KEY];
    [newMeeting setValue:[NSNumber numberWithInt:self.userID] forKey:USER_ID_KEY];
    [newMeeting setValue:[self getAttendeeListFromJsonArray:[deserializedDictionary objectForKey:ATTENDANCE_KEY]] forKey:ATTENDANCE_KEY];
    [newMeeting setValue:[deserializedDictionary objectForKey:DESCRIPTION_KEY] forKey:MEETING_DESCRIPTION_KEY];
    [context save:&error];
}


-(void)alertUserOfErrorToLoadMeetingDetails
{
    [[[UIAlertView alloc] initWithTitle:ERROR_MESSAGE message:MEETING_NOT_FOUND_MESSAGE delegate:self cancelButtonTitle:OK_BUTTON otherButtonTitles: nil] show];
}

+(NSString*)getDateTimeStringFromEpochString:(NSString*)epochString
{
    return [iWinScheduleViewMeetingViewController getStringDateTimeFromDate:[NSDate dateWithTimeIntervalSince1970:[epochString doubleValue]]];
}

-(BOOL)populateMeetingRowWithDetails:(NSDictionary*)deserializedDictionary
{
    
    if (!deserializedDictionary) return NO;
    [self.meetingDetail addObject:[iWinMeetingViewController getDateTimeStringFromEpochString:[deserializedDictionary objectForKey:DATE_TIME_KEY]]];
    [self.meetingLocations addObject:[deserializedDictionary objectForKey:LOCATION_KEY]];
    [self addMeetingDetailsToLocalDatabase:deserializedDictionary];
    
    return YES;
}


-(void)populateMeetingListDetails
{
    for (int i = 0; i < [self.meetingID count]; i++) {
        
        if (![self populateMeetingRowWithDetails:[self getMeetingDetailsFromStringId:self.meetingID[i]]]) {
            [self alertUserOfErrorToLoadMeetingDetails];
        }
    }
}

-(NSArray*)getDataFromDatabase
{
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    
    NSManagedObjectContext *context = [appDelegate managedObjectContext];
    
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:MEETING_HEADER inManagedObjectContext:context];
    
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
    self.scheduleMeetingVC = [[iWinScheduleViewMeetingViewController alloc] initWithNibName:VIEW_AND_SCHEDULE_MEETING_NIB bundle:nil withUserID:self.userID withMeetingID:-1];
    self.scheduleMeetingVC.viewMeetingDelegate = self;
    [self.scheduleMeetingVC setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.scheduleMeetingVC setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.scheduleMeetingVC animated:YES completion:nil];
    self.scheduleMeetingVC.view.superview.bounds = CGRectMake(MODAL_XOFFSET,MODAL_YOFFSET,MODAL_WIDTH,MODAL_HEIGHT);
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    CustomSubtitledCell *cell = (CustomSubtitledCell *)[tableView dequeueReusableCellWithIdentifier:MEETING_CELL_ID];
    if (cell == nil)
    {
        cell = [[CustomSubtitledCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:MEETING_CELL_ID];
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
    self.scheduleMeetingVC = [[iWinScheduleViewMeetingViewController alloc] initWithNibName:VIEW_AND_SCHEDULE_MEETING_NIB bundle:nil withUserID:self.userID withMeetingID:[self.meetingID[indexPath.row] integerValue]];
    self.scheduleMeetingVC.viewMeetingDelegate = self;
    [self.scheduleMeetingVC setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.scheduleMeetingVC setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.scheduleMeetingVC animated:YES completion:nil];
    self.scheduleMeetingVC.view.superview.bounds = CGRectMake(MODAL_XOFFSET,MODAL_YOFFSET,MODAL_WIDTH,MODAL_HEIGHT);
    
}

-(void)deleteCell:(NSInteger)row
{
    self.selectedMeeting = row;
    UIAlertView *deleteAlertView = [[UIAlertView alloc] initWithTitle:CONFIRM_DELETE_TITLE message:DELETE_MEETING_MESSAGE delegate:self cancelButtonTitle:NO_DELETE_OPTION otherButtonTitles:YES_DELETE_OPTION, nil];
    [deleteAlertView show];
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 1)
    {
        NSString *url = [NSString stringWithFormat:MEETING_URL, DATABASE_URL,[[self.meetingID objectAtIndex:self.selectedMeeting] integerValue]];
        NSError *error = [self.backendUtility deleteRequestForUrl:url];
        if (!error)
        {
            self.selectedMeeting = -1;
            [self refreshMeetingList];
        }
    }
}

@end
