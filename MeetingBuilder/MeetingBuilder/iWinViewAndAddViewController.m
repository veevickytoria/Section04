//
//  iWinViewAndAddViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinViewAndAddViewController.h"
#import "iWinAddUsersViewController.h"
#import <QuartzCore/QuartzCore.h>

@interface iWinViewAndAddViewController ()
@property (nonatomic) NSMutableArray *itemList;
@property (nonatomic) BOOL isEditing;
@property (nonatomic) iWinAgendaItemViewController *agendaItemViewController;
@property (strong, nonatomic) iWinAddUsersViewController *userViewController;
@property (nonatomic) NSInteger agendaID;
@end

@implementation iWinViewAndAddViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withID: (NSInteger) agendaID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
//        self.isEditing = isEditing;
        self.agendaID = agendaID;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.itemList = [[NSMutableArray alloc] init];
    
    self.headerLabel.text = @"Create Agenda";
    
    if (self.isEditing)
    {
        self.titleTextField.text = @"Agenda 101";
        [self.itemList addObject:@"Item 1"];
        [self.itemList addObject:@"Item 2"];
        [self.itemList addObject:@"Item 3"];
        self.headerLabel.text = @"View Agenda";
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"ItemCell"];
    if (cell == nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"ItemCell"];
    }
    
    NSString *agendaItemName = [[self.itemList objectAtIndex:indexPath.row] objectForKey:@"title"];
    cell.textLabel.text = agendaItemName;
    return cell;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.itemList.count;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    //This is where edit on a row happen: indexPath.row
    
    NSDictionary *agendaItem = [self.itemList objectAtIndex:indexPath.row];
    NSString *agendaItemName = [agendaItem objectForKey:@"title"];
    NSString *agendaItemDuration = [agendaItem objectForKey:@"duration"];
    NSString *agendaItemDescription = [agendaItem objectForKey:@"description"];

    
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    self.agendaItemViewController = [[iWinAgendaItemViewController alloc]
                                     initWithNibName:@"iWinAgendaItemViewController" bundle:nil];
    self.agendaItemViewController.itemTitle = agendaItemName;
    self.agendaItemViewController.itemDuration = agendaItemDuration;
    self.agendaItemViewController.itemDescription = agendaItemDescription;

    self.agendaItemViewController.itemIndex = indexPath.row;
    
    
    self.agendaItemViewController.itemDelegate = self;
    [self.agendaItemViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.agendaItemViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.agendaItemViewController animated:YES completion:nil];
    self.agendaItemViewController.view.superview.bounds = CGRectMake(0,0,556,283);
    
}

- (IBAction)onClickSave
{
    //save agenda
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)onClickCancel
{
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)onClickAddItem
{
    self.agendaItemViewController = [[iWinAgendaItemViewController alloc] initWithNibName:@"iWinAgendaItemViewController" bundle:nil];
    self.agendaItemViewController.itemDelegate = self;
    [self.agendaItemViewController setModalPresentationStyle:UIModalPresentationFormSheet];
    [self.agendaItemViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.agendaItemViewController animated:YES completion:nil];
    self.agendaItemViewController.view.superview.bounds = CGRectMake(0,0,556,283);
    
}

- (IBAction)onClickAddAttendees
{
    self.userViewController = [[iWinAddUsersViewController alloc] initWithNibName:@"iWinAddUsersViewController" bundle:nil withPageName:@"Meeting" inEditMode:self.isEditing];
    [self.userViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.userViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.userViewController animated:YES completion:nil];
    self.userViewController.view.superview.bounds = CGRectMake(0,0,768,1003);
}

-(void)saveItem:(NSString *)name duration: (NSString*) duration description:(NSString*)
description itemIndex: (NSInteger *) itemIndex
{
    if((NSInteger)self.agendaItemViewController.itemIndex > -1){
        NSDictionary *agendaItem = @{@"title" : name, @"duration": duration, @"description": description};
        [self.itemList replaceObjectAtIndex:self.agendaItemViewController.itemIndex withObject:agendaItem];
    }
    
    else{
    NSDictionary *agendaItem = @{@"title" : name, @"duration": duration, @"description": description};
    [self.itemList addObject:agendaItem];
    }
    
    [self.itemTableView reloadData];
    [self dismissViewControllerAnimated:YES completion:Nil];
}

-(void) cancel
{
    [self dismissViewControllerAnimated:YES completion:Nil];
}
@end
