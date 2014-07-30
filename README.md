# GRAILS SPRING BATCH PLUGIN

This plugin adds the Spring Batch framework to a Grails project. It's intent is to minimize/eliminate the need for verbose XML files to configure Spring Batch jobs. 

## Getting Started

The Grails Spring Batch plugin is built using Grails 2.x, it currently has not been tested against Grails 1.3.x and will not install in those versions.

To install the plugin, add the following entry to your `BuildConfig.groovy` file in the plugins sections:
```groovy
compile ':spring-batch:2.0.8'
```

Once the plugin is installed, you can define your Spring Batch job configuration in a Groovy script file in your application's `grails-app/batch` directory. The script's filename must end with BatchConfig (i.e. `SimpleJobBatchConfig.groovy`). Define your Spring Batch job using the Grails BeanBuilder syntax (just like in the `resources.groovy` file).

To launch a job from your application do the following:

1. Inject the Spring Batch Job Launcher and the job you defined in your configuration file into the controller or service (or lookup it up from the `grailsApplication.mainContext`)
2. Call the `jobLauncher.launch()` method with a reference to your job and a `JobParameters` object. Spring Batch will take care of the rest

`grails-app/batch/SimpleJob.groovy`
```groovy
beans {

    batch.job(id: 'simpleJob') {
        batch.step(id: 'logStart') {
            batch.tasklet(ref: 'printStartMessage')
        }
    }

    printStartMessage(PrintStartMessageTasklet) { bean ->
        bean.autowire = "byName"
    }

}
```

`grails-app/services/foo/FooService`
```groovy
class FooService {
   def jobLauncher
   def simpleJob

   public void launchFoo() {
      jobLauncher.launch(simpleJob, new JobParameters())
   }
}
```


## Alternative Method for starting a job:
Once you've defined your job, you can choose to start it using the SpringBatchService.  

To do so, inject the springBatchService into your artifact with:
```groovy
def springBatchService
```

You can then start your service:
```groovy
springBatchService.launch('myJobName')
```

The method signature for such is:
```groovy
Map launch(String jobName, boolean canBeConcurrent=true, JobParameters jobParams = null, String jobLauncherName = null)
```
There are defaults for the last 3 parameters.  
 * When canBeConcurrent is set to false, The plugin will look to see if this job is already executing and abort launch if running.  When true, it will always launch a new job.
 * If no jobParams are submitted, a default JobParameters is created with a map [date: new Date()]
 * If you want to override the jobLauncher, you can do so with the jobLauncher Bean Name - initially configured choices configured by the plugin are "jobLauncher" and "syncJobLauncher".  The default is the "jobLauncher" bean

The return value is a map containing whether the job was successful, and a message regarding either why it failed or what job was started


## Monitoring
You can check the status of the last execution of a job with springBatchService:
```groovy
springBatchService.status('myJobName')
```
Which will return a map with [success:successRun, running:isRunningNow, executionStartTime:lastExecutionStartTime, executionEndTime: lastExecutionEndTime]


## Supported Features

The plugin creates the following Spring Beans:
* jobRepository (JobRepositoryFactoryBean)
* jobLauncher (SimpleJobLauncher)
* jobExplorer (JobExplorerFactoryBean)
* jobRegistry (MapJobRegistry)
* jobRegistryPostProcessor (ReloadableJobRegistryBeanPostProcessor)
* jobOperator (SimpleJobOperator)

These beans use the defined dataSource bean for your application and expected the Spring Batch tables to be available in this dataSource and prefixed with `BATCH_`.

The plugin provides the following scripts:
* CreateBatchTables - takes 1 argument that matches a supported DB for Spring Batch: db2, derby, h2, hsqldb, mysql, oracle10g, postgresql, sqlserver, sybase

## Plugin configuration

+ dataSource - name of datasource to use for Spring Batch data
  + Type: String
  + Default: dataSource
+ tablePrefix - prefix of Spring Batch tables in database. If set to a non-empty string, assumes there is a `_` separating the prefix and the table name
  + Type: String
  + Default: BATCH
+ loadTables - if true, will attempt to execute the Spring Batch DDL for the specified database type during startup. Should set to true in test phases.
  + Type: Boolean
  + Default: false
+ maxVarCharLength- Messages in the job_execution, step_execution, and job_execution_context are truncated to this value.  If you increase it above its default of 2500, you'll need to adjust your table constraints accordingly.
  + Type: Integer
  + Default: 2500
+ database - The database type to use: db2, derby, h2, hsqldb, mysql, oracle10g, postgresql, sqlserver, sybase
  + Type: String
  + Default: h2
