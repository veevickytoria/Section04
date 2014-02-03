//
//  iWinMergeNoteViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 1/23/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>
@protocol UserDelegate <NSObject>
@end
@interface iWinMergeNoteViewController : UIViewController <UITableViewDataSource, UITableViewDelegate>
@property (nonatomic) id<UserDelegate> userDelegate;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil noteContent:(NSString *)content userNames:(NSMutableArray *)names notes:(NSMutableArray *)notes noteID:(NSInteger)noteID;
@property (nonatomic) IBOutlet UITableView *userListTable;
@property (nonatomic) IBOutlet UITableView *noteListTable;
@property (nonatomic) NSMutableArray *notes;
@property (nonatomic) NSMutableArray *names;
@property (weak, nonatomic) IBOutlet UIButton *cancelButton;
@end
