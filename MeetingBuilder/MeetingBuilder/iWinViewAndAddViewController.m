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
#import "iWinBackEndUtility.h"
#import "iWinConstants.h"

@interface iWinViewAndAddViewController ()
@property (nonatomic) NSMutableArray *itemList;
@property (nonatomic) BOOL isEditing;
@property (nonatomic) NSDate *startDate;
@property (nonatomic) NSDate *endDate;
@property (nonatomic) iWinAgendaItemViewController *agendaItemViewController;
@property (nonatomic) iWinAddUsersViewController *userViewController;
@property (strong, nonatomic) iWinBackEndUtility *backendUtility;
@property (weak, nonatomic) IBOutlet UILabel *timerLabel;
@property (weak, nonatomic) IBOutlet UILabel *currentAgendaItemLabel;
@end


@implementation iWinViewAndAddViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil startDate:(NSDate *)startDate endDate:(NSDate *)endDate
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    self.startDate = startDate;
    self.endDate  = endDate;
    if (self) {
        // Custom initialization
//        self.isEditing = isEditing;
        self.agendaID = 0;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.backendUtility = [[iWinBackEndUtility alloc] init];
    self.itemList = [[NSMutableArray alloc] init];

    if (!self.isAgendaCreated) {
        self.headerLabel.text = @"Create Agenda";
    
        if (self.isEditing)
        {
            self.titleTextField.text = @"Agenda 101";
            [self.itemList addObject:@"Item 1"];
            [self.itemList addObject:@"Item 2"];
            [self.itemList addObject:@"Item 3"];
            self.headerLabel.text = @"View Agenda";
        }
    } else {
        NSString *url = [NSString stringWithFormat:@"%@/Agenda/%d", DATABASE_URL,self.agendaID];
        url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
        self.titleTextField.text = [deserializedDictionary objectForKey:@"title"];
        
        int conteneSize = [[deserializedDictionary objectForKey:@"content"] count] + 1;
        for(int i = 1; i < conteneSize; i++){
            NSDictionary *item = [[deserializedDictionary objectForKey:@"content"] objectForKey:[[NSNumber numberWithInt:i] stringValue]];
            [self.itemList addObject:item];
        }
        
        // setup timer
        [NSTimer scheduledTimerWithTimeInterval:0.25 target:self selector:@selector(trySetupAgendaTimer) userInfo:nil repeats:YES];
    }
}

-(void)trySetupAgendaTimer
{
    if ( [self meetingInProgress]) {
        [self setTimerControlsToCurrentItem];
    }
    else {
        [self hideTimerControls];
    }
}

-(void)setTimerControlsToCurrentItem
{
    NSInteger pastMinutes = 0;
    NSInteger itemMinutes;
    NSInteger itemMinutesRemaining;
    NSInteger secRemaining;
    // find current item
    for (NSDictionary *item in self.itemList) {
        itemMinutes = [[item objectForKey:@"time"] doubleValue];
        pastMinutes += itemMinutes;
        itemMinutesRemaining = pastMinutes - [self getElapsedAgendaTimeInMinutes];
        secRemaining = 60 - [self getElapsedAgendaTimeInSeconds] % 60;
        if (itemMinutesRemaining >= 0  && secRemaining >= 0) {
            [self setTimerControlFields:[self getTimerTextRemaining:itemMinutesRemaining :secRemaining] currentItemText:[item objectForKey:@"title"]];
            [self showTimerControls];
            break;
        }
    }
}

-(NSString *)getTimerTextRemaining:(NSInteger)itemMinutesRemaining :(NSInteger)secRemaining
{
    NSString *secRemainingZeroHolder = @"";
    if (secRemaining < 10) {
        secRemainingZeroHolder = @"0";
    }
    
    NSString *timerTextRemaining = [NSString stringWithFormat:@"%d:%@%d", itemMinutesRemaining, secRemainingZeroHolder, secRemaining];
    return timerTextRemaining;
}

-(void)setTimerControlFields:(NSString *)timerLabelText currentItemText:(NSString *)currentItemText
{
    self.timerLabel.text = timerLabelText;
    self.currentAgendaItemLabel.text = currentItemText;
}


