//
//  iWinHomeScreenViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "iWinScheduleViewMeetingViewController.h"
#import "iWinAddAndViewTaskViewController.h"


@interface iWinHomeScreenViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, ViewMeetingDelegate, ViewTaskDelegate>

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger)userID;
@property (weak, nonatomic) IBOutlet UITableView *feedTable;

@end
