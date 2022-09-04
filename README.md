# Bayes Java Dota Challlenge

This is the [task](TASK.md).

## Assumptions

* As noted in the [Task](/TASK.md) The non-relevant data (Item usages, Healing etc) are not processed and persisted
* As noted in the [Task](/TASK.md) The non-hero events (Kills, Item purchases, Damage done and Spell casts) are not processed and persisted
* The files will not be too heavy which can cause Out of Memory error

## Environment Setup
* Java 11 (or higher)
* maven 3
* Postman (for testing)

## Building the application
* Execute the following command to build the project 

<code>
  mvn clean install
</code>

## Running the application
* Navigate in to the target folder
* Execute the following command to run the Application (replace the version with the current application version)

<code>
    java -jar dota-challenge-[version].jar
</code>

* Alternatively if you need to run via IDE, simply run the DotaChallengeApplication class.

## Testing the application
* Boot up the application is mentioned above
* Import the Postman Local environment setup file found [here](/postman/bayes-local-env.postman_environment.json)
* Import the Postman API Collection file found [here](/postman/bayes-dota.postman_collection.json)
* In the *send-data* API call, Add a combat file as the binary body and execute the *POST* request. ([Sample file 1](/data/combatlog_1.log.txt) | [Sample file 2](/data/combatlog_2.log.txt))
* Dispatch *GET* API calls to retrieve the processed information
* The pre-configured H2 in-memory database is used for the persistance
* Once the Combat logs are processed, visit [H2 Console](http://localhost:8080/h2-console/login.do), login with H2 Datasource properties configured in the application.yml and execute the SQL queries to verify the results

## Possible Enhancements
### File upload process
#### Problems 
* Since the Combat files are published via REST API, Multi GB files will be troublesome to process as the file content is sent via the network
* And this can lead to Out of Memory errors if large files are uploaded

#### Solution
* Transfer remote file via FTP to the host machine of this application
* Implement a Spring scheduler (or a Java File Watcher Service which will generate an event if a new file is received) to scan a particular directory of the host machine
* The Scheduler pickes up the FTP'd files and hands them over to a new Thread to process (Let's call is File Reader Thread) with a help of an Executor Service
* The File Reader Thread reads the file line by line using a Buffered reader and writes the content to a Blocking Queue
* Another Thread (let's call it File Processor Thread) reads the Events from the Blocking Queue using a while loop to make it continuously running and processes them and finally persists them in to the DB
* Once the File Reader thread finishes up the file read process, a unique object (let's call it Poison pill) is submitted into the Blocking Queue
* Once the processor thread receives the Poison pill, it will exit from the while loop so that will not be in-definitely waiting for events
* Since the Blocking Queue is introduced, the File Reader thread will get blocked if the queue is full (Will save the Application Memory) and the processor thread will get blocked if the Queue is empty (Will save the CPU from unnecessary processes)
* This design will prevent the Application Memory being Overloaded with File contents and will save the application from Out Of Memory errors



