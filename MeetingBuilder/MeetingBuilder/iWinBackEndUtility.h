//
//  iWinBackEndUtility.h
//  MeetingBuilder
//
//  Created by CSSE Department on 1/26/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface iWinBackEndUtility : NSObject

-(NSDictionary *) getRequestForUrl:(NSString*)url;
-(NSDictionary *) postRequestForUrl:(NSString*)url withDictionary:(NSDictionary*)dictionary;
-(NSDictionary *) putRequestForUrl:(NSString*)url withDictionary:(NSDictionary*)dictionary;
-(NSError*) deleteRequestForUrl:(NSString*)url;

@end
