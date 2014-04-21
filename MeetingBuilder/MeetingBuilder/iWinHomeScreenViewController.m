//
//  iWinHomeScreenViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinHomeScreenViewController.h"
#import "iWinBackEndUtility.h"
#import "iWinMeetingViewController.h"
#import "iWinAddAndViewTaskViewController.h"
#import "iWinConstants.h"

@interface iWinHomeScreenViewController ()
@property (nonatomic) NSMutableArray *headers;
@property (nonatomic) NSMutableArray *taskFeed;
@property (nonatomic) NSMutableArray *notificationFeed;
@property (nonatomic) NSMutableArray *meetingFeed;
@property (nonatomic) NSMutableArray *noteFeed;
@property (nonatomic) NSMutableArray *noteFeedSubtitle;
@property (strong, nonatomic) iWinBackEndUtility *backendUtility;
@property (strong, nonatomic) iWinScheduleViewMeetingViewController *scheduleMeetingVC;
@property (strong, nonatomic) iWinAddAndViewTaskViewController *addViewTaskViewController;
@property (nonatomic) NSInteger userID;
@end

//constants
NSString* const ID_KEY = @"id";
NSString* const MEETING_HEADER = @"Meeting";
NSString* const TASK_HEADER = @"Task";
NSString* const MISC_HEADER = @"Miscellaneous";
NSString* const IS_COMPLETED_KEY = @"isCompleted";
NSString* const SCHEDULE_KEY = @"schedule";
NSString* const MEETING_KEY = @"meeting";
NSString* const TYPE_KEY = @"type";
NSString* const NOTIFICATIONS_KEY = @"notifications";
NSString* const USER_NAME_KEY = @"userName";
NSString* const NOTE_TITLE_KEY = @"noteTitle";
NSString* const DATE_TIME_START = @"datetimeStart";
NSString* const DATE_TIME_END = @"datetimeEnd";
NSString* const TASK_URL = @"%@/Task/%ld";
NSString* const SCHEDULE_URL = @"%@/User/Schedule/%@";
NSString* const NOTIFICATION_URL = @"%@/User/Notification/%@";
NSString* const SHARING_URL = @"%@/User/Sharing/%ld";
NSString* const SHARED_BY_STRING = @"Shared by %@";
NSString* const NOTE_KEY = @"notes";
NSString* const SHARED_WITH_FORMAT = @"Note '%@' has been shared with you.";
NSString* const TITLE_KEY = @"title";
NSString* const DESCRIPTION_KEY = @"description";
NSString* const TO_SEPARATOR = @" to ";
NSString* const COLON_SEPARATOR = @": ";
NSString* const DATE_TIME_KEY = @"datetime";

@implementation iWinHomeScreenViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger)userID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        NSMutableDictionary *defaultDict = [[NSMutableDictionary alloc] init];
        [defaultDict setValue:[NSString stringWithFormat:@"%ld", (long)userID] forKey:USER_ID_KEY];
        [[NSUserDefaults standardUserDefaults] registerDefaults:defaultDict];
        [[NSUserDefaults standardUserDefaults] synchronize];
        self.userID = userID;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];    
    self.headers = [[NSMutableArray alloc] init];
    self.taskFeed = [[NSMutableArray alloc] init];
    self.notificationFeed = [[NSMutableArray alloc] init];
    self.meetingFeed = [[NSMutableArray alloc] init];
    self.backendUtility = [[iWinBackEndUtility alloc] init];
    
    [self.headers addObject:MEETING_HEADER];
    [self.headers addObject:TASK_HEADER];
    [self.headers addObject:MISC_HEADER];
    
    [self initMeetingAndTaskFeed];
    [self initNotificationFeed];
    [self initMiscellanous];
    
}

- (void)addTask:(NSDictionary *)element
{
    NSString *url;
    NSInteger taskID =[[element objectForKey:ID_KEY] integerValue];
    
    url = [NSString stringWithFormat:TASK_URL, DATABASE_URL,(long)taskID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *taskDeserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    if(![[taskDeserializedDictionary objectForKey:IS_COMPLETED_KEY] boolValue]){
        [self.taskFeed addObject:element];
    }
}

- (void)initMeetingAndTaskFeed
{
    NSString *url = [NSString stringWithFormat:SCHEDULE_URL, DATABASE_URL,[[NSUserDefaults standardUserDefaults] objectForKey:USER_ID_KEY]];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    if (deserializedDictionary)
    {
        NSArray *jsonArray = [deserializedDictionary objectForKey:SCHEDULE_KEY];
        for(int i = 0; i < [jsonArray count]; i++){
            NSDictionary* element = [jsonArray objectAtIndex:i];
            if([[element objectForKey:TYPE_KEY] isEqualToString:MEETING_KEY]){
                [self.meetingFeed addObject:element];
            }
            
            else {
                [self addTask:element];
            }
        }
    }
}

- (void)initNotificationFeed
{
    NSString *urlNotification = [NSString stringWithFormat:NOTIFICATION_URL, DATABASE_URL,[[NSUserDefaults standardUserDefaults] objectForKey:USER_ID_KEY]];
    
    urlNotification = [urlNotification stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionaryNotification = [self.backendUtility getRequestForUrl:urlNotification];
    
    if (deserializedDictionaryNotification)
    {
        NSArray *jsonArray = [deserializedDictionaryNotification objectForKey:NOTIFICATIONS_KEY];
        for(int i = 0; i < [jsonArray count]; i++){
            NSDictionary* element = [jsonArray objectAtIndex:i];
            [self.notificationFeed addObject:element];
        }
    }
}

-(void)initMiscellanous
{
    self.noteFeed = [[NSMutableArray alloc] init];
    self.noteFeedSubtitle = [[NSMutableArray alloc] init];
    
    NSString *url = [NSString stringWithFormat:SHARING_URL, DATABASE_URL,(long)self.userID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    // parse json into variables --> Array of dictionaries
    for (NSDictionary *sharingUser in deserializedDictionary ) {
        // then for each user - put there userName into table view
        [self.noteFeedSubtitle addObject:[NSString stringWithFormat:SHARED_BY_STRING,[sharingUser objectForKey:USER_NAME_KEY]]];
    
        for (NSDictionary *d in [sharingUser objectForKey:NOTE_KEY])
            [self.noteFeed addObject:[NSString stringWithFormat:SHARED_WITH_FORMAT, [d objectForKey:NOTE_TITLE_KEY]]];
    }
}


-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{

      UITableViewCell *cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"Cell"];
    
    
        if (indexPath.section == 0)
        {
            NSDictionary* meeting = (NSDictionary*)[self.meetingFeed objectAtIndex:indexPath.row];
            cell.textLabel.text = (NSString*)[meeting objectForKey:TITLE_KEY];
            cell.detailTextLabel.text = (NSString*)[iWinMeetingViewController getDateTimeStringFromEpochString:[meeting objectForKey:DATE_TIME_START]];
        }
    
        else if (indexPath.section == 1)
        {
            NSDictionary* task = (NSDictionary*)[self.taskFeed objectAtIndex:indexPath.row];
            cell.detailTextLabel.text= (NSString*)[[[iWinMeetingViewController getDateTimeStringFromEpochString:[task objectForKey:DATE_TIME_START]]
                                                    stringByAppendingString:TO_SEPARATOR]stringByAppendingString:[iWinMeetingViewController getDateTimeStringFromEpochString:[task objectForKey:DATE_TIME_END]]];
            cell.textLabel.text = (NSString*)[[[task objectForKey:TITLE_KEY] stringByAppendingString:COLON_SEPARATOR] stringByAppendingString:[task objectForKey:DESCRIPTION_KEY]];
        }
    
        else
        {
            NSInteger notificationLength = self.notificationFeed.count;
            
            if (indexPath.row < notificationLength){
                NSDictionary* notification = (NSDictionary*) [self.notificationFeed objectAtIndex:indexPath.row];
                cell.textLabel.text = (NSString*)[[[notification objectForKey:DESCRIPTION_KEY] stringByAppendingString:COLON_SEPARATOR]stringByAppendingString:[iWinMeetingViewController getDateTimeStringFromEpochString:[notification objectForKey:DATE_TIME_KEY]]];
            }else{
                cell.textLabel.text = (NSString*)[self.noteFeed objectAtIndex:(notificationLength - indexPath.row)];
                cell.detailTextLabel.text = (NSString*)[self.noteFeedSubtitle objectAtIndex:(notificationLength - indexPath.row)];
            }
        }
    
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    //meeting
    if(indexPath.section == 0){
        [tableView deselectRowAtIndexPath:indexPath animated:YES];
        self.scheduleMeetingVC = [[iWinScheduleViewMeetingViewController alloc] initWithNibName:VIEW_AND_SCHEDULE_MEETING_NIB bundle:nil withUserID:[[[NSUserDefaults standardUserDefaults] objectForKey:USER_ID_KEY] intValue] withMeetingID:[[[self.meetingFeed objectAtIndex:indexPath.row] objectForKey:ID_KEY] intValue]];
        self.scheduleMeetingVC.viewMeetingDelegate = self;
        [self.scheduleMeetingVC setModalPresentationStyle:UIModalPresentationPageSheet];
        [self.scheduleMeetingVC setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
        
        [self presentViewController:self.scheduleMeetingVC animated:YES completion:nil];
        self.scheduleMeetingVC.view.superview.bounds = CGRectMake(MODAL_XOFFSET,MODAL_YOFFSET,MODAL_WIDTH,MODAL_HEIGHT);
        
    }
    else if(indexPath.section == 1){ //task
        self.addViewTaskViewController = [[iWinAddAndViewTaskViewController alloc] initWithNibName:ADD_AND_VIEW_TASK_NIB bundle:nil withUserID:[[[NSUserDefaults standardUserDefaults] objectForKey:USER_ID_KEY] intValue] withTaskID: [[[self.taskFeed objectAtIndex:indexPath.row] objectForKey:ID_KEY] intValue]];
        [self.addViewTaskViewController setModalPresentationStyle:UIModalPresentationPageSheet];
        [self.addViewTaskViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
        self.addViewTaskViewController.viewTaskDelegate = self;
        
        [self presentViewController:self.addViewTaskViewController animated:YES completion:nil];
        self.addViewTaskViewController.view.superview.bounds = CGRectMake(MODAL_XOFFSET,MODAL_YOFFSET,MODAL_WIDTH,MODAL_HEIGHT);
    
    }

}


-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (section == 0)
    {
        return self.meetingFeed.count;
    }
    if (section == 1)
    {
        return self.taskFeed.count;
    }
    else
    {
        return (self.notificationFeed.count + self.noteFeed.count);
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return self.headers.count;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
    return [self.headers objectAtIndex:section];
}

-(void)refreshMeetingList {
    [self.scheduleMeetingVC dismissViewControllerAnimated:YES completion:nil];
    [self viewDidLoad];
    [self.feedTable reloadData];
}

-(void)refreshTaskList
{
    [self.addViewTaskViewController dismissViewControllerAnimated:YES completion:nil];
    [self viewDidLoad ];
    [self.feedTable reloadData];
}



@end
