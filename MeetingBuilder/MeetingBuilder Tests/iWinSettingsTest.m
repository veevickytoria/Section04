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
    // Put setup code here; it will be run once, before the first test case.
    self.settingsVC = [[iWinViewAndChangeSettingsViewController alloc] initWithNibName:@"iWinViewAndChangeSettingsViewController" bundle:nil withID:1];
    [self.settingsVC viewDidLoad];
    [self.settingsVC view];
    
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    
    NSManagedObjectContext *context = [appDelegate managedObjectContext];
    
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Contact" inManagedObjectContext:context];
    
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDesc];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"userID = 1"];
    [request setPredicate:predicate];
    
    NSError *error;
    NSArray *result = [context executeFetchRequest:request
                                             error:&error];
    self.contact = (Contact*)[result objectAtIndex:0];
    
    
    
    NSEntityDescription *entityDesc1 = [NSEntityDescription entityForName:@"Settings" inManagedObjectContext:context];
    
    NSFetchRequest *request1 = [[NSFetchRequest alloc] init];
    [request1 setEntity:entityDesc1];
    
    NSPredicate *predicate1 = [NSPredicate predicateWithFormat:@"userID = 1"];
    [request1 setPredicate:predicate1];
    
    NSError *error1;
    NSArray *result1 = [context executeFetchRequest:request1
                                              error:&error1];
    self.settings = (Settings*)[result1 objectAtIndex:0];

}

- (void)tearDown
{
    // Put teardown code here; it will be run once, after the last test case.
    [super tearDown];
}

- (void)testEmailChangeCorrectOldPassword
{
    [self.settingsVC.saveAndEditButton sendActionsForControlEvents:UIControlEventTouchUpInside];
    self.settingsVC.emailTextField.text = @"EMAIL";
    self.settingsVC.oldPasswordTextField.text = @"123456";
    [self.settingsVC.saveAndEditButton sendActionsForControlEvents:UIControlEventTouchUpInside];
    
    XCTAssertTrue([self.settings.email isEqualToString:@"EMAIL"], @"Check failed");
    XCTAssertTrue([self.contact.email isEqualToString:@"EMAIL"], @"Check failed");
    XCTAssertTrue([self.settings.password isEqualToString:@"123456"], @"Check failed");


    
    [self.settingsVC.saveAndEditButton sendActionsForControlEvents:UIControlEventTouchUpInside];
    self.settingsVC.emailTextField.text = @"shahdk@rose-hulman.edu";
    self.settingsVC.oldPasswordTextField.text = @"123456";
    [self.settingsVC.saveAndEditButton sendActionsForControlEvents:UIControlEventTouchUpInside];
    
    XCTAssertTrue([self.settings.email isEqualToString:@"shahdk@rose-hulman.edu"], @"Check failed");
    XCTAssertTrue([self.contact.email isEqualToString:@"shahdk@rose-hulman.edu"], @"Check failed");
    XCTAssertTrue([self.settings.password isEqualToString:@"123456"], @"Check failed");


}

- (void)testEmailChangeIncorrectOrNoOldPassword
{
    [self.settingsVC.saveAndEditButton sendActionsForControlEvents:UIControlEventTouchUpInside];
    self.settingsVC.emailTextField.text = @"EMAIL";
    self.settingsVC.oldPasswordTextField.text = @"NotRightPassword";
    [self.settingsVC.saveAndEditButton sendActionsForControlEvents:UIControlEventTouchUpInside];
    
    XCTAssertTrue([self.settings.email isEqualToString:@"shahdk@rose-hulman.edu"], @"Check failed");
    XCTAssertTrue([self.contact.email isEqualToString:@"shahdk@rose-hulman.edu"], @"Check failed");
    XCTAssertTrue([self.settings.password isEqualToString:@"123456"], @"Check failed");

}

- (void)testEmailAndPasswordChangeCorrectPassword
{
    [self.settingsVC.saveAndEditButton sendActionsForControlEvents:UIControlEventTouchUpInside];
    self.settingsVC.emailTextField.text = @"EMAIL";
    self.settingsVC.oldPasswordTextField.text = @"123456";
    self.settingsVC.passwordTextField.text = @"newPass12";
    self.settingsVC.confirmPasswordTextField.text = @"newPass12";
    [self.settingsVC.saveAndEditButton sendActionsForControlEvents:UIControlEventTouchUpInside];
    
    XCTAssertTrue([self.settings.email isEqualToString:@"EMAIL"], @"Check failed");
    XCTAssertTrue([self.contact.email isEqualToString:@"EMAIL"], @"Check failed");
    XCTAssertTrue([self.settings.password isEqualToString:@"newPass12"], @"Check failed");


    
    [self.settingsVC.saveAndEditButton sendActionsForControlEvents:UIControlEventTouchUpInside];
    self.settingsVC.emailTextField.text = @"shahdk@rose-hulman.edu";
    self.settingsVC.oldPasswordTextField.text = @"newPass12";
    [self.settingsVC.saveAndEditButton sendActionsForControlEvents:UIControlEventTouchUpInside];
    
    XCTAssertTrue([self.settings.email isEqualToString:@"shahdk@rose-hulman.edu"], @"Check failed");
    XCTAssertTrue([self.contact.email isEqualToString:@"shahdk@rose-hulman.edu"], @"Check failed");
    XCTAssertTrue([self.settings.password isEqualToString:@"newPass12"], @"Check failed");


    
    [self.settingsVC.saveAndEditButton sendActionsForControlEvents:UIControlEventTouchUpInside];
    self.settingsVC.oldPasswordTextField.text = @"newPass12";
    self.settingsVC.passwordTextField.text = @"123456";
    self.settingsVC.confirmPasswordTextField.text = @"123456";
    [self.settingsVC.saveAndEditButton sendActionsForControlEvents:UIControlEventTouchUpInside];

    XCTAssertTrue([self.settings.email isEqualToString:@"shahdk@rose-hulman.edu"], @"Check failed");
    XCTAssertTrue([self.contact.email isEqualToString:@"shahdk@rose-hulman.edu"], @"Check failed");
    XCTAssertTrue([self.settings.password isEqualToString:@"123456"], @"Check failed");
}

- (void)testEmailAndPasswordChangeIncorrectOrNoOldPassword
{
    [self.settingsVC.saveAndEditButton sendActionsForControlEvents:UIControlEventTouchUpInside];
    self.settingsVC.emailTextField.text = @"EMAIL";
    self.settingsVC.oldPasswordTextField.text = @"NotRightPassword";
    self.settingsVC.passwordTextField.text = @"newPass12";
    self.settingsVC.confirmPasswordTextField.text = @"newPass12";
    [self.settingsVC.saveAndEditButton sendActionsForControlEvents:UIControlEventTouchUpInside];

    XCTAssertTrue([self.settings.email isEqualToString:@"shahdk@rose-hulman.edu"], @"Check failed");
    XCTAssertTrue([self.contact.email isEqualToString:@"shahdk@rose-hulman.edu"], @"Check failed");
    XCTAssertTrue([self.settings.password isEqualToString:@"123456"], @"Check failed");
}

@end
