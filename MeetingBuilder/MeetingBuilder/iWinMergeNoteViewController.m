//
//  iWinMergeNoteViewController.m
//  MeetingBuilder
//
//  Created by Richard Shomer on 11/4/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinMergeNoteViewController.h"

@interface iWinMergeNoteViewController ()
@property (nonatomic) BOOL isEditing;
@end

@implementation iWinMergeNoteViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil inEditMode:(BOOL)isEditing
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.isEditing = isEditing;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)saveClicked:(id)sender {
    [self.mergeDelegate saveMergeClicked];
}

- (IBAction)cancelClicked:(id)sender {
    [self.mergeDelegate cancelMergeClicked];
}
@end
