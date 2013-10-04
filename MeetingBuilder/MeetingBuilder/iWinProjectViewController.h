//
//  iWinProjectViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 10/2/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface iWinProjectViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, UIAlertViewDelegate, NSURLConnectionDelegate>
@property (strong, nonatomic) NSMutableData *responseData;
@property (weak, nonatomic) IBOutlet UITableView *projectTable;
- (IBAction)onAddNewProject:(id)sender;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withEmail:(NSString *)email;
@end
