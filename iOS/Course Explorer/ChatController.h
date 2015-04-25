//
//  ChatController.h
//  Course Explorer
//
//  Created by Ryan Wing on 4/22/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "WebSocketService.h"

@interface ChatController : UIViewController<UITableViewDataSource, UITextFieldDelegate>

@property (nonatomic, retain) UIAlertView* alert;
@property (nonatomic, retain) WebSocketService* service;
@property (nonatomic, retain) NSString* name;

@property (weak, nonatomic) IBOutlet UITableView* tableView;
@property (weak, nonatomic) IBOutlet UITextField* messageEdit;

@end
