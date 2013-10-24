//
//  iWinMainViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 10/2/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "iWinLoginViewController.h"
#import "iWinRegisterViewController.h"
#import "iWinProjectViewController.h"

@interface iWinMainViewController : UIViewController <iWinLoginDelegate, iWinRegisterVCDelegate>
@property (weak, nonatomic) IBOutlet UIView *mainView;
@property (weak, nonatomic) IBOutlet UIView *slideView;
@property (weak, nonatomic) IBOutlet UIView *menuView;
- (IBAction)onClickMenu;
- (IBAction)onClickLogOut;
@property (weak, nonatomic) IBOutlet UIButton *menuButton;

@end
