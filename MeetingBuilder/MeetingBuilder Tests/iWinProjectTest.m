//
//  iWinProjectTest.m
//  MeetingBuilder
//
//  Created by Brodie Lockard on 2/2/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "iWinProjectListViewController.h"
#import "iWinViewAndCreateProjectViewController.h"
#import "Project.h"

@interface iWinProjectTest : XCTestCase

@property (strong, nonatomic) iWinViewAndCreateProjectViewController *projectVC;
@property (strong, nonatomic) iWinProjectListViewController *projectListVC;
@end

@implementation iWinProjectTest

- (void)setUp
{
    [super setUp];
    // Put setup code here; it will be run once, before the first test case.
    self.projectVC = [[iWinViewAndCreateProjectViewController alloc] initWithNibName:@"iWinViewAndCreateProjectViewController" bundle:nil withUserID:-1 withProjectID:-1];
    self.projectListVC = [[iWinProjectListViewController alloc] initWithNibName:@"iWinProjectListViewController" bundle:nil withUserID:-1];
    [self.projectVC viewDidLoad];
    [self.projectVC view];
    [self.projectListVC viewDidLoad];
    [self.projectListVC view];
}

- (void)tearDown
{
    // Put teardown code here; it will be run once, after the last test case.
    [super tearDown];
}

- (void)testEditedInfo
{
    self.projectVC.projectTitleField.text = @"Junior Project";
    XCTAssertTrue([self.projectVC.projectTitleField.text isEqualToString:@"Junior Project"], @"Check failed");
}

- (void)testFakeUserHasNoProjects
{
    XCTAssertTrue(self.projectListVC.projectList.count, @"Check failed");
}

@end
