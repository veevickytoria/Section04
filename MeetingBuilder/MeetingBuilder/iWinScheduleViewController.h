//
//  iWinScheduleViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 3/21/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TKCalendarDayView.h"
#import "TKCalendarDayEventView.h"
#import "iWinMeetingViewController.h"

@interface iWinScheduleViewController : UIViewController <TKCalendarDayViewDelegate, TKCalendarDayViewDataSource, ReloadScheduleDelegate>

@property (nonatomic, strong) TKCalendarDayView *dayView;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger)userID;
-(void) loadScheduleView;
-(void) setUserID:(NSInteger)userID;
@end
