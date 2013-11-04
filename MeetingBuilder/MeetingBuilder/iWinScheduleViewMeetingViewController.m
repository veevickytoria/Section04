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
@end

@implementation iWinScheduleViewMeetingViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil inEditMode:(BOOL)isEditing
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.isEditing = isEditing;
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
        self.titleField.text = @"Meeting 1";
        self.startTimeField.text = @"10/24/13 4:00 PM";
        self.endTimeField.text = @"10/24/13 5:00 PM";
        self.durationField.text = @"1 hr";
        self.placeField.text = @"O259";
        [self.addAgendaButton setTitle:@"Agenda 101" forState:UIControlStateNormal];
        self.saveAndAddMoreButton.hidden = YES;
    }
    
    self.saveButton.layer.cornerRadius = 7;
    self.saveButton.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    self.saveButton.layer.borderWidth = 1.0f;
    
    self.saveAndAddMoreButton.layer.cornerRadius = 7;
    self.saveAndAddMoreButton.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    self.saveAndAddMoreButton.layer.borderWidth = 1.0f;
    
    self.cancelButton.layer.cornerRadius = 7;
    self.cancelButton.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    self.cancelButton.layer.borderWidth = 1.0f;
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
@end
