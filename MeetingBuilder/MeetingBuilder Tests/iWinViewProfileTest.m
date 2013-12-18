//
//  iWinViewProfileTest.m
//  MeetingBuilder
//
//  Created by CSSE Department on 12/13/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "iWinViewProfileViewController.h"
#import "Contact.h"
//#import "iWinEditProfileViewController.h"

@interface iWinViewProfileTest : XCTestCase
//@property (strong, nonatomic) iWinEditProfileViewController *editVC;
@property (strong, nonatomic) iWinViewProfileViewController *viewVC;
@property (strong, nonatomic) Contact *contact;
@end

@implementation iWinViewProfileTest

- (void)setUp
{
    [super setUp];
    // Put setup code here; it will be run once, before the first test case.
    self.viewVC = [[iWinViewProfileViewController alloc] initWithNibName:@"iWinViewProfileViewController" bundle:nil];
    
    //[self.viewVC veiwDidLoad];
}

- (void)tearDown
{
    // Put teardown code here; it will be run once, after the last test case.
    [super tearDown];
}

- (void)testExample
{
    //XCTFail(@"No implementation for \"%s\"", __PRETTY_FUNCTION__);
}

@end
