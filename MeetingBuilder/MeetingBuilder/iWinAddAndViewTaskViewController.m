//
//  iWinAddAndViewTaskViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/25/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinAddAndViewTaskViewController.h"
#import "iWinAddUsersViewController.h"
#import <QuartzCore/QuartzCore.h>

@interface iWinAddAndViewTaskViewController ()
@property (nonatomic) NSInteger taskID;
@property (nonatomic) NSInteger userID;
@property (nonatomic) BOOL isEditing;
@property (strong, nonatomic) iWinAddUsersViewController *userViewController;


@property (nonatomic) NSDate *endDate;
@property (strong, nonatomic) NSManagedObjectContext *context;
@property (strong, nonatomic) UIPopoverController *popOverController;
@property (strong, nonatomic) OCCalendarViewController* ocCalVC;
@property (strong, nonatomic) UIDatePicker *enddatePicker;
@property (strong, nonatomic) NSDateFormatter *dateFormatter;

@end

@implementation iWinAddAndViewTaskViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger)userID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.userID = userID;
    }
    return self;
}

- (IBAction)onClickAddAssignees
{
    self.userViewController = [[iWinAddUsersViewController alloc] initWithNibName:@"iWinAddUsersViewController" bundle:nil withPageName:@"Meeting" inEditMode:self.isEditing];
    [self.userViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.userViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.userViewController animated:YES completion:nil];
    self.userViewController.view.superview.bounds = CGRectMake(0,0,768,1003);
}

- (void)formatTime:(NSDate *)date
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"hh:mm a"];
    NSDate *currentDate = [NSDate date];
    self.endTimeLabel.text = [formatter stringFromDate:currentDate];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.headerLabel.text = @"Add New Task";
    self.saveAndAddMoreButton.hidden = NO;
    if (self.isEditing)
    {
        self.headerLabel.text = @"View Task";
        self.saveAndAddMoreButton.hidden = YES;
        self.titleField.text = @"Research Library";
        self.dueField.text = @"10/23/13 9:00 PM";
        self.descriptionField.text = @"Description about the task";
        self.createdByField.text = @"Jim";
    }
    

    self.saveAndAddMoreButton.hidden = NO;
    self.endDateLabel.userInteractionEnabled = YES;
    self.endTimeLabel.userInteractionEnabled = YES;
    
    [self setGestureRecognizers];
    
    self.dateFormatter = [[NSDateFormatter alloc] init];
    [self.dateFormatter setDateFormat:@"MM/dd/yyyy"];
    self.endDateLabel.text = [self.dateFormatter stringFromDate:[NSDate date]];
    self.endDate = [NSDate date];
    [self formatTime:[NSDate date]];

    
}

-(void) setGestureRecognizers
{
    UITapGestureRecognizer *tapEndDate = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(endDateClicked)];
    
    UITapGestureRecognizer *tapEndTime = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(endTimeClicked)];
    
    [self.endDateLabel addGestureRecognizer:tapEndDate];
    
    [self.endTimeLabel addGestureRecognizer:tapEndTime];
}

- (IBAction)onClickCancel
{
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)onClickSave
{
   
    NSArray *keys = [NSArray arrayWithObjects:
                     @"title",
                     @"isCompleted",
                     @"description",
                     @"deadline",
                     @"dateCreated",
                     @"dateAssigned",
                     @"completionCriteria",
                     @"assignedTo",
                     @"assignedFrom",
                     @"createdBy",
                     nil];
    
    NSArray *objects = [NSArray arrayWithObjects:
                        self.titleField.text,
                        @"False",
                        self.descriptionField.text,
                        [NSString stringWithFormat:@"%@ %@", self.endDateLabel.text, self.endTimeLabel.text],
                        [NSString stringWithFormat:@"%@ %@", self.endDateLabel.text, self.endTimeLabel.text],
                        [NSString stringWithFormat:@"%@ %@", self.endDateLabel.text, self.endTimeLabel.text],
                        @"nothing",
                        [[NSNumber numberWithInt:self.userID] stringValue],
                        [[NSNumber numberWithInt:self.userID] stringValue],
                        [[NSNumber numberWithInt:self.userID] stringValue],
                        nil];
    
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    NSData *jsonData;
    NSString *jsonString;
    
    if ([NSJSONSerialization isValidJSONObject:jsonDictionary])
    {
        jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary options:0 error:nil];
        jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Task/"];
    
    NSMutableURLRequest * urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
    [urlRequest setHTTPMethod:@"POST"];
    [urlRequest setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    [urlRequest setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [urlRequest setValue:[NSString stringWithFormat:@"%d", [jsonData length]] forHTTPHeaderField:@"Content-length"];
    [urlRequest setHTTPBody:jsonData];
    NSURLResponse * response = nil;
    NSError * error = nil;
    NSData * data =[NSURLConnection sendSynchronousRequest:urlRequest
                                         returningResponse:&response
                                                     error:&error];
    NSError *jsonParsingError = nil;
    NSDictionary *deserializedDictionary = (NSDictionary *)[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingAllowFragments|NSJSONReadingMutableContainers error:&jsonParsingError];
    self.taskID = [[deserializedDictionary objectForKey:@"taskID"] integerValue];
//    [self dismissViewControllerAnimated:YES completion:nil];
    
}

- (IBAction)onClickSaveAndAddMore
{
    //save and clear textfields
    self.headerLabel.text = @"Add New Task";
    self.saveAndAddMoreButton.hidden = NO;
    self.titleField.text = @"";
    self.dueField.text = @"";
    self.descriptionField.text = @"";
    self.createdByField.text = @"";
}


- (void)endDateClicked
{
    self.ocCalVC = [[OCCalendarViewController alloc] initAtPoint:CGPointMake(self.endDateLabel.frame.origin.x+84, self.endDateLabel.frame.origin.y+32) inView:self.view];
    self.ocCalVC.selectionMode = OCSelectionSingleDate;
    self.ocCalVC.delegate = self;
    [self.ocCalVC setStartDate:self.endDate];
    [self.ocCalVC setEndDate:self.endDate];
    [self.view addSubview:self.ocCalVC.view];
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
        self.endDate = selectedDate;
        self.endDateLabel.text = [dateFormatter stringFromDate:selectedDate];

    }
    [self.ocCalVC.view removeFromSuperview];
}

-(void)completedWithNoSelection
{
    [self.ocCalVC.view removeFromSuperview];
}

@end
