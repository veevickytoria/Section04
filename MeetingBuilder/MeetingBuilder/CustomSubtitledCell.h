//
//  CustomSubtitledCell.h
//  MeetingBuilder
//
//  Created by CSSE Department on 2/1/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol SubtitledCellDelegate <NSObject>

-(void)deleteCell:(NSInteger)row;

@end

@interface CustomSubtitledCell : UITableViewCell
@property (nonatomic) id<SubtitledCellDelegate> subTitledDelegate;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *detailLabel;
@property (weak, nonatomic) IBOutlet UIButton *deleteButton;
- (IBAction)onClickDelete;
-(void)initCell;
@end
