//
//  iWinAgendaItemViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinAgendaItemViewController.h"
#import <QuartzCore/QuartzCore.h>

@interface iWinAgendaItemViewController ()
@property (nonatomic) BOOL isEditing;
@end

@implementation iWinAgendaItemViewController

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
    self.headerLabel.text = @"Add New Item";
    if (self.isEditing)
    {
        self.headerLabel.text = @"View Item";
        self.durationTextField.text = @"1 hr";
        self.descriptionField.text = @"Enter description here.";
    }
    [self updateButtonUI:self.saveButton];
    [self updateButtonUI:self.cancelButton];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)onClickSave
{
    [self.itemDelegate saveItem:self.titleTextField.text];
}

- (IBAction)onClickCancel
{
    [self.itemDelegate cancel];
}

-(void) updateButtonUI:(UIButton *)button
{
    button.layer.cornerRadius = 7;
    button.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    button.layer.borderWidth = 1.0f;
}

@end
