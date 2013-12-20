//
//  iWinViewAndChangeSettingsViewController.m
//  MeetingBuilder
//
//  Created by Richard Shomer on 11/4/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinViewAndChangeSettingsViewController.h"

@interface iWinViewAndChangeSettingsViewController ()

@end

@implementation iWinViewAndChangeSettingsViewController

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
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)clickCancel:(id)sender {
    [self.settingsDelegate onclickCancelSettings];
}

- (IBAction)clickSave:(id)sender {
    [self.settingsDelegate onClickSaveSettings];
}
@end
