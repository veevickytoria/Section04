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
@property (nonatomic) id<SettingsDelegate> settingsDelegate;

- (IBAction)clickCancel:(id)sender;
- (IBAction)clickSave:(id)sender;
@end
