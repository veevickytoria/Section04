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


@end