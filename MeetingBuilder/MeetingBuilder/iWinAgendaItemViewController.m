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

const float FIELD_BPRDER_WIDTH = 0.7f;
const float FIELD_CORNER_RADIUS = 7.0f;

@implementation iWinAgendaItemViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.itemIndex = -1;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.descriptionField.layer.borderColor = [[UIColor lightGrayColor] CGColor];
    self.descriptionField.layer.borderWidth = FIELD_BPRDER_WIDTH;
    self.descriptionField.layer.cornerRadius = FIELD_CORNER_RADIUS;
    self.headerLabel.text = self.itemTitle;
    self.titleTextField.text = self.itemTitle;
    self.durationTextField.text = self.itemDuration;
    self.descriptionField.text = self.itemDescription;
}

- (IBAction)onClickSave
{
    [self.itemDelegate saveItem :self.titleTextField.text duration :self.durationTextField.text description :self.descriptionField.text itemIndex:self.itemIndex];
}

- (IBAction)onClickCancel
{
    [self.itemDelegate cancel];
}

@end
