//
//  iWinGroupTest.m
//  MeetingBuilder
//
//  Created by Brodie Lockard on 1/20/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import "iWinGroupTest.h"

@implementation iWinGroupTest
- (void)setUp
{
    [super setUp];
    // Put setup code here; it will be run once, before the first test case.
    self.viewVC = [[iWinViewAndCreateGroupViewController alloc] initWithNibName:@"iWinViewAndCreateGroupViewController" bundle:nil withUserID:-1 withGroupID:-1];
    [self.viewVC viewDidLoad];
    [self.viewVC view];
}

- (void)tearDown
{
    // Put teardown code here; it will be run once, after the last test case.
    [super tearDown];
}

- (void)testEditedUserInfo
{
    self.viewVC.groupTitleField.text = @"Team iWin";
    
    XCTAssertTrue([self.viewVC.groupTitleField.text isEqualToString:@"Team iWin"], @"Check failed");
}

@end
