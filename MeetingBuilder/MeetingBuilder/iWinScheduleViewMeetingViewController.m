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
//@property (strong, nonatomic) Meeting *meeting;
@property (strong, nonatomic) NSMutableArray *userList;
@property (strong, nonatomic) NSDateFormatter *dateFormatter;
@property (nonatomic) NSUInteger rowToDelete;
@property (nonatomic) UIAlertView *deleteAlertView;
@property (strong, nonatomic) iWinBackEndUtility *backendUtility;
@property (nonatomic) NSDictionary *existMeeting;
@property (nonatomic) NSInteger agendaID;

@end

@implementation iWinScheduleViewMeetingViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger)userID withMeetingID:(NSInteger)meetingID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        
        self.meetingID = meetingID;
        self.isEditing = YES;
        self.userID = userID;
        if (meetingID == -1)
        {
           self.isEditing = NO;
        }
        //tracker for every hour from 8am - 5pm
    }
    return self;
}

- (void)formatTime:(NSDate *)date
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"hh:mm a"];
    self.startTimeLabel.text = [formatter stringFromDate:date];
    NSDate *currentDate = [NSDate date];
    NSDate *datePlusFiveMinutes = [currentDate dateByAddingTimeInterval:300];
    self.endTimeLabel.text = [formatter stringFromDate:datePlusFiveMinutes];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    //_isAgendaCreated = false;
    
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    self.context = [appDelegate managedObjectContext];
    self.isStartDate = NO;
    self.headerLabel.text = @"Schedule a Meeting";
    [self.addAgendaButton setTitle:@"Add Agenda" forState:UIControlStateNormal];
    self.saveAndAddMoreButton.hidden = NO;
    self.deleteMeetingButton.hidden = !self.isEditing;
    self.userList = [[NSMutableArray alloc] init];
    self.backendUtility = [[iWinBackEndUtility alloc] init];
    self.startDateLabel.userInteractionEnabled = YES;
    self.endDateLabel.userInteractionEnabled = YES;
    
    [self setGestureRecognizers];
    
    self.dateFormatter = [[NSDateFormatter alloc] init];
    [self.dateFormatter setDateFormat:@"MM/dd/yyyy"];
    
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
    self.headerLabel.text = @"View Meeting";
    self.saveAndAddMoreButton.hidden = YES;
    
//    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Meeting" inManagedObjectContext:self.context];
//    
//    NSFetchRequest *request = [[NSFetchRequest alloc] init];
//    [request setEntity:entityDesc];
//    
//    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"meetingID = %d", self.meetingID];
//    [request setPredicate:predicate];
//    
//    NSError *error;
//    NSArray *result = [self.context executeFetchRequest:request
//                                                  error:&error];
    
    
    NSString *url = [NSString stringWithFormat:@"%@/Meeting/%d",DATABASE_URL,self.meetingID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    if (!deserializedDictionary)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Meetings not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        self.existMeeting  = deserializedDictionary;

    }
    
    self.titleField.text = [self.existMeeting objectForKey:@"title"];
    self.placeField.text = [self.existMeeting objectForKey:@"location"];
    
    [self setAgendaTitle];
    [self initDateTimeLabels];
    [self initAttendees];
}

-(void) setAgendaTitle
{
    NSString *url = [NSString stringWithFormat:@"%@/Meeting/Agenda/%d", DATABASE_URL,self.meetingID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    NSString *agendaTitle;
    
    if (!deserializedDictionary)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Meeting agenda not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    else if([deserializedDictionary objectForKey:@"errorID"]){
        [self.addAgendaButton setTitle:@"Add Agenda" forState:UIControlStateNormal];
    }
    
    else
    {
        agendaTitle =  [deserializedDictionary objectForKey:@"title"];
        [self.addAgendaButton setTitle:agendaTitle forState:UIControlStateNormal];
    }
    
    self.agendaID = [[deserializedDictionary objectForKey:@"agendaID"] integerValue];
}


+(NSString *)getStringTimeFromDate:(NSDate *)date
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    //Set the AM and PM symbols
    [formatter setAMSymbol:@"AM"];
    [formatter setPMSymbol:@"PM"];
    //Specify only 2 M for month, 2 d for day and 2 h for hour
    [formatter setDateFormat:@"hh:mm a"];
    return [formatter stringFromDate:date];
}

