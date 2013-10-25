//
//  iWinViewAndAddViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol AgendaDelegate <NSObject>

-(void)goToViewMeeting;

@end

@interface iWinViewAndAddViewController : UIViewController <UITableViewDataSource, UITableViewDelegate>
- (IBAction)onClickSave;
- (IBAction)onClickCancel;
- (IBAction)onClickAddItem;
- (IBAction)onClickAddAttendees;
@property (weak, nonatomic) IBOutlet UILabel *headerLabel;
@property (nonatomic) id<AgendaDelegate> agendaDelegate;
@property (weak, nonatomic) IBOutlet UITextField *titleTextField;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil inEditMode:(BOOL)isEditing;
@end
