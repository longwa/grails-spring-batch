package grails.plugins.springbatch.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class JobModel {

    String name

    Integer executionCount
    Integer jobInstanceCount

    Collection<String> stepNames  // These appear to come from the job service in a non-useful order

    Boolean launchable
    Boolean incrementable
	
	Boolean currentlyRunning

    // lastJobParameters
}