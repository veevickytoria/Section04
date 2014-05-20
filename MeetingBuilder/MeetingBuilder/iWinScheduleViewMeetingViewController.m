//
//  iWinScheduleViewMeetingViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinScheduleViewMeetingViewController.h"
#import "iWinAddUsersViewController.h"
#import "iWinAppDelegate.h"
#import <QuartzCore/QuartzCore.h>
#import "Meeting.h"
#import "iWinBackEndUtility.h"
#import "Contact.h"
#import "Settings.h"
#import "iWinConstants.h"

@interface iWinScheduleViewMeetingViewController ()
@property (nonatomic) BOOL isEditing;
@property (nonatomic) BOOL isStartDate;
@property (nonatomic) NSDate *startDate;
@property (nonatomic) NSDate *endDate;
@property (nonatomic) NSInteger meetingID;
@property (strong, nonatomic) NSManagedObjectContext *context;
@property (strong, nonatomic) UIPopoverController *popOverController;
@property (strong, nonatomic) OCCalendarViewController* ocCalVC;
@property (strong, nonatomic) iWinViewAndAddViewController *agendaController;
@property (strong, nonatomic) iWinAddUsersViewController *userViewController;
@property (strong, nonatomic) UIDatePicker *datePicker;
@property (strong, nonatomic) UIDatePicker *enddatePicker;
@property (nonatomic) NSInteger userID;
@property (strong, nonatomic) NSMutableArray *userList;
@property (strong, nonatomic) NSDateFormatter *dateFormatter;
@property (nonatomic) NSUInteger rowToDelete;
@property (nonatomic) UIAlertView *deleteAlertView;
@property (strong, nonatomic) iWinBackEndUtility *backendUtility;
@property (nonatomic) NSDictionary *existMeeting;
@property (nonatomic) NSInteger agendaID;

@end

NSString* const SCHEDULE_MEETING_TITLE = @"Schedule a Meeting";

NSString* const ADD_AGENDA_TITLE = @"Add Agenda";
NSString* const VIEW_MEETING_TITLE = @"View Meeting";
NSString* const MEETING_AGENDA_URL = @"%@/Meeting/Agenda/%d";
NSString* const MEETING_AGENDA_NOT_FOUND_MESSAGE = @"Meeting agenda not found";
NSString* const ERROR_ID_KEY = @"errorID";
NSString* const AM_SYMBOL = @"AM";
NSString* const PM_SYMBOL = @"PM";
NSString* const ATTENDEE_CELL_ID = @"AttendeeCell";
const int NUM_OF_SUGGESTED_TIMES = 10;
NSString* const USER_SCHEDULE_URL = @"%@/User/Schedule/%d";
NSString* const SCHEDULE_NOT_FOUND_MESSAGE = @"Schedule not found";
const int START_HOUR = 8;
const int END_HOUR = 17;
NSString* const MEETING_LIST_URL = @"%@/Meeting/";
const int DATE_PICKER_X_OFFSET = 84;
const int DATE_PICKER_Y_OFFSET = 32;
const int SAVE_TIME_BUTTON_X_POS = 220;
const int SAVE_TIME_BUTTON_Y_POS = 10;
const int SAVE_TIME_BUTTON_WIDTH = 75;
const int SAVE_TIME_BUTTON_HEIGHT = 50;
const int DATE_PICKER_X_POS = 0;
const int DATE_PICKER_Y_POS = 30;
const int DATE_PICKER_WIDTH = 320;
const int DATE_PICKER_HEIGHT = 216;
const int DATE_PICKER_INTERVAL = 5;
const int POP_OVER_WIDTH = 320;
const int POP_OVER_HEIGHT = 250;

@implementation iWinScheduleViewMeetingViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger)userID withMeetingID:(NSInteger)meetingID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.meetingID = meetingID;
        self.isEditing = YES;
        self.userID = userID;
        if (meetingID == -1)
        {
           self.isEditing = NO;
        }
    }
    return self;
}

- (void)formatTime:(NSDate *)date
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:TIME_FORMAT];
    self.startTimeLabel.text = [formatter stringFromDate:date];
    NSDate *currentDate = [NSDate date];
    NSDate *datePlusFiveMinutes = [currentDate dateByAddingTimeInterval:300];
    self.endTimeLabel.text = [formatter stringFromDate:datePlusFiveMinutes];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    self.context = [appDelegate managedObjectContext];
    self.isStartDate = NO;
    self.headerLabel.text = SCHEDULE_MEETING_TITLE;
    [self.addAgendaButton setTitle:ADD_AGENDA_TITLE forState:UIControlStateNormal];
    self.saveAndAddMoreButton.hidden = NO;
    self.deleteMeetingButton.hidden = !self.isEditing;
    self.userList = [[NSMutableArray alloc] init];
    self.backendUtility = [[iWinBackEndUtility alloc] init];
    self.startDateLabel.userInteractionEnabled = YES;
    self.endDateLabel.userInteractionEnabled = YES;
    
    [self setGestureRecognizers];
    
    self.dateFormatter = [[NSDateFormatter alloc] init];
    [self.dateFormatter setDateFormat:DATE_FORMAT];
    
    if (self.isEditing)
    {
        
        [self initForExistingMeeting];
    }
    else
    {
        self.startDateLabel.text = [self.dateFormatter stringFromDate:[NSDate date]];
        self.endDateLabel.text = [self.dateFormatter stringFromDate:[NSDate date]];
        
        self.startDate = [NSDate date];
        self.endDate = [NSDate date];
        [self formatTime:[NSDate date]];
    }
    
}

-(void) setGestureRecognizers
{
    UITapGestureRecognizer *tapStartDate = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(startDateClicked)];
    UITapGestureRecognizer *tapEndDate = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(endDateClicked)];
    
    UITapGestureRecognizer *tapStartTime = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(startTimeClicked)];
    UITapGestureRecognizer *tapEndTime = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(endTimeClicked)];
    
    [self.startDateLabel addGestureRecognizer:tapStartDate];
    [self.endDateLabel addGestureRecognizer:tapEndDate];
    
    [self.startTimeLabel addGestureRecognizer:tapStartTime];
    [self.endTimeLabel addGestureRecognizer:tapEndTime];
}

-(void) initForExistingMeeting
{
    self.headerLabel.text = VIEW_MEETING_TITLE;
    self.saveAndAddMoreButton.hidden = YES;
    
    NSString *url = [NSString stringWithFormat:MEETING_URL,DATABASE_URL,self.meetingID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    if (!deserializedDictionary)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:ERROR_MESSAGE message:MEETING_NOT_FOUND_MESSAGE delegate:self cancelButtonTitle:OK_BUTTON otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        self.existMeeting  = deserializedDictionary;

    }
    
    self.titleField.text = [self.existMeeting objectForKey:TITLE_KEY];
    self.placeField.text = [self.existMeeting objectForKey:LOCATION_KEY];
    
    [self setAgendaTitle];
    [self initDateTimeLabels];
    [self initAttendees];
}

-(void) setAgendaTitle
{
    NSString *url = [NSString stringWithFormat:MEETING_AGENDA_URL, DATABASE_URL,self.meetingID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    NSString *agendaTitle;
    
    if (!deserializedDictionary)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:ERROR_MESSAGE message:MEETING_AGENDA_NOT_FOUND_MESSAGE delegate:self cancelButtonTitle:OK_BUTTON otherButtonTitles: nil];
        [alert show];
    }
    else if([deserializedDictionary objectForKey:ERROR_ID_KEY]){
        [self.addAgendaButton setTitle:ADD_AGENDA_TITLE forState:UIControlStateNormal];
    }
    
    else
    {
        agendaTitle =  [deserializedDictionary objectForKey:TITLE_KEY];
        [self.addAgendaButton setTitle:agendaTitle forState:UIControlStateNormal];
    }
    
    self.agendaID = [[deserializedDictionary objectForKey:AGENDA_ID_KEY] integerValue];
}


+(NSString *)getStringTimeFromDate:(NSDate *)date
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setAMSymbol:AM_SYMBOL];
    [formatter setPMSymbol:PM_SYMBOL];
    [formatter setDateFormat:TIME_FORMAT];
    return [formatter stringFromDate:date];
}

+(NSString *)getStringDateFromDate:(NSDate *)date
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    //Specify only 2 M for month, 2 d for day and 2 h for hour
    [formatter setDateFormat:DATE_FORMAT];
    return [formatter stringFromDate:date];
}

+(NSString *)getStringDateTimeFromDate:(NSDate *)date
{
    return [NSString stringWithFormat:DATE_TIME_STRING_FORMAT, [self getStringDateFromDate:date], [self getStringTimeFromDate:date]];
}


-(void) initDateTimeLabels
{
    NSDate* startDateAndTime = [NSDate dateWithTimeIntervalSince1970:[[self.existMeeting objectForKey:DATE_TIME_KEY] doubleValue]];
    NSDate* endDateAndTime = [NSDate dateWithTimeIntervalSince1970:[[self.existMeeting objectForKey:END_DATE_TIME_KEY] doubleValue]];
    
    NSString *startdate = [iWinScheduleViewMeetingViewController getStringDateFromDate:startDateAndTime];
    NSString *starttime = [iWinScheduleViewMeetingViewController getStringTimeFromDate:startDateAndTime];
    
    NSString *enddate = [iWinScheduleViewMeetingViewController getStringDateFromDate:endDateAndTime];
    NSString *endtime = [iWinScheduleViewMeetingViewController getStringTimeFromDate:endDateAndTime];
    
    self.startDateLabel.text = startdate;
    self.endDateLabel.text = enddate;
    
    self.startTimeLabel.text = starttime;
    self.endTimeLabel.text = endtime;
    
    self.startDate = [self.dateFormatter dateFromString:startdate];
    self.endDate = [self.dateFormatter dateFromString:enddate];
}

