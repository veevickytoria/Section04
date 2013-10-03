//
//  iWinRegisterViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 10/2/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol iWinRegisterVCDelegate <NSObject>

-(void)onRegister;
-(void)onCancel;

@end

@interface iWinRegisterViewController : UIViewController
@property (weak, nonatomic) IBOutlet UITextField *nameField;
@property (weak, nonatomic) IBOutlet UITextField *emailField;
@property (weak, nonatomic) IBOutlet UITextField *passwordField;
@property (weak, nonatomic) IBOutlet UITextField *confirmPasswordField;
@property (weak, nonatomic) id<iWinRegisterVCDelegate> registerDelegate;
- (IBAction)onClickRegister:(id)sender;
- (IBAction)onClickCancel:(id)sender;

@end
