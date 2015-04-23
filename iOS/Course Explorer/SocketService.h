//
//  SocketService.h
//  Course Explorer
//
//  Created by Ryan Wing on 4/18/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SocketService : NSObject<NSStreamDelegate>

@property (nonatomic, retain) NSMutableArray* messages;

- (void) connect;
- (void) send:(NSString*)message;
- (NSString*) receive;
- (void) disconnect;

@end
