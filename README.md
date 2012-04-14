# GRAILS SPRING BATCH PLUGIN

This plugin adds the Spring Batch framework to a Grails project. It's intent is to minimize/eliminate the need for verbose XML files to configure Spring Batch jobs. 

## Getting Started

The Grails Spring Batch plugin is built using Grails 2.x, it currently has not been tested against Grails 1.3.x and will not install in those versions.

To install the plugin,
<pre><code>grails install plugin spring-batch</code></pre>

or add the following entry to your BuildConfig.groovy file in the plugins sections:
<pre><code>compile ':spring-batch:0.1'</code></pre>

## Supported Features

The plugin creates the following Spring Beans:
* jobRepository (JobRepositoryFactoryBean)
* jobLauncher (SimpleJobLauncher)
* jobExplorer (JobExplorerFactoryBean)

These beans use the defined dataSource bean for your application and expected the Spring Batch tables to be available in this dataSource under and prefixed with "batch_".

## Configuration

### Jobs

To create a Spring Batch job, create a new class under "grails-app/batch" and end the class name with "BatchJob". All jobs are created as a SimpleJob.
The following fields are available for configuration on a Job class:
* steps - list of steps for the job. 
* validator - specify the JobParametersValidator for this job. Class or String.
* incrementor - specify the JobParametersIncrementor for this job. Class or String.
* listeners - list of listeners for this job. Classes or Strings.

The Framework will try to configure this settings based on convention if no entry is defined. For example:
<pre><code>
class FooBatchJob { }
</code></pre>

By default, the plugin will try to configre the following:
* Steps - a single step with class name FooBatchStep. Must be in same package as FooBatchJob.
* Validator - FooBatchValidator. Must be in same package as FooBatchJob.
* Incrementor - FooBatchIncrementor. Must be in same package as FooBatchJob.
* Listeners - a single listener with class name FooBatchJobListener. Must be in same package as FooBatchJob.

The only required item is a single step for the job. If the validator, incrementor, or listener cannot be found, they are not configured for the job.

### Steps

To create a Spring Batch step, create a new class under "grails-app/batch" and end the class name with "BatchStep". All steps are created as a TaskletStep.
Steps can be added to a job by setting a list to the "steps" property in the job. The list can contain classes or strings. For example:
<pre></code>
class FooBatchJob {
   def steps = [ProcessBatchStep, "report"]
}
</code></pre>

The step list contains 2 declarations:
* Class - Load the specified Class and use it.
* String - try to resolve String using the Job's prefix (i.e. "Foo"). Must be located in the same package as the Job. In the example, the plugin will try to resolve "FooReportBatchStep".

The following items are available to configure on a step:
* tasklet - the tasklet to handle the steps actions. Class or String.

By default, if a tasklet field is not specifed, the plugin will attempt to find a class in the same package with the same prefix as the step (i.e. FooProcessBatchTasklet).

<pre><code>
class FooProcessBatchStep {
   def tasklet = "list"
}
</code></pre>

Will try to load a class with the name ListBatchTasklet. (Note, this ignores the step's prefix.)

### Tasklets

To create a Spring Batch tasklet, create a new class under "grails-app/batch" and end the class name with "BatchTasklet". Additionally, the class must implement the Tasklet interface from Spring Batch (or extend a class that implements the interface).

The following items are avabile to configure on a tasklet:
* None at this time - support for chuncking coming soon!

### Job Parameters Validator

Define a class in "grails-app/batch" that ends with "BatchValidator" and implements the JobParameterValidator interface. Add the validator to the Job using the 'validator' property on the job, or name it by convention using the same prefix as the job (i.e. 'FooBatchJob' and 'FooBatchValidator').

### Job Parameters Incrementor

Define a class in "grails-app/batch" that ends with "BatchIncrementor" and implements the JobParameterIncrementor interface. Add the incrementor to the Job using the 'incrementor' property on the job, or name it by convention using the same prefix as the job (i.e. 'FooBatchJob' and 'FooBatchIncrementor').

### Job Listeners

Define a class in "grails-app/batch" that end with "BatchJobListener" and implements the JobExecutionListener interface. Add the listener to the Job using the 'listeners' property (remember it's a List!), or name it by convention using the same prefix as the job (i.e. 'FooBatchJob' and 'FooBatchJobListener', note that if multiple listeners are required, then you must declare them all using the 'listeners' property).

## Versions

+ v.0.1 - Support for SimpleJob, TaskletStep, Tasklet, JobParametersValidator, JobParametersIncrementor, JobExecutionListener.
+ v.0.1.1 - Minor update to include extra verification of tasklet, validator, incrementor, and jobListener artifacts.

## Feature Backlog

+ Step Execution Listeners
+ Chunk Oriented Processing (including reader and writer)
+ Chunk, itemReader, itemProcess, itemWriter listeners