+(NSString *)getStringDateFromDate:(NSDate *)date
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    //Specify only 2 M for month, 2 d for day and 2 h for hour
    [formatter setDateFormat:@"MM/dd/yyyy"];
    return [formatter stringFromDate:date];
}

+(NSString *)getStringDateTimeFromDate:(NSDate *)date
{
    return [NSString stringWithFormat:@"%@ %@", [self getStringDateFromDate:date], [self getStringTimeFromDate:date]];
}


-(void) initDateTimeLabels
{
    NSDate* startDateAndTime = [NSDate dateWithTimeIntervalSince1970:[[self.existMeeting objectForKey:@"datetime"] doubleValue]];
    NSDate* endDateAndTime = [NSDate dateWithTimeIntervalSince1970:[[self.existMeeting objectForKey:@"endDatetime"] doubleValue]];
    
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

-(Contact *)getContactForID:(NSString*)userID
{
    NSString *url = [NSString stringWithFormat:@"%@/User/%@", DATABASE_URL,userID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    if (!deserializedDictionary)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Meetings not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
            [alert show];
    }
    else
    {
        if (deserializedDictionary.count > 0)
        {
            NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Contact" inManagedObjectContext:self.context];
            Contact *c = [[Contact alloc] initWithEntity:entityDesc insertIntoManagedObjectContext:self.context];
            c.userID = (NSNumber*)[deserializedDictionary objectForKey:@"userID"];
            c.name = (NSString *)[deserializedDictionary objectForKey:@"name"];
            c.email = (NSString *)[deserializedDictionary objectForKey:@"email"];
            return c;
            
        }
    }
    
    return nil;
}

-(void) initAttendees
{
    NSMutableArray *attendeeArray = [[NSMutableArray alloc] init];
    NSArray* attendeeFromDatabase = [self.existMeeting objectForKey:@"attendance"];
    for(int i = 0; i < [attendeeFromDatabase count]; i++){
        [attendeeArray addObject:[(NSDictionary*)[attendeeFromDatabase objectAtIndex:i] objectForKey:@"userID"]];
    }

    for (int i=0; i<[attendeeArray count]; i++)
    {
        [self.userList addObject:[self getContactForID:(NSString *)attendeeArray[i]]];
    }
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
       
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"AttendeeCell"];
    if (cell == nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"AttendeeCell"];
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
        UIAlertView *deleteAlertView = [[UIAlertView alloc] initWithTitle:@"Confirm Delete" message:@"Are you sure you want to delete this meeting?" delegate:self cancelButtonTitle:@"No, just kidding!" otherButtonTitles:@"Yes, please", nil];
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
            NSString *url = [NSString stringWithFormat:@"%@/Meeting/%d",DATABASE_URL,self.meetingID];
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

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)onAddAgenda
{
    self.agendaController = [[iWinViewAndAddViewController alloc] initWithNibName:@"iWinViewAndAddViewController" bundle:nil startDate:[self makeDateFromText:self.startDateLabel.text timeText:self.startTimeLabel.text] endDate:[self makeDateFromText:self.endDateLabel.text timeText:self.endTimeLabel.text]];
    self.agendaController.meetingID = self.meetingID;
    self.agendaController.userID = self.userID;
    self.agendaController.agendaID = self.agendaID;
    
    [self.agendaController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.agendaController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    self.agendaController.agendaDelegate = self;
    self.agendaController.isAgendaCreated = [self.addAgendaButton.titleLabel.text isEqualToString:@"Add Agenda"] ? NO : YES;
    [self presentViewController:self.agendaController animated:YES completion:nil];
    self.agendaController.view.superview.bounds = CGRectMake(MODAL_XOFFSET, MODAL_YOFFSET, MODAL_WIDTH, MODAL_HEIGHT);
}

- (IBAction)onAddAttendees
{
    self.userViewController = [[iWinAddUsersViewController alloc] initWithNibName:@"iWinAddUsersViewController" bundle:nil withPageName:@"Meeting" inEditMode:self.isEditing];
    [self.userViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.userViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    self.userViewController.userDelegate = self;
    [self.userViewController initAttendeesList:self.userList];
    [self presentViewController:self.userViewController animated:YES completion:nil];
    self.userViewController.view.superview.bounds = CGRectMake(MODAL_XOFFSET, MODAL_YOFFSET, MODAL_WIDTH, MODAL_HEIGHT);
}

- (IBAction)onViewMySchedule {
}

-(void)selectedUsers:(NSMutableArray *)userList
{
    self.userList = userList;
    [self.attendeeTableView reloadData];
    [self getSuggestedTime];
}

-(void)getSuggestedTime {
    int index;
    int times[10];
    
    for (int a = 0; a < 10; a++){
        times[a] = 0;
    }
    
    for (Contact *c in self.userList){
        NSString *url = [NSString stringWithFormat:@"%@/User/Schedule/%d", DATABASE_URL,[c.userID intValue]];
        url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
        
        if (!deserializedDictionary)
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Schedule not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
            [alert show];
        }
        else
        {
            NSArray *jsonArray = [deserializedDictionary objectForKey:@"schedule"];
            if (jsonArray.count > 0)
            {
                for (NSDictionary* meetings in jsonArray)
                {
                    if ([[meetings objectForKey:@"type"] isEqualToString:@"meeting"]){
                        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
                        //Set the AM and PM symbols
                        [dateFormatter setAMSymbol:@"AM"];
                        [dateFormatter setPMSymbol:@"PM"];
                        //Specify only 2 M for month, 2 d for day and 2 h for hour
                        [dateFormatter setDateFormat:@"MM/dd/yyyy hh:mm a"];
                        
                        NSDate *date = [dateFormatter dateFromString:[meetings objectForKey:@"datetimeStart"]];
                        NSCalendar *calendar = [NSCalendar currentCalendar];
                        NSDateComponents *components = [calendar components:(NSHourCalendarUnit | NSMinuteCalendarUnit) fromDate:date];
                        NSInteger startHour = [components hour];
                        
                        date = [dateFormatter dateFromString:[meetings objectForKey:@"datetimeEnd"]];
                        components = [calendar components:(NSHourCalendarUnit | NSMinuteCalendarUnit) fromDate:date];
                        NSInteger endHour = [components hour];
                        
                        NSDateComponents *dayComp1 = [calendar components:(NSDayCalendarUnit | NSMonthCalendarUnit | NSYearCalendarUnit) fromDate:date];
                        
                        [dateFormatter setDateFormat:@"MM/dd/yyyy"];
                        NSString *sdl = (NSString*) self.startDateLabel.text;
                        date = [dateFormatter dateFromString:sdl];
                        
                        NSDateComponents *dayComp2 = [calendar components:(NSDayCalendarUnit | NSMonthCalendarUnit | NSYearCalendarUnit) fromDate:date];
                        
                        NSDate *date1 = [calendar dateFromComponents:dayComp1];
                        NSDate *date2 = [calendar dateFromComponents:dayComp2];
                        
                        
                        //currently assuming all meetings are all within 8am - 5pm on the same day.
                        if ([date1 isEqualToDate:date2]){
                            
                            if (startHour >= 8 && endHour <= 17 && startHour < endHour){
                                for (index = startHour; index <= endHour; index++) {
                                    times[index - 8]++;
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
    for (int i = 0; i < 10; i++){
        if (times[min] > times[i]){
            min = i;
        }
    }
    min += 8;
    NSDateFormatter* nsdf = [[NSDateFormatter alloc] init];
    [nsdf setDateFormat:@"HH:mm"];
    NSString* hrStr = [NSString stringWithFormat:@"%d:00", min];
    NSDate* stDateTime = [nsdf dateFromString:hrStr];
    
    [nsdf setDateFormat:@"hh:mm a"];
    NSString* strDateTime = [nsdf stringFromDate:stDateTime];
    
    NSDateFormatter *outputFormatter = [[NSDateFormatter alloc] init];
    [outputFormatter setDateFormat:@"hh:mm a"];
    
    //self.startDate = [outputFormatter dateFromString:strDateTime];
    self.startTimeLabel.text = strDateTime;
}

-(void) saveNewMeeting
{
    
    NSMutableArray *userIDJsonDictionary = [[NSMutableArray alloc] init];
    for (int i = 0; i<[self.userList count]; i++)
    {
        Contact *c = (Contact *)self.userList[i];
        NSArray *userIDKeys = [[NSArray alloc] initWithObjects:@"userID", nil];
        NSArray *userIDObjects = [[NSArray alloc] initWithObjects:[c.userID stringValue], nil];
        NSDictionary *dict = [NSDictionary dictionaryWithObjects:userIDObjects forKeys:userIDKeys];
        [userIDJsonDictionary addObject:dict];
    }
    
    
    NSString *startEpochString = [self makeEpochStringFromDateAndTimeStrings:self.startDateLabel.text timeString:self.startTimeLabel.text];
    
    NSString *endEpochString = [self makeEpochStringFromDateAndTimeStrings:self.endDateLabel.text timeString:self.endTimeLabel.text];
    
    
    NSArray *keys = [NSArray arrayWithObjects:@"userID", @"title", @"location", @"datetime", @"endDatetime", @"description", @"attendance", nil];
    NSArray *objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.userID] stringValue], self.titleField.text, self.placeField.text, startEpochString, endEpochString, @"Test Meeting", userIDJsonDictionary, nil];
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    NSString *url = [NSString stringWithFormat:@"%@/Meeting/", DATABASE_URL];
    NSDictionary *deserializedDictionary = [self.backendUtility postRequestForUrl:url withDictionary:jsonDictionary];
    self.meetingID = [[deserializedDictionary objectForKey:@"meetingID"] integerValue];
}

-(NSString *) makeEpochStringFromDateAndTimeStrings:(NSString*)dateString timeString:(NSString *)timeString
{
    NSTimeInterval interval = [[self makeDateFromText:dateString timeText:timeString] timeIntervalSince1970];
    return [NSString stringWithFormat:@"%f", interval];
}

-(void) saveNewAgenda
{
    
    NSString *url = [NSString stringWithFormat:@"%@/Agenda/%d", DATABASE_URL,self.agendaID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *getAgenda = [self.backendUtility getRequestForUrl:url];
    
    if (!getAgenda)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Meetings not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    
    
    
    NSArray *keys = [NSArray arrayWithObjects:@"user", @"title", @"meeting", @"content", nil];
    NSArray *objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.userID] stringValue], [getAgenda objectForKey:@"title"], [[NSNumber numberWithInt:self.meetingID] stringValue], [getAgenda objectForKey:@"content"], nil];
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    url = [NSString stringWithFormat:@"%@/Agenda/", DATABASE_URL];
    NSDictionary *deserializedDictionary = [self.backendUtility postRequestForUrl:url withDictionary:jsonDictionary];
    self.agendaID = [[deserializedDictionary objectForKey:@"agendaID"] integerValue];
    
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
    [self scheduleNotification];
    NSLog(@"%@", NSStringFromClass([self.viewMeetingDelegate class]));
    [self.viewMeetingDelegate refreshMeetingList];
}

-(void) updateMeetingInfo
{
    
    NSString *startEpochString = [self makeEpochStringFromDateAndTimeStrings:self.startDateLabel.text timeString:self.startTimeLabel.text];
    
    NSString *endEpochString = [self makeEpochStringFromDateAndTimeStrings:self.endDateLabel.text timeString:self.endTimeLabel.text];
    
    NSString *url = [NSString stringWithFormat:@"%@/Meeting/", DATABASE_URL];
    
    NSArray *keys = [NSArray arrayWithObjects:@"meetingID", @"field", @"value", nil];
    NSArray *objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.meetingID] stringValue], @"title", self.titleField.text,nil];
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    [self.backendUtility putRequestForUrl:url withDictionary:jsonDictionary];

    
    objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.meetingID] stringValue], @"location", self.placeField.text,nil];
    jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    [self.backendUtility putRequestForUrl:url withDictionary:jsonDictionary];
    
    objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.meetingID] stringValue], @"dateTime", startEpochString, nil];
    jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    [self.backendUtility putRequestForUrl:url withDictionary:jsonDictionary];
    
    objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.meetingID] stringValue], @"endDatetime", endEpochString, nil];
    jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    [self.backendUtility putRequestForUrl:url withDictionary:jsonDictionary];
}

