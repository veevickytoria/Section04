//
//  iWinAgendaItemViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol AgendaItemDelegate <NSObject>

-(void)saveItem:(NSString *)name duration:(NSString*)duration description:(NSString*)description
      itemIndex: (NSInteger) itemIndex;


-(void)cancel;

@end

@interface iWinAgendaItemViewController : UIViewController
@property (weak, nonatomic) IBOutlet UILabel *headerLabel;
- (IBAction)onClickSave;
- (IBAction)onClickCancel;

//for after editting, resign the fileds
@property (nonatomic, assign) NSString* itemTitle;
@property (nonatomic, assign) NSString* itemDuration;
@property (nonatomic, assign) NSString* itemDescription;

@property (nonatomic, assign) NSInteger itemIndex;

@property (weak, nonatomic) IBOutlet UITextField *titleTextField;
@property (weak, nonatomic) IBOutlet UITextField *durationTextField;
@property (weak, nonatomic) IBOutlet UIButton *cancelButton;
@property (weak, nonatomic) IBOutlet UITextView *descriptionField;
@property (weak, nonatomic) IBOutlet UIButton *saveButton;
@property (nonatomic) id<AgendaItemDelegate> itemDelegate;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil;
@end
