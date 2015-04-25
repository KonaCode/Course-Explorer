//
//  WebSocketService.m
//  Course Explorer
//
//  Created by Ryan Wing on 4/25/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import "WebSocketService.h"

@implementation WebSocketService

SRWebSocket* webSocket;
NSString* serviceURL = @"ws://www.regisscis.net:80/WebSocketServer/chat";

- (NSMutableArray*) messages
{
   if(!_messages)
   {
      _messages = [[NSMutableArray alloc] init];
   }
   
   return _messages;
}

- (id) initWithDelegate:(id)delegate
{
   if([super init])
   {
      self.delegate = delegate;
   }
   
   return self;
}

- (void) connect
{
   webSocket = [[SRWebSocket alloc] initWithURLRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:serviceURL]]];
   webSocket.delegate = self;
   [webSocket open];
}

- (void) send:(NSString*)message
{
   [webSocket send:message];
   [self.messages addObject:message];
   
   NSLog(@"WebSocket sent: %@", message);
}

- (void) disconnect
{
   [webSocket close];
   webSocket = nil;
}

- (void) webSocketDidOpen:(SRWebSocket*)webSocket
{
   BOOL result = true;
   
   NSLog(@"WebSocket is connected.");

   if(result && [self.delegate respondsToSelector:@selector(webSocketService:isReady:)])
   {
      [self.delegate webSocketService:self isReady:];
   }
}

- (void) webSocket:(SRWebSocket*)webSocket didFailWithError:(NSError*)error
{
   NSLog(@"WebSocket error: %@", error);
}

- (void) webSocket:(SRWebSocket*)webSocket didCloseWithCode:(NSInteger)code reason:(NSString*)reason wasClean:(BOOL)wasClean
{
   NSLog(@"WebSocket has closed.");
}

- (void) webSocket:(SRWebSocket*)webSocket didReceiveMessage:(id)message
{
   NSString* output = (NSString*)message;
   BOOL result = (output != nil);

   NSLog(@"WebSocket received: %@", message);

   if(result)
   {
      [self.messages addObject:output];
   }

   if(result && [self.delegate respondsToSelector:@selector(webSocketService:didReceive:)])
   {
      [self.delegate webSocketService:self didReceive:output];
   }

   if(!result)
   {
      NSLog(@"Web Socket Service Error: Cannot Handle Receive Event");
   }
}

- (void) webSocketService:(WebSocketService*)client isReady:(NSString*)message
{
}

- (void) webSocketService:(WebSocketService*)client didReceive:(NSString*)message
{
}

@end
