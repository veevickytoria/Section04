//
//  iWinTaskListViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinTaskListViewController.h"
#import "iWinAddAndViewTaskViewController.h"
#import <QuartzCore/QuartzCore.h>

@interface iWinTaskListViewController ()
@property (strong, nonatomic) NSMutableArray *itemList;
@property (strong, nonatomic) NSMutableArray *itemDetail;
@property (strong, nonatomic) iWinAddAndViewTaskViewController *addViewTaskViewController;
@end

@implementation iWinTaskListViewController

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
    
    self.itemList = [[NSMutableArray alloc] init];
    self.itemDetail = [[NSMutableArray alloc] init];
    
    [self.itemList addObject:@"Research Libraries"];
    [self.itemDetail addObject:@"Due: 10/24/13 9:00 pm"];
    
    [self.itemList addObject:@"Finish MS3"];
    [self.itemDetail addObject:@"Due: 10/25/13 11:10 pm"];
    
    
    self.createTaskButton.layer.cornerRadius = 7;
    self.createTaskButton.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    self.createTaskButton.layer.borderWidth = 1.0f;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)onClickCreateNewTask
{
    //[self.taskListDelegate createNewTaskClicked:NO];
    self.addViewTaskViewController = [[iWinAddAndViewTaskViewController alloc] initWithNibName:@"iWinAddAndViewTaskViewController" bundle:nil inEditMode:NO];
    [self.addViewTaskViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.addViewTaskViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.addViewTaskViewController animated:YES completion:nil];
    self.addViewTaskViewController.view.superview.bounds = CGRectMake(0,0,768,1003);
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"TaskCell"];
    if (cell == nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"TaskCell"];
    }
    
    cell.textLabel.text = (NSString *)[self.itemList objectAtIndex:indexPath.row];
    cell.detailTextLabel.text = (NSString *)[self.itemDetail objectAtIndex:indexPath.row];
    
    if (indexPath.row == 0)
    {
        cell.accessoryType = UITableViewCellAccessoryCheckmark;
    }
    return cell;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.itemList.count;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    self.addViewTaskViewController = [[iWinAddAndViewTaskViewController alloc] initWithNibName:@"iWinAddAndViewTaskViewController" bundle:nil inEditMode:YES];
    [self.addViewTaskViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.addViewTaskViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.addViewTaskViewController animated:YES completion:nil];
    self.addViewTaskViewController.view.superview.bounds = CGRectMake(0,0,768,1003);
}
@end