-(Contact *)getContactForID:(NSInteger)userID
{
    NSString *url = [NSString stringWithFormat:USER_ID_URL, DATABASE_URL,userID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    if (!deserializedDictionary)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:ERROR_MESSAGE message:MEETING_NOT_FOUND_MESSAGE delegate:self cancelButtonTitle:OK_BUTTON otherButtonTitles: nil];
            [alert show];
    }
    else
    {
        if (deserializedDictionary.count > 0)
        {
            NSEntityDescription *entityDesc = [NSEntityDescription entityForName:CONTACT_ENTITY inManagedObjectContext:self.context];
            Contact *c = [[Contact alloc] initWithEntity:entityDesc insertIntoManagedObjectContext:self.context];
            c.userID = (NSNumber*)[deserializedDictionary objectForKey:USER_ID_KEY];
            c.name = (NSString *)[deserializedDictionary objectForKey:NAME_KEY];
            c.email = (NSString *)[deserializedDictionary objectForKey:EMAIL_KEY];
            return c;
            
        }
    }
    
    return nil;
}

-(void) initAttendees
{
    NSMutableArray *attendeeArray = [[NSMutableArray alloc] init];
    NSArray* attendeeFromDatabase = [self.existMeeting objectForKey:ATTENDANCE_KEY];
    for(int i = 0; i < [attendeeFromDatabase count]; i++){
        [attendeeArray addObject:[(NSDictionary*)[attendeeFromDatabase objectAtIndex:i] objectForKey:USER_ID_KEY]];
    }

    for (int i=0; i<[attendeeArray count]; i++)
    {
        [self.userList addObject:[self getContactForID:[attendeeArray[i] integerValue]]];
    }
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
       
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ATTENDEE_CELL_ID];
    if (cell == nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:ATTENDEE_CELL_ID];
    }
    
    Contact *c = (Contact *)[self.userList objectAtIndex:indexPath.row];
    
    cell.textLabel.text =  c.name;
    if (c.name.length == 0){
        cell.textLabel.text = c.email;
    }
    cell.detailTextLabel.text = c.email;
    return cell;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.userList.count;
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    return YES;
}

// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        self.rowToDelete = indexPath.row;
        UIAlertView *deleteAlertView = [[UIAlertView alloc] initWithTitle:CONFIRM_DELETE_TITLE message:DELETE_MEETING_MESSAGE delegate:self cancelButtonTitle:NO_DELETE_OPTION otherButtonTitles:YES_DELETE_OPTION, nil];
        [deleteAlertView show];
    }
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if ([alertView isEqual:self.deleteAlertView])
    {
        if (buttonIndex == 1)
        {
            //Perform deletion
            NSString *url = [NSString stringWithFormat:MEETING_URL,DATABASE_URL,self.meetingID];
            NSError * error = [self.backendUtility deleteRequestForUrl:url];
            if (!error)
            {
                [self.viewMeetingDelegate refreshMeetingList];
            }
        }
    }
    else
    {
        if (buttonIndex == 1)
        {
            [self.userList removeObjectAtIndex:self.rowToDelete];
            [self.attendeeTableView reloadData];
        }
        else
        {
            self.rowToDelete = -1;
        }
    }
}


