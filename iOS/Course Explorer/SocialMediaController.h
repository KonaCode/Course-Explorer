//
//  SocialMediaControllerViewController.h
//  Course Explorer
//
//  Created by Ryan Wing on 4/6/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import <Social/Social.h>
#import <UIKit/UIKit.h>

@interface SocialMediaController : SLComposeViewController

@property (nonatomic, retain) UIAlertView* alert;

- (IBAction) exitHere:(UIStoryboardSegue*)sender;

- (IBAction)FacebookShare:(id)sender;
- (IBAction)TwitterShare:(id)sender;
- (IBAction)loginButton:(id)sender;

@property (nonatomic, assign) Boolean hasFacebookLogin;
@property (nonatomic, assign) Boolean hasTwitterLogin;

@end
