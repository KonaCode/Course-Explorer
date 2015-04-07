//
//  SocialMediaControllerViewController.h
//  Course Explorer
//
//  Created by Ryan Wing on 4/6/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import <Social/Social.h>
#import <UIKit/UIKit.h>

@interface SocialMediaControllerViewController : SLComposeViewController

@property (nonatomic, retain) UIAlertView* alert;

- (IBAction)shareButton:(id)sender;
- (IBAction)FacebookShare:(id)sender;
- (IBAction)TwitterShare:(id)sender;

@end
