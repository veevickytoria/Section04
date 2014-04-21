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

NSString* const CREATE_AGENDA_TITLE = @"Create Agenda";
NSString* const VIEW_AGENDA_TITLE = @"View Agenda";
NSString* const TIME_KEY = @"time";
NSString* const ITEM_CELL_ID = @"ItemCell";
const int AGENDA_ITEM_VC_X_POS = 0;
const int AGENDA_ITEM_VC_Y_POS = 0;
const int AGENDA_ITEM_VC_WIDTH = 556;
const int AGENDA_ITEM_VC_HEIGHT = 283;
NSString* const ADD_AGENDA_ITEM_TITLE = @"Add New Item";

@implementation iWinViewAndAddViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil startDate:(NSDate *)startDate endDate:(NSDate *)endDate
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    self.startDate = startDate;
    self.endDate  = endDate;
    if (self) {
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
    self.totalDuration = 0;
    if (!self.isAgendaCreated) {
        self.headerLabel.text = CREATE_AGENDA_TITLE;
    
        if (self.isEditing)
        {
            self.headerLabel.text = VIEW_AGENDA_TITLE;
        }
    } else {
        NSString *url = [NSString stringWithFormat:AGENDA_URL, DATABASE_URL,self.agendaID];
        url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
        self.titleTextField.text = [deserializedDictionary objectForKey:TITLE_KEY];
        
        int conteneSize = [[deserializedDictionary objectForKey:CONTENT_KEY] count] + 1;
        for(int i = 1; i < conteneSize; i++){
            NSDictionary *item = [[deserializedDictionary objectForKey:CONTENT_KEY] objectForKey:[[NSNumber numberWithInt:i] stringValue]];
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
        itemMinutes = [[item objectForKey:TIME_KEY] doubleValue];
        pastMinutes += itemMinutes;
        itemMinutesRemaining = pastMinutes - [self getElapsedAgendaTimeInMinutes];
        secRemaining = 60 - [self getElapsedAgendaTimeInSeconds] % 60;
        if (itemMinutesRemaining >= 0  && secRemaining >= 0) {
            [self setTimerControlFields:[self getTimerTextRemaining:itemMinutesRemaining :secRemaining] currentItemText:[item objectForKey:TITLE_KEY]];
            [self showTimerControls];
            break;
        }
    }
}

-(NSString *)getTimerTextRemaining:(NSInteger)itemMinutesRemaining :(NSInteger)secRemaining
{
    NSString *secRemainingZeroHolder = EMPTY_STRING;
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
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ITEM_CELL_ID];
    if (cell == nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ITEM_CELL_ID];
    }
    
    NSString *agendaItemName = [[self.itemList objectAtIndex:indexPath.row] objectForKey:TITLE_KEY];
    
    cell.textLabel.text = agendaItemName;
    return cell;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.itemList.count;
}


-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSDictionary *agendaItem = [self.itemList objectAtIndex:indexPath.row];
    NSString *agendaItemName = [agendaItem objectForKey:TITLE_KEY];
    NSString *agendaItemDuration = [agendaItem objectForKey:TIME_KEY];
    NSString *agendaItemDescription = [agendaItem objectForKey:DESCRIPTION_KEY];

    
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    self.agendaItemViewController = [[iWinAgendaItemViewController alloc]
                                     initWithNibName:AGENDA_ITEM_NIB bundle:nil];
    self.agendaItemViewController.itemTitle = agendaItemName;
    self.agendaItemViewController.itemDuration = agendaItemDuration;
    self.agendaItemViewController.itemDescription = agendaItemDescription;

    self.agendaItemViewController.itemIndex = indexPath.row;
    
    
    self.agendaItemViewController.itemDelegate = self;
    [self.agendaItemViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.agendaItemViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.agendaItemViewController animated:YES completion:nil];
    self.agendaItemViewController.view.superview.bounds = CGRectMake(AGENDA_ITEM_VC_X_POS, AGENDA_ITEM_VC_Y_POS, AGENDA_ITEM_VC_WIDTH, AGENDA_ITEM_VC_HEIGHT);
    
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
    NSString *url = [NSString stringWithFormat:AGENDA_LIST_URL, DATABASE_URL];
    NSArray *keys = [NSArray arrayWithObjects:AGENDA_ID_KEY, TITLE_KEY, MEETING_KEY, USER_KEY, CONTENT_KEY, nil];
    NSArray *objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.agendaID] stringValue], self.titleTextField.text, [[NSNumber numberWithInt:self.meetingID] stringValue], [[NSNumber numberWithInt:self.userID] stringValue], self.itemList, nil];
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    [self.backendUtility putRequestForUrl:url withDictionary:jsonDictionary];

}


-(void) saveNewAgenda
{
    NSString *url = [NSString stringWithFormat:AGENDA_LIST_URL, DATABASE_URL];
    NSArray *keys = [NSArray arrayWithObjects:TITLE_KEY, USER_KEY, CONTENT_KEY,nil];
    NSArray *objects = [NSArray arrayWithObjects: self.titleTextField.text,[[NSNumber numberWithInt:self.userID] stringValue], self.itemList, nil];
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    NSDictionary *deserializedDictionary = [self.backendUtility postRequestForUrl:url withDictionary:jsonDictionary];
    self.agendaID = [[deserializedDictionary objectForKey:AGENDA_ID_KEY] integerValue];
}



- (IBAction)onClickCancel
{
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)onClickAddItem
{
    self.agendaItemViewController = [[iWinAgendaItemViewController alloc] initWithNibName:AGENDA_ITEM_NIB bundle:nil];
    self.agendaItemViewController.itemDelegate = self;
    [self.agendaItemViewController setModalPresentationStyle:UIModalPresentationFormSheet];
    [self.agendaItemViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    self.agendaItemViewController.itemTitle = ADD_AGENDA_ITEM_TITLE;
    [self presentViewController:self.agendaItemViewController animated:YES completion:nil];
    self.agendaItemViewController.view.superview.bounds = CGRectMake(AGENDA_ITEM_VC_X_POS, AGENDA_ITEM_VC_Y_POS, AGENDA_ITEM_VC_WIDTH, AGENDA_ITEM_VC_HEIGHT);
    
}

-(void)saveItem:(NSString *)name duration: (NSString*) duration description:(NSString*)
description itemIndex: (NSInteger) itemIndex
{
    if((NSInteger)self.agendaItemViewController.itemIndex > -1){
        NSDictionary *agendaItem = @{TITLE_KEY : name, TIME_KEY: duration, DESCRIPTION_KEY: description, CONTENT_KEY: EMPTY_STRING};
        [self.itemList replaceObjectAtIndex:self.agendaItemViewController.itemIndex withObject:agendaItem];
    }
    
    else{
        NSDictionary *agendaItem = @{TITLE_KEY : name, TIME_KEY: duration, DESCRIPTION_KEY: description, CONTENT_KEY: EMPTY_STRING};
    [self.itemList addObject:agendaItem];
    }
    self.totalDuration += [duration integerValue];
    [self.itemTableView reloadData];
    [self dismissViewControllerAnimated:YES completion:Nil];
}

-(void) cancel
{
    [self dismissViewControllerAnimated:YES completion:Nil];
}


@end