- (IBAction)onAddAgenda
{
    self.agendaController = [[iWinViewAndAddViewController alloc] initWithNibName:VIEW_AND_ADD_AGENDA_NIB bundle:nil startDate:[self makeDateFromText:self.startDateLabel.text timeText:self.startTimeLabel.text] endDate:[self makeDateFromText:self.endDateLabel.text timeText:self.endTimeLabel.text]];
    self.agendaController.meetingID = self.meetingID;
    self.agendaController.userID = self.userID;
    self.agendaController.agendaID = self.agendaID;
    
    [self.agendaController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.agendaController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    self.agendaController.agendaDelegate = self;
    self.agendaController.isAgendaCreated = [self.addAgendaButton.titleLabel.text isEqualToString:ADD_AGENDA_TITLE] ? NO : YES;
    [self presentViewController:self.agendaController animated:YES completion:nil];
    self.agendaController.view.superview.bounds = CGRectMake(MODAL_XOFFSET, MODAL_YOFFSET, MODAL_WIDTH, MODAL_HEIGHT);
}

- (IBAction)onAddAttendees
{
    self.userViewController = [[iWinAddUsersViewController alloc] initWithNibName:ADD_USERS_NIB bundle:nil withPageName:MEETING_HEADER inEditMode:self.isEditing];
    [self.userViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.userViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    self.userViewController.userDelegate = self;
    [self.userViewController initAttendeesList:self.userList];
    [self presentViewController:self.userViewController animated:YES completion:nil];
    self.userViewController.view.superview.bounds = CGRectMake(MODAL_XOFFSET, MODAL_YOFFSET, MODAL_WIDTH, MODAL_HEIGHT);
}

-(void)selectedUsers:(NSMutableArray *)userList
{
    self.userList = userList;
    [self.attendeeTableView reloadData];
    //[self formatTime:[self getSuggestedTime]];
}

-(NSDate*)getSuggestedTime {
    int index;
    int times[NUM_OF_SUGGESTED_TIMES];
    
    for (int a = 0; a < NUM_OF_SUGGESTED_TIMES; a++){
        times[a] = 0;
    }
    
    Contact *c;
    if (self.userList.count == 0){
        c = [self getContactForID:self.userID];
    }
    for (Contact *contact in self.userList){
        c = contact;
        NSString *url = [NSString stringWithFormat:USER_SCHEDULE_URL, DATABASE_URL,[c.userID intValue]];
        url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
        
        if (!deserializedDictionary)
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:ERROR_MESSAGE message:SCHEDULE_NOT_FOUND_MESSAGE delegate:self cancelButtonTitle:OK_BUTTON otherButtonTitles: nil];
            [alert show];
        }
        else
        {
            NSArray *jsonArray = [deserializedDictionary objectForKey:SCHEDULE_KEY];
            if (jsonArray.count > 0)
            {
                for (NSDictionary* meetings in jsonArray)
                {
                    if ([[meetings objectForKey:TYPE_KEY] isEqualToString:MEETING_KEY]){
                        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
                        //Set the AM and PM symbols
                        [dateFormatter setAMSymbol:AM_SYMBOL];
                        [dateFormatter setPMSymbol:PM_SYMBOL];
                        //Specify only 2 M for month, 2 d for day and 2 h for hour
                        [dateFormatter setDateFormat:[NSString stringWithFormat:DATE_TIME_STRING_FORMAT, DATE_FORMAT, TIME_FORMAT]];
                        
                        NSDate *date = [dateFormatter dateFromString:[meetings objectForKey:DATE_TIME_START]];
                        NSCalendar *calendar = [NSCalendar currentCalendar];
                        NSDateComponents *components = [calendar components:(NSHourCalendarUnit | NSMinuteCalendarUnit) fromDate:date];
                        NSInteger startHour = [components hour];
                        
                        date = [dateFormatter dateFromString:[meetings objectForKey:DATE_TIME_END]];
                        components = [calendar components:(NSHourCalendarUnit | NSMinuteCalendarUnit) fromDate:date];
                        NSInteger endHour = [components hour];
                        
                        NSDateComponents *dayComp1 = [calendar components:(NSDayCalendarUnit | NSMonthCalendarUnit | NSYearCalendarUnit) fromDate:date];
                        
                        [dateFormatter setDateFormat:DATE_FORMAT];
                        NSString *sdl = (NSString*) self.startDateLabel.text;
                        date = [dateFormatter dateFromString:sdl];
                        
                        NSDateComponents *dayComp2 = [calendar components:(NSDayCalendarUnit | NSMonthCalendarUnit | NSYearCalendarUnit) fromDate:date];
                        
                        NSDate *date1 = [calendar dateFromComponents:dayComp1];
                        NSDate *date2 = [calendar dateFromComponents:dayComp2];
                        
                        
                        //currently assuming all meetings are all within 8am - 5pm on the same day.
                        if ([date1 isEqualToDate:date2]){
                            
                            if (startHour >= START_HOUR && endHour <= END_HOUR && startHour < endHour){
                                for (index = startHour; index <= endHour; index++) {
                                    times[index - START_HOUR]++;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    int min = 0;
    
    //gets the index of the lowest used time
    for (int i = 0; i < NUM_OF_SUGGESTED_TIMES; i++){
        if (times[min] > times[i]){
            min = i;
        }
    }
    min += 8;
    NSDateFormatter* nsdf = [[NSDateFormatter alloc] init];
    [nsdf setDateFormat:@"HH:mm"];
    NSString* hrStr = [NSString stringWithFormat:@"%d:00", min];
    NSDate* stDateTime = [nsdf dateFromString:hrStr];
    
    return stDateTime;
}

-(void) saveNewMeeting
{
    
    NSMutableArray *userIDJsonDictionary = [[NSMutableArray alloc] init];
    for (int i = 0; i<[self.userList count]; i++)
    {
        Contact *c = (Contact *)self.userList[i];
        NSArray *userIDKeys = [[NSArray alloc] initWithObjects:USER_ID_KEY, nil];
        NSArray *userIDObjects = [[NSArray alloc] initWithObjects:[c.userID stringValue], nil];
        NSDictionary *dict = [NSDictionary dictionaryWithObjects:userIDObjects forKeys:userIDKeys];
        [userIDJsonDictionary addObject:dict];
    }
    
    
    NSString *startEpochString = [self makeEpochStringFromDateAndTimeStrings:self.startDateLabel.text timeString:self.startTimeLabel.text];
    
    NSString *endEpochString = [self makeEpochStringFromDateAndTimeStrings:self.endDateLabel.text timeString:self.endTimeLabel.text];
    
    
    NSArray *keys = [NSArray arrayWithObjects:USER_ID_KEY, TITLE_KEY, LOCATION_KEY, DATE_TIME_KEY, END_DATE_TIME_KEY, DESCRIPTION_KEY, ATTENDANCE_KEY, nil];
    NSArray *objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.userID] stringValue], self.titleField.text, self.placeField.text, startEpochString, endEpochString, @"Test Meeting", userIDJsonDictionary, nil];
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    NSString *url = [NSString stringWithFormat:MEETING_LIST_URL, DATABASE_URL];
    NSDictionary *deserializedDictionary = [self.backendUtility postRequestForUrl:url withDictionary:jsonDictionary];
    self.meetingID = [[deserializedDictionary objectForKey:MEETING_ID_KEY] integerValue];
}

-(NSString *) makeEpochStringFromDateAndTimeStrings:(NSString*)dateString timeString:(NSString *)timeString
{
    NSTimeInterval interval = [[self makeDateFromText:dateString timeText:timeString] timeIntervalSince1970];
    return [NSString stringWithFormat:@"%ld", (long)interval];
}

-(void) saveNewAgenda
{
    
    NSString *url = [NSString stringWithFormat:AGENDA_URL, DATABASE_URL,self.agendaID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *getAgenda = [self.backendUtility getRequestForUrl:url];
    
    if (!getAgenda)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:ERROR_MESSAGE message:MEETING_NOT_FOUND_MESSAGE delegate:self cancelButtonTitle:OK_BUTTON otherButtonTitles: nil];
        [alert show];
    }
    
    
    
    NSArray *keys = [NSArray arrayWithObjects:USER_KEY, TITLE_KEY, MEETING_KEY, CONTENT_KEY, nil];
    NSArray *objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.userID] stringValue], [getAgenda objectForKey:TITLE_KEY], [[NSNumber numberWithInt:self.meetingID] stringValue], [getAgenda objectForKey:CONTENT_KEY], nil];
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    url = [NSString stringWithFormat:AGENDA_LIST_URL, DATABASE_URL];
    NSDictionary *deserializedDictionary = [self.backendUtility postRequestForUrl:url withDictionary:jsonDictionary];
    self.agendaID = [[deserializedDictionary objectForKey:AGENDA_ID_KEY] integerValue];
    
}

- (IBAction)onClickSave
{
    if (!self.isEditing)
    {
        [self saveNewMeeting];
        if(self.agendaID > 0){
            [self saveNewAgenda];
        }
    }
    else
    {
        [self updateMeetingInfo];
        if(self.agendaID > 0){
            [self updateAgendaInfo];
        }
    }
    NSLog(@"%@", NSStringFromClass([self.viewMeetingDelegate class]));
    [self.viewMeetingDelegate refreshMeetingList];
}

-(void) updateMeetingInfo
{
    
    NSString *startEpochString = [self makeEpochStringFromDateAndTimeStrings:self.startDateLabel.text timeString:self.startTimeLabel.text];
    
    NSString *endEpochString = [self makeEpochStringFromDateAndTimeStrings:self.endDateLabel.text timeString:self.endTimeLabel.text];
    
    NSString *url = [NSString stringWithFormat:MEETING_LIST_URL, DATABASE_URL];
    
    NSArray *keys = [NSArray arrayWithObjects:MEETING_ID_KEY, @"field", @"value", nil];
    NSArray *objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.meetingID] stringValue], TITLE_KEY, self.titleField.text,nil];
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    [self.backendUtility putRequestForUrl:url withDictionary:jsonDictionary];

    
    objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.meetingID] stringValue], LOCATION_KEY, self.placeField.text,nil];
    jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    [self.backendUtility putRequestForUrl:url withDictionary:jsonDictionary];
    
    objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.meetingID] stringValue], DATE_TIME_KEY, startEpochString, nil];
    jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    [self.backendUtility putRequestForUrl:url withDictionary:jsonDictionary];
    
    objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.meetingID] stringValue], END_DATE_TIME_KEY, endEpochString, nil];
    jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    [self.backendUtility putRequestForUrl:url withDictionary:jsonDictionary];
}

