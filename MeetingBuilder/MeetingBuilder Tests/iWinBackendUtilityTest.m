//
//  iWinBackendUtilityTest.m
//  MeetingBuilder
//
//  Created by CSSE Department on 4/2/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "iWinConstants.h"
#import "iWinBackEndUtility.h"

@interface iWinBackendUtilityTest : XCTestCase
@property (strong, nonatomic) iWinBackEndUtility *backendUtility;
@property (nonatomic) NSInteger userID;
@end

@implementation iWinBackendUtilityTest

- (void)setUp
{
    [super setUp];
    // Put setup code here. This method is called before the invocation of each test method in the class.
    self.backendUtility = [[iWinBackEndUtility alloc] init];
}

- (void)tearDown
{
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [super tearDown];
}

- (void)testPostRequest
{
    NSString *email = @"mailBackendTest@mail.com";
    NSString *password = @"passwordTest";
    NSString *name = @"Mr. Test";
    NSString *phone = @"123-456-7890";
    NSString *company = @"Test. Co.";
    NSString *title = @"Tester";
    NSString *location = @"Test Land";
    
    //post
    NSString *url = [NSString stringWithFormat:@"%@/User/", DATABASE_URL];
    NSArray *keys = [NSArray arrayWithObjects:@"name", @"password", @"email", @"phone", @"company", @"title", @"location", nil];
    NSArray *objects = [NSArray arrayWithObjects:name, password, email, phone, company, title, location,nil];
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    NSDictionary *deserializedDictionary = [self.backendUtility postRequestForUrl:url withDictionary:jsonDictionary];
    self.userID = -1;
    if (deserializedDictionary) {
        self.userID = [[deserializedDictionary objectForKey:@"userID"] integerValue];
    } else{
        XCTFail(@"Put request failed");
    }
    XCTAssertTrue(self.userID > -1, @"No userID returned");
    
    //delete so that tests can be re-ran without duplicate email error from backend.
    url = [NSString stringWithFormat:@"%@/User/%d", DATABASE_URL,self.userID];
    NSError *error = [self.backendUtility deleteRequestForUrl:url];
}


