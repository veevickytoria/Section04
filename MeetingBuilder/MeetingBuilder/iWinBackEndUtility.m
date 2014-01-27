//
//  iWinBackEndUtility.m
//  MeetingBuilder
//
//  Created by CSSE Department on 1/26/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import "iWinBackEndUtility.h"

@implementation iWinBackEndUtility

-(NSDictionary *) getRequestForUrl:(NSString*)url
{
    NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url] cachePolicy:NSURLRequestReloadIgnoringLocalAndRemoteCacheData timeoutInterval:30];
    [urlRequest setHTTPMethod:@"GET"];
    NSURLResponse * response = nil;
    NSError * error = nil;
    NSData * data = [NSURLConnection sendSynchronousRequest:urlRequest
                                          returningResponse:&response
                                                      error:&error];
    
    if (error)
    {
        return nil;
    }
    NSError *jsonParsingError = nil;
    NSDictionary *deserializedDictionary = (NSDictionary *)[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers|NSJSONReadingAllowFragments error:&jsonParsingError];
    return deserializedDictionary;
}

-(NSDictionary *) postRequestForUrl:(NSString*)url withDictionary:(NSDictionary*)dictionary
{
    return nil;
}

-(NSDictionary *) putRequestForUrl:(NSString*)url withDictionary:(NSDictionary*)dictionary
{
    return nil;
}

-(NSError*) deleteRequestForUrl:(NSString*)url
{
    NSMutableURLRequest * urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
    [urlRequest setHTTPMethod:@"DELETE"];
    NSURLResponse * response = nil;
    NSError * error = nil;
    [NSURLConnection sendSynchronousRequest:urlRequest
                          returningResponse:&response
                                      error:&error];
    return error;
}

@end
