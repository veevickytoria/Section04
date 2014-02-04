//
//  iWinViewAndAddNotesViewController.h
//  MeetingBuilder
//
//  Created by Richard Shomer on 10/29/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "iWinAddUsersViewController.h"
#import "iWinMergeNoteViewController.h"

@protocol ViewAddNoteDelegate <NSObject>

//-(void)saveNoteClicked;
//-(void)cancelNoteClicked;
//-(void)mergeNoteClicked;
-(void)refreshNoteList;
@end


@interface iWinViewAndAddNotesViewController : UIViewController <UIAlertViewDelegate, UserDelegate, MergeNoteDelegate>
@property (nonatomic) id<ViewAddNoteDelegate> addNoteDelegate;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withNoteID:(NSInteger)noteID withUserID:(NSInteger)userID;
- (IBAction)saveButton:(id)sender;
- (IBAction)cancelButton:(id)sender;
- (IBAction)shareNotesButton:(id)sender;
- (IBAction)mergeNotesButton:(id)sender;
- (IBAction)confirmDeleteAlert;
@property (weak, nonatomic) IBOutlet UITextField *titleField;
@property (weak, nonatomic) IBOutlet UITextView *noteField;
@property (weak, nonatomic) IBOutlet UIButton *saveButton;
@property (weak, nonatomic) IBOutlet UIButton *deleteButton;
@end
