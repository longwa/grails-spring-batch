package grails.plugins.springbatch.ui

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin

import org.junit.Test
import org.springframework.batch.admin.service.JobService
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobInstance
import org.springframework.batch.core.JobParameters

@TestMixin(GrailsUnitTestMixin)
class JobInstanceModelUnitTests {

    @Test
    void testFromService() {
        def jobExecutionMock = new JobExecution(1)
        jobExecutionMock.status = BatchStatus.COMPLETED
        def jobExecutionMock2 = new JobExecution(2)
        jobExecutionMock2.status = BatchStatus.FAILED

        def executionList = [jobExecutionMock, jobExecutionMock2]

        def jobServiceMock = mockFor(JobService)
        jobServiceMock.demand.getJobExecutionsForJobInstance(1..1) {String name, Long id ->
            return executionList
        }

        def jobParameters = new JobParameters()
        def jobInstance = new JobInstance(1, jobParameters, "testJob")

        JobInstanceModel jobInstanceModel = JobInstanceModel.fromService(jobServiceMock.createMock() as JobService, jobInstance)

        assert jobInstanceModel
        assert 1 == jobInstanceModel.id
        assert 2 == jobInstanceModel.jobExecutionCount
        assert executionList == jobInstanceModel.executions
        assert jobParameters == jobInstanceModel.jobParameters
        assert BatchStatus.COMPLETED == jobInstanceModel.lastJobExecutionStatus

        jobServiceMock.verify()
    }
}
