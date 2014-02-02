//
//  iWinHomeScreenViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinHomeScreenViewController.h"
#import "iWinBackEndUtility.h"



@interface iWinHomeScreenViewController ()
@property (nonatomic) NSMutableArray *headers;
@property (nonatomic) NSMutableArray *taskFeed;
@property (nonatomic) NSMutableArray *notificationFeed;
@property (nonatomic) NSMutableArray *meetingFeed;
@property (strong, nonatomic) iWinBackEndUtility *backendUtility;

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
    
    [self.taskFeed addObject:@"Jim has invited you to Sprint Planning."];
    [self.taskFeed addObject:@"12:00 - 13:00, 10/25/13"];
    [self.meetingFeed addObject:@"Steve has assigned you Research Libraries"];
    [self.meetingFeed addObject:@"Due: 12:00, 10/26/13"];
    [self.notificationFeed addObject:@"Mary shared Meeting Minutes 9/21/13"];
    
    
    //For meetingFeed and taskFeed
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/User/Schedule/%@", [[NSUserDefaults standardUserDefaults] objectForKey:@"userID"]];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    if (!deserializedDictionary)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"schedule not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        NSArray *jsonArray = [deserializedDictionary objectForKey:@"schedule"];
        for(int i = 0; i < [jsonArray count]; i++){
            NSDictionary* element = [jsonArray objectAtIndex:i];
            
            if([[element objectForKey:@"type"] isEqualToString:@"meeting"]){
                [self.meetingFeed addObject:[[element objectForKey:@"title"] stringByAppendingString:[element objectForKey:@"description"]]];
                
                [self.meetingFeed addObject:[  [[element objectForKey:@"datetimeStart"] stringByAppendingString:@" to "]stringByAppendingString:[element objectForKey:@"datetimeEnd"] ]];
            }
            
            else {
                [self.taskFeed addObject:[[element objectForKey:@"title"] stringByAppendingString:[element objectForKey:@"description"]]];
                
                [self.taskFeed addObject:[  [[element objectForKey:@"datetimeStart"] stringByAppendingString:@" to "]stringByAppendingString:[element objectForKey:@"datetimeEnd"] ]];
            }
        }
    }
    
    
    //For notificationFeed
    NSString *urlNotification = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/User/Notification/%@", [[NSUserDefaults standardUserDefaults] objectForKey:@"userID"]];

    urlNotification = [urlNotification stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionaryNotification = [self.backendUtility getRequestForUrl:urlNotification];
    
    if (!deserializedDictionaryNotification)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"notification not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        NSArray *jsonArray = [deserializedDictionaryNotification objectForKey:@"notifications"];
        for(int i = 0; i < [jsonArray count]; i++){
            NSDictionary* element = [jsonArray objectAtIndex:i];
            
            [self.notificationFeed addObject:[[element objectForKey:@"description"] stringByAppendingString:[element objectForKey:@"datetime"]]];
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
            cell.detailTextLabel.text= (NSString*)[self.meetingFeed objectAtIndex:(indexPath.row * 2 + 1)];
            cell.textLabel.text = (NSString*)[self.meetingFeed objectAtIndex:(indexPath.row * 2)];
        }
        else if (indexPath.section == 1)
        {
            cell.detailTextLabel.text= (NSString*)[self.taskFeed objectAtIndex:(indexPath.row * 2 + 1)];
            cell.textLabel.text = (NSString*)[self.taskFeed objectAtIndex:(indexPath.row * 2)];
        }
        else
        {
            cell.textLabel.text = (NSString*)[self.notificationFeed objectAtIndex:0];
        }
    
    return cell;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (section == 0)
    {
        return (self.meetingFeed.count)/2;
    }
    if (section == 1)
    {
        return (self.taskFeed.count)/2;
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


@end
