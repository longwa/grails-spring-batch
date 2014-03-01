package grails.plugins.springbatch.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class JobModel {

    String name

    Integer executionCount
    Integer jobInstanceCount

    Collection<String> stepNames

    Boolean launchable
    Boolean incrementable

    // lastJobParameters
}