//
//  iWinScheduleViewMeetingViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinScheduleViewMeetingViewController.h"
#import "iWinViewAndAddViewController.h"
#import <QuartzCore/QuartzCore.h>

@interface iWinScheduleViewMeetingViewController ()
@property (nonatomic) BOOL isEditing;
@property (nonatomic) BOOL isStartDate;
@property (nonatomic) NSDate *startDate;
@property (nonatomic) NSDate *endDate;
@property (strong, nonatomic) NSString *meetingID;
@property (strong, nonatomic) NSString *dateTime;
@property (strong, nonatomic) NSString *title;
@property (strong, nonatomic) NSString *location;
@property (strong, nonatomic) UIPopoverController *popOverController;
@property (strong, nonatomic) OCCalendarViewController* ocCalVC;
@property (strong, nonatomic) iWinViewAndAddViewController *agendaController;
@end

@implementation iWinScheduleViewMeetingViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil inEditMode:(BOOL)isEditing withID:(NSString*) meetingID withDateTime:(NSString*) dateTime withTitle:(NSString*) title withLocation:(NSString*) location
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.isEditing = isEditing;
        self.meetingID = meetingID;
        self.dateTime = dateTime;
        self.location = location;
        self.title = title;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.isStartDate = NO;
    self.headerLabel.text = @"Schedule a Meeting";
    [self.addAgendaButton setTitle:@"Add Agenda" forState:UIControlStateNormal];
    self.saveAndAddMoreButton.hidden = NO;
    if (self.isEditing)
    {
        self.headerLabel.text = @"View Meeting";
        self.titleField.text = self.title;
        self.startDateLabel.text = self.dateTime;
        self.endDateLabel.text = self.dateTime;
        self.durationField.text = @"1 hr";
        self.placeField.text = self.location;
        [self.addAgendaButton setTitle:@"Agenda 101" forState:UIControlStateNormal];
        self.saveAndAddMoreButton.hidden = YES;
    }
    
    self.startDateLabel.userInteractionEnabled = YES;
    self.endDateLabel.userInteractionEnabled = YES;
    
    UITapGestureRecognizer *tapStartDate = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(startDateClicked)];
    UITapGestureRecognizer *tapEndDate = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(endDateClicked)];
    
    UITapGestureRecognizer *tapStartTime = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(startTimeClicked)];
    UITapGestureRecognizer *tapEndTime = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(endTimeClicked)];
    
    [self.startDateLabel addGestureRecognizer:tapStartDate];
    [self.endDateLabel addGestureRecognizer:tapEndDate];
    
    [self.startTimeLabel addGestureRecognizer:tapStartTime];
    [self.endTimeLabel addGestureRecognizer:tapEndTime];
    
    [self updateButtonUI:self.saveButton];
    [self updateButtonUI:self.cancelButton];
    [self updateButtonUI:self.saveAndAddMoreButton];
    [self updateButtonUI:self.addAgendaButton];
    [self updateButtonUI:self.addAttendeesButton];
    [self updateButtonUI:self.visitScheduleButton];
    
    [self updateLabelUI:self.startDateLabel];
    [self updateLabelUI:self.endDateLabel];
    [self updateLabelUI:self.startTimeLabel];
    [self updateLabelUI:self.endTimeLabel];
    
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"MM/dd/yyyy"];
    self.startDateLabel.text = [dateFormatter stringFromDate:[NSDate date]];
    self.endDateLabel.text = [dateFormatter stringFromDate:[NSDate date]];
    
    self.startDate = [NSDate date];
    self.endDate = [NSDate date];
    
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"hh:mm a"];
    self.startTimeLabel.text = [formatter stringFromDate:[NSDate date]];
    NSDate *currentDate = [NSDate date];
    NSDate *datePlusFiveMinutes = [currentDate dateByAddingTimeInterval:300];
    self.endTimeLabel.text = [formatter stringFromDate:datePlusFiveMinutes];
}

-(void) updateButtonUI:(UIButton *)button
{
    button.layer.cornerRadius = 7;
    button.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    button.layer.borderWidth = 1.0f;
}

-(void) updateLabelUI:(UILabel *)label
{
    label.layer.cornerRadius = 7;
    label.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    label.layer.borderWidth = 1.0f;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)onAddAgenda
{
    if ([self.addAgendaButton.titleLabel.text isEqualToString:@"Add Agenda"])
    {
        self.agendaController = [[iWinViewAndAddViewController alloc] initWithNibName:@"iWinViewAndAddViewController" bundle:nil inEditMode:NO];
    }
    else
    {
        self.agendaController = [[iWinViewAndAddViewController alloc] initWithNibName:@"iWinViewAndAddViewController" bundle:nil inEditMode:YES];
    }
    [self.agendaController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.agendaController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.agendaController animated:YES completion:nil];
    self.agendaController.view.superview.bounds = CGRectMake(0,0,768,1003);
}

//- (IBAction)onAddAttendees
//{
//    [self.scheduleDelegate addAttenddesClicked:self.isEditing];
//}

- (IBAction)onViewMySchedule {
}