-(NSDate *)makeDateFromText:(NSString *)dateText timeText:(NSString *)timeText
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    dateFormatter.dateFormat = @"MM/dd/yyyy h:mm a";
    NSString *dateTime = [NSString stringWithFormat:@"%@ %@", dateText, timeText];
    return [dateFormatter dateFromString:dateTime];
}

-(void) updateAgendaInfo
{
    NSString *url = [NSString stringWithFormat:@"%@/Agenda/%d", DATABASE_URL,self.agendaID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *getAgenda = [self.backendUtility getRequestForUrl:url];
    bool checkupdateAgenda = [[getAgenda objectForKey:@"meetingID"] integerValue] == self.meetingID;
    
    
    if (!getAgenda)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Meetings not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    
    else if(checkupdateAgenda){
        NSArray *keys = [NSArray arrayWithObjects:@"user", @"title", @"meeting", @"content", @"agendaID", nil];
        NSArray *objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.userID] stringValue], [getAgenda objectForKey:@"title"], [[NSNumber numberWithInt:self.meetingID] stringValue], [getAgenda objectForKey:@"content"], [[NSNumber numberWithInt:self.agendaID] stringValue], nil];
        NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
        url = [NSString stringWithFormat:@"%@/Agenda/", DATABASE_URL];
        NSDictionary *deserializedDictionary = [self.backendUtility putRequestForUrl:url withDictionary:jsonDictionary];
        self.agendaID = [[deserializedDictionary objectForKey:@"agendaID"] integerValue];
    }
    
    else{
        [self saveNewAgenda];
    }
}


