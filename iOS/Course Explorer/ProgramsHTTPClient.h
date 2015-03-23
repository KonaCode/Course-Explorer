//
//  ProgramsHTTPClient.h
//  Course Explorer
//
//  Created by Ryan Wing on 3/19/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "AFHTTPSessionManager.h"

@protocol ProgramsHTTPClientDelegate;

@interface ProgramsHTTPClient : AFHTTPSessionManager

@property (nonatomic, weak) id<ProgramsHTTPClientDelegate>delegate;

+ (ProgramsHTTPClient*)sharedProgramsHTTPClient;

- (instancetype)initWithBaseURL:(NSURL*)url;
- (void)retrieveAll;

@end

@protocol ProgramsHTTPClientDelegate <NSObject>
@optional
- (void)programsHTTPClient:(ProgramsHTTPClient*)client didRetrievePrograms:(id)responseObject;
- (void)programsHTTPClient:(ProgramsHTTPClient*)client didFailWithError:(NSError*)error;

@end
