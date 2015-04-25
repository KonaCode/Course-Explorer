//
//  SocketService.m
//  Course Explorer
//
//  Created by Ryan Wing on 4/18/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import "SocketService.h"

@implementation SocketService

NSInputStream* inputStream;
NSOutputStream* outputStream;
NSString* url = @"ws://www.regisscis.net:80/WebSocketServer/chat";

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
   CFReadStreamRef readStream;
   CFWriteStreamRef writeStream;
   CFStreamCreatePairWithSocketToHost(NULL, (CFStringRef)@"localhost", 80, &readStream, &writeStream);

   inputStream = (__bridge NSInputStream*)readStream;
   outputStream = (__bridge NSOutputStream*)writeStream;

   [inputStream setDelegate:self];
   [outputStream setDelegate:self];
   
   [inputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
   [outputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];

   [inputStream open];
   [outputStream open];
}

- (void) send:(NSString*)message
{
   NSData* data = [[NSData alloc] initWithData:[message dataUsingEncoding:NSASCIIStringEncoding]];

   [self.messages addObject:message];
   [outputStream write:[data bytes] maxLength:[data length]];
}

- (void) stream:(NSStream*)stream handleEvent:(NSStreamEvent)event
{
   BOOL result = (stream != nil);
   
   if(result && (stream == inputStream) && (event == NSStreamEventHasBytesAvailable))
   {
      uint8_t buffer[1024];
      NSString* output = nil;
      
      long length = [inputStream read:buffer maxLength: sizeof(buffer)];
      
      result = (length > 0);
      
      if(result)
      {
         output = [[NSString alloc] initWithBytes:buffer length:length encoding:NSASCIIStringEncoding];
         result = (output != nil);
      }
      
      if(result)
      {
         NSLog(@"Socket Service Received: %@", output);
         
         [self.messages addObject:output];
      }
         
      if(result && [self.delegate respondsToSelector:@selector(socketService:didReceive:)])
      {
         [self.delegate socketService:self didReceive:output];
      }
      
      if(!result)
      {
         NSLog(@"Socket Service Error: Cannot Handle Input Stream Event");
      }
   }
}

- (void) disconnect
{
   [inputStream close];
   [outputStream close];
   
   [inputStream removeFromRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
   [outputStream removeFromRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
   
   [inputStream setDelegate:nil];
   [outputStream setDelegate:nil];
   
   inputStream = nil;
   outputStream = nil;
}

- (void)socketService:(SocketService*)client didReceive:(NSString*)message
{
}

@end
