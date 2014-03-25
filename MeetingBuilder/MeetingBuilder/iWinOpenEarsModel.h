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
#import "iWinMenuViewController.h"

//@protocol OpenEarsDelegate <NSObject>
//
//-(void)speechToText:(NSString *)hypothesis;
//-(void)speakNow;
//-(void)loading;
//-(void)detecting;
//
//@end

@interface iWinOpenEarsModel : NSObject <OpenEarsEventsObserverDelegate>
-(void) startListening;
//@property (nonatomic) id<OpenEarsDelegate> openEarsDelegate;
@property (nonatomic) id<MenuDelegate> menuDelegate;
@property (nonatomic) UIButton *voiceCommand;
- (void)initialize;
-(void)addNavWord:(NSString*) word;
@end
