//
//  ProgramsHTTPClient.m
//  Course Explorer
//
//  Created by Ryan Wing on 3/19/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import "AFHTTPSessionManager.h"
#import "Program.h"
#import "ProgramsHTTPClient.h"
#import "ProgramsResponseSerializer.h"

static NSString* const ProgramsURLString = @"http://regisscis.net/Regis2/webresources/regis2.program";
static NSString* const CoursesURLString = @"http://regisscis.net/Regis2/webresources/regis2.course";
static ResponseType ProgramsResponseType = XML;

@implementation ProgramsHTTPClient

+ (ProgramsHTTPClient*)sharedProgramsHTTPClient
{
   static ProgramsHTTPClient* programsHTTPClientInstance = nil;
   static dispatch_once_t onceToken;

   dispatch_once(&onceToken, ^
   {
      programsHTTPClientInstance = [[self alloc] initWithBaseURL:[NSURL URLWithString:ProgramsURLString]];
   });
   
   return programsHTTPClientInstance;
}

- (instancetype)initWithBaseURL:(NSURL*)url
{
   self = [super initWithBaseURL:url];

   if(self)
   {
      switch(ProgramsResponseType)
      {
      case JSON:
         self.requestSerializer = [AFJSONRequestSerializer serializer];
         self.responseSerializer = [AFJSONResponseSerializer serializer];
         break;
      case XML:
         self.responseSerializer = [ProgramsResponseSerializer serializer];
         
         ProgramsResponseSerializer* responseSerializer = (ProgramsResponseSerializer*)self.responseSerializer;
         responseSerializer.responseType = ProgramsResponseType;
         break;
      }
   }
   
   return self;
}

- (void)retrieveAll
{
   Boolean result = true;
   NSMutableDictionary* parameters = [NSMutableDictionary dictionary];

   if(result)
   {
      // Set the accept HTTP header value to the desired response
      switch(ProgramsResponseType)
      {
      case JSON:
         [self.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"Accept"];
         break;
      case XML:
         [self.requestSerializer setValue:@"application/xml" forHTTPHeaderField:@"Accept"];
         break;
      }

      [self GET:ProgramsURLString parameters:parameters success:^(NSURLSessionDataTask* task, id responseObject)
      {
         if([self.delegate respondsToSelector:@selector(programsHTTPClient:didRetrievePrograms:)])
         {
            [self.delegate programsHTTPClient:self didRetrievePrograms:responseObject];
         }
      }
      failure:^(NSURLSessionDataTask* task, NSError* error)
      {
         if([self.delegate respondsToSelector:@selector(programsHTTPClient:didFailWithError:)])
         {
            [self.delegate programsHTTPClient:self didFailWithError:error];
         }
      }];
   }
   
   if(result)
   {
   }
}

@end
