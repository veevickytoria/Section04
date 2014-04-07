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

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
//inEditMode:(BOOL)isEditing
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        //self.isEditing = isEditing;]
        self.itemIndex = -1;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.descriptionField.layer.borderColor = [[UIColor lightGrayColor] CGColor];
    self.descriptionField.layer.borderWidth = 0.7f;
    self.descriptionField.layer.cornerRadius = 7.0f;
    self.headerLabel.text = self.itemTitle;
    self.titleTextField.text = self.itemTitle;
    self.durationTextField.text = self.itemDuration;
    self.descriptionField.text = self.itemDescription;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)onClickSave
{
    //[self.itemDelegate saveItem:self.titleTextField.text];
    [self.itemDelegate saveItem :self.titleTextField.text duration :self.durationTextField.text description :self.descriptionField.text itemIndex:self.itemIndex];
}

- (IBAction)onClickCancel
{
    [self.itemDelegate cancel];
}

@end
