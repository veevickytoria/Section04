//
//  iWinAddUserTest.m
//  MeetingBuilder
//
//  Created by CSSE Department on 12/11/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "iWinAddUsersViewController.h"
#import "iWinContact.h"

@interface iWinAddUserTest : XCTestCase
@property (strong, nonatomic) iWinAddUsersViewController *addUsersVC;
@property (strong, nonatomic) iWinContact *contact;
@end

@implementation iWinAddUserTest

- (void)setUp
{
    [super setUp];
    // Put setup code here; it will be run once, before the first test case.
    self.addUsersVC = [[iWinAddUsersViewController alloc] initWithNibName:@"iWinAddUsersViewController" bundle:nil withPageName:@"Meeting" inEditMode:NO];
    
    self.contact = [[iWinContact alloc] init];
    [self.contact initializeWithFirstName:@"Dharmin" withLastName:@"Shah" withEmail:@"shahdk@rose-hulman.edu"];
    [self.addUsersVC.userList addObject:self.contact];
}

- (void)tearDown
{
    // Put teardown code here; it will be run once, after the last test case.
    [super tearDown];
}

- (void)testInvalidSearch
{
    [self.addUsersVC searchForUserWithString:@"Joe"];
    XCTAssertTrue(self.addUsersVC.filteredList.count == 0, @"Search failed");
}

- (void)testValidFirstNameSearch
{
    [self.addUsersVC searchForUserWithString:@"Dhar"];
    XCTAssertTrue(self.addUsersVC.filteredList.count == 1, @"Search failed");
    
    iWinContact *contact = (iWinContact *)[self.addUsersVC.filteredList objectAtIndex:0];
    XCTAssertTrue([contact.firstName isEqualToString:@"Dharmin"], @"Search failed");
    XCTAssertTrue([contact.lastName isEqualToString:@"Shah"], @"Search failed");
    XCTAssertTrue([contact.email isEqualToString:@"shahdk@rose-hulman.edu"], @"Search failed");
}

- (void)testValidLastNameSearch
{
    [self.addUsersVC searchForUserWithString:@"Shah"];
    XCTAssertTrue(self.addUsersVC.filteredList.count == 1, @"Search failed");
    
    iWinContact *contact = (iWinContact *)[self.addUsersVC.filteredList objectAtIndex:0];
    XCTAssertTrue([contact.firstName isEqualToString:@"Dharmin"], @"Search failed");
    XCTAssertTrue([contact.lastName isEqualToString:@"Shah"], @"Search failed");
    XCTAssertTrue([contact.email isEqualToString:@"shahdk@rose-hulman.edu"], @"Search failed");
}

- (void)testValidEmailSearch
{
    [self.addUsersVC searchForUserWithString:@"shahdk"];
    XCTAssertTrue(self.addUsersVC.filteredList.count == 1, @"Search failed");
    
    iWinContact *contact = (iWinContact *)[self.addUsersVC.filteredList objectAtIndex:0];
    XCTAssertTrue([contact.firstName isEqualToString:@"Dharmin"], @"Search failed");
    XCTAssertTrue([contact.lastName isEqualToString:@"Shah"], @"Search failed");
    XCTAssertTrue([contact.email isEqualToString:@"shahdk@rose-hulman.edu"], @"Search failed");
}

@end
