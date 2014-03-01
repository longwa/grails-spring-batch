package grails.plugins.springbatch.model

import groovy.transform.EqualsAndHashCode

import org.springframework.batch.admin.service.JobService
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepExecution

@EqualsAndHashCode
class StepExecutionModel {

    Long id
    Long jobExecutionId
    String name
    Date startDateTime
    Long duration
    BatchStatus status
    Integer reads
    Integer writes
    Integer skips
    ExitStatus exitStatus
}
