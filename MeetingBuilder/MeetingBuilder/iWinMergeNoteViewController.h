//
//  iWinMergeNoteViewController.h
//  MeetingBuilder
//
//  Created by Richard Shomer on 11/4/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol MergeNoteDelegate <NSObject>

-(void)saveMergeClicked;
-(void)cancelMergeClicked;
@end

@interface iWinMergeNoteViewController : UIViewController

@property (nonatomic) id<MergeNoteDelegate> mergeDelegate;
- (IBAction)saveClicked:(id)sender;
- (IBAction)cancelClicked:(id)sender;
@property (weak, nonatomic) IBOutlet UISearchBar *noteToMerge;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil inEditMode:(BOOL)isEditing;

@end
