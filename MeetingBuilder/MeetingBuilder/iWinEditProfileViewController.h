//
//  iWinEditProfileViewController.h
//  MeetingBuilder
//
//  Created by CSSE Department on 12/13/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol ProfileDelegate <NSObject>
-(void) onClickChangePicture;
@end

@interface iWinEditProfileViewController : UIViewController
-(IBAction) onChangePicture:(id)sender;
-(IBAction) onSave:(id)sender;
-(IBAction) onCancel:(id)sender;
-(id) initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil company:(NSString *)company email:(NSString *)email phone:(NSString *)phone position:(NSString *)position moreAboutMe:(NSString *)moreAboutMe profilePic: (UIImage *) profilePic;

@property (weak, nonatomic) IBOutlet UIButton *changePicture;
@property (weak, nonatomic) IBOutlet UIButton *save;
@property (weak, nonatomic) IBOutlet UIButton *cancel;

@property (weak, nonatomic) IBOutlet UITextField *companyField;
@property (weak, nonatomic) IBOutlet UITextField *emailField;
@property (weak, nonatomic) IBOutlet UITextField *phoneField;
@property (weak, nonatomic) IBOutlet UITextField *positionField;
@property (weak, nonatomic) IBOutlet UITextField *moreAboutMeField;
@property (weak, nonatomic) IBOutlet UIImageView *profilePicView;


@property (nonatomic) id<ProfileDelegate> profileDelegate;

@end
