//
//  iWinHomeScreenViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinHomeScreenViewController.h"

@interface iWinHomeScreenViewController ()
@property (nonatomic) NSMutableArray *headers;
@property (nonatomic) NSMutableArray *taskFeed;
@property (nonatomic) NSMutableArray *notificationFeed;
@property (nonatomic) NSMutableArray *meetingFeed;
@end

@implementation iWinHomeScreenViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
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
    
    [self.headers addObject:@"Meeting"];
    [self.headers addObject:@"Task"];
    [self.headers addObject:@"Miscellaneous"];
    
    [self.taskFeed addObject:@"Jim has invited you to Sprint Planning."];
    [self.taskFeed addObject:@"12:00 - 13:00, 10/25/13"];
    [self.meetingFeed addObject:@"Steve has assigned you Research Libraries"];
    [self.meetingFeed addObject:@"Due: 12:00, 10/26/13"];
    [self.notificationFeed addObject:@"Mary shared Meeting Minutes 9/21/13"];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
//    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Cell"];
//    if (cell == nil)
//    {
      UITableViewCell *cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"Cell"];
//    }
    
    
        if (indexPath.section == 0)
        {
            cell.detailTextLabel.text= (NSString*)[self.meetingFeed objectAtIndex:1];
            cell.textLabel.text = (NSString*)[self.meetingFeed objectAtIndex:0];
        }
        else if (indexPath.section == 1)
        {
            cell.detailTextLabel.text= (NSString*)[self.taskFeed objectAtIndex:1];
            cell.textLabel.text = (NSString*)[self.taskFeed objectAtIndex:0];
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
