//
//  ChatJoinController.h
//  Course Explorer
//
//  Created by Ryan Wing on 4/22/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ChatJoinController : UIViewController

@property (nonatomic, retain) UIAlertView* alert;
@property (weak, nonatomic) IBOutlet UITextField* nameEdit;

- (IBAction)exitHere:(UIStoryboardSegue*)sender;

@end
