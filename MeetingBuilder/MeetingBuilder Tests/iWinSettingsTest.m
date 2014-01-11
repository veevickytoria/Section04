//
//  iWinSettingsTest.m
//  MeetingBuilder
//
//  Created by CSSE Department on 1/11/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "iWinViewAndChangeSettingsViewController.h"

@interface iWinSettingsTest : XCTestCase
@property (strong, nonatomic) iWinViewAndChangeSettingsViewController *settingsVC;
@end

@implementation iWinSettingsTest

- (void)setUp
{
    [super setUp];
    // Put setup code here; it will be run once, before the first test case.
    self.settingsVC = [[iWinViewAndChangeSettingsViewController alloc] initWithNibName:@"iWinViewAndChangeSettingsViewController" bundle:nil withID:1];
    [self.settingsVC viewDidLoad];
    [self.settingsVC view];

}

- (void)tearDown
{
    // Put teardown code here; it will be run once, after the last test case.
    [super tearDown];
}

- (void)testEmailChangeCorrectOldPassword
{
    XCTFail(@"No implementation for \"%s\"", __PRETTY_FUNCTION__);
}

- (void)testEmailChangeIncorrectOrNoOldPassword
{
    XCTFail(@"No implementation for \"%s\"", __PRETTY_FUNCTION__);
}

- (void)testPasswordChangeCorrectOldPassword
{
    XCTFail(@"No implementation for \"%s\"", __PRETTY_FUNCTION__);
}

- (void)testPasswordChangeIncorrectOrNoOldPassword
{
    XCTFail(@"No implementation for \"%s\"", __PRETTY_FUNCTION__);
}

- (void)testEmailAndPasswordChangeCorrectPassword
{
    XCTFail(@"No implementation for \"%s\"", __PRETTY_FUNCTION__);
}

- (void)testEmailAndPasswordChangeIncorrectOrNoOldPassword
{
    XCTFail(@"No implementation for \"%s\"", __PRETTY_FUNCTION__);
}

@end
