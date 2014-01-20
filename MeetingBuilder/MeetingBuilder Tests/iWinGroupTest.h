//
//  iWinGroupTest.h
//  MeetingBuilder
//
//  Created by Brodie Lockard on 1/20/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "iWinViewAndCreateGroupViewController.h"
#import "Contact.h"

@interface iWinGroupTest : XCTestCase

@property (strong, nonatomic) iWinViewAndCreateGroupViewController *viewVC;
@property (strong, nonatomic) Contact *contact;

@end