-(NSDate *)makeDateFromText:(NSString *)dateText timeText:(NSString *)timeText
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    dateFormatter.dateFormat = [NSString stringWithFormat:DATE_TIME_STRING_FORMAT, DATE_FORMAT, TIME_FORMAT];
    NSString *dateTime = [NSString stringWithFormat:DATE_TIME_STRING_FORMAT, dateText, timeText];
    return [dateFormatter dateFromString:dateTime];
}

-(void) updateAgendaInfo
{
    NSString *url = [NSString stringWithFormat:AGENDA_URL, DATABASE_URL,self.agendaID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *getAgenda = [self.backendUtility getRequestForUrl:url];
    bool checkupdateAgenda = [[getAgenda objectForKey:MEETING_ID_KEY] integerValue] == self.meetingID;
    
    
    if (!getAgenda)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:ERROR_MESSAGE message:MEETING_NOT_FOUND_MESSAGE delegate:self cancelButtonTitle:OK_BUTTON otherButtonTitles: nil];
        [alert show];
    }
    
    else if(checkupdateAgenda){
        NSArray *keys = [NSArray arrayWithObjects:USER_KEY, TITLE_KEY, MEETING_KEY, CONTENT_KEY, AGENDA_ID_KEY, nil];
        NSArray *objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.userID] stringValue], [getAgenda objectForKey:TITLE_KEY], [[NSNumber numberWithInt:self.meetingID] stringValue], [getAgenda objectForKey:CONTENT_KEY], [[NSNumber numberWithInt:self.agendaID] stringValue], nil];
        NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
        url = [NSString stringWithFormat:AGENDA_LIST_URL, DATABASE_URL];
        NSDictionary *deserializedDictionary = [self.backendUtility putRequestForUrl:url withDictionary:jsonDictionary];
        self.agendaID = [[deserializedDictionary objectForKey:AGENDA_ID_KEY] integerValue];
    }
    
    else{
        [self saveNewAgenda];
    }
}



- (IBAction)onClickSaveAndAddMore
{
    [self saveNewMeeting];
    
    self.headerLabel.text = SCHEDULE_MEETING_TITLE;
    self.titleField.text = EMPTY_STRING;
    self.startDate = [NSDate date];
    self.endDate = [NSDate date];
    [self formatTime:[NSDate date]];
    self.placeField.text = EMPTY_STRING;
    [self.addAgendaButton setTitle:ADD_AGENDA_TITLE forState:UIControlStateNormal];
    self.userList = [[NSMutableArray alloc] init];
    [self.attendeeTableView reloadData];
}

- (IBAction)onDeleteMeeting {
    
    
    self.deleteAlertView = [[UIAlertView alloc] initWithTitle:CONFIRM_DELETE_TITLE message:DELETE_CONTACT_MESSAGE delegate:self cancelButtonTitle:NO_DELETE_OPTION otherButtonTitles:YES_DELETE_OPTION, nil];
    [self.deleteAlertView show];
}

- (IBAction)onClickCancel
{
    [self.viewMeetingDelegate refreshMeetingList];
}


- (void)startDateClicked
{
    self.isStartDate = YES;
    self.ocCalVC = [[OCCalendarViewController alloc] initAtPoint:CGPointMake(self.startDateLabel.frame.origin.x+DATE_PICKER_X_OFFSET, self.startDateLabel.frame.origin.y+DATE_PICKER_Y_OFFSET) inView:self.view];
    [self.ocCalVC setStartDate:self.startDate];
    [self.ocCalVC setEndDate:self.startDate];
    self.ocCalVC.selectionMode = OCSelectionSingleDate;
    self.ocCalVC.delegate = self;
    [self.view addSubview:self.ocCalVC.view];
    
}

- (void)endDateClicked
{
    self.isStartDate = NO;
    
    self.ocCalVC = [[OCCalendarViewController alloc] initAtPoint:CGPointMake(self.endDateLabel.frame.origin.x+DATE_PICKER_X_OFFSET, self.endDateLabel.frame.origin.y+DATE_PICKER_Y_OFFSET) inView:self.view];
    self.ocCalVC.selectionMode = OCSelectionSingleDate;
    self.ocCalVC.delegate = self;
    [self.ocCalVC setStartDate:self.endDate];
    [self.ocCalVC setEndDate:self.endDate];
    [self.view addSubview:self.ocCalVC.view];
}



-(void) startTimeClicked
{
    UIViewController* popoverContent = [[UIViewController alloc] init];
    
    UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(SAVE_TIME_BUTTON_X_POS, SAVE_TIME_BUTTON_Y_POS, SAVE_TIME_BUTTON_WIDTH, SAVE_TIME_BUTTON_HEIGHT)];
    [button setTitle:SAVE_BUTTON forState:UIControlStateNormal];
    [button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [button addTarget:self action:@selector(saveStartTime) forControlEvents:UIControlEventTouchUpInside];
    
    
    UIView *popoverView = [[UIView alloc] init];
    [popoverView addSubview:button];
    
    self.datePicker=[[UIDatePicker alloc]init];
    self.datePicker.frame=CGRectMake(DATE_PICKER_X_POS,DATE_PICKER_Y_POS, DATE_PICKER_WIDTH, DATE_PICKER_HEIGHT);
    self.datePicker.datePickerMode = UIDatePickerModeTime;
    [self.datePicker setMinuteInterval:DATE_PICKER_INTERVAL];
    
    [popoverView addSubview:self.datePicker];
    
    popoverContent.view = popoverView;
    self.popOverController = [[UIPopoverController alloc] initWithContentViewController:popoverContent];

    
    
    [self.popOverController setPopoverContentSize:CGSizeMake(POP_OVER_WIDTH, POP_OVER_HEIGHT) animated:NO];
    [self.popOverController presentPopoverFromRect:CGRectMake(self.startTimeLabel.frame.origin.x, self.startTimeLabel.frame.origin.y+2, self.startTimeLabel.frame.size.width, self.startTimeLabel.frame.size.height)  inView:self.view permittedArrowDirections:UIPopoverArrowDirectionUp animated:YES];
}