- (IBAction)onClickSave
{
    //save the meeting
//    NSArray *keys = [NSArray arrayWithObjects:@"Title", @"ID", @"DateTime", @"Location", nil];
//    NSArray *objects = [NSArray arrayWithObjects:self.titleField.text, @"ID",self.startDateLabel.text,self.placeField.text,nil];
//    
//    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
//    NSData *jsonData;
//    NSString *jsonString;
//    
//    if ([NSJSONSerialization isValidJSONObject:jsonDictionary])
//    {
//        jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary options:0 error:nil];
//        jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
//    }
//    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Meeting.php?method=createMeeting&user=%@", @"a"];
//    
//    NSMutableURLRequest * urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
//    [urlRequest setHTTPMethod:@"POST"];
//    [urlRequest setValue:@"application/json" forHTTPHeaderField:@"Accept"];
//    [urlRequest setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
//    [urlRequest setValue:[NSString stringWithFormat:@"%d", [jsonData length]] forHTTPHeaderField:@"Content-length"];
//    [urlRequest setHTTPBody:jsonData];
//    NSURLResponse * response = nil;
//    NSError * error = nil;
//    [NSURLConnection sendSynchronousRequest:urlRequest
//                          returningResponse:&response
//                                      error:&error];
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)onClickSaveAndAddMore
{
    //save it
    
//    NSArray *keys = [NSArray arrayWithObjects:@"Title", @"ID", @"DateTime", @"Location", nil];
//    NSArray *objects = [NSArray arrayWithObjects:self.titleField.text, @"ID",self.startDateLabel.text,self.placeField.text,nil];
//    
//    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
//    NSData *jsonData;
//    NSString *jsonString;
//    
//    if ([NSJSONSerialization isValidJSONObject:jsonDictionary])
//    {
//        jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary options:0 error:nil];
//        jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
//    }
//    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Meeting.php?method=createMeeting&user=%@", @"a"];
//    
//    NSMutableURLRequest * urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
//    [urlRequest setHTTPMethod:@"POST"];
//    [urlRequest setValue:@"application/json" forHTTPHeaderField:@"Accept"];
//    [urlRequest setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
//    [urlRequest setValue:[NSString stringWithFormat:@"%d", [jsonData length]] forHTTPHeaderField:@"Content-length"];
//    [urlRequest setHTTPBody:jsonData];
//    NSURLResponse * response = nil;
//    NSError * error = nil;
//    [NSURLConnection sendSynchronousRequest:urlRequest
//                            returningResponse:&response
//                                          error:&error];
    
    self.headerLabel.text = @"Schedule a Meeting";
    self.titleField.text = @"";
    self.startDateLabel.text = @"";
    self.endDateLabel.text = @"";
    self.durationField.text = @"";
    self.placeField.text = @"";
    [self.addAgendaButton setTitle:@"Add Agenda" forState:UIControlStateNormal];
}

- (IBAction)onClickCancel
{
    //[self.scheduleDelegate cancelClicked];
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
    
    UIView *popoverView = [[UIView alloc] init];   //view
    
    UIDatePicker *datePicker=[[UIDatePicker alloc]init];//Date picker
    datePicker.frame=CGRectMake(0,30,320, 216);
    datePicker.datePickerMode = UIDatePickerModeTime;
    [datePicker setMinuteInterval:5];
    //[datePicker addTarget:self action:@selector(Result) forControlEvents:UIControlEventValueChanged];
    [popoverView addSubview:datePicker];

    popoverContent.view = popoverView;
    self.popOverController = [[UIPopoverController alloc] initWithContentViewController:popoverContent];
    //popoverController.delegate=self;
    
    [self.popOverController setPopoverContentSize:CGSizeMake(320, 250) animated:NO];
    [self.popOverController presentPopoverFromRect:CGRectMake(self.startTimeLabel.frame.origin.x, self.startTimeLabel.frame.origin.y+2, self.startTimeLabel.frame.size.width, self.startTimeLabel.frame.size.height)  inView:self.view permittedArrowDirections:UIPopoverArrowDirectionUp animated:YES];
}

-(void) endTimeClicked
{
    UIViewController* popoverContent = [[UIViewController alloc] init]; //ViewController
    
    UIView *popoverView = [[UIView alloc] init];   //view
    
    UIDatePicker *datePicker=[[UIDatePicker alloc]init];//Date picker
    datePicker.frame=CGRectMake(0,30,320, 216);
    datePicker.datePickerMode = UIDatePickerModeTime;
    [datePicker setMinuteInterval:5];
    //[datePicker addTarget:self action:@selector(Result) forControlEvents:UIControlEventValueChanged];
    [popoverView addSubview:datePicker];
    
    popoverContent.view = popoverView;
    self.popOverController = [[UIPopoverController alloc] initWithContentViewController:popoverContent];
    //popoverController.delegate=self;
    
    [self.popOverController setPopoverContentSize:CGSizeMake(320, 250) animated:NO];
    [self.popOverController presentPopoverFromRect:CGRectMake(self.endTimeLabel.frame.origin.x, self.endTimeLabel.frame.origin.y+2, self.endTimeLabel.frame.size.width, self.endTimeLabel.frame.size.height)  inView:self.view permittedArrowDirections:UIPopoverArrowDirectionUp animated:YES];
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
@end
