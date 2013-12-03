//
//  iWinOpenEarsModel.h
//  MeetingBuilder
//
//  Created by CSSE Department on 11/27/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <OpenEars/LanguageModelGenerator.h>
#import <OpenEars/PocketsphinxController.h>
#import <OpenEars/FliteController.h>
#import <OpenEars/OpenEarsLogging.h>
#import <OpenEars/AcousticModel.h>
#import <Slt/Slt.h>

@protocol OpenEarsDelegate <NSObject>

-(void)speechToText:(NSString *)hypothesis;
-(void)speakNow;
-(void)loading;
-(void)detecting;

@end

@interface iWinOpenEarsModel : NSObject <OpenEarsEventsObserverDelegate>
-(void) startListening;
@property (nonatomic) id<OpenEarsDelegate> openEarsDelegate;
- (void)initialize;
@end
