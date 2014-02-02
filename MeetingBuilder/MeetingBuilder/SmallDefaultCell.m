//
//  SmallDefaultCell.m
//  MeetingBuilder
//
//  Created by CSSE Department on 2/1/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import "SmallDefaultCell.h"

@interface SmallDefaultCell ()
@property BOOL movedButton;
@end

const int DEFAULT_DELETE_BUTTON_WIDTH = 60;

@implementation SmallDefaultCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

-(void)initSmallCell
{
    self.movedButton = NO;
        
    UISwipeGestureRecognizer *swipeRight=[[UISwipeGestureRecognizer alloc]initWithTarget:self action:@selector(swipedRight:)];
    swipeRight.direction=UISwipeGestureRecognizerDirectionRight;
        
    UISwipeGestureRecognizer *swipeLeft=[[UISwipeGestureRecognizer alloc]initWithTarget:self action:@selector(swipedLeft:)];
    swipeLeft.direction=UISwipeGestureRecognizerDirectionLeft;
    
    [self.contentView addGestureRecognizer:swipeLeft];
    [self.contentView addGestureRecognizer:swipeRight];

}

- (IBAction)onClickDelete {
    [self animateSlidingButton:YES];
    self.movedButton = !self.movedButton;
    [self.smallCellDelegate deleteCell:self.deleteButton.tag];
}

-(void)swipedLeft:(UISwipeGestureRecognizer*)gestureRecognizer
{
    if (!self.movedButton)
    {
        [self animateSlidingButton:NO];
        self.movedButton = !self.movedButton;
    }
    
}

-(void)swipedRight:(UISwipeGestureRecognizer*)gestureRecognizer
{
    if (self.movedButton)
    {
        [self animateSlidingButton:YES];
        self.movedButton = !self.movedButton;
    }
}

-(void)animateSlidingButton:(BOOL)moveRight
{
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.4];
    
    CGRect oldFrame = self.deleteButton.frame;
    
    if (moveRight)
    {
        self.deleteButton.frame = CGRectMake(oldFrame.origin.x+DEFAULT_DELETE_BUTTON_WIDTH, oldFrame.origin.y, oldFrame.size.width, oldFrame.size.height);
    }
    else
    {
        self.deleteButton.frame = CGRectMake(oldFrame.origin.x-DEFAULT_DELETE_BUTTON_WIDTH, oldFrame.origin.y, oldFrame.size.width, oldFrame.size.height);
    }
    [UIView commitAnimations];
}

@end
