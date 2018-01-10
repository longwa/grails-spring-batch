package grails.plugins.springbatch.ui

import grails.plugins.springbatch.SpringBatchService
import grails.plugins.springbatch.model.JobExecutionModel
import grails.plugins.springbatch.model.JobInstanceModel
import grails.plugins.springbatch.model.JobModel
import grails.plugins.springbatch.model.StepExecutionModel
import grails.testing.services.ServiceUnitTest
import org.springframework.batch.admin.service.JobService
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobInstance
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.StepExecution
import spock.lang.Specification

class SpringBatchUiServiceUnitTests extends Specification implements ServiceUnitTest<SpringBatchUiService> {
    void setup() {
        service.jobService = Mock(JobService)
        service.springBatchService = Mock(SpringBatchService)
    }

    void "get job models"() {
        when:
        def jobUiModel = service.getJobModels([:])

        then:
        assert jobUiModel
        assert 2 == jobUiModel.resultsTotalCount
        assert 2 == jobUiModel.results.size()

        and:
        1 * service.jobService.countJobs() >> 2
        1 * service.jobService.listJobs(0, 2) >> ['job1', 'job2']
        2 * service.jobService.countJobExecutionsForJob(_) >> { String name -> name == "job1" ? 0 : 2 }
        2 * service.jobService.countJobInstances(_) >> 2
        2 * service.jobService.isLaunchable(_) >> { String name -> name == "job1" }
        2 * service.jobService.isIncrementable(_) >> false
        2 * service.jobService.listJobExecutionsForJob(_, _, _)

        2 * service.springBatchService.hasRunningExecutions(_) >> true
    }

    void "get job models with parameters"() {
        when:
        def jobUiModel = service.getJobModels([offset: 1, max: 1])

        then:
        jobUiModel
        2 == jobUiModel.resultsTotalCount
        1 == jobUiModel.results.size()

        and:
        1 * service.jobService.countJobs() >> 2
        1 * service.jobService.listJobs(1, 1) >> ["job2"]
        1 * service.jobService.countJobExecutionsForJob("job2") >> 2
        1 * service.jobService.countJobInstances(_) >> 2
        1 * service.jobService.isLaunchable("job2") >> false
        1 * service.jobService.isIncrementable("job2") >> false
        1 * service.jobService.listJobExecutionsForJob(_, _, _)

        1 * service.springBatchService.hasRunningExecutions(_) >> true
    }

    void testJobModel() {
        when:
        JobModel model = service.jobModel("testJob")

        then:
        model
        "testJob" == model.name
        5 == model.executionCount
        model.launchable
        !model.incrementable

        and:
        1 * service.jobService.countJobExecutionsForJob(_) >> 5
        1 * service.jobService.countJobInstances(_) >> 2
        1 * service.jobService.isLaunchable(_) >> true
        1 * service.jobService.isIncrementable(_) >> false
        1 * service.jobService.listJobExecutionsForJob(_, _, _)
        1 * service.jobService.getStepNamesForJob(_) >> ['step1', 'step2']

        1 * service.springBatchService.hasRunningExecutions(_) >> true
    }

    void testGetJobInstanceModels() {
        def jobParameters1 = new JobParameters()
        def jobInstance1 = new JobInstance(1, "job1")
        def jobParameters2 = new JobParameters()
        def jobInstance2 = new JobInstance(2, "job1")

        def jobExecutionMock = new JobExecution(jobInstance1, 1, jobParameters1, "test")
        jobExecutionMock.with {
            status = BatchStatus.COMPLETED
            startTime = new Date()
            endTime = new Date()
            jobInstance = jobInstance1
        }
        def jobExecutionMock2 = new JobExecution(jobInstance2, 2, jobParameters2, "test")
        jobExecutionMock2.with {
            status = BatchStatus.FAILED
            startTime = new Date()
            endTime = new Date()
            jobInstance = jobInstance2
        }

        def executionList = [jobExecutionMock, jobExecutionMock2]

        when:
        def jobInstanceUiModel = service.getJobInstanceModels("job1", [:])

        then:
        jobInstanceUiModel
        2 == jobInstanceUiModel.resultsTotalCount
        2 == jobInstanceUiModel.results.size()

        and:
        1 * service.jobService.countJobInstances("job1") >> 2
        1 * service.jobService.listJobInstances("job1", 0, 2) >> [jobInstance1, jobInstance2]
        2 * service.jobService.getJobExecutionsForJobInstance("job1", _) >> { String name, Long id ->
            id == 1 ? executionList : []
        }
    }

