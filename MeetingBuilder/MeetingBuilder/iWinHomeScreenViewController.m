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
@property (strong, nonatomic) iWinBackEndUtility *backendUtility;
@property (strong, nonatomic) iWinScheduleViewMeetingViewController *scheduleMeetingVC;
@property (strong, nonatomic) iWinAddAndViewTaskViewController *addViewTaskViewController;
@end



@implementation iWinHomeScreenViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger)userID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        NSMutableDictionary *defaultDict = [[NSMutableDictionary alloc] init];
        [defaultDict setValue:[NSString stringWithFormat:@"%d", userID] forKey:@"userID"];
        [[NSUserDefaults standardUserDefaults] registerDefaults:defaultDict];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
    return self;
}


- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    self.headers = [[NSMutableArray alloc] init];
    self.taskFeed = [[NSMutableArray alloc] init];
    self.notificationFeed = [[NSMutableArray alloc] init];
    self.meetingFeed = [[NSMutableArray alloc] init];
    self.backendUtility = [[iWinBackEndUtility alloc] init];
    
    [self.headers addObject:@"Meeting"];
    [self.headers addObject:@"Task"];
    [self.headers addObject:@"Miscellaneous"];
    
//    [self.taskFeed addObject:@"Jim has invited you to Sprint Planning."];
//    [self.taskFeed addObject:@"12:00 - 13:00, 10/25/13"];
//    [self.meetingFeed addObject:@"Steve has assigned you Research Libraries"];
//    [self.meetingFeed addObject:@"Due: 12:00, 10/26/13"];
//    [self.notificationFeed addObject:@"Mary shared Meeting Minutes 9/21/13"];
    
    
    //For meetingFeed and taskFeed
    NSString *url = [NSString stringWithFormat:@"%@/User/Schedule/%@", DATABASE_URL,[[NSUserDefaults standardUserDefaults] objectForKey:@"userID"]];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    if (!deserializedDictionary)
    {
//        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"schedule not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
//        [alert show];
    }
    else
    {
        NSArray *jsonArray = [deserializedDictionary objectForKey:@"schedule"];
        for(int i = 0; i < [jsonArray count]; i++){
            NSDictionary* element = [jsonArray objectAtIndex:i];
            
            if([[element objectForKey:@"type"] isEqualToString:@"meeting"]){
                [self.meetingFeed addObject:element];
            }
            
            else {
                NSInteger taskID =[[element objectForKey:@"id"] integerValue];
                
                url = [NSString stringWithFormat:@"%@/Task/%@", DATABASE_URL,[[NSNumber numberWithInt:taskID] stringValue]];
                url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
                deserializedDictionary = [self.backendUtility getRequestForUrl:url];
                
                if([deserializedDictionary objectForKey:@"isCompleted"]){
                    [self.taskFeed addObject:element];
                }
            }
        }
    }
    
    
    //For notificationFeed
    NSString *urlNotification = [NSString stringWithFormat:@"%@/User/Notification/%@", DATABASE_URL,[[NSUserDefaults standardUserDefaults] objectForKey:@"userID"]];

    urlNotification = [urlNotification stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionaryNotification = [self.backendUtility getRequestForUrl:urlNotification];
    
    if (!deserializedDictionaryNotification)
    {
//        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"notification not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
//        [alert show];
    }
    else
    {
        NSArray *jsonArray = [deserializedDictionaryNotification objectForKey:@"notifications"];
        for(int i = 0; i < [jsonArray count]; i++){
            NSDictionary* element = [jsonArray objectAtIndex:i];
            [self.notificationFeed addObject:element];
        }
    }
    
}



- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{

      UITableViewCell *cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"Cell"];
    
    
        if (indexPath.section == 0)
        {
            NSDictionary* meeting = (NSDictionary*)[self.meetingFeed objectAtIndex:indexPath.row];
  
            cell.textLabel.text = (NSString*)[meeting objectForKey:@"title"];
                        //stringByAppendingString:@": "] stringByAppendingString:[meeting objectForKey:@"description"]];
            cell.detailTextLabel.text = (NSString*)[meeting objectForKey:@"datetimeStart"];
                        //stringByAppendingString:@" to "] stringByAppendingString:[meeting objectForKey:@"datetimeEnd"]] ;
        }
    
        else if (indexPath.section == 1)
        {
            NSDictionary* task = (NSDictionary*)[self.taskFeed objectAtIndex:indexPath.row];
            
            cell.detailTextLabel.text= (NSString*)[[[task objectForKey:@"datetimeStart"] stringByAppendingString:@" to "]stringByAppendingString:[task objectForKey:@"datetimeEnd"]];
            cell.textLabel.text = (NSString*)[[[task objectForKey:@"title"] stringByAppendingString:@": "] stringByAppendingString:[task objectForKey:@"description"]];
        }
    
        else
        {
            NSDictionary* notification = (NSDictionary*) [self.notificationFeed objectAtIndex:indexPath.row];
            cell.textLabel.text = (NSString*)[[[notification objectForKey:@"description"] stringByAppendingString:@": "]stringByAppendingString:[notification objectForKey:@"datetime"]];
        }
    
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    //meeting
    if(indexPath.section == 0){
        [tableView deselectRowAtIndexPath:indexPath animated:YES];
        self.scheduleMeetingVC = [[iWinScheduleViewMeetingViewController alloc] initWithNibName:@"iWinScheduleViewMeetingViewController" bundle:nil withUserID:[[[NSUserDefaults standardUserDefaults] objectForKey:@"userID"] intValue] withMeetingID:[[[self.meetingFeed objectAtIndex:indexPath.row] objectForKey:@"id"] intValue]];
        self.scheduleMeetingVC.viewMeetingDelegate = self;
        [self.scheduleMeetingVC setModalPresentationStyle:UIModalPresentationPageSheet];
        [self.scheduleMeetingVC setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
        
        [self presentViewController:self.scheduleMeetingVC animated:YES completion:nil];
        self.scheduleMeetingVC.view.superview.bounds = CGRectMake(MODAL_XOFFSET,MODAL_YOFFSET,MODAL_WIDTH,MODAL_HEIGHT);
        
    }
    else if(indexPath.section == 1){ //task
        self.addViewTaskViewController = [[iWinAddAndViewTaskViewController alloc] initWithNibName:@"iWinAddAndViewTaskViewController" bundle:nil withUserID:[[[NSUserDefaults standardUserDefaults] objectForKey:@"userID"] intValue] withTaskID: [[[self.taskFeed objectAtIndex:indexPath.row] objectForKey:@"id"] intValue]];
        [self.addViewTaskViewController setModalPresentationStyle:UIModalPresentationPageSheet];
        [self.addViewTaskViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
        self.addViewTaskViewController.viewTaskDelegate = self;
        
        [self presentViewController:self.addViewTaskViewController animated:YES completion:nil];
        //self.addViewTaskViewController.view.superview.bounds = CGRectMake(MODAL_XOFFSET,MODAL_YOFFSET,MODAL_WIDTH,MODAL_HEIGHT);
    
    }
    else{ //notification
        
        
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
        return self.notificationFeed.count;
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
