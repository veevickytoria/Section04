//
//  iWinEditProfileViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 12/13/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinEditProfileViewController.h"
#import "iWinViewProfileViewController.h"
#import <QuartzCore/QuartzCore.h>

@interface iWinEditProfileViewController ()

@end

@implementation iWinEditProfileViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    [self updateButtonUI:self.save];
    [self updateButtonUI:self.cancel];
    [self updateButtonUI:self.changePicture];
    
    [self updateLabelUI:self.startDateLabel];
    
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) updateButtonUI: (UIButton *)button
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

-(IBAction)onChangePicture:(id)sender
{
    
}

-(IBAction)onSave:(id)sender
{
    
}

-(IBAction)onCancel:(id)sender
{
    
}

@end
