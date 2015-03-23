//
//  ProgramsResponseSerializer.m
//  Course Explorer
//
//  Created by Ryan Wing on 3/20/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import "Program.h"
#import "ProgramsResponseSerializer.h"
#import "XMLParser.h"

@implementation ProgramsResponseSerializer

- (id)responseObjectForResponse:(NSURLResponse*)response data:(NSData*)data error:(NSError* __autoreleasing*)error
{
   id result = [super responseObjectForResponse:response data:data error:error];
   id programsResponse = nil;

   if((*error) == nil)
   {
      NSMutableDictionary* dictionary = nil;

      switch([self responseType])
      {
      case JSON:
         dictionary = [[NSMutableDictionary alloc] init];
         programsResponse = [self ParseAsJSON:dictionary];
         break;
      case XML:
         dictionary = [XMLParser dictionaryForXMLData:data];
         programsResponse = [self ParseAsXML:dictionary];
         break;
      }
   }
   else
   {
      NSError* responseError = (*error);

      (*error) = [NSError errorWithDomain:responseError.domain code:responseError.code userInfo:result];
   }
   
   return programsResponse;
}

- (id)ParseAsJSON:(NSDictionary*)dictionary
{
   NSMutableArray* result = [[NSMutableArray alloc] init];
   
   return result;
}

- (id)ParseAsXML:(NSDictionary*)dictionary
{
   NSMutableArray* result = [[NSMutableArray alloc] init];

   // If we have a response object, then cast it to a dictionary and use the
   // XML parser to extract the programs by ID and name
   NSDictionary* programsDictionary = dictionary[@"programs"];
   
   if((programsDictionary != nil) && ([programsDictionary count] > 0))
   {
      for(NSDictionary* program in programsDictionary.allValues[0])
      {
         if(program == nil)
         {
            continue;
         }
         
         NSString* programID = program[@"id"][@"text"];
         NSString* programName = program[@"name"][@"text"];
         
         if(programID && programName)
         {
            NSNumberFormatter* formatter = [[NSNumberFormatter alloc] init];
            formatter.numberStyle = NSNumberFormatterDecimalStyle;
            
            Program* newProgram = [[Program alloc] init];
            
            newProgram.id = [formatter numberFromString:programID];
            newProgram.name = programName;
            
            [result addObject:newProgram];
         }
      }
   }

   return result;
}

@end
