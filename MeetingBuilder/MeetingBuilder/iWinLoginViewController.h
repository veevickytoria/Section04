//
//  iWinLoginViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 10/2/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol iWinLoginDelegate <NSObject>

-(void)login;
-(void)joinUs;

@end

@interface iWinLoginViewController : UIViewController
@property (weak, nonatomic) IBOutlet UITextField *userNameField;
@property (weak, nonatomic) IBOutlet UITextField *passwordField;
@property (weak, nonatomic) IBOutlet UIButton *loginButton;
@property (weak, nonatomic) id<iWinLoginDelegate>loginDelegate;
- (IBAction)onClickLogin:(id)sender;
- (IBAction)onClickJoinUs:(id)sender;

@end
