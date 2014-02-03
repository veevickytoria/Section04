//
//  LargeDefaultCell.h
//  MeetingBuilder
//
//  Created by CSSE Department on 2/1/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol LargeCellDelegate <NSObject>

-(void)deleteCell:(NSInteger)row;

@end

@interface LargeDefaultCell : UITableViewCell
@property (nonatomic) id<LargeCellDelegate> largeCellDelegate;
@property (weak, nonatomic) IBOutlet UIButton *deleteButton;
- (IBAction)onClickDelete;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
-(void)initLargeCell;
@end
