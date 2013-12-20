//
//  iWinRegisterTest.m
//  MeetingBuilder
//
//  Created by CSSE Department on 12/19/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinRegisterTest.h"
#import "iWinRegisterViewController.h"


@interface iWinRegisterTest()

@property (strong, nonatomic) iWinRegisterViewController *viewVC;
@end


@implementation iWinRegisterTest
- (void)setUp
{
    [super setUp];
    // Put setup code here; it will be run once, before the first test case.
    self.viewVC = [[iWinRegisterViewController alloc] initWithNibName:@"iWinRegisterViewController" bundle:nil];
    [self.viewVC viewDidLoad];
    [self.viewVC view];
    
   // [self.viewVC onClickRegister:nil]
}

- (void)tearDown
{
    // Put teardown code here; it will be run once, after the last test case.
    [super tearDown];
}

- (void)mySetup
{
    self.viewVC.nameField.text = @"John";
    self.viewVC.emailField.text = @"John@yahoo.com";
    self.viewVC.passwordField.text = @"password";
    self.viewVC.confirmPasswordField.text = @"password";
}


- (void)testValidateFailNoName
{
    [self mySetup];
    self.viewVC.nameField.text = @"";
    XCTAssertTrue([[self.viewVC validateRegistration] isEqualToString:@"Please enter your name!"], @"Check failed");
}

- (void)testValidateFailNoEmail
{
    [self mySetup];
    self.viewVC.emailField.text = @"";
    XCTAssertTrue([[self.viewVC validateRegistration] isEqualToString:@"Please enter a valid email address!"], @"Check failed");
}

- (void)testValidateFailWithNoPasswords
{
    [self mySetup];
    self.viewVC.passwordField.text = @"";
    self.viewVC.confirmPasswordField.text = @"";
    XCTAssertTrue([[self.viewVC validateRegistration] isEqualToString:@"Password must be at least 6 characters!"], @"Check failed");
}

- (void)testValidateFailWithJustFirstPassword
{
    [self mySetup];
    self.viewVC.confirmPasswordField.text = @"";
    XCTAssertTrue([[self.viewVC validateRegistration] isEqualToString:@"Please enter matching passwords!"], @"Check failed");
}

- (void)testValidateFailWithJustFirstPasswordOfShortLength
{
    [self mySetup];
    self.viewVC.passwordField.text = @"hik";
    XCTAssertTrue([[self.viewVC validateRegistration] isEqualToString:@"Password must be at least 6 characters!"], @"Check failed");
}

- (void)testValidateFailWithBothMatchingButTooShort
{
    [self mySetup];
    self.viewVC.passwordField.text = @"pass";
    self.viewVC.confirmPasswordField.text = @"pass";
    XCTAssertTrue([[self.viewVC validateRegistration] isEqualToString:@"Password must be at least 6 characters!"], @"Check failed");
}

- (void)testValidateFailWithMismatchingEnteredPasswords
{
    [self mySetup];
    self.viewVC.confirmPasswordField.text = @"tester!";
    XCTAssertTrue([[self.viewVC validateRegistration] isEqualToString:@"Please enter matching passwords!"], @"Check failed");
}

- (void)testValidateFailWithMismatchingEnteredPasswordsWithShortLengthFirst
{
    [self mySetup];
    self.viewVC.passwordField.text = @"efg!";
    XCTAssertTrue([[self.viewVC validateRegistration] isEqualToString:@"Password must be at least 6 characters!"], @"Check failed");
}

- (void)testValidateFailWithNonValidEmail
{
    [self mySetup];
    self.viewVC.emailField.text = @"sdfsdfd";
    XCTAssertTrue([[self.viewVC validateRegistration] isEqualToString:@"Please enter a valid email address!"], @"Check failed");
}

- (void)testValidateSuccessWithValidRequiredInputs
{
    [self mySetup];
    XCTAssertTrue([[self.viewVC validateRegistration] isEqualToString:@""], @"Check failed");
}

@end