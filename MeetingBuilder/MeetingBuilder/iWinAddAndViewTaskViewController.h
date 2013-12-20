//
//  iWinAddAndViewTaskViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 10/25/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface iWinAddAndViewTaskViewController : UIViewController
- (IBAction)onClickCancel;
- (IBAction)onClickSave;
- (IBAction)onClickSaveAndAddMore;
@property (weak, nonatomic) IBOutlet UIButton *saveAndAddMoreButton;
@property (weak, nonatomic) IBOutlet UIButton *saveButton;
@property (weak, nonatomic) IBOutlet UIButton *addAssigneeButton;
@property (weak, nonatomic) IBOutlet UIButton *cancelButton;
@property (weak, nonatomic) IBOutlet UILabel *headerLabel;
@property (weak, nonatomic) IBOutlet UITextField *titleField;
@property (weak, nonatomic) IBOutlet UITextField *dueField;
@property (weak, nonatomic) IBOutlet UITextView *descriptionField;
@property (weak, nonatomic) IBOutlet UITextField *createdByField;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil inEditMode:(BOOL)isEditing;
- (IBAction)onClickAddAssignees;

@end
