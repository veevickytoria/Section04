//
//  iWinViewAndAddViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "iWinAgendaItemViewController.h"

@protocol AgendaDelegate <NSObject>

-(void)goToViewMeeting;
-(void)addAttendeesForAgenda:(BOOL)isEditing;

@end

@interface iWinViewAndAddViewController : UIViewController <UITableViewDataSource, UITableViewDelegate, AgendaItemDelegate>
- (IBAction)onClickSave;
- (IBAction)onClickCancel;
- (IBAction)onClickAddItem;
- (IBAction)onClickAddAttendees;
@property (weak, nonatomic) IBOutlet UIButton *saveButton;
@property (weak, nonatomic) IBOutlet UIButton *cancelButton;
@property (weak, nonatomic) IBOutlet UILabel *headerLabel;
@property (nonatomic) id<AgendaDelegate> agendaDelegate;
@property (weak, nonatomic) IBOutlet UITextField *titleTextField;
@property (weak, nonatomic) IBOutlet UIButton *addAttendeesButton;
@property (weak, nonatomic) IBOutlet UIButton *addItemButton;
@property (weak, nonatomic) IBOutlet UITableView *itemTableView;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil inEditMode:(BOOL)isEditing;
@end