-(void) saveStartTime
{
    NSDateFormatter *outputFormatter = [[NSDateFormatter alloc] init];
    [outputFormatter setDateFormat:TIME_FORMAT];
    
    NSDateComponents *components = [[NSCalendar currentCalendar] components:NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit fromDate:self.startDate];
    NSDate *dateOne = [[NSCalendar currentCalendar] dateFromComponents:components];
    components = [[NSCalendar currentCalendar] components: NSYearCalendarUnit |  NSMonthCalendarUnit | NSDayCalendarUnit fromDate:[NSDate date]];
    NSDate *dateTwo = [[NSCalendar currentCalendar] dateFromComponents:components];
    components = [[NSCalendar currentCalendar] components: NSHourCalendarUnit |  NSMinuteCalendarUnit fromDate:self.datePicker.date];
    NSDate *startTimeDate = [[NSCalendar currentCalendar] dateFromComponents:components];
    
    if ([dateTwo compare:dateOne] == NSOrderedSame){
        
        
        
        components = [[NSCalendar currentCalendar] components: NSHourCalendarUnit |  NSMinuteCalendarUnit fromDate:[NSDate date]];
        dateTwo = [[NSCalendar currentCalendar] dateFromComponents:components];
        
        if ([startTimeDate compare:dateTwo] == NSOrderedSame || NSOrderedDescending){
            [self.startTimeLabel setText:[outputFormatter stringFromDate:self.datePicker.date]];
        }
    }
    else {
        [self.startTimeLabel setText:[outputFormatter stringFromDate:self.datePicker.date]];
    }
    
    components = [[NSCalendar currentCalendar] components: NSHourCalendarUnit |  NSMinuteCalendarUnit fromDate:[outputFormatter dateFromString:self.endTimeLabel.text]];
    NSDate *endTimeDate = [[NSCalendar currentCalendar] dateFromComponents:components];
    
    if ([endTimeDate compare:startTimeDate] == NSOrderedAscending || NSOrderedSame){
        
        NSDate *datePlusFiveMinutes = [startTimeDate dateByAddingTimeInterval:300];
        self.endTimeLabel.text = [outputFormatter stringFromDate:datePlusFiveMinutes];
    }
    
    [self.popOverController dismissPopoverAnimated:YES];
    
}

-(void) endTimeClicked
{
    UIViewController* popoverContent = [[UIViewController alloc] init];
    
    UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(SAVE_TIME_BUTTON_X_POS, SAVE_TIME_BUTTON_Y_POS, SAVE_TIME_BUTTON_WIDTH, SAVE_TIME_BUTTON_HEIGHT)];
    [button setTitle:SAVE_BUTTON forState:UIControlStateNormal];
    [button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [button addTarget:self action:@selector(saveEndTime) forControlEvents:UIControlEventTouchUpInside];
    
    UIView *popoverView = [[UIView alloc] init];
    [popoverView addSubview:button];
    
    
    self.enddatePicker=[[UIDatePicker alloc]init];
    self.enddatePicker.frame=CGRectMake(DATE_PICKER_X_POS, DATE_PICKER_Y_POS, DATE_PICKER_WIDTH, DATE_PICKER_HEIGHT);
    self.enddatePicker.datePickerMode = UIDatePickerModeTime;
    [self.enddatePicker setMinuteInterval:DATE_PICKER_INTERVAL];
    [popoverView addSubview:self.enddatePicker];
    
    popoverContent.view = popoverView;
    self.popOverController = [[UIPopoverController alloc] initWithContentViewController:popoverContent];
    
    [self.popOverController setPopoverContentSize:CGSizeMake(POP_OVER_WIDTH, POP_OVER_HEIGHT) animated:NO];
    [self.popOverController presentPopoverFromRect:CGRectMake(self.endTimeLabel.frame.origin.x, self.endTimeLabel.frame.origin.y+2, self.endTimeLabel.frame.size.width, self.endTimeLabel.frame.size.height)  inView:self.view permittedArrowDirections:UIPopoverArrowDirectionUp animated:YES];
}


