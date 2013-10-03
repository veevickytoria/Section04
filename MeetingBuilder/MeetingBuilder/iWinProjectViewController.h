//
//  iWinProjectViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 10/2/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface iWinProjectViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, UIAlertViewDelegate>
@property (weak, nonatomic) IBOutlet UITableView *projectTable;
- (IBAction)onAddNewProject:(id)sender;

@end
