//
//  iWinViewProfileViewController.h
//  MeetingBuilder
//
//  Created by Richard Shomer on 11/6/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol ProfileDelegate <NSObject>


@end

@interface iWinViewProfileViewController : UIViewController
@property (nonatomic) id<ProfileDelegate> profileDelegate;


@end
