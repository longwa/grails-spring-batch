GRAILS SPRING BATCH PLUGIN
===

This plugin adds the Spring Batch framework to a Grails project. It's intent is to minimize/eliminate the need for verbose XML files to configure Spring Batch jobs. 

Getting Started
---

The Grails Spring Batch plugin is built using Grails 2.x, it currently has not been tested against Grails 1.3.x and will not install in those versions.

To install the plugin,
<pre><code>grails install plugin spring-batch</code></pre>

or add the following entry to your BuildConfig.groovy file in the plugins sections:
<pre><code>compile ':spring-batch:0.1'</code></pre>

Supported Features
---

The plugin creates the following Spring Beans:
* jobRepository (JobRepositoryFactoryBean)
* jobLauncher (SimpleJobLauncher)
* jobExplorer (JobExplorerFactoryBean)

These beans use the defined dataSource bean for your application and expected the Spring Batch tables to be available in this dataSource under and prefixed with "batch_".

Creating Jobs
---

To create a Spring Batch job, create a new class under "grails-app/batch" and end the class name with "Job". This class MUST implemented the Spring Batch Job interface (or extends from a class that implements the Job interface).

Declare the steps for the job by defining in your job class.
<pre><code>def static steps = []</code></pre>
The steps static property should be an array list containg Step classes or a String (e.g. "process"). The list can contain both Class and String entries. The order of entries in the list is the step order for the Job. Declaring a step using a Class will link that specific class to the job. If declaring using a String, the plugin looks for a class in the same package as the Job and has a name that starts with the Job name and ends with "Step". For example:
<pre><code>
class FooJob extends SimpleJob {
   def static steps = ['process']
}
</code></pre>
Will attempt to find a class in the same package as FooJob that is named "FooProcessStep".
