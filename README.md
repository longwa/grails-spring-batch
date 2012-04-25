# GRAILS SPRING BATCH PLUGIN

This plugin adds the Spring Batch framework to a Grails project. It's intent is to minimize/eliminate the need for verbose XML files to configure Spring Batch jobs. 

## Getting Started

The Grails Spring Batch plugin is built using Grails 2.x, it currently has not been tested against Grails 1.3.x and will not install in those versions.

To install the plugin,
<pre><code>grails install plugin spring-batch</code></pre>

or add the following entry to your BuildConfig.groovy file in the plugins sections:
<pre><code>compile ':spring-batch:0.2'</code></pre>

## Supported Features

The plugin creates the following Spring Beans:
* jobRepository (JobRepositoryFactoryBean)
* jobLauncher (SimpleJobLauncher)
* jobExplorer (JobExplorerFactoryBean)

These beans use the defined dataSource bean for your application and expected the Spring Batch tables to be available in this dataSource under and prefixed with "batch_".

The plugin provides the following scripts:
* CreateBatchTables - takes 1 argument that matches a supported DB for Spring Batch: db2, derby, h2, hsqldb, mysql, oracle10g, postgresql, sqlserver, sybase


## Configuration

User the Groovy BeanBuilder DSL to define your job configurations in the grails-app/batch directory. End each configuration name with "BatchConfig" (i.e. JobBatchConfig.groovy). These groovy files will be copied into your classpath and imported. To use the Spring Batch namespace in your config file, declare the following:
<pre><code>xmlns batch:"http://www.springframework.org/schema/batch"</code></pre>
inside the beans {} closure.

## Versions

+ v.0.2 - Started over to make everything simplier. Define your configurations in the grails-app/batch folder in scripts named *BatchConfig.groovy. Use the groovy BeanBuilder syntax to define. The plugin provides jobLauncher, jobRepository, and jobExplorer for you.
+ v.0.1.1 - Minor update to include extra verification of tasklet, validator, incrementor, and jobListener artifacts.
+ v.0.1 - Support for SimpleJob, TaskletStep, Tasklet, JobParametersValidator, JobParametersIncrementor, JobExecutionListener.

## Feature Backlog

+ Auto-detect simple classes like listeners and policies and expose them automatically as beans.