- (void)testPutRequest
{
    NSString *email = @"mailBackendTesting@mail.com";
    NSString *password = @"passwordTest";
    NSString *name = @"Mr. Test";
    NSString *phone = @"123-456-7890";
    NSString *company = @"Test. Co.";
    NSString *title = @"Tester";
    NSString *location = @"Test Land";
    
    //post
    NSString *url = [NSString stringWithFormat:@"%@/User/", DATABASE_URL];
    NSArray *keys = [NSArray arrayWithObjects:@"name", @"password", @"email", @"phone", @"company", @"title", @"location", nil];
    NSArray *objects = [NSArray arrayWithObjects:name, password, email, phone, company, title, location,nil];
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    NSDictionary *deserializedDictionary = [self.backendUtility postRequestForUrl:url withDictionary:jsonDictionary];
    self.userID = -1;
    if (deserializedDictionary) {
        self.userID = [[deserializedDictionary objectForKey:@"userID"] integerValue];
    } else{
        XCTFail(@"Put request failed");
    }
    XCTAssertTrue(self.userID > -1, @"No userID returned");
    
    email = @"changeing@change.com";
    password = @"changechange";
    name = @"change. change";
    phone = @"111-111-1111";
    company = @"change. change.";
    title = @"change";
    location = @"change";
    
    //put
    url = [NSString stringWithFormat:@"%@/User/", DATABASE_URL];
    NSArray *fields = [NSArray arrayWithObjects:@"name", @"email", @"phone", @"company", @"title", @"location",nil];
    NSArray *values = [NSArray arrayWithObjects:name, email, phone, company, title, location,nil];
    keys = [NSArray arrayWithObjects:@"userID", @"field", @"value", nil];
    
    for (int i = 0; i < fields.count; i++) {
        
        NSArray *objects = [NSArray arrayWithObjects:[NSNumber numberWithInt:self.userID], fields[i], values[i], nil];
        
        NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
        [self.backendUtility putRequestForUrl:url withDictionary:jsonDictionary];
    }

    
    //get
    url = [NSString stringWithFormat: @"%@/User/%d", DATABASE_URL, self.userID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    NSDictionary *userInfo = [self.backendUtility getRequestForUrl:url];
    
    XCTAssertTrue(userInfo, @"Backend returned a valid object");
    
    XCTAssertTrue([name isEqualToString:(NSString *) [userInfo objectForKey:@"name"]], @"Name incorrect");
    XCTAssertTrue([email isEqualToString:(NSString *) [userInfo objectForKey:@"email"]], @"Name incorrect");
    XCTAssertTrue([phone isEqualToString:(NSString *) [userInfo objectForKey:@"phone"]], @"Name incorrect");
    XCTAssertTrue([company isEqualToString:(NSString *) [userInfo objectForKey:@"company"]], @"Name incorrect");
    XCTAssertTrue([title isEqualToString:(NSString *) [userInfo objectForKey:@"title"]], @"Name incorrect");
    XCTAssertTrue([location isEqualToString:(NSString *) [userInfo objectForKey:@"location"]], @"Name incorrect");
    
    //delete so that tests can be re-ran without duplicate email error from backend.
    url = [NSString stringWithFormat:@"%@/User/%d", DATABASE_URL,self.userID];
    NSError *error = [self.backendUtility deleteRequestForUrl:url];
    
}

- (void)testGetRequest
{
    NSString *email = @"mailBackendTesting@mail.com";
    NSString *password = @"passwordTest";
    NSString *name = @"Mr. Test";
    NSString *phone = @"123-456-7890";
    NSString *company = @"Test. Co.";
    NSString *title = @"Tester";
    NSString *location = @"Test Land";
    
    //post
    NSString *url = [NSString stringWithFormat:@"%@/User/", DATABASE_URL];
    NSArray *keys = [NSArray arrayWithObjects:@"name", @"password", @"email", @"phone", @"company", @"title", @"location", nil];
    NSArray *objects = [NSArray arrayWithObjects:name, password, email, phone, company, title, location,nil];
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    NSDictionary *deserializedDictionary = [self.backendUtility postRequestForUrl:url withDictionary:jsonDictionary];
    self.userID = -1;
    if (deserializedDictionary) {
        self.userID = [[deserializedDictionary objectForKey:@"userID"] integerValue];
    } else{
        XCTFail(@"Put request failed");
    }
    XCTAssertTrue(self.userID > -1, @"No userID returned");
    
    //get
    url = [NSString stringWithFormat: @"%@/User/%d", DATABASE_URL, self.userID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    NSDictionary *userInfo = [self.backendUtility getRequestForUrl:url];
    
    XCTAssertTrue(userInfo, @"Backend returned a valid object");
    
    XCTAssertTrue([name isEqualToString:(NSString *) [userInfo objectForKey:@"name"]], @"Name incorrect");
    XCTAssertTrue([email isEqualToString:(NSString *) [userInfo objectForKey:@"email"]], @"Name incorrect");
    XCTAssertTrue([phone isEqualToString:(NSString *) [userInfo objectForKey:@"phone"]], @"Name incorrect");
    XCTAssertTrue([company isEqualToString:(NSString *) [userInfo objectForKey:@"company"]], @"Name incorrect");
    XCTAssertTrue([title isEqualToString:(NSString *) [userInfo objectForKey:@"title"]], @"Name incorrect");
    XCTAssertTrue([location isEqualToString:(NSString *) [userInfo objectForKey:@"location"]], @"Name incorrect");
    
    //delete so that tests can be re-ran without duplicate email error from backend.
    url = [NSString stringWithFormat:@"%@/User/%d", DATABASE_URL,self.userID];
    NSError *error = [self.backendUtility deleteRequestForUrl:url];
}

- (void)testDeleteRequest
{
    NSString *email = @"mailBackendTest@mail.com";
    NSString *password = @"passwordTest";
    NSString *name = @"Mr. Test";
    NSString *phone = @"123-456-7890";
    NSString *company = @"Test. Co.";
    NSString *title = @"Tester";
    NSString *location = @"Test Land";
    
    //post
    NSString *url = [NSString stringWithFormat:@"%@/User/", DATABASE_URL];
    NSArray *keys = [NSArray arrayWithObjects:@"name", @"password", @"email", @"phone", @"company", @"title", @"location", nil];
    NSArray *objects = [NSArray arrayWithObjects:name, password, email, phone, company, title, location,nil];
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    NSDictionary *deserializedDictionary = [self.backendUtility postRequestForUrl:url withDictionary:jsonDictionary];
    self.userID = -1;
    if (deserializedDictionary) {
        self.userID = [[deserializedDictionary objectForKey:@"userID"] integerValue];
    } else{
        XCTFail(@"Put request failed");
    }
    XCTAssertTrue(self.userID > -1, @"No userID returned");
    
    //delete
    
    url = [NSString stringWithFormat:@"%@/User/%d", DATABASE_URL,self.userID];
    NSError *error = [self.backendUtility deleteRequestForUrl:url];
    
    //get
    url = [NSString stringWithFormat: @"%@/User/%d", DATABASE_URL, self.userID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    NSDictionary *userInfo = [self.backendUtility getRequestForUrl:url];
    
   
    
    XCTAssertFalse([name isEqualToString:(NSString *) [userInfo objectForKey:@"name"]], @"Name incorrect");
    XCTAssertFalse([email isEqualToString:(NSString *) [userInfo objectForKey:@"email"]], @"Name incorrect");
    XCTAssertFalse([phone isEqualToString:(NSString *) [userInfo objectForKey:@"phone"]], @"Name incorrect");
    XCTAssertFalse([company isEqualToString:(NSString *) [userInfo objectForKey:@"company"]], @"Name incorrect");
    XCTAssertFalse([title isEqualToString:(NSString *) [userInfo objectForKey:@"title"]], @"Name incorrect");
    XCTAssertFalse([location isEqualToString:(NSString *) [userInfo objectForKey:@"location"]], @"Name incorrect");
    
    
}

@end
