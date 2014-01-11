//
//  iWinViewAndChangeSettingsViewController.h
//  MeetingBuilder
//
//  Created by Richard Shomer on 11/4/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol SettingsDelegate <NSObject>

-(void)onClickSaveSettings;
-(void)onclickCancelSettings;
@end

@interface iWinViewAndChangeSettingsViewController : UIViewController
//@property (nonatomic) id<SettingsDelegate> settingsDelegate;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withID:(NSInteger) userID;

- (IBAction)clickCancel:(id)sender;
- (IBAction)clickSave:(id)sender;

@property (weak, nonatomic) IBOutlet UITextField *emailTextField;
@property (weak, nonatomic) IBOutlet UITextField *oldPasswordTextField;
@property (weak, nonatomic) IBOutlet UITextField *passwordTextField;
@property (weak, nonatomic) IBOutlet UITextField *confirmPasswordTextField;
@property (weak, nonatomic) IBOutlet UIPickerView *whenToNotifyPicker;
@property (weak, nonatomic) IBOutlet UISwitch *shouldNotifySwitch;
@property (weak, nonatomic) IBOutlet UIButton *cancelButton;
@property (weak, nonatomic) IBOutlet UIButton *saveAndEditButton;

@end
