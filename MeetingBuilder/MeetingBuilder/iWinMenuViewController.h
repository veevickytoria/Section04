//
//  iWinMenuViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 3/20/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol MenuDelegate <NSObject>

-(void)goToHomePage;
-(void)goToMeetings;
-(void)goToProfile;
-(void)goToTasks;
-(void)goToNotes;
-(void)goToSettings;
-(void)goToLogout;

@end

@interface iWinMenuViewController : UIViewController

@property (weak, nonatomic) IBOutlet UIButton *homePageButton;
@property (weak, nonatomic) IBOutlet UIButton *meetingsButton;
@property (weak, nonatomic) IBOutlet UIButton *profileButton;
@property (weak, nonatomic) IBOutlet UIButton *tasksButton;
@property (weak, nonatomic) IBOutlet UIButton *notesButton;
@property (weak, nonatomic) IBOutlet UIButton *settingsButton;
@property (weak, nonatomic) IBOutlet UIButton *logOutButton;
@property (weak, nonatomic) id<MenuDelegate> menuDelegate;

- (IBAction)onClickHome;
- (IBAction)onClickMeetings;
- (IBAction)onClickProfile;
- (IBAction)onClickTasks;
- (IBAction)onClickNotes;
- (IBAction)onClickSettings;
- (IBAction)onClickLogout;

@end
