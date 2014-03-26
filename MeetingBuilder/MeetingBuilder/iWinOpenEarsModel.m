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
@property (nonatomic) NSMutableArray *words;
@end

@implementation iWinOpenEarsModel

- (void)initialize
{
    
    self.pocketsphinxController = [[PocketsphinxController alloc] init];
    self.openEarsEventsObserver = [[OpenEarsEventsObserver alloc] init];
    self.lmGenerator = [[LanguageModelGenerator alloc] init];
    self.slt = [[Slt alloc] init];
    self.fliteController = [[FliteController alloc] init];
    
    self.words = [[NSMutableArray alloc] initWithObjects:@"GO", @"TO", @"HOME", @"MEETINGS", @"PROFILE", @"TASK", @"NOTES", @"SETTINGS", @"LOG", @"OUT", @"MENU", @"SCHEDULE", @"CREATE", @"EDIT",  nil];

    
    [self initOpenEars];
    [self.openEarsEventsObserver setDelegate:self];
}

-(void) initOpenEars
{
    NSString *name = @"MyLanguageModelFiles";
    
    NSError *err = [self.lmGenerator generateLanguageModelFromArray:[NSArray arrayWithArray:self.words] withFilesNamed:name forAcousticModelAtPath:[AcousticModel pathToModel:@"AcousticModelEnglish"]];
    
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
}

-(void)addNavWord:(NSString*) word
{
    [self.words addObject:word];
    [self initOpenEars];
}

- (void) pocketsphinxDidReceiveHypothesis:(NSString *)hypothesis recognitionScore:(NSString *)recognitionScore utteranceID:(NSString *)utteranceID {
    NSLog(@"The received hypothesis is %@ with a score of %@ and an ID of %@", hypothesis, recognitionScore, utteranceID);
    [self.pocketsphinxController stopListening];
    double delayInSeconds = 0.45;
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, delayInSeconds * NSEC_PER_SEC);
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
        [self detectCommand:hypothesis];
    });
    
}

-(void) detectCommand:(NSString*)hypothesis
{
    if (([hypothesis rangeOfString:@"GO"].location != NSNotFound) && ([hypothesis rangeOfString:@"HOME"].location != NSNotFound))
    {
        [self.menuDelegate goToHomePage];
    }
    else if (([hypothesis rangeOfString:@"GO"].location != NSNotFound) && ([hypothesis rangeOfString:@"MEETINGS"].location != NSNotFound))
    {
        [self.menuDelegate goToMeetings];
    }
    else if (([hypothesis rangeOfString:@"GO"].location != NSNotFound) && ([hypothesis rangeOfString:@"PROFILE"].location != NSNotFound))
    {
        [self.menuDelegate goToProfile];
    }
    else if (([hypothesis rangeOfString:@"GO"].location != NSNotFound) && ([hypothesis rangeOfString:@"TASK"].location != NSNotFound))
    {
        [self.menuDelegate goToTasks];
    }
    else if (([hypothesis rangeOfString:@"GO"].location != NSNotFound) && ([hypothesis rangeOfString:@"NOTES"].location != NSNotFound))
    {
        [self.menuDelegate goToNotes];
    }
    else if (([hypothesis rangeOfString:@"GO"].location != NSNotFound) && ([hypothesis rangeOfString:@"SETTINGS"].location != NSNotFound))
    {
        [self.menuDelegate goToSettings];
    }
    else if (([hypothesis rangeOfString:@"LOG"].location != NSNotFound) && ([hypothesis rangeOfString:@"OUT"].location != NSNotFound))
    {
        [self.menuDelegate goToLogout];
    }
    [self.voiceCommand setTitle:@"Voice Command" forState:UIControlStateNormal];
    self.voiceCommand.userInteractionEnabled = YES;
}

-(void) startListening
{
    [self disableVoiceCommandButtonInteraction];
    [self.voiceCommand setTitle:@"Wait..." forState:UIControlStateNormal];
    [self.pocketsphinxController startListeningWithLanguageModelAtPath:self.lmPath dictionaryAtPath:self.dicPath acousticModelAtPath:[AcousticModel pathToModel:@"AcousticModelEnglish"] languageModelIsJSGF:NO];
}

- (void) pocketsphinxDidStartCalibration {
    double delayInSeconds = 0.15;
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, delayInSeconds * NSEC_PER_SEC);
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
        [self disableVoiceCommandButtonInteraction];
        [self.voiceCommand setTitle:@"Wait..." forState:UIControlStateNormal];
    });
    
}

- (void) pocketsphinxDidCompleteCalibration {
    [self disableVoiceCommandButtonInteraction];
    [self.voiceCommand setTitle:@"Wait..." forState:UIControlStateNormal];
}

- (void) pocketsphinxDidStartListening {
    [self disableVoiceCommandButtonInteraction];
    double delayInSeconds = 0.25;
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, delayInSeconds * NSEC_PER_SEC);
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
       // [self.fliteController say:[NSString stringWithFormat:@"Speak Now"] withVoice:self.slt];
        [self.voiceCommand setTitle:@"Speak Now" forState:UIControlStateNormal];
    });
    
    
}

- (void) pocketsphinxDidDetectSpeech {
    [self disableVoiceCommandButtonInteraction];
    [self.voiceCommand setTitle:@"Detecting" forState:UIControlStateNormal];
}

- (void) pocketsphinxDidDetectFinishedSpeech {
    
    [self disableVoiceCommandButtonInteraction];
    double delayInSeconds = 0.35;
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, delayInSeconds * NSEC_PER_SEC);
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
        //[self.fliteController say:[NSString stringWithFormat:@"Analyzing"] withVoice:self.slt];
        [self.voiceCommand setTitle:@"Detecting" forState:UIControlStateNormal];
    });
}

-(void)disableVoiceCommandButtonInteraction{
    self.voiceCommand.userInteractionEnabled = NO;
}

@end