-(void) scheduleNotification
{
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Settings" inManagedObjectContext:self.context];

    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDesc];

    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"userID = %d", self.userID];
    [request setPredicate:predicate];

    NSError *error;
    NSArray *result = [self.context executeFetchRequest:request
                                                  error:&error];
    
    Settings *settings = (Settings *)[result objectAtIndex:0];
    if ([settings.shouldNotify boolValue])
    {
        [self removeOldNotifications];
        
        NSString *meetingStartDate = [NSString stringWithFormat:@"%@ %@", self.startDateLabel.text, self.startTimeLabel.text];
        NSDateFormatter *formatter = [[NSDateFormatter alloc]init];
        [formatter setDateFormat:@"MM/dd/yyyy hh:mm a"];
        NSDate *dateTimeOfMeeting = [formatter dateFromString:meetingStartDate];

        UILocalNotification* localNotification = [[UILocalNotification alloc] init];
        localNotification.timeZone = [NSTimeZone defaultTimeZone];
        
        NSNumber *meetingID = [NSNumber numberWithInt:self.meetingID];
        NSArray *key = [[NSArray alloc] initWithObjects:@"meetingID", nil];
        NSArray *object = [[NSArray alloc] initWithObjects:meetingID, nil];
        NSDictionary *userInfo = [[NSDictionary alloc] initWithObjects:object forKeys:key];
        localNotification.userInfo = userInfo;
        
        NSDateComponents *dateComponents = [[NSDateComponents alloc] init];
        NSDate *fireDate;
        switch ([settings.whenToNotify integerValue]) {
            case 0:
                fireDate = dateTimeOfMeeting;
                localNotification.alertBody = [NSString stringWithFormat:@"%@ meeting starts now", self.titleField.text];
                break;
            case 1:
                [dateComponents setMinute:-5];
                fireDate = [[NSCalendar currentCalendar] dateByAddingComponents:dateComponents toDate:dateTimeOfMeeting options:0];
                localNotification.alertBody = [NSString stringWithFormat:@"%@ meeting starts in 5 minutes", self.titleField.text];
                break;
            case 2:
                [dateComponents setMinute:-15];
                fireDate = [[NSCalendar currentCalendar] dateByAddingComponents:dateComponents toDate:dateTimeOfMeeting options:0];
                localNotification.alertBody = [NSString stringWithFormat:@"%@ meeting starts in 15 minutes", self.titleField.text];
                break;
            case 3:
                [dateComponents setMinute:-30];
                fireDate = [[NSCalendar currentCalendar] dateByAddingComponents:dateComponents toDate:dateTimeOfMeeting options:0];
                localNotification.alertBody = [NSString stringWithFormat:@"%@ meeting starts in 30 minutes", self.titleField.text];
                break;
            case 4:
                [dateComponents setHour:-1];
                fireDate = [[NSCalendar currentCalendar] dateByAddingComponents:dateComponents toDate:dateTimeOfMeeting options:0];
                localNotification.alertBody = [NSString stringWithFormat:@"%@ meeting starts in 1 hour", self.titleField.text];
                break;
            case 5:
                [dateComponents setHour:-2];
                fireDate = [[NSCalendar currentCalendar] dateByAddingComponents:dateComponents toDate:dateTimeOfMeeting options:0];
                localNotification.alertBody = [NSString stringWithFormat:@"%@ meeting starts in 2 hours", self.titleField.text];
                break;
            case 6:
                [dateComponents setDay:-1];
                fireDate = [[NSCalendar currentCalendar] dateByAddingComponents:dateComponents toDate:dateTimeOfMeeting options:0];
                localNotification.alertBody = [NSString stringWithFormat:@"%@ meeting starts in 1 day", self.titleField.text];
                break;
            case 7:
                [dateComponents setDay:-2];
                fireDate = [[NSCalendar currentCalendar] dateByAddingComponents:dateComponents toDate:dateTimeOfMeeting options:0];
                localNotification.alertBody = [NSString stringWithFormat:@"%@ meeting starts in 2 days", self.titleField.text];
                break;
            default:
                break;
        }
        localNotification.fireDate = fireDate;
        [[NSNotificationCenter defaultCenter] postNotificationName:@"reloadData" object:self];
        localNotification.applicationIconBadgeNumber = [[UIApplication sharedApplication] applicationIconBadgeNumber] + 1;
        [[UIApplication sharedApplication] scheduleLocalNotification:localNotification];
    }
}
-(void) removeOldNotifications
{
    NSArray *notifications = [[UIApplication sharedApplication] scheduledLocalNotifications];
    for (int i=0; i<[notifications count]; i++)
    {
        UILocalNotification* notification = [notifications objectAtIndex:i];
        NSDictionary *userInfo = notification.userInfo;
        NSInteger meetingId=[[userInfo valueForKey:@"meetingID"] integerValue];
        if (meetingId == self.meetingID)
        {
            [[UIApplication sharedApplication] cancelLocalNotification:notification];
            break;
        }
    }
}