-(void)hideTimerControls
{
    self.timerLabel.hidden = YES;
    self.currentAgendaItemLabel.hidden = YES;
}


-(void)showTimerControls
{
    self.timerLabel.hidden = NO;
    self.currentAgendaItemLabel.hidden = NO;
}

-(BOOL)meetingInProgress
{
    return [self getElapsedAgendaTimeInSeconds] > 0 && [NSDate timeIntervalSinceReferenceDate] < [self.endDate timeIntervalSince1970];
}


-(NSInteger)getElapsedAgendaTimeInMinutes
{
    return [self getElapsedAgendaTimeInSeconds] / 60;
}

-(NSInteger)getElapsedAgendaTimeInSeconds
{
    return [[NSDate date] timeIntervalSince1970] - [self.startDate timeIntervalSince1970];
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
    
//    for(int i = 0; i < self.itemList.count; i++){
//        NSString *agendaItemName = [[self.itemList objectAtIndex:i] objectForKey:@"title"];
//        NSLog(@"item is: %@\n", agendaItemName);
//    }
    
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
    
    //int index = (self.itemList.count - 1) - indexPath.row;
    NSDictionary *agendaItem = [self.itemList objectAtIndex:indexPath.row];
    NSString *agendaItemName = [agendaItem objectForKey:@"title"];
    NSString *agendaItemDuration = [agendaItem objectForKey:@"time"];
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
    if (self.agendaID == 0)
    {
        [self saveNewAgenda];
    }
    else
    {
        [self updateAgendaInfo];
    }
    [self.agendaDelegate onSaveAgenda: self.agendaID];
}

-(void) updateAgendaInfo
{
    NSString *url = [NSString stringWithFormat:@"%@/Agenda/", DATABASE_URL];
    
    NSArray *keys = [NSArray arrayWithObjects:@"agendaID", @"title", @"meeting", @"user", @"content", nil];
    NSArray *objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.agendaID] stringValue], self.titleTextField.text, [[NSNumber numberWithInt:self.meetingID] stringValue], [[NSNumber numberWithInt:self.userID] stringValue], self.itemList, nil];
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    [self.backendUtility putRequestForUrl:url withDictionary:jsonDictionary];

}


-(void) saveNewAgenda
{
    NSArray *keys = [NSArray arrayWithObjects:@"title", @"user", @"content",nil];
    NSArray *objects = [NSArray arrayWithObjects: self.titleTextField.text,[[NSNumber numberWithInt:self.userID] stringValue], self.itemList, nil];
    
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    NSString *url = [NSString stringWithFormat:@"%@/Agenda/", DATABASE_URL];
    NSDictionary *deserializedDictionary = [self.backendUtility postRequestForUrl:url withDictionary:jsonDictionary];
    self.agendaID = [[deserializedDictionary objectForKey:@"agendaID"] integerValue];
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
    self.userViewController = [[iWinAddUsersViewController alloc] initWithNibName:@"iWinAddUsersViewController" bundle:nil withPageName:@"Agenda" inEditMode:self.isEditing];
    [self.userViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.userViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
  //  self.userViewController.userDelegate = self;
    [self presentViewController:self.userViewController animated:YES completion:nil];
    self.userViewController.view.superview.bounds = CGRectMake(MODAL_XOFFSET, MODAL_YOFFSET, MODAL_WIDTH, MODAL_HEIGHT);
}

-(void)saveItem:(NSString *)name duration: (NSString*) duration description:(NSString*)
description itemIndex: (NSInteger *) itemIndex
{
    //TODO: PUT DESCRIPTION BACK!!!
    if((NSInteger)self.agendaItemViewController.itemIndex > -1){
        NSDictionary *agendaItem = @{@"title" : name, @"time": duration, @"description": description, @"content": @""};
        [self.itemList replaceObjectAtIndex:self.agendaItemViewController.itemIndex withObject:agendaItem];
    }
    
    else{
        NSDictionary *agendaItem = @{@"title" : name, @"time": duration, @"description": description, @"content": @""};
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
