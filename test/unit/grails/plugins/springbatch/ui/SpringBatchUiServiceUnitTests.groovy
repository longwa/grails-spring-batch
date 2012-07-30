package grails.plugins.springbatch.ui

import grails.test.mixin.TestFor
import org.junit.Test
import org.junit.Before
import org.springframework.batch.admin.service.JobService
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobInstance
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepExecution

@TestFor(SpringBatchUiService)
class SpringBatchUiServiceUnitTests {

    def jobServiceMock

    @Before
    public void setUp() {
        jobServiceMock = mockFor(JobService, true)
    }

    @Test
    public void testGetJobModels() {

        jobServiceMock.demand.countJobs(2..2) { ->
            return 2
        }
        jobServiceMock.demand.listJobs(1..1) { int offset, int max ->
            assert 0 == offset
            assert 2 == max
            return ["job1", "job2"]
        }
        jobServiceMock.demand.countJobExecutionsForJob(2..2) {String name ->
            assert (name == "job1" || name == "job2")
            if(name == "job1") {
                return 0
            } else {
                return 2
            }
        }
        jobServiceMock.demand.isLaunchable(2..2) {String name ->
            assert (name == "job1" || name == "job2")
            if(name == "job1") {
                return true
            } else {
                return false
            }
        }
        jobServiceMock.demand.isIncrementable(2..2) {String name ->
            assert (name == "job1" || name == "job2")
            return false
        }

        service.jobService = jobServiceMock.createMock()

        def jobUiModel = service.getJobUiModel()

        assert jobUiModel
        assert 2 == jobUiModel.modelTotal
        assert 2 == jobUiModel.modelInstances.size()

        jobServiceMock.verify()
    }

    @Test
    public void testGetJobInstanceModels() {
        def jobExecutionMock = new JobExecution(1)
        jobExecutionMock.status = BatchStatus.COMPLETED
        def jobExecutionMock2 = new JobExecution(2)
        jobExecutionMock2.status = BatchStatus.FAILED

        def jobParameters1 = new JobParameters()
        def jobInstance1 = new JobInstance(1, jobParameters1, "job1")
        def jobParameters2 = new JobParameters()
        def jobInstance2 = new JobInstance(2, jobParameters2, "job1")

        def executionList = [jobExecutionMock, jobExecutionMock2]

        jobServiceMock.demand.countJobInstances(2..2) {String jobName ->
            assert "job1" == jobName
            return 2
        }
        jobServiceMock.demand.listJobInstances(1..1) {String jobName, int offset, int max ->
            assert "job1" == jobName
            assert 0 == offset
            assert 2 == max
            return [jobInstance1, jobInstance2]
        }
        jobServiceMock.demand.getJobExecutionsForJobInstance(2..2) {String jobName, Long id ->
            assert "job1" == jobName
            assert (1 == id || 2 == id)
            if(id == 1) {
                return executionList
            } else {
                return []
            }
        }

        service.jobService = jobServiceMock.createMock()

        def jobInstanceUiModel = service.getJobInstanceUiModel("job1")

        assert jobInstanceUiModel
        assert 2 == jobInstanceUiModel.modelTotal
        assert 2 == jobInstanceUiModel.modelInstances.size()
        assert "job1" == jobInstanceUiModel.jobName

        jobServiceMock.verify()
    }

    @Test
    public void testGetJobExecutionModels() {
        JobInstance jobInstance = new JobInstance(1, null, "job1")
        JobExecution jobExecution = new JobExecution(jobInstance, 1)
        jobExecution.startTime = new Date()
        jobExecution.endTime = new Date(jobExecution.startTime.time + 10000)
        jobExecution.status = BatchStatus.COMPLETED
        jobExecution.exitStatus = ExitStatus.COMPLETED

        StepExecution stepExecution = new StepExecution("step1", jobExecution)
        stepExecution.startTime = new Date()
        stepExecution.endTime = new Date(stepExecution.startTime.time + 20000)
        jobExecution.addStepExecutions([stepExecution])

        jobServiceMock.demand.getJobExecutionsForJobInstance(2..2) {String jobName, Long id ->
            assert "job1" == jobName
            assert 1 == id
            return [jobExecution]
        }

        service.jobService = jobServiceMock.createMock()

        def jobExecutionUiModel = service.getJobExecutionUiModel("job1", 1)

        assert jobExecutionUiModel
        assert 1 == jobExecutionUiModel.modelTotal
        assert 1 == jobExecutionUiModel.modelInstances.size()
        assert "job1" == jobExecutionUiModel.jobName
        assert 1 == jobExecutionUiModel.jobInstanceId

        jobServiceMock.verify()
    }

    @Test
    public void testGetStepExecutionModels() {
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

        jobServiceMock.demand.getStepExecutions(2..2) {Long id ->
            assert 1 == id
            return [stepExecution]
        }

        service.jobService = jobServiceMock.createMock()

        def stepExecutionUiModel = service.getStepExecutionUiModel(1)

        assert stepExecutionUiModel
        assert 1 == stepExecutionUiModel.modelTotal
        assert 1 == stepExecutionUiModel.modelInstances.size()
        assert 1 == stepExecutionUiModel.jobExecutionId

        jobServiceMock.verify()
    }

}