    void testGetJobInstanceModels_withParams() {
        def jobParameters1 = new JobParameters()
        def jobInstance1 = new JobInstance(1, "job1")
        def jobParameters2 = new JobParameters()
        def jobInstance2 = new JobInstance(2, "job1")

        def jobExecutionMock = new JobExecution(1, jobParameters1)
        jobExecutionMock.with {
            status = BatchStatus.COMPLETED
            startTime = new Date()
            endTime = new Date()
            jobInstance = jobInstance1
        }
        def jobExecutionMock2 = new JobExecution(2, jobParameters2)
        jobExecutionMock2.with {
            status = BatchStatus.FAILED
            startTime = new Date()
            endTime = new Date()
            jobInstance = jobInstance2
        }

        def executionList = [jobExecutionMock, jobExecutionMock2]

        when:
        def jobInstanceUiModel = service.getJobInstanceModels("job1", [offset: 1, max: 1])

        then:
        jobInstanceUiModel
        2 == jobInstanceUiModel.resultsTotalCount
        1 == jobInstanceUiModel.results.size()

        and:
        1 * service.jobService.countJobInstances("job1") >> 2
        1 * service.jobService.listJobInstances("job1", 1, 1) >> [jobInstance1]
        1 * service.jobService.getJobExecutionsForJobInstance("job1", 1L) >> executionList
    }


    void testJobInstanceModel() {
        def jobParameters1 = new JobParameters()
        def jobInstance1 = new JobInstance(1, "job1")
        def jobParameters2 = new JobParameters()
        def jobInstance2 = new JobInstance(2, "job1")

        def jobExecutionMock = new JobExecution(jobInstance1, 1, jobParameters1, "test")
        jobExecutionMock.with {
            status = BatchStatus.COMPLETED
            startTime = new Date()
            endTime = new Date()
            jobInstance = jobInstance1
        }
        def jobExecutionMock2 = new JobExecution(jobInstance2, 2, jobParameters2, "test")
        jobExecutionMock2.with {
            status = BatchStatus.FAILED
            startTime = new Date()
            endTime = new Date()
            jobInstance = jobInstance2
        }

        def executionList = [jobExecutionMock, jobExecutionMock2]

        def jobParameters = new JobParameters()
        def jobInstance = new JobInstance(1, "testJob")

        when:
        JobInstanceModel jobInstanceModel = service.jobInstanceModel(jobInstance)

        then:
        jobInstanceModel
        1L == jobInstanceModel.id
        2 == jobInstanceModel.jobExecutionCount
        2 == executionList.size()
        jobParameters.parameters.size() == jobInstanceModel.jobParameters.size()
        BatchStatus.COMPLETED == jobInstanceModel.lastJobExecutionStatus

        and:
        1 * service.jobService.getJobExecutionsForJobInstance(_, _) >> executionList
    }

    void testGetJobExecutionModels() {
        JobInstance jobInstance = new JobInstance(1, "job1")
        JobExecution jobExecution = new JobExecution(jobInstance, 1, new JobParameters(), "test")
        jobExecution.startTime = new Date()
        jobExecution.endTime = new Date(jobExecution.startTime.time + 10000)
        jobExecution.status = BatchStatus.COMPLETED
        jobExecution.exitStatus = ExitStatus.COMPLETED

        StepExecution stepExecution = new StepExecution("step1", jobExecution)
        stepExecution.startTime = new Date()
        stepExecution.endTime = new Date(stepExecution.startTime.time + 20000)
        jobExecution.addStepExecutions([stepExecution])

        when:
        def jobExecutionUiModel = service.getJobExecutionModels("job1", 1, [:])

        then:
        jobExecutionUiModel
        1 == jobExecutionUiModel.resultsTotalCount
        1 == jobExecutionUiModel.results.size()

        and:
        1 * service.jobService.getJobExecutionsForJobInstance("job1", 1L) >> [jobExecution]
    }

    void testGetJobExecutionModels_withParams() {
        JobInstance jobInstance = new JobInstance(1, "job1")
        JobExecution jobExecution = new JobExecution(jobInstance, 1, new JobParameters(), "test")
        jobExecution.startTime = new Date()
        jobExecution.endTime = new Date(jobExecution.startTime.time + 10000)
        jobExecution.status = BatchStatus.COMPLETED
        jobExecution.exitStatus = ExitStatus.COMPLETED

        StepExecution stepExecution = new StepExecution("step1", jobExecution)
        stepExecution.startTime = new Date()
        stepExecution.endTime = new Date(stepExecution.startTime.time + 20000)
        jobExecution.addStepExecutions([stepExecution])

        JobExecution jobExecution2 = new JobExecution(jobInstance, 2, new JobParameters(), "test")
        jobExecution2.startTime = new Date()
        jobExecution2.endTime = new Date(jobExecution2.startTime.time + 10000)
        jobExecution2.status = BatchStatus.COMPLETED
        jobExecution2.exitStatus = ExitStatus.COMPLETED

        StepExecution stepExecution2 = new StepExecution("step1", jobExecution2)
        stepExecution2.startTime = new Date()
        stepExecution2.endTime = new Date(stepExecution2.startTime.time + 20000)
        jobExecution2.addStepExecutions([stepExecution2])

        when:
        def jobExecutionUiModel = service.getJobExecutionModels("job1", 1, [offset: 1, max: 1])

        then:
        jobExecutionUiModel
        2 == jobExecutionUiModel.resultsTotalCount
        1 == jobExecutionUiModel.results.size()

        and:
        1 * service.jobService.getJobExecutionsForJobInstance("job1", 1L) >> [jobExecution, jobExecution2]
    }

