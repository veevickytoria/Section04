//
//  iWinViewProfileViewController.m
//  MeetingBuilder
//
//  Created by Richard Shomer on 11/6/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinEditProfileViewController.h"
#import "iWinViewProfileViewController.h"
#import "iWinViewAndAddViewController.h" //May not need
#import <QuartzCore/QuartzCore.h>

@interface iWinViewProfileViewController ()
@property (strong, nonatomic) iWinEditProfileViewController *editProfileViewController;
@end

@implementation iWinViewProfileViewController

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
    
    [self updateButtonUI:self.editProfile];
    [self updateButtonUI:self.changePicture];
}

-(void) updateButtonUI: (UIButton *)button
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

-(IBAction)onChangePicture:(id)sender
{
    
}

-(IBAction)onEditProfile:(id)sender
{
    self.editProfileViewController = [[iWinEditProfileViewController alloc] initWithNibName:@"iWinEditProfileViewController" bundle:nil];
    
    [self.editProfileViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.editProfileViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.editProfileViewController animated:YES completion:nil];
    self.editProfileViewController.view.superview.bounds = CGRectMake(0,0,768,1003);
}
@end
