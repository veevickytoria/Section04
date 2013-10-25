//
//  iWinAddUsersViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/25/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinAddUsersViewController.h"

@interface iWinAddUsersViewController ()
@property (nonatomic) NSString *pageName;
@property (nonatomic) BOOL isEditing;
@end

@implementation iWinAddUsersViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withPageName:(NSString *)pageName inEditMode:(BOOL)isEditing
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.pageName = pageName;
        self.isEditing = isEditing;
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

- (IBAction)onClickSendInvite
{
}

- (IBAction)onClickSave
{
    //save
    [self.userDelegate returnToPreviousView:self.pageName inEditMode:self.isEditing];
}

- (IBAction)onClickCancel
{
    [self.userDelegate returnToPreviousView:self.pageName inEditMode:self.isEditing];
}
@end
