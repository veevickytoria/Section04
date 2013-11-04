//
//  iWinAddAndViewTaskViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/25/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinAddAndViewTaskViewController.h"
#import <QuartzCore/QuartzCore.h>

@interface iWinAddAndViewTaskViewController ()
@property (nonatomic) BOOL isEditing;
@end

@implementation iWinAddAndViewTaskViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil inEditMode:(BOOL)isEditing
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.isEditing = isEditing;
    }
    return self;
}

- (IBAction)onClickAddAssignees
{
    [self.taskDelegate addAssigneesForTask:self.isEditing];
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
    
    [self updateButtonUI:self.saveButton];
    [self updateButtonUI:self.saveAndAddMoreButton];
    [self updateButtonUI:self.cancelButton];
    [self updateButtonUI:self.addAssigneeButton];
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

- (IBAction)onClickCancel
{
    [self.taskDelegate goToTaskList];
}

- (IBAction)onClickSave
{
    //save task
    [self.taskDelegate goToTaskList];
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
@end
