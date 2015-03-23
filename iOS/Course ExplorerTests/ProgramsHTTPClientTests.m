//
//  ProgramsHTTPClientTests.m
//  Course Explorer
//
//  Created by Ryan Wing on 3/22/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <XCTest/XCTest.h>

#import "ProgramsHTTPClient.h"

@interface ProgramsHTTPClientTests : XCTestCase<ProgramsHTTPClientDelegate>
{
   ProgramsHTTPClient* client;
   NSMutableArray* programs;

   XCTestExpectation* expectation;
}

@end

@implementation ProgramsHTTPClientTests

- (void)setUp
{
   [super setUp];
   // Put setup code here. This method is called before the invocation of each test method in the class.

   if(client == nil)
   {
      client = [ProgramsHTTPClient sharedProgramsHTTPClient];
      client.delegate = self;
   }
   
   if(programs == nil)
   {
      programs = [[NSMutableArray alloc] init];
   }
}

- (void)tearDown
{
   // Put teardown code here. This method is called after the invocation of each test method in the class.
   [super tearDown];
}

- (void)testRetrievePropgrams
{
   // This functional test case sets an expectation for the client to retrieve all
   // programs and then return a set that is not empty.
   if(expectation == nil)
   {
      expectation = [self expectationWithDescription:@"Handler Called"];
   }

   [client retrieveAll];

   [self waitForExpectationsWithTimeout:1.0 handler:nil];
   
   XCTAssertGreaterThan([programs count], 0);
}

- (void)testPerformanceExample
{
   // This is an example of a performance test case.
   [self measureBlock:^
   {
      // Put the code you want to measure the time of here.
   }];
}

- (void)programsHTTPClient:(ProgramsHTTPClient*)client didRetrievePrograms:(id)responseObject
{
   BOOL result = (responseObject != nil);
   
   if(result)
   {
      NSMutableArray* programCollection = (NSMutableArray*)responseObject;
      
      programs = [programCollection mutableCopy];
   }
   else
   {
      [programs removeAllObjects];
   }
   
   NSUInteger numProgramsFound = [programs count];
   NSString* description = [[NSString alloc] initWithFormat:@"%@ SCIS Programs", (numProgramsFound == 0) ? @"No" : [NSString stringWithFormat:@"%@", @(numProgramsFound)]];
   
   [expectation fulfill];
   
   NSLog(@"%@ Found", description);
}

- (void)programsHTTPClient:(ProgramsHTTPClient*)client didFailWithError:(NSError*)error
{
   NSLog(@"Error: %@", error);
}

@end
