//
//  ProgramsController.m
//  Course Explorer
//
//  Created by Ryan Wing on 3/8/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import <CoreData/CoreData.h>
#import <RKInMemoryManagedObjectCache.h>
#import <RKManagedObjectStore.h>
#import <RKMIMETypes.h>
#import <RKObjectManager.h>
#import <RKResponseDescriptor.h>

#import "Course.h"
#import "Program.h"
#import "ProgramsController.h"

@interface ProgramsController ()

@end

@implementation ProgramsController

BOOL requestBusy;

@synthesize alert = _alert;
@synthesize fetchedResultsController = _fetchedResultsController;
@synthesize objects = _objects;

- (void)viewDidLoad
{
   [super viewDidLoad];
   [self initializeRestKit];
   [self.tableView registerClass:[UITableViewCell class] forCellReuseIdentifier:@"ProgramCell"];
   
   requestBusy = false;
   
   // Uncomment the following line to preserve selection between presentations.
   // self.clearsSelectionOnViewWillAppear = NO;
    
   // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
   // self.navigationItem.rightBarButtonItem = self.editButtonItem;
}

- (void)didReceiveMemoryWarning
{
   [super didReceiveMemoryWarning];
   // Dispose of any resources that can be recreated.
}

- (BOOL)refresh
{
   BOOL result = true;
   
   if(result)
   {
      self.alert.title = @"Retrieve Programs Status";
      self.alert.message = @"Contacting Server...";
   }

   [self.alert show];

   if(result)
   {
   }
   
   if(result)
   {
      self.alert.message = @"Success";
   }
   else
   {
      self.alert.message = @"Retrieve Failed";
      
      NSLog(@"%@", self.alert.message);
   }
   
   return result;
}

- (UIAlertView*)alert
{
   if(!_alert)
   {
      self.alert = [[UIAlertView alloc] initWithTitle:@"Alert" message:@"Working" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil, nil];
   }
   
   return _alert;
}

- (NSFetchedResultsController*)fetchedResultsController
{
   if(!_fetchedResultsController)
   {
      NSError* error = nil;
      NSFetchRequest* fetchRequest = [NSFetchRequest fetchRequestWithEntityName:NSStringFromClass([Program class])];
      
      fetchRequest.sortDescriptors = @[[NSSortDescriptor sortDescriptorWithKey:@"program.title" ascending:YES]];
      
      self.fetchedResultsController = [[NSFetchedResultsController alloc] initWithFetchRequest:fetchRequest managedObjectContext:[RKManagedObjectStore defaultStore].mainQueueManagedObjectContext sectionNameKeyPath:@"program.title" cacheName:@"Program"];
      self.fetchedResultsController.delegate = self;
      
      [self.fetchedResultsController performFetch:&error];

      NSAssert(!error, @"Error performing fetch request: %@", error);
   }
   
   
   return _fetchedResultsController;
}

- (NSArray*)objects
{
   if(!_objects)
   {
      _objects = [[NSArray alloc] init];
   }

   if(([_objects count] == 0) && !requestBusy)
   {
      NSDictionary* queryParams = nil;

      requestBusy = true;

      [[RKObjectManager sharedManager] getObjectsAtPath:@"/Regis2/webresources/regis2.program" parameters:queryParams
      success:^(RKObjectRequestOperation* operation, RKMappingResult* mappingResult)
      {
         _objects = mappingResult.array;

         [self.tableView reloadData];
         [self.alert dismissWithClickedButtonIndex:0 animated:YES];

         NSLog(@"Objects: %@", self.objects);
         
         requestBusy = false;
      }
      failure:^(RKObjectRequestOperation* operation, NSError* error)
      {
         self.alert.title = @"Error";
         self.alert.message = [NSString stringWithFormat:@"%@", error];
         [self.alert show];

         NSLog(@"Error: %@", error);

         requestBusy = false;
      }];
   }

   return _objects;
}

