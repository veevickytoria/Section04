//
//  iWinProjectViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 10/2/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "iWinScheduleViewMeetingViewController.h"

@interface iWinMeetingViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, UIAlertViewDelegate, NSURLConnectionDelegate, ViewMeetingDelegate>
@property (strong, nonatomic) NSMutableData *responseData;
@property (weak, nonatomic) IBOutlet UIButton *scheduleMeetingButton;
@property (weak, nonatomic) IBOutlet UITableView *projectTable;
- (IBAction)onScheduleNewMeeting;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withID:(NSInteger)userID;
@end
