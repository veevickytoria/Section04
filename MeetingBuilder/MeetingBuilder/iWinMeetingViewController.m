//
//  iWinProjectViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/2/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinMeetingViewController.h"
#import <QuartzCore/QuartzCore.h>
#import "iWinScheduleViewMeetingViewController.h"

@interface iWinMeetingViewController ()
@property (strong, nonatomic) NSMutableArray *meetingList;
@property (strong, nonatomic) NSMutableArray *meetingDetail;
@property (strong, nonatomic) NSString* email;
@property (strong, nonatomic) iWinScheduleViewMeetingViewController *scheduleMeetingVC;
@property (strong, nonatomic) NSMutableArray *meetingID;
@property (strong, nonatomic) NSMutableArray *meetingLocations;

@end

@implementation iWinMeetingViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withEmail:(NSString *)email
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.email = email;
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
//    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Meeting.php?method=getMeetings&user=%@", self.email];
//    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
//    NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url] cachePolicy:NSURLRequestReloadIgnoringLocalAndRemoteCacheData timeoutInterval:30];
//    [urlRequest setHTTPMethod:@"GET"];
//    NSURLResponse * response = nil;
//    NSError * error = nil;
//    NSData * data = [NSURLConnection sendSynchronousRequest:urlRequest
//                                          returningResponse:&response
//                                                      error:&error];
//    //check login
//    NSArray *jsonArray;
//    if (error)
//    {
//        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Meetings not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
//        [alert show];
//    }
//    else
//    {
//        NSError *jsonParsingError = nil;
//        jsonArray = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers|NSJSONReadingAllowFragments error:&jsonParsingError];
//    }
//    if (jsonArray.count > 0)
//    {
//        for (NSDictionary* meetings in jsonArray)
//        {
//            [self.meetingList addObject:[meetings objectForKey:@"title"]];
//            [self.meetingDetail addObject:[meetings objectForKey:@"datetime"]];
//            [self.meetingID addObject:[meetings objectForKey:@"id"]];
//            [self.meetingLocations addObject:[meetings objectForKey:@"location"]];
//        }
//        
//    }

    
    [self.meetingList addObject:@"Meeting 1"];
    [self.meetingDetail addObject:@"10/24/13 9:00 pm"];
    [self.meetingID addObject:@"1"];
    [self.meetingLocations addObject:@"O257"];
    
    [self.meetingList addObject:@"Meeting 2"];
    [self.meetingDetail addObject:@"10/25/13 9:10 pm"];
    [self.meetingID addObject:@"2"];
    [self.meetingLocations addObject:@"O257"];
    
    [self.meetingList addObject:@"Meeting 3"];
    [self.meetingDetail addObject:@"10/26/13 7:00 pm"];
    [self.meetingID addObject:@"3"];
    [self.meetingLocations addObject:@"O257"];
    
    self.scheduleMeetingButton.layer.cornerRadius = 7;
    self.scheduleMeetingButton.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    self.scheduleMeetingButton.layer.borderWidth = 2.0f;

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(IBAction) onScheduleNewMeeting
{
    //[self.meetingListDelegate scheduleMeetingClicked:NO];
    self.scheduleMeetingVC = [[iWinScheduleViewMeetingViewController alloc] initWithNibName:@"iWinScheduleViewMeetingViewController" bundle:nil inEditMode:NO withID:nil withDateTime:nil withTitle:nil withLocation:nil];
    
    [self.scheduleMeetingVC setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.scheduleMeetingVC setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.scheduleMeetingVC animated:YES completion:nil];
    self.scheduleMeetingVC.view.superview.bounds = CGRectMake(0,0,768,1003);
}

//- (IBAction)onAddNewProject:(id)sender
//{
//    UIAlertView *projectAlertView = [[UIAlertView alloc] initWithTitle:@"New Project" message:@"Enter Project Name" delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"Ok", nil];
//    [projectAlertView setAlertViewStyle:UIAlertViewStylePlainTextInput];
//    [projectAlertView show];
//}

//-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
//{
//    if (buttonIndex == 1)
//    {
//        NSString *projectName = [alertView textFieldAtIndex:0].text;
//        projectName = [projectName stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
//        if (projectName.length > 0)
//        {
//            //add project to db.
//            NSString *url = [NSString stringWithFormat:@"http://localhost:8888/db_api.php?action=write&table=Project&email=%@&name=%@", self.email, projectName];
//            url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
//            NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:url]];
//            NSURLResponse * response = nil;
//            NSError * error = nil;
//            NSData * data = [NSURLConnection sendSynchronousRequest:urlRequest
//                                                  returningResponse:&response
//                                                              error:&error];
//        }
//        NSString *url = [NSString stringWithFormat:@"http://localhost:8888/db_api.php?action=read&table=Project&email=%@", self.email];
//        url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
//        NSData *dataURL = [NSData dataWithContentsOfURL:[NSURL URLWithString:url]];
//        NSString *strResult = [[NSString alloc] initWithData:dataURL encoding:NSUTF8StringEncoding];
//        
//        self.projectList = [[NSMutableArray alloc] initWithArray:[strResult componentsSeparatedByString:@"___"]];
//        //self.projectList = [NSKeyedUnarchiver unarchiveObjectWithData:self.responseData];
//        [self.projectTable reloadData];
//    }
//}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
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
//    [self.meetingListDelegate scheduleMeetingClicked:YES withID:self.meetingID[indexPath.row] withDateTime:self.meetingDetail[indexPath.row] withTitle:self.meetingList[indexPath.row] withLocation:self.meetingLocations[indexPath.row]];
    
    self.scheduleMeetingVC = [[iWinScheduleViewMeetingViewController alloc] initWithNibName:@"iWinScheduleViewMeetingViewController" bundle:nil inEditMode:YES withID:self.meetingID[indexPath.row] withDateTime:self.meetingDetail[indexPath.row] withTitle:self.meetingList[indexPath.row] withLocation:self.meetingLocations[indexPath.row]];
    
    [self.scheduleMeetingVC setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.scheduleMeetingVC setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.scheduleMeetingVC animated:YES completion:nil];
    self.scheduleMeetingVC.view.superview.bounds = CGRectMake(0,0,768,1003);
    
}

@end
