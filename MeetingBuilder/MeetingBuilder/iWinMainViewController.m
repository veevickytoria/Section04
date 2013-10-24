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
@property BOOL movedView;
@property (nonatomic) UISwipeGestureRecognizer * swiperight;
@property (nonatomic) UISwipeGestureRecognizer * swipeleft;
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

-(void) enableSliding
{
    [self.slideView addGestureRecognizer:self.swipeleft];
    [self.slideView addGestureRecognizer:self.swiperight];
    self.menuButton.hidden = NO;
}

-(void) disableSliding
{
    [self.slideView removeGestureRecognizer:self.swipeleft];
    [self.slideView removeGestureRecognizer:self.swiperight];
    self.menuButton.hidden = YES;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.loginViewController = [[iWinLoginViewController alloc] initWithNibName:@"iWinLoginViewController" bundle:nil];
    self.registerViewController = [[iWinRegisterViewController alloc] initWithNibName:@"iWinRegisterViewController" bundle:nil];
    self.movedView = false;
    self.registerViewController.registerDelegate = self;
    self.loginViewController.loginDelegate = self;
    [self.mainView  addSubview:self.loginViewController.view];
    [self.loginViewController.view setBounds:self.mainView.bounds];
    
    self.swiperight=[[UISwipeGestureRecognizer alloc]initWithTarget:self action:@selector(swiperight:)];
    self.swiperight.direction=UISwipeGestureRecognizerDirectionRight;
    
    
    self.swipeleft=[[UISwipeGestureRecognizer alloc]initWithTarget:self action:@selector(swipeleft:)];
    self.swipeleft.direction=UISwipeGestureRecognizerDirectionLeft;
    
    self.menuButton.hidden = YES;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) login:(NSString *)email
{
    [self removeSubViews];
    
    [self enableSliding];
     
    self.projectViewController = [[iWinProjectViewController alloc] initWithNibName:@"iWinProjectViewController" bundle:nil withEmail:email];
    [self.mainView  addSubview:self.projectViewController.view];
    [self.projectViewController.view setBounds:self.mainView.bounds];
}

-(void)swipeleft:(UISwipeGestureRecognizer*)gestureRecognizer
{
    if (self.movedView)
    {
        [self animateSlidingMenu:NO];
        self.movedView = !self.movedView;
    }
}

-(void)swiperight:(UISwipeGestureRecognizer*)gestureRecognizer
{
    if (!self.movedView)
    {
        [self animateSlidingMenu:YES];
        self.movedView = !self.movedView;
    }
}


-(void) joinUs
{
    [self removeSubViews];
    [self.mainView  addSubview:self.registerViewController.view];
    [self.registerViewController.view setBounds:self.mainView.bounds];
    
    [self enableSliding];
}

-(void) onRegister:(NSString *)email
{
    [self removeSubViews];
    self.projectViewController = [[iWinProjectViewController alloc] initWithNibName:@"iWinProjectViewController" bundle:nil withEmail:email];
    [self.mainView  addSubview:self.projectViewController.view];
    [self.projectViewController.view setBounds:self.mainView.bounds];
    
}

-(void) onCancel
{
    [self removeSubViews];
    
    [self.slideView removeGestureRecognizer:self.swipeleft];
    [self.slideView removeGestureRecognizer:self.swiperight];
    self.menuButton.hidden = YES;
    
    [self.mainView  addSubview:self.loginViewController.view];
    [self.loginViewController.view setBounds:self.mainView.bounds];
    
    [self disableSliding];
}

-(void)removeSubViews
{
    NSArray *subViews = [self.mainView subviews];
    for (int i=0; i<subViews.count; i++)
    {
        [subViews[i] removeFromSuperview];
    }
}

- (IBAction)onClickMenu
{
    if (!self.movedView)
    {
        [self animateSlidingMenu:YES];
    }
    else
    {
        [self animateSlidingMenu:NO];
    }
    self.movedView = !self.movedView;
}

- (IBAction)onClickLogOut
{
    [self animateSlidingMenu:NO];
    self.loginViewController = [[iWinLoginViewController alloc] initWithNibName:@"iWinLoginViewController" bundle:nil];
    [self.mainView  addSubview:self.loginViewController.view];
    [self.loginViewController.view setBounds:self.mainView.bounds];
    
    [self disableSliding];
}

-(void)animateSlidingMenu:(BOOL)moveRight
{
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.4];
    
    CGRect oldFrame = self.menuView.frame;
    CGRect oldFrameMain = self.slideView.frame;
    
    if (moveRight)
    {
        self.menuView.frame = CGRectMake(0, oldFrame.origin.y, oldFrame.size.width, oldFrame.size.height);
        self.slideView.frame = CGRectMake(oldFrameMain.origin.x+200,oldFrameMain.origin.y,oldFrameMain.size.width,oldFrameMain.size.height);
    }
    else
    {
        self.menuView.frame = CGRectMake(-200, oldFrame.origin.y, oldFrame.size.width, oldFrame.size.height);
        self.slideView.frame = CGRectMake(0,oldFrameMain.origin.y,oldFrameMain.size.width,oldFrameMain.size.height);
    }
    [UIView commitAnimations];
}

@end
