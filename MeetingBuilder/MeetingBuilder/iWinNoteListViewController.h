//
//  iWinNoteListViewController.h
//  MeetingBuilder
//
//  Created by Richard Shomer on 10/29/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol NoteListDelegate <NSObject>

-(void)createNoteClicked :(BOOL)isEditing;
@end

@interface iWinNoteListViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, UIAlertViewDelegate, NSURLConnectionDelegate>
@property (strong, nonatomic) NSMutableData *responseData;
@property (weak, nonatomic) IBOutlet UIButton *createNoteButton;
@property (weak, nonatomic) IBOutlet UITableView *noteTable;
@property (nonatomic) id<NoteListDelegate> noteListDelegate;
- (IBAction)onCreateNewNote;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withEmail:(NSString *)email;
@end