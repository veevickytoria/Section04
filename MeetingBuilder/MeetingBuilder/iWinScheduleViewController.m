//
//  iWinScheduleViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 3/21/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import "iWinScheduleViewController.h"
#import "iWinBackEndUtility.h"
#import <QuartzCore/QuartzCore.h>
#import "NSDate+TKCategory.h"
#import "NSDate+CalendarGrid.h"

@interface iWinScheduleViewController ()
@property (strong, nonatomic) iWinBackEndUtility *backendUtility;
@property (nonatomic) NSInteger userID;
@end

@implementation iWinScheduleViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger)userID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.userID = userID;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.backendUtility = [[iWinBackEndUtility alloc] init];
    [self refreshSchedule];
}

-(void)refreshSchedule
{
    CGRect scheduleFrame = CGRectMake(self.view.bounds.origin.x, self.view.bounds.origin.y+61, self.view.bounds.size.width, self.view.bounds.size.height - 91);
    self.dayView = [[TKCalendarDayView alloc] initWithFrame:scheduleFrame];
	self.dayView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    self.dayView.backgroundColor = [UIColor blackColor];
	self.dayView.delegate = self;
	self.dayView.dataSource = self;
}

- (void) viewDidUnload {
	self.dayView = nil;
}

#pragma mark TKCalendarDayViewDelegate
- (NSArray *) calendarDayTimelineView:(TKCalendarDayView*)calendarDayTimeline eventsForDate:(NSDate *)eventDate{
    
	NSDateComponents *info = [[NSDate date] dateComponentsWithTimeZone:calendarDayTimeline.timeZone];
	info.second = 0;
	NSMutableArray *ret = [NSMutableArray array];
    
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/User/Schedule/%d", self.userID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    if (!deserializedDictionary)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Schedule not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        NSArray *jsonArray = [deserializedDictionary objectForKey:@"schedule"];
        if (jsonArray.count > 0)
        {
            for (NSDictionary* meetings in jsonArray)
            {
                if ([[meetings objectForKey:@"type"] isEqualToString:@"meeting"]){
                    TKCalendarDayEventView *event = [calendarDayTimeline dequeueReusableEventView];
                    if(event == nil) event = [TKCalendarDayEventView eventView];
                    
                    event.identifier = nil;
                    event.titleLabel.text = [meetings objectForKey:@"title"];
                    event.locationLabel.text = [meetings objectForKey:@"description"];
                    
                    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
                    //Set the AM and PM symbols
                    [dateFormatter setAMSymbol:@"AM"];
                    [dateFormatter setPMSymbol:@"PM"];
                    //Specify only 2 M for month, 2 d for day and 2 h for hour
                    [dateFormatter setDateFormat:@"MM/dd/yyyy hh:mm a"];
                    
                    NSDate *date = [dateFormatter dateFromString:[iWinScheduleViewMeetingViewController getStringDateTimeFromDate:[NSDate dateWithTimeIntervalSince1970:[[meetings objectForKey:@"datetimeStart"] doubleValue]]]];
                    event.startDate = date;
                    
                    date = [dateFormatter dateFromString:[iWinScheduleViewMeetingViewController getStringDateTimeFromDate:[NSDate dateWithTimeIntervalSince1970:[[meetings objectForKey:@"datetimeEnd"] doubleValue]]]];
                    event.endDate = date;
                    
                    [ret addObject:event];
                }
                
            }
        }
    }
    
    return ret;
}
- (void) calendarDayTimelineView:(TKCalendarDayView*)calendarDayTimeline eventViewWasSelected:(TKCalendarDayEventView *)eventView{
    
}

- (void) calendarDayTimelineView:(TKCalendarDayView*)calendarDayTimeline didMoveToDate:(NSDate*)eventDate{
	
}

-(void) loadScheduleView {
    [self refreshSchedule];
	[self.view addSubview:self.dayView];
}

@end
