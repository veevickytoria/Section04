//
//  iWinScheduleViewMeetingViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinScheduleViewMeetingViewController.h"

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
    if (self.isEditing)
    {
        self.titleField.text = @"Meeting 1";
        self.startTimeField.text = @"10/24/13 4:00 PM";
        self.endTimeField.text = @"10/24/13 5:00 PM";
        self.durationField.text = @"1 hr";
        self.placeField.text = @"O259";
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)onAddAgenda {
}

- (IBAction)onAddAttendees {
}

- (IBAction)onViewMySchedule {
}

- (IBAction)onClickSave
{
    //save the meeting
    [self.scheduleDelegate saveClicked];
}

- (IBAction)onClickSaveAndAddMore {
}

- (IBAction)onClickCancel
{
    [self.scheduleDelegate cancelClicked];
}
@end
