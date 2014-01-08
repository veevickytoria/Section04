//
//  iWinAddUserTest.m
//  MeetingBuilder
//
//  Created by CSSE Department on 12/11/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "iWinAddUsersViewController.h"
#import "Contact.h"
#import "iWinAppDelegate.h"

@interface iWinAddUserTest : XCTestCase
@property (strong, nonatomic) iWinAddUsersViewController *addUsersVC;
@end

@implementation iWinAddUserTest

- (void)setUp
{
    [super setUp];
    // Put setup code here; it will be run once, before the first test case.
    self.addUsersVC = [[iWinAddUsersViewController alloc] initWithNibName:@"iWinAddUsersViewController" bundle:nil withPageName:@"Meeting" inEditMode:NO];
    [self.addUsersVC viewDidLoad];
    
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    NSManagedObjectContext *context = [appDelegate managedObjectContext];
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Contact" inManagedObjectContext:context];
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDesc];
    
    NSError *error;
    NSArray *result = [context executeFetchRequest:request
                                                error:&error];

    self.addUsersVC.userList = [[NSMutableArray alloc] initWithArray:result];
    
    NSLog(@"%@", self.addUsersVC.userList);
}

- (void)tearDown
{
    // Put teardown code here; it will be run once, after the last test case.
    [super tearDown];
}

- (void)testInvalidSearch
{
    [self.addUsersVC filterContentForSearchText:@"Joe@Joe.JoeDotCom" scope:nil];
    XCTAssertTrue(self.addUsersVC.filteredList.count == 0, @"Search failed");
}

- (void)testValidFirstNameSearch
{
    [self.addUsersVC filterContentForSearchText:@"Dhar" scope:nil];
    XCTAssertTrue(self.addUsersVC.filteredList.count == 1, @"Search failed");
    
    Contact *contact = (Contact *)[self.addUsersVC.filteredList objectAtIndex:0];
    XCTAssertTrue([contact.name isEqualToString:@"Dharmin Shah"], @"Search failed");
    XCTAssertTrue([contact.email isEqualToString:@"shahdk@rose-hulman.edu"], @"Search failed");
}

- (void)testValidLastNameSearch
{
    [self.addUsersVC filterContentForSearchText:@"Shah" scope:nil];
    XCTAssertTrue(self.addUsersVC.filteredList.count == 1, @"Search failed");
    
    Contact *contact = (Contact *)[self.addUsersVC.filteredList objectAtIndex:0];
    XCTAssertTrue([contact.name isEqualToString:@"Dharmin Shah"], @"Search failed");
    XCTAssertTrue([contact.email isEqualToString:@"shahdk@rose-hulman.edu"], @"Search failed");
}

- (void)testValidEmailSearch
{
    [self.addUsersVC filterContentForSearchText:@"shahdk" scope:nil];
    XCTAssertTrue(self.addUsersVC.filteredList.count == 1, @"Search failed");
    
    Contact *contact = (Contact *)[self.addUsersVC.filteredList objectAtIndex:0];
    XCTAssertTrue([contact.name isEqualToString:@"Dharmin Shah"], @"Search failed");
    XCTAssertTrue([contact.email isEqualToString:@"shahdk@rose-hulman.edu"], @"Search failed");
}

@end
