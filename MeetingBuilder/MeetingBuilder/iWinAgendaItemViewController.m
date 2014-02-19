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
    // Do any additional setup after loading the view from its nib.
    
    
    
    //self.headerLabel.text = @"Add New Item";
    //if (self.isEditing)
    //{
        self.headerLabel.text = self.itemTitle;
        self.titleTextField.text = self.itemTitle;
        self.durationTextField.text = self.itemDuration;
        self.descriptionField.text = self.itemDescription;
    //}
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
