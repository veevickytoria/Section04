//
//  iWinScheduleViewMeetingViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinScheduleViewMeetingViewController.h"
#import <QuartzCore/QuartzCore.h>

@interface iWinScheduleViewMeetingViewController ()
@property (nonatomic) BOOL isEditing;
@property (strong, nonatomic) NSString *meetingID;
@property (strong, nonatomic) NSString *dateTime;
@property (strong, nonatomic) NSString *title;
@property (strong, nonatomic) NSString *location;
@property (strong, nonatomic) UIPopoverController *popOverController;
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
    self.headerLabel.text = @"Schedule a Meeting";
    [self.addAgendaButton setTitle:@"Add Agenda" forState:UIControlStateNormal];
    self.saveAndAddMoreButton.hidden = NO;
    if (self.isEditing)
    {
        self.headerLabel.text = @"View Meeting";
        self.titleField.text = self.title;
        self.startTimeField.text = self.dateTime;
        self.endTimeField.text = self.dateTime;
        self.durationField.text = @"1 hr";
        self.placeField.text = self.location;
        [self.addAgendaButton setTitle:@"Agenda 101" forState:UIControlStateNormal];
        self.saveAndAddMoreButton.hidden = YES;
    }
    
    [self updateButtonUI:self.saveButton];
    [self updateButtonUI:self.cancelButton];
    [self updateButtonUI:self.saveAndAddMoreButton];
    [self updateButtonUI:self.addAgendaButton];
    [self updateButtonUI:self.addAttendeesButton];
    [self updateButtonUI:self.visitScheduleButton];
}

-(void) updateButtonUI:(UIButton *)button
{
    button.layer.cornerRadius = 7;
    button.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    button.layer.borderWidth = 1.0f;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)onAddAgenda
{
    if ([self.addAgendaButton.titleLabel.text isEqualToString:@"Add Agenda"])
        [self.scheduleDelegate addAgendaClicked:NO];
    else
        [self.scheduleDelegate addAgendaClicked:YES];
}

- (IBAction)onAddAttendees
{
    [self.scheduleDelegate addAttenddesClicked:self.isEditing];
}

- (IBAction)onViewMySchedule {
}

- (IBAction)onClickSave
{
    //save the meeting
    NSArray *keys = [NSArray arrayWithObjects:@"Title", @"ID", @"DateTime", @"Location", nil];
    NSArray *objects = [NSArray arrayWithObjects:self.titleField.text, @"ID",self.startTimeField.text,self.placeField.text,nil];
    
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    NSData *jsonData;
    NSString *jsonString;
    
    if ([NSJSONSerialization isValidJSONObject:jsonDictionary])
    {
        jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary options:0 error:nil];
        jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Meeting.php?method=createMeeting&user=%@", @"a"];
    
    NSMutableURLRequest * urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
    [urlRequest setHTTPMethod:@"POST"];
    [urlRequest setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    [urlRequest setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [urlRequest setValue:[NSString stringWithFormat:@"%d", [jsonData length]] forHTTPHeaderField:@"Content-length"];
    [urlRequest setHTTPBody:jsonData];
    NSURLResponse * response = nil;
    NSError * error = nil;
    [NSURLConnection sendSynchronousRequest:urlRequest
                          returningResponse:&response
                                      error:&error];
    [self.scheduleDelegate saveClicked];
}

- (IBAction)onClickSaveAndAddMore
{
    //save it
    self.headerLabel.text = @"Schedule a Meeting";
    self.titleField.text = @"";
    self.startTimeField.text = @"";
    self.endTimeField.text = @"";
    self.durationField.text = @"";
    self.placeField.text = @"";
    [self.addAgendaButton setTitle:@"Add Agenda" forState:UIControlStateNormal];
}

- (IBAction)onClickCancel
{
    [self.scheduleDelegate cancelClicked];
}
- (IBAction)startTimeClicked {
    UIViewController* popoverContent = [[UIViewController alloc] init]; //ViewController
    
    UIView *popoverView = [[UIView alloc] init];   //view
    
    UIDatePicker *datePicker=[[UIDatePicker alloc]init];//Date picker
    datePicker.frame=CGRectMake(0,44,320, 216);
    datePicker.datePickerMode = UIDatePickerModeDateAndTime;
    [datePicker setMinuteInterval:5];
    [datePicker setTag:10];
    //[datePicker addTarget:self action:@selector(Result) forControlEvents:UIControlEventValueChanged];
    [popoverView addSubview:datePicker];
    
    popoverContent.view = popoverView;
    self.popOverController = [[UIPopoverController alloc] initWithContentViewController:popoverContent];
    //popoverController.delegate=self;
    
    [self.popOverController setPopoverContentSize:CGSizeMake(320, 264) animated:NO];
    [self.popOverController presentPopoverFromRect:self.startTimeField.frame inView:self.view permittedArrowDirections:UIPopoverArrowDirectionLeft animated:YES];//tempButton.frame
}

- (IBAction)endTimeClicked {
    UIViewController* popoverContent = [[UIViewController alloc] init]; //ViewController
    
    UIView *popoverView = [[UIView alloc] init];   //view
    
    UIDatePicker *datePicker=[[UIDatePicker alloc]init];//Date picker
    datePicker.frame=CGRectMake(0,44,320, 216);
    datePicker.datePickerMode = UIDatePickerModeDateAndTime;
    [datePicker setMinuteInterval:5];
    [datePicker setTag:10];
    //[datePicker addTarget:self action:@selector(Result) forControlEvents:UIControlEventValueChanged];
    [popoverView addSubview:datePicker];
    
    popoverContent.view = popoverView;
    self.popOverController = [[UIPopoverController alloc] initWithContentViewController:popoverContent];
    //popoverController.delegate=self;
    
    [self.popOverController setPopoverContentSize:CGSizeMake(320, 264) animated:NO];
    [self.popOverController presentPopoverFromRect:self.endTimeField.frame inView:self.view permittedArrowDirections:UIPopoverArrowDirectionLeft animated:YES];//tempButton.frame

}
@end