- (void)initializeRestKit
{
   BOOL useManagedObjectContext = false;
   BOOL useHTTPClient = false;
   BOOL useCustom = true;
   
   if(useManagedObjectContext)
   {
      RKObjectManager* manager = [RKObjectManager managerWithBaseURL:[NSURL URLWithString:@"http://regisscis.net/Regis2/webresources"]];
      NSManagedObjectModel* objectModel = [NSManagedObjectModel mergedModelFromBundles:nil];
      RKManagedObjectStore* objectStore = [[RKManagedObjectStore alloc] initWithManagedObjectModel:objectModel];
      
      manager.managedObjectStore = objectStore;
      
      // Create a map for the program class
      RKEntityMapping* programMapping = [RKEntityMapping mappingForEntityForName:NSStringFromClass([Program class]) inManagedObjectStore:objectStore];
      
      programMapping.identificationAttributes = @[ @"program_id", @"title" ];
      [programMapping addAttributeMappingsFromDictionary:@{ @"id": @"program_id", @"name": @"title" }];
      
      // Create a map for the course class
      RKEntityMapping* courseMapping = [RKEntityMapping mappingForEntityForName:NSStringFromClass([Course class]) inManagedObjectStore:objectStore];
      
      courseMapping.identificationAttributes = @[ @"course_id", @"title", @"program_id", @"program_title" ];
      [courseMapping addAttributeMappingsFromDictionary:@{ @"id": @"course_id", @"name": @"title", @"program_id": @"program_id", @"program_title": @"program_title" }];
      
      // Create the persistence store
      [objectStore createPersistentStoreCoordinator];
      
      NSURL* applicationDocumentsDirectory = [[[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory inDomains:NSUserDomainMask] lastObject];
      NSURL* storeURL = [applicationDocumentsDirectory URLByAppendingPathComponent:@"CourseExplorer-SCIS.sqlite"];
      NSString* storePath = [storeURL path];
      NSError* error = nil;
      
      NSPersistentStore* persistentStore = [objectStore addSQLitePersistentStoreAtPath:storePath fromSeedDatabaseAtPath:nil withConfiguration:nil options:@{ NSMigratePersistentStoresAutomaticallyOption: @YES, NSInferMappingModelAutomaticallyOption: @YES } error:&error];
      
      NSAssert(persistentStore, @"Failed to initialize the persistent store: %@", error);
      
      [objectStore createManagedObjectContexts];
      objectStore.managedObjectCache = [[RKInMemoryManagedObjectCache alloc] initWithManagedObjectContext:objectStore.persistentStoreManagedObjectContext];
   }

   if(useCustom)
   {
      NSURL* baseURL = [NSURL URLWithString:@"http://regisscis.net"];
      AFHTTPClient* client = [[AFHTTPClient alloc] initWithBaseURL:baseURL];
      
      RKObjectManager* objectManager = [[RKObjectManager alloc] initWithHTTPClient:client];
      NSManagedObjectModel* objectModel = [NSManagedObjectModel mergedModelFromBundles:nil];
      RKManagedObjectStore* objectStore = [[RKManagedObjectStore alloc] initWithManagedObjectModel:objectModel];
      RKObjectMapping* programMapping = [RKObjectMapping mappingForClass:[Program class]];
      NSString* keyPath = nil; //[[NSString alloc] initWithFormat:@"response"];
      
      objectManager.managedObjectStore = objectStore;
      [objectManager setAcceptHeaderWithMIMEType:RKMIMETypeJSON];
      [objectManager setRequestSerializationMIMEType:RKMIMETypeJSON];
      
      // Create a map for the program class
//      RKEntityMapping* programMapping = [RKEntityMapping mappingForEntityForName:NSStringFromClass([Program class]) inManagedObjectStore:objectStore];
      
//      programMapping.identificationAttributes = @[ @"program_id", @"title" ];
      [programMapping addAttributeMappingsFromDictionary:@{ @"id": @"program_id", @"name": @"title" }];

      RKResponseDescriptor* responseDescriptor = [RKResponseDescriptor responseDescriptorWithMapping:programMapping method:RKRequestMethodGET pathPattern:@"/Regis2/webresources/regis2.program" keyPath:keyPath statusCodes:[NSIndexSet indexSetWithIndex:200]];
      
      [objectManager addResponseDescriptor:responseDescriptor];
   }

   if(useHTTPClient)
   {
      NSURL* baseURL = [NSURL URLWithString:@"http://regisscis.net"];
      AFHTTPClient* client = [[AFHTTPClient alloc] initWithBaseURL:baseURL];
      
      RKObjectManager* objectManager = [[RKObjectManager alloc] initWithHTTPClient:client];
      RKObjectMapping* programMapping = [RKObjectMapping mappingForClass:[Program class]];

      [objectManager setAcceptHeaderWithMIMEType:RKMIMETypeJSON];
      [objectManager setRequestSerializationMIMEType:RKMIMETypeJSON];
      [programMapping addAttributeMappingsFromArray:@[ @"id", @"name" ]];
      
      RKResponseDescriptor* responseDescriptor = [RKResponseDescriptor responseDescriptorWithMapping:programMapping method:RKRequestMethodGET pathPattern:@"/Regis2/webresources/regis2.program" keyPath:@"response.programs" statusCodes:[NSIndexSet indexSetWithIndex:200]];
      
      [objectManager addResponseDescriptor:responseDescriptor];
   }
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView*)tableView
{
   NSInteger result = 0;
   
   if([self.objects count] > 0)
   {
      result = 1;
   }

   // Return the number of sections.
   return result;
}

- (NSInteger)tableView:(UITableView*)tableView numberOfRowsInSection:(NSInteger)section
{
   NSInteger result = [self.objects count];

   // Return the number of rows in the section.
   return result;
}

- (UITableViewCell*)tableView:(UITableView*)tableView cellForRowAtIndexPath:(NSIndexPath*)indexPath
{
   UITableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:@"ProgramCell" forIndexPath:indexPath];
   
   // Configure the cell...
   Program* program = self.objects[indexPath.row];

   cell.textLabel.text = [program description];
   cell.textLabel.font = [UIFont systemFontOfSize:14];

   return cell;
}

/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/

/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
   if (editingStyle == UITableViewCellEditingStyleDelete)
   {
      // Delete the row from the data source
      [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
   }
   else if (editingStyle == UITableViewCellEditingStyleInsert)
   {
      // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
   }
}
*/

/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
{
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
