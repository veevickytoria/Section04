//
//  iWinMainViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/2/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinMainViewController.h"

@interface iWinMainViewController ()
@property (strong, nonatomic) iWinLoginViewController *loginViewController;
@property (strong, nonatomic) iWinRegisterViewController *registerViewController;
@property (strong, nonatomic) iWinProjectViewController *projectViewController;
@end

@implementation iWinMainViewController

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
    self.loginViewController = [[iWinLoginViewController alloc] initWithNibName:@"iWinLoginViewController" bundle:nil];
    self.registerViewController = [[iWinRegisterViewController alloc] initWithNibName:@"iWinRegisterViewController" bundle:nil];
    self.projectViewController = [[iWinProjectViewController alloc] initWithNibName:@"iWinProjectViewController" bundle:nil];
    self.registerViewController.registerDelegate = self;
    self.loginViewController.loginDelegate = self;
    [self.view  addSubview:self.loginViewController.view];
    [self.loginViewController.view setBounds:self.view.bounds];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) login
{
    [self removeSubViews];
    [self.view  addSubview:self.projectViewController.view];
    [self.projectViewController.view setBounds:self.view.bounds];
}

-(void) joinUs
{
    [self removeSubViews];
    [self.view  addSubview:self.registerViewController.view];
    [self.registerViewController.view setBounds:self.view.bounds];
}

-(void) onRegister
{
    [self removeSubViews];
    [self.view  addSubview:self.projectViewController.view];
    [self.projectViewController.view setBounds:self.view.bounds];
}

-(void) onCancel
{
    [self removeSubViews];
    [self.view  addSubview:self.loginViewController.view];
    [self.loginViewController.view setBounds:self.view.bounds];
}

-(void)removeSubViews
{
    NSArray *subViews = [self.view subviews];
    for (int i=0; i<subViews.count; i++)
    {
        [subViews[i] removeFromSuperview];
    }
}

@end
