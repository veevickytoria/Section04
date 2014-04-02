//
//  iWinSettingsTest.m
//  MeetingBuilder
//
//  Created by CSSE Department on 1/11/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "iWinViewAndChangeSettingsViewController.h"
#import "iWinAppDelegate.h"
#import "Contact.h"
#import "Settings.h"

@interface iWinSettingsTest : XCTestCase
@property (strong, nonatomic) iWinViewAndChangeSettingsViewController *settingsVC;
@property (strong, nonatomic) Contact *contact;
@property (strong, nonatomic) Settings *settings;
@end

@implementation iWinSettingsTest

- (void)setUp
{
    [super setUp];

}

- (void)tearDown
{
    // Put teardown code here; it will be run once, after the last test case.
    [super tearDown];
}

- (void)testEmailChangeCorrectOldPassword
{

}

- (void)testEmailChangeIncorrectOrNoOldPassword
{
    
}

- (void)testEmailAndPasswordChangeCorrectPassword
{
    
}

- (void)testEmailAndPasswordChangeIncorrectOrNoOldPassword
{
   
}

@end
