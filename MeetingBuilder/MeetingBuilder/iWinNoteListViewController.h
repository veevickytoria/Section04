//
//  iWinNoteListViewController.h
//  MeetingBuilder
//
//  Created by Richard Shomer on 10/29/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "iWinViewAndAddNotesViewController.h"
#import "LargeDefaultCell.h"

@interface iWinNoteListViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, UIAlertViewDelegate, NSURLConnectionDelegate, ViewAddNoteDelegate, LargeCellDelegate>
@property (strong, nonatomic) NSMutableData *responseData;
@property (weak, nonatomic) IBOutlet UIButton *createNoteButton;
@property (weak, nonatomic) IBOutlet UITableView *noteTable;
- (IBAction)onCreateNewNote;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger)userID;
@end