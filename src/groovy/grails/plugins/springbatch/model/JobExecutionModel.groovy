package grails.plugins.springbatch.model

import groovy.transform.EqualsAndHashCode

import org.springframework.batch.admin.service.JobService
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobExecution

@EqualsAndHashCode
class JobExecutionModel {

    Long id
    Date startDateTime
    Long duration
    BatchStatus status
    ExitStatus exitStatus
}