+ jmx
  + enable - enables export of Spring Batch resources through a local MBean
      + Type: Boolean
      + Default: false
  + name - the service name with which to export the resources
      + Type: String
      + Default: jobOperator
  + remote
      + enable - enables export of Spring Batch resources through a remote RMI Registry (default connection string is service:jmx:rmi://localhost/jndi/rmi://localhost:1099/springBatch)
          + Type: Boolean
          + Default: false
      + name - the service name with which to export the resources
          + Type: String
          + Default: springBatch
      + rmi
          + port - the port on which the RMI Registry is attached
              + Type: Integer
              + Default: 1099

## Job Definition

The plugin expects your job configuration to be defined using the Grails BeanBuilder DSL in the grails-app/batch directory.
End each configuration name with `BatchConfig` (i.e. `JobBatchConfig.groovy`).
These groovy files will be copied into your classpath and imported.
The plugin automatically registers the Spring Batch namespace under the handle `batch`.
To use a different namespace in your config file, declare the following:
```groovy
xmlns mybatchns:"http://www.springframework.org/schema/batch"
```

inside the beans {} closure.

## Sample Project Spring-Batch-Test

A sample / test project is included with the original plugin source, available at `test/projects/spring-batch-test`.  To get running, follow the instructions below:  

* Zip up the github repository, unzip it into directory called grails-spring-batch.  Then go into `test/projects/spring-batch-test` directory.  
* The default project uses h2, so run the command 
```
    grails create-batch-tables
```

* After that start the application (grails run-app), you will find it at http://localhost:8080/spring-batch-test/
* Check db tables were created.  Go to dbconsole at http://localhost:8080/spring-batch-test/dbconsole/.  
   Substitute jdbc string to connect to the devDb: `jdbc:h2:mem:devDb;MVCC=TRUE`.
   Connect and make sure a bunch of tables that start with BATCH* exist.   All of the tables will have zero entries until the first run. 
* Run Included Simple Job.  Go to console, http://localhost:8080/spring-batch-test/console.  Type in or paste the following code into console window:  

```groovy
    import org.springframework.batch.core.JobParameters

    simpleJob = ctx.simpleJob
    ctx.jobLauncher.run(simpleJob, new JobParameters());
```

After pressing execute, you can go to the application console (i.e. shell), and you should see text `Starting Job`.  That means the batch job ran fine.  You can view the definition and modify at `ROOT/test\projects\spring-batch-test\grails-app\batch\SimpleJobBatchConfig.groovy`.

* See run record in the db.   Running select queries on batch tables will now show you information written about the run. 


## Sample Project Simple-Schedule
An additional test project is included at `test/projects/simple-schedule`.  The purpose of which is to demonstrate how to add basic Spring Scheduling to your project.  You are free though to use any scheduler with the plugin.  

You can run it by cloning the grails-spring-batch repository to your system, go to the yourCloneDirectory/test/projects/simple-schedule directory and run the command:
```    grails run-app
```
You can view: 
The schedule in src/groovy/scheduling/Schedule.groovy,
The required configuration in grails-app/conf/Config.groovy
The example jobs are in grails-app/batch/*JobBatchConfig.groovy

The jobs will run fairly frequently to demonstrate the difference between async and sync jobs.  Be aware that sync jobs block the scheduler when running.


## Versions
+ v.2.0.8
  + Added ability to configure size of exit messages for job executions, step executions and contexts
  + Added ability to start/restart/stop jobs in more locations
  + Refactored / cleaned up UI in an attempt to expose more information from the api, and make the controllers slightly more rest-like.  
  + Fixed some bugs with paging, and a null pointer from the spring-batch-admin library.  
  + Added more test data to test project.  
  + Added sorting to job names.  
  + Modify UI to use grails i18n support in more locations
  + Create simple-schedule example project showing how to use basic scheduling available from the plugin
  + Handle null job parameters on start/restart requests
  + Added duration taglib to convert differences between long values as natural time duration
  + Display whether a job is currently running.
  + Add to views info regarding step executions
  + Add method to get the status of a job for use with uptime
  + Add Codenarc configuration
  + Allow user to stop all executions for all jobs.
+ v.1.0
  + upgrade plugin to Grails 2.2.3
  + Dependent plugin updates
  + Update to Spring Batch 2.1.9.RELEASE
  + Update to Spring Batch Admin 1.2.2.RELEASE
  + Fix controller redirect errors
  + Fix compiler failure on plugin removal
  + Automatically register the batch namespace into the Spring Bean Builder
+ v.1.0.RC2
  + Dependent plugin updates
+ v.1.0.RC1
  + Implement reloading of BatchConfig files.
  + Add support for configuring the JMX export names using the jmx.name and jmx.remote.name config variables.
  + Fix support for dynamic loading of MySQL tables during tests.
  + Add some generic controllers/views for exposing Spring Batch jobs and information
+ v.0.2.2 - Minor tweaks. Changed default table prefix to be upper case since that's what the Spring Batch library expects. Allow closure style config. Some package refactoring.
+ v.0.2.1 - Added jobOperator bean for use in UI, added JMX export, and loading of tables for in memory database
+ v.0.2 - Started over to make everything simplier. Define your configurations in the grails-app/batch folder in scripts named *BatchConfig.groovy. Use the groovy BeanBuilder syntax to define. The plugin provides jobLauncher, jobRepository, and jobExplorer for you.
+ v.0.1.1 - Minor update to include extra verification of tasklet, validator, incrementor, and jobListener artifacts.
+ v.0.1 - Support for SimpleJob, TaskletStep, Tasklet, JobParametersValidator, JobParametersIncrementor, JobExecutionListener.

## Feature Backlog

+ Upgrade to Spring Batch 2.2.0.RELEASE - will require a minimum Grails version of 2.1.1 (due to Spring requirements)
+ Provide artifact based configuration and annotations in addition to DSL configuration
+ Implement a Spring Batch DSL similar to Spring Integration DSL.
+ Auto-detect simple classes like listeners and policies and expose them automatically as beans.
