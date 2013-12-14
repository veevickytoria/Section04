//
//  iWinViewProfileViewController.h
//  MeetingBuilder
//
//  Created by Richard Shomer on 11/6/13.
//  Modified by Gordon Hazzard and Brodie Lockard.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol ProfileDelegate <NSObject>
-(void) onClickEditProfile;
-(void) onClickChangePicture;

@end

@interface iWinViewProfileViewController : UIViewController
- (IBAction)onChangePicture:(id)sender;
- (IBAction)onEditProfile:(id)sender;
-(id) initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil;
@property (weak, nonatomic) IBOutlet UIButton *changePicture;
@property (weak, nonatomic) IBOutlet UIButton *editProfile;
@property (nonatomic) id<ProfileDelegate> profileDelegate;


@end