- (IBAction)onClickSaveAndAddMore
{
    [self saveNewMeeting];
    
    self.headerLabel.text = @"Schedule a Meeting";
    self.titleField.text = @"";
    self.startDateLabel.text = @"";
    self.endDateLabel.text = @"";
    self.placeField.text = @"";
    [self.addAgendaButton setTitle:@"Add Agenda" forState:UIControlStateNormal];
}

- (IBAction)onDeleteMeeting {
    
    
    self.deleteAlertView = [[UIAlertView alloc] initWithTitle:@"Confirm Delete" message:@"Are you sure you want to delete this contact?" delegate:self cancelButtonTitle:@"No, just kidding!" otherButtonTitles:@"Yes, please", nil];
    [self.deleteAlertView show];
}

- (IBAction)onClickCancel
{
    [self dismissViewControllerAnimated:YES completion:nil];
}


- (void)startDateClicked
{
    self.isStartDate = YES;
    self.ocCalVC = [[OCCalendarViewController alloc] initAtPoint:CGPointMake(self.startDateLabel.frame.origin.x+84, self.startDateLabel.frame.origin.y+32) inView:self.view];
    [self.ocCalVC setStartDate:self.startDate];
    [self.ocCalVC setEndDate:self.startDate];
    self.ocCalVC.selectionMode = OCSelectionSingleDate;
    self.ocCalVC.delegate = self;
    [self.view addSubview:self.ocCalVC.view];
    
}