-(void) saveEndTime
{
    NSDateFormatter *outputFormatter = [[NSDateFormatter alloc] init];
    [outputFormatter setDateFormat:TIME_FORMAT];
    
    NSDateComponents *components = [[NSCalendar currentCalendar] components:NSHourCalendarUnit | NSMinuteCalendarUnit fromDate:self.startDate];
    NSDate *dateOne = [[NSCalendar currentCalendar] dateFromComponents:components];
    components = [[NSCalendar currentCalendar] components: NSHourCalendarUnit |  NSMinuteCalendarUnit fromDate:self.endDate];
    NSDate *dateTwo = [[NSCalendar currentCalendar] dateFromComponents:components];
    
    components = [[NSCalendar currentCalendar] components: NSHourCalendarUnit |  NSMinuteCalendarUnit fromDate:[outputFormatter dateFromString:self.startTimeLabel.text]];
    NSDate *startTimeDate = [[NSCalendar currentCalendar] dateFromComponents:components];
    
    components = [[NSCalendar currentCalendar] components: NSHourCalendarUnit |  NSMinuteCalendarUnit fromDate:self.enddatePicker.date];
    NSDate *endTimeDate = [[NSCalendar currentCalendar] dateFromComponents:components];
    
    if ([dateTwo compare:dateOne] == NSOrderedDescending)
    {
        [self.endTimeLabel setText:[outputFormatter stringFromDate:self.enddatePicker.date]];
    }
    else if ([dateTwo compare:dateOne] == NSOrderedSame) {
        if ([startTimeDate compare:endTimeDate] == NSOrderedAscending) {
            [self.endTimeLabel setText:[outputFormatter stringFromDate:self.enddatePicker.date]];
        }
    }
    [self.popOverController dismissPopoverAnimated:YES];
    
    
}

- (void)completedWithDate:(NSDate *)selectedDate
{
    NSDateComponents *components = [[NSCalendar currentCalendar] components:NSYearCalendarUnit | NSMonthCalendarUnit |  NSDayCalendarUnit fromDate:[NSDate date]];
    NSDate *dateOne = [[NSCalendar currentCalendar] dateFromComponents:components];
    components = [[NSCalendar currentCalendar] components:NSYearCalendarUnit | NSMonthCalendarUnit |  NSDayCalendarUnit fromDate:selectedDate];
    NSDate *dateTwo = [[NSCalendar currentCalendar] dateFromComponents:components];
    
    if ([dateTwo compare:dateOne] == NSOrderedDescending || [dateTwo compare:dateOne] == NSOrderedSame)
    {
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:DATE_FORMAT];
        if (self.isStartDate)
        {
            self.startDate = selectedDate;
            if ([self.startDate compare:self.endDate] == NSOrderedAscending)
            {
                self.startDateLabel.text = [dateFormatter stringFromDate:selectedDate];
            }
            else
            {
                self.startDateLabel.text = [dateFormatter stringFromDate:selectedDate];
                self.endDateLabel.text = [dateFormatter stringFromDate:selectedDate];
                self.endDate = selectedDate;
            }
        }
        else
        {
            self.endDate = selectedDate;
            if ([self.endDate compare:self.startDate] == NSOrderedDescending)
            {
                self.endDateLabel.text = [dateFormatter stringFromDate:selectedDate];
            }
            else
            {
                self.startDateLabel.text = [dateFormatter stringFromDate:selectedDate];
                self.endDateLabel.text = [dateFormatter stringFromDate:selectedDate];
                self.startDate = selectedDate;
            }
        }
    }
    [self.ocCalVC.view removeFromSuperview];
}

-(void)completedWithNoSelection
{
    [self.ocCalVC.view removeFromSuperview];
}

-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

-(void) onSaveAgenda:(NSInteger)agendaID
{
    self.agendaID = agendaID;
    
    NSString *url = [NSString stringWithFormat:AGENDA_URL, DATABASE_URL,self.agendaID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    NSString *agendaTitle = [deserializedDictionary objectForKey:TITLE_KEY];
    [self.addAgendaButton setTitle:agendaTitle forState:UIControlStateNormal];
    
    NSInteger totalDuration = self.agendaController.totalDuration;
    
    NSDateFormatter *outputFormatter = [[NSDateFormatter alloc] init];
    [outputFormatter setDateFormat:TIME_FORMAT];
    
    NSDateComponents *components = [[NSCalendar currentCalendar] components: NSHourCalendarUnit |  NSMinuteCalendarUnit fromDate:[outputFormatter dateFromString:self.startTimeLabel.text]];
    NSDate *startTimeDate = [[NSCalendar currentCalendar] dateFromComponents:components];
    NSDate *datePlusFiveMinutes = [startTimeDate dateByAddingTimeInterval:(totalDuration*60)];
    self.endTimeLabel.text = [outputFormatter stringFromDate:datePlusFiveMinutes];
    
    
    [self.agendaController dismissViewControllerAnimated:YES completion:nil];
    
}

@end
