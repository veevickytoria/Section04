//
//  iWinViewAndAddNotesViewController.m
//  MeetingBuilder
//
//  Created by Richard Shomer on 10/29/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinViewAndAddNotesViewController.h"

@interface iWinViewAndAddNotesViewController ()
@property (nonatomic) BOOL isEditing;
@property (nonatomic) iWinMergeNoteViewController *mergeNoteViewController;
@end

@implementation iWinViewAndAddNotesViewController

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

- (IBAction)saveButton:(id)sender {
    [self.addNoteDelegate saveNoteClicked];
}
- (IBAction)cancelButton:(id)sender {
    [self.addNoteDelegate cancelNoteClicked];
}

- (IBAction)mergeNotesButton:(id)sender {
    self.mergeNoteViewController = [[iWinMergeNoteViewController alloc] initWithNibName:@"iWinMergeNoteViewController" bundle:nil inEditMode:NO];
    self.mergeNoteViewController.mergeDelegate = self;
    [self.mergeNoteViewController setModalPresentationStyle:UIModalPresentationFormSheet];
    [self.mergeNoteViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.mergeNoteViewController animated:YES completion:nil];
    self.mergeNoteViewController.view.superview.bounds = CGRectMake(0,0,597,200);
}


-(void)saveMergeClicked{
    [self dismissViewControllerAnimated:YES completion:Nil];
    
}
-(void)cancelMergeClicked
{
    [self dismissViewControllerAnimated:YES completion:Nil];
}

@end
