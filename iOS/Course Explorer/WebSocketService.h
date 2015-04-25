//
//  WebSocketService.h
//  Course Explorer
//
//  Created by Ryan Wing on 4/25/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import <Foundation/Foundation.h>

#import <SRWebSocket.h>

@interface WebSocketService : NSObject<SRWebSocketDelegate>

@property (nonatomic, retain) id delegate;
@property (nonatomic, retain) NSMutableArray* messages;

- (id) initWithDelegate:(id)delegate;
- (void) connect;
- (void) send:(NSString*)message;
- (void) disconnect;

- (void)webSocketService:(WebSocketService*)client isReady:(NSString*)message;
- (void)webSocketService:(WebSocketService*)client didReceive:(NSString*)message;

@end