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
    [self.viewVC viewDidLoad];
    [self.viewVC view];    
}

- (void)tearDown
{
    // Put teardown code here; it will be run once, after the last test case.
    [super tearDown];
}

- (void)testCheckUserInfo
{



}

- (void)testEditedUserInfo
{
    [self.viewVC.editProfile sendActionsForControlEvents:UIControlEventTouchUpInside];
    self.viewVC.displayNameTextField.text = @"Shah Dharmin";
    self.viewVC.companyTextField.text = @"A";
    self.viewVC.emailTextField.text = @"B";
    self.viewVC.phoneTextField.text = @"C";
    self.viewVC.locationTextField.text = @"D";
    self.viewVC.titleTextField.text = @"E";
    
    [self.viewVC.editProfile sendActionsForControlEvents:UIControlEventTouchUpInside];
    XCTAssertTrue([self.viewVC.displayNameTextField.text isEqualToString:@"Shah Dharmin"], @"Check failed");
    XCTAssertTrue([self.viewVC.companyTextField.text isEqualToString:@"A"], @"Check failed");
    XCTAssertTrue([self.viewVC.emailTextField.text isEqualToString:@"B"], @"Check failed");
    XCTAssertTrue([self.viewVC.phoneTextField.text isEqualToString:@"C"], @"Check failed");
    XCTAssertTrue([self.viewVC.locationTextField.text isEqualToString:@"D"], @"Check failed");
    XCTAssertTrue([self.viewVC.titleTextField.text isEqualToString:@"E"], @"Check failed");
    
    [self.viewVC.editProfile sendActionsForControlEvents:UIControlEventTouchUpInside];
    self.viewVC.displayNameTextField.text = @"Dharmin Shah";
    self.viewVC.companyTextField.text = @"iWin LLC";
    self.viewVC.emailTextField.text = @"shahdk@rose-hulman.edu";
    self.viewVC.phoneTextField.text = @"(812)345-9876";
    self.viewVC.locationTextField.text = @"Terre Haute, IN";
    self.viewVC.titleTextField.text = @"Product Owner";
    
    [self.viewVC.editProfile sendActionsForControlEvents:UIControlEventTouchUpInside];

    
}

- (void)testCancelEditUser
{
  
}

@end