- (void)endDateClicked
{
    self.isStartDate = NO;
    
    self.ocCalVC = [[OCCalendarViewController alloc] initAtPoint:CGPointMake(self.endDateLabel.frame.origin.x+84, self.endDateLabel.frame.origin.y+32) inView:self.view];
    self.ocCalVC.selectionMode = OCSelectionSingleDate;
    self.ocCalVC.delegate = self;
    [self.ocCalVC setStartDate:self.endDate];
    [self.ocCalVC setEndDate:self.endDate];
    [self.view addSubview:self.ocCalVC.view];
}

-(void) startTimeClicked
{
    UIViewController* popoverContent = [[UIViewController alloc] init]; //ViewController
    
    UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(220, 10, 75, 50)];
    [button setTitle:@"Save" forState:UIControlStateNormal];
    [button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [button addTarget:self action:@selector(saveStartTime) forControlEvents:UIControlEventTouchUpInside];
    
    
    UIView *popoverView = [[UIView alloc] init];   //view
    [popoverView addSubview:button];
    
    //    UIDatePicker *datePicker=[[UIDatePicker alloc]init];//Date picker
    self.datePicker=[[UIDatePicker alloc]init];//Date picker
    self.datePicker.frame=CGRectMake(0,30,320, 216);
    self.datePicker.datePickerMode = UIDatePickerModeTime;
    [self.datePicker setMinuteInterval:5];
    //[datePicker addTarget:self action:@selector(Result) forControlEvents:UIControlEventValueChanged];
    [popoverView addSubview:self.datePicker];
    
    popoverContent.view = popoverView;
    self.popOverController = [[UIPopoverController alloc] initWithContentViewController:popoverContent];
    //popoverController.delegate=self;
    
    
    [self.popOverController setPopoverContentSize:CGSizeMake(320, 250) animated:NO];
    [self.popOverController presentPopoverFromRect:CGRectMake(self.startTimeLabel.frame.origin.x, self.startTimeLabel.frame.origin.y+2, self.startTimeLabel.frame.size.width, self.startTimeLabel.frame.size.height)  inView:self.view permittedArrowDirections:UIPopoverArrowDirectionUp animated:YES];
}