    void testJobExecutionModel() {
        JobInstance jobInstance = new JobInstance(1, "simpleJob")
        JobExecution jobExecution = new JobExecution(jobInstance, 1, null, "test")
        jobExecution.startTime = new Date()
        jobExecution.endTime = dateWithDuration(new Date(), 10000)
        jobExecution.status = BatchStatus.COMPLETED
        jobExecution.exitStatus = ExitStatus.COMPLETED

        StepExecution stepExecution = new StepExecution("step1", jobExecution)
        stepExecution.startTime = new Date()
        stepExecution.endTime = dateWithDuration(stepExecution.startTime, 20000)
        jobExecution.addStepExecutions([stepExecution])

        when:
        JobExecutionModel jobExecutionModel = service.jobExecutionModel(jobExecution)

        then:
        jobExecutionModel
        jobExecution.id == jobExecutionModel.id
        jobExecution.jobInstance.id == jobExecutionModel.instanceId
        jobExecution.jobInstance.jobName == jobExecutionModel.jobName
        jobExecution.startTime == jobExecutionModel.startDateTime
        jobExecution.endTime == dateWithDuration(jobExecutionModel.startDateTime, jobExecutionModel.duration)
        jobExecution.status == jobExecutionModel.status
        jobExecution.exitStatus == jobExecutionModel.exitStatus
    }

    void testGetStepExecutionModels() {
        JobExecution jobExecution = new JobExecution(2)
        StepExecution stepExecution = new StepExecution("testStep1", jobExecution, 1)
        stepExecution.startTime = new Date()
        stepExecution.endTime = new Date(stepExecution.startTime.time + 15000)
        stepExecution.status = BatchStatus.ABANDONED
        stepExecution.readCount = 3
        stepExecution.writeCount = 5
        stepExecution.writeSkipCount = 7
        stepExecution.readSkipCount = 9
        stepExecution.processSkipCount = 11
        stepExecution.exitStatus = ExitStatus.UNKNOWN

        when:
        def stepExecutionUiModel = service.getStepExecutionModels(1L, [:])

        then:
        stepExecutionUiModel
        1 == stepExecutionUiModel.resultsTotalCount
        1 == stepExecutionUiModel.results.size()

        and:
        1 * service.jobService.getStepExecutions(1L) >> [stepExecution]
    }

    void testGetStepExecutionModels_withParams() {
        JobExecution jobExecution = new JobExecution(2)
        StepExecution stepExecution = new StepExecution("testStep1", jobExecution, 1)
        stepExecution.startTime = new Date()
        stepExecution.endTime = new Date(stepExecution.startTime.time + 15000)
        stepExecution.status = BatchStatus.ABANDONED
        stepExecution.readCount = 3
        stepExecution.writeCount = 5
        stepExecution.writeSkipCount = 7
        stepExecution.readSkipCount = 9
        stepExecution.processSkipCount = 11
        stepExecution.exitStatus = ExitStatus.UNKNOWN

        StepExecution stepExecution2 = new StepExecution("testStep2", jobExecution, 1)
        stepExecution2.startTime = new Date()
        stepExecution2.endTime = new Date(stepExecution.startTime.time + 15000)
        stepExecution2.status = BatchStatus.ABANDONED
        stepExecution2.readCount = 3
        stepExecution2.writeCount = 5
        stepExecution2.writeSkipCount = 7
        stepExecution2.readSkipCount = 9
        stepExecution2.processSkipCount = 11
        stepExecution2.exitStatus = ExitStatus.UNKNOWN

        when:
        def stepExecutionUiModel = service.getStepExecutionModels(1L, [offset: 1, max: 1])

        then:
        stepExecutionUiModel
        2 == stepExecutionUiModel.resultsTotalCount
        1 == stepExecutionUiModel.results.size()

        and:
        StepExecutionModel stepExecutionModel = stepExecutionUiModel.results[0]
        stepExecutionModel
        stepExecution2.stepName == stepExecutionModel.name
        stepExecution2.startTime == stepExecutionModel.startDateTime
        stepExecution2.endTime == dateWithDuration(stepExecutionModel.startDateTime, stepExecutionModel.duration)
        stepExecution2.status == stepExecutionModel.status
        stepExecution2.readCount == stepExecutionModel.reads
        stepExecution2.writeCount == stepExecutionModel.writes
        stepExecution2.skipCount == stepExecutionModel.skips
        stepExecution2.exitStatus == stepExecutionModel.exitStatus

        and:
        1 * service.jobService.getStepExecutions(1L) >> [stepExecution, stepExecution2]
    }


    private Date dateWithDuration(Date date, long duration) {
        new Date(date.time + duration)
    }
}
