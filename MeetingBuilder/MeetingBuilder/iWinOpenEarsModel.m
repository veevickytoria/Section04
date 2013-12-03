//
//  iWinOpenEarsModel.m
//  MeetingBuilder
//
//  Created by CSSE Department on 11/27/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinOpenEarsModel.h"

@interface iWinOpenEarsModel ()
@property (nonatomic) NSString *lmPath;
@property (nonatomic) NSString *dicPath;
@property (nonatomic) LanguageModelGenerator *lmGenerator;
@property (strong, nonatomic) PocketsphinxController *pocketsphinxController;
@property (strong, nonatomic) OpenEarsEventsObserver *openEarsEventsObserver;
@property (strong, nonatomic) FliteController *fliteController;
@property (nonatomic, strong) Slt *slt;
@end

@implementation iWinOpenEarsModel

- (void)initialize
{
    
    self.pocketsphinxController = [[PocketsphinxController alloc] init];
    self.openEarsEventsObserver = [[OpenEarsEventsObserver alloc] init];
    self.lmGenerator = [[LanguageModelGenerator alloc] init];
    self.slt = [[Slt alloc] init];
    self.fliteController = [[FliteController alloc] init];
    
    NSArray *words = [NSArray arrayWithObjects:@"GO", @"MEETINGS", @"TASK", @"TO", @"HOME", @"LOG", @"OUT", @"PROFILE", @"SETTINGS", nil];
    NSString *myCorpus = [NSString stringWithFormat:@"%@/%@",[[NSBundle mainBundle] resourcePath], @"Dictionary.txt"];
    NSLog(@"%@", myCorpus);

    //NSError *error;
    //NSString *x = [NSString stringWithContentsOfFile:myCorpus encoding:NSUTF8StringEncoding error:&error];
    
    //NSArray *words = [x componentsSeparatedByString:@"\n"];
    NSString *name = @"MyLanguageModelFiles";
    
    NSError *err = [self.lmGenerator generateLanguageModelFromArray:[NSArray arrayWithArray:words] withFilesNamed:name forAcousticModelAtPath:[AcousticModel pathToModel:@"AcousticModelEnglish"]];
    
    NSDictionary *languageGeneratorResults = nil;
    self.lmPath = nil;
    self.dicPath = nil;
	
    if([err code] == noErr) {
        
        languageGeneratorResults = [err userInfo];
		
        self.lmPath = [languageGeneratorResults objectForKey:@"LMPath"];
        self.dicPath = [languageGeneratorResults objectForKey:@"DictionaryPath"];
		
    } else {
        NSLog(@"Error: %@",[err localizedDescription]);
    }
    
    [self.openEarsEventsObserver setDelegate:self];
}

- (void) pocketsphinxDidReceiveHypothesis:(NSString *)hypothesis recognitionScore:(NSString *)recognitionScore utteranceID:(NSString *)utteranceID {
    NSLog(@"The received hypothesis is %@ with a score of %@ and an ID of %@", hypothesis, recognitionScore, utteranceID);
    [self.pocketsphinxController stopListening];
    double delayInSeconds = 0.45;
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, delayInSeconds * NSEC_PER_SEC);
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
        [self.openEarsDelegate speechToText:hypothesis];
    });
    
}

-(void) startListening
{
    [self.pocketsphinxController startListeningWithLanguageModelAtPath:self.lmPath dictionaryAtPath:self.dicPath acousticModelAtPath:[AcousticModel pathToModel:@"AcousticModelEnglish"] languageModelIsJSGF:NO];
}

- (void) pocketsphinxDidStartCalibration {
    [self.openEarsDelegate loading];
    double delayInSeconds = 0.15;
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, delayInSeconds * NSEC_PER_SEC);
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
        [self.fliteController say:[NSString stringWithFormat:@"Calibrating"] withVoice:self.slt];
    });
    
}

- (void) pocketsphinxDidCompleteCalibration {
    [self.openEarsDelegate loading];
}

- (void) pocketsphinxDidStartListening {
    [self.openEarsDelegate speakNow];
    double delayInSeconds = 0.25;
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, delayInSeconds * NSEC_PER_SEC);
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
        [self.fliteController say:[NSString stringWithFormat:@"Speak Now"] withVoice:self.slt];
    });
    
    
}

- (void) pocketsphinxDidDetectSpeech {
    [self.openEarsDelegate detecting];
}

- (void) pocketsphinxDidDetectFinishedSpeech {
    
    [self.openEarsDelegate detecting];
    double delayInSeconds = 0.35;
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, delayInSeconds * NSEC_PER_SEC);
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
        [self.fliteController say:[NSString stringWithFormat:@"Analyzing"] withVoice:self.slt];
    });
}

@end