-(void) saveStartTime
{
    NSDateFormatter *outputFormatter = [[NSDateFormatter alloc] init];
    [outputFormatter setDateFormat:@"hh:mm a"];
    
    [self.startTimeLabel setText:[outputFormatter stringFromDate:self.datePicker.date]];
    [self.popOverController dismissPopoverAnimated:YES];
}

-(void) endTimeClicked
{
    UIViewController* popoverContent = [[UIViewController alloc] init]; //ViewController
    
    UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(220, 10, 75, 50)];
    [button setTitle:@"Save" forState:UIControlStateNormal];
    [button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [button addTarget:self action:@selector(saveEndTime) forControlEvents:UIControlEventTouchUpInside];
    
    UIView *popoverView = [[UIView alloc] init];   //view
    [popoverView addSubview:button];
    
    //UIDatePicker *enddatePicker=[[UIDatePicker alloc]init];//Date picker
    self.enddatePicker=[[UIDatePicker alloc]init];//Date picker
    self.enddatePicker.frame=CGRectMake(0,30,320, 216);
    self.enddatePicker.datePickerMode = UIDatePickerModeTime;
    [self.enddatePicker setMinuteInterval:5];
    //[datePicker addTarget:self action:@selector(Result) forControlEvents:UIControlEventValueChanged];
    [popoverView addSubview:self.enddatePicker];
    
    popoverContent.view = popoverView;
    self.popOverController = [[UIPopoverController alloc] initWithContentViewController:popoverContent];
    //popoverController.delegate=self;
    
    [self.popOverController setPopoverContentSize:CGSizeMake(320, 250) animated:NO];
    [self.popOverController presentPopoverFromRect:CGRectMake(self.endTimeLabel.frame.origin.x, self.endTimeLabel.frame.origin.y+2, self.endTimeLabel.frame.size.width, self.endTimeLabel.frame.size.height)  inView:self.view permittedArrowDirections:UIPopoverArrowDirectionUp animated:YES];
}


-(void) saveEndTime
{
    NSDateFormatter *outputFormatter = [[NSDateFormatter alloc] init];
    [outputFormatter setDateFormat:@"hh:mm a"];
    
    [self.endTimeLabel setText:[outputFormatter stringFromDate:self.enddatePicker.date]];
    [self.popOverController dismissPopoverAnimated:YES];
    
    
}

- (void)completedWithDate:(NSDate *)selectedDate
{
    if ([selectedDate compare:[NSDate date]] == NSOrderedDescending)
    {
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"MM/dd/yyyy"];
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
    
    NSString *url = [NSString stringWithFormat:@"%@/Agenda/%d", DATABASE_URL,self.agendaID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    NSString *agendaTitle = [deserializedDictionary objectForKey:@"title"];
    [self.addAgendaButton setTitle:agendaTitle forState:UIControlStateNormal];
    
    
    [self.agendaController dismissViewControllerAnimated:YES completion:nil];
    
}

@end
