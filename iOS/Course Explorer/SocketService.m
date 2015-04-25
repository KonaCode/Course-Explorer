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
   CFStreamCreatePairWithSocketToHost(NULL, (CFStringRef)@"localhost", 8080, &readStream, &writeStream);

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

- (NSString*) receive
{
   uint8_t buffer[1024];
   long length = 0;
   NSString* result = nil;
   
   while([inputStream hasBytesAvailable])
   {
      length = [inputStream read:buffer maxLength: sizeof(buffer)];
      
      if(length > 0)
      {
         NSString* output = [[NSString alloc] initWithBytes:buffer length:length encoding:NSASCIIStringEncoding];
         
         if(output != nil)
         {
            [self.messages addObject:output];
            
            NSLog(@"Socket Service Received: %@", output);
         }
      }
   }
   
   if([self.delegate respondsToSelector:@selector(socketService:didReceive:)])
   {
      [self.delegate socketService:self didReceive:result];
   }

   return result;
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

@end
