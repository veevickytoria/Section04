//
//  iWinProjectListViewController.h
//  MeetingBuilder
//
//  Created by Brodie Lockard on 2/2/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "iWinViewAndCreateProjectViewController.h"
#import "CustomSubtitledCell.h"

@interface iWinProjectListViewController : UIViewController
- (IBAction)onClickCreateProject:(id)sender;
- (IBAction)onClickBackToProfile:(id)sender;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger)userID;
@property (weak, nonatomic) IBOutlet UITableView *projectTable;

@end
