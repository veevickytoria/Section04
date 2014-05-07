//
//  iWinMenuViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 3/20/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import "iWinMenuViewController.h"

@interface iWinMenuViewController ()
@property (nonatomic) UIButton *lastClicked;
@end

@implementation iWinMenuViewController

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
    [self updateSelectedMenu:self.homePageButton];
}

- (void) updateSelectedMenu:(UIButton*)newButton
{
    if (self.lastClicked)
    {
        self.lastClicked.backgroundColor = [UIColor clearColor];
        [self.lastClicked setTitleColor:[UIColor lightGrayColor] forState:UIControlStateNormal];
    }
    newButton.backgroundColor = [UIColor whiteColor];
    [newButton setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    self.lastClicked = newButton;
}

- (IBAction)onClickHome {
    [self updateSelectedMenu:self.homePageButton];
    [self.menuDelegate goToHomePage:YES];
}

- (IBAction)onClickMeetings {
    [self updateSelectedMenu:self.meetingsButton];
    [self.menuDelegate goToMeetings:YES];
}

- (IBAction)onClickProfile {
    [self updateSelectedMenu:self.profileButton];
    [self.menuDelegate goToProfile:YES];
}

- (IBAction)onClickTasks {
    [self updateSelectedMenu:self.tasksButton];
    [self.menuDelegate goToTasks:YES];
}

- (IBAction)onClickNotes {
    [self updateSelectedMenu:self.notesButton];
    [self.menuDelegate goToNotes:YES];
}

- (IBAction)onClickSettings {
    [self updateSelectedMenu:self.settingsButton];
    [self.menuDelegate goToSettings:YES];
}

- (IBAction)onClickLogout {
    [self updateSelectedMenu:self.logOutButton];
    [self.menuDelegate goToLogout];
}
@end
