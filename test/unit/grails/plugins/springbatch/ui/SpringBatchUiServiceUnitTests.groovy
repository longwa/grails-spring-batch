package grails.plugins.springbatch.ui

import grails.plugins.springbatch.SpringBatchService
import grails.plugins.springbatch.model.JobExecutionModel
import grails.plugins.springbatch.model.JobInstanceModel
import grails.plugins.springbatch.model.JobModel
import grails.plugins.springbatch.model.StepExecutionModel
import grails.test.mixin.*

import org.junit.Before
import org.junit.Test
import org.springframework.batch.admin.service.JobService
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobInstance
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.StepExecution

@TestFor(SpringBatchUiService)
class SpringBatchUiServiceUnitTests {

    def jobServiceMock
	def springBatchServiceMock

    @Before
    void setUp() {
        jobServiceMock = mockFor(JobService, true)
		springBatchServiceMock = mockFor(SpringBatchService, true)
    }

    @Test
    void testGetJobModels() {

        jobServiceMock.demand.countJobs(1) { ->
            return 2
        }
        jobServiceMock.demand.listJobs(1) { int offset, int max ->
            assert 0 == offset
            assert 2 == max
            return ["job1", "job2"]
        }
        jobServiceMock.demand.countJobExecutionsForJob(2) {String name ->
            assert (name == "job1" || name == "job2")
            if(name == "job1") {
                return 0
            } else {
                return 2
            }
        }
		jobServiceMock.demand.countJobInstances(2){String name ->
			return 2
		}
        jobServiceMock.demand.isLaunchable(2) {String name ->
            assert (name == "job1" || name == "job2")
            if(name == "job1") {
                return true
            } else {
                return false
            }
        }
        jobServiceMock.demand.isIncrementable(2) {String name ->
            assert (name == "job1" || name == "job2")
            return false
        }
		springBatchServiceMock.demand.hasRunningExecutions(2){String name ->
			return true
		}
		jobServiceMock.demand.listJobExecutionsForJob(2){String name, int start, int max ->
			return null
		}

        service.jobService = jobServiceMock.createMock()
		service.springBatchService = springBatchServiceMock.createMock()

        def jobUiModel = service.getJobModels([:])

        assert jobUiModel
        assert 2 == jobUiModel.resultsTotalCount
        assert 2 == jobUiModel.results.size()

        jobServiceMock.verify()
		springBatchServiceMock.verify()
    }
	
    @Test
    void testGetJobModels_withParams() {

        jobServiceMock.demand.countJobs(1) { ->
            return 2
        }
        jobServiceMock.demand.listJobs(1) { int offset, int max ->
            assert 1 == offset
            assert 1 == max
            return ["job2"]
        }
        jobServiceMock.demand.countJobExecutionsForJob(1) {String name ->
            assert name == "job2"
            return 2
        }
		jobServiceMock.demand.countJobInstances(1){String name ->
			return 2
		}
		jobServiceMock.demand.isLaunchable(1) {String name ->
            assert name == "job2"
            return false
        }
        jobServiceMock.demand.isIncrementable(1) {String name ->
            assert name == "job2"
            return false
        }
		springBatchServiceMock.demand.hasRunningExecutions(1){String name ->
			return true
		}
		jobServiceMock.demand.listJobExecutionsForJob(1){String name, int start, int max ->
			return null
		}

        service.jobService = jobServiceMock.createMock()
		service.springBatchService = springBatchServiceMock.createMock()

        def jobUiModel = service.getJobModels([offset: 1, max: 1])

        assert jobUiModel
        assert 2 == jobUiModel.resultsTotalCount
        assert 1 == jobUiModel.results.size()

        jobServiceMock.verify()
		springBatchServiceMock.verify()
    }
	
	@Test
	void testJobModel() {
		def jobServiceMock = mockFor(JobService)
		jobServiceMock.demand.countJobExecutionsForJob(1..1) {String name ->
			return 5
		}
		jobServiceMock.demand.countJobInstances(1){String name ->
			return 2
		}
		jobServiceMock.demand.isLaunchable(1..1) {String name ->
			return true
		}
		jobServiceMock.demand.isIncrementable(1..1) {String name ->
			return false
		}
		springBatchServiceMock.demand.hasRunningExecutions(1){String name ->
			return true
		}
		jobServiceMock.demand.listJobExecutionsForJob(1){String name, int start, int max ->
			return null
		}
		jobServiceMock.demand.getStepNamesForJob(1){String name ->
			return ['step1', 'step2']
		}
		
		service.jobService = jobServiceMock.createMock()
		service.springBatchService = springBatchServiceMock.createMock()

		JobModel model = service.jobModel("testJob")

		assert model
		assert "testJob" == model.name
		assert 5 == model.executionCount
		assert model.launchable
		assert !model.incrementable

		jobServiceMock.verify()
		springBatchServiceMock.verify()
	}

    @Test
    void testGetJobInstanceModels() {

        def jobParameters1 = new JobParameters()
        def jobInstance1 = new JobInstance(1, jobParameters1, "job1")
        def jobParameters2 = new JobParameters()
        def jobInstance2 = new JobInstance(2, jobParameters2, "job1")
        
		def jobExecutionMock = new JobExecution(1)
		jobExecutionMock.with {
			status = BatchStatus.COMPLETED
			startTime = new Date()
			endTime = new Date()
			jobInstance = jobInstance1
		}
        def jobExecutionMock2 = new JobExecution(2)
		jobExecutionMock2.with {
			status = BatchStatus.FAILED
			startTime = new Date()
			endTime = new Date()
			jobInstance = jobInstance2
        }

        def executionList = [jobExecutionMock, jobExecutionMock2]

        jobServiceMock.demand.countJobInstances(1) {String jobName ->
            assert "job1" == jobName
            return 2
        }
        jobServiceMock.demand.listJobInstances(1) {String jobName, int offset, int max ->
            assert "job1" == jobName
            assert 0 == offset
            assert 2 == max
            return [jobInstance1, jobInstance2]
        }
        jobServiceMock.demand.getJobExecutionsForJobInstance(2) {String jobName, Long id ->
            assert "job1" == jobName
            assert (1 == id || 2 == id)
            if(id == 1) {
                return executionList
            } else {
                return []
            }
        }

        service.jobService = jobServiceMock.createMock()

        def jobInstanceUiModel = service.getJobInstanceModels("job1", [:])

        assert jobInstanceUiModel
        assert 2 == jobInstanceUiModel.resultsTotalCount
        assert 2 == jobInstanceUiModel.results.size()

        jobServiceMock.verify()
    }

    @Test
    void testGetJobInstanceModels_withParams() {
        
        def jobParameters1 = new JobParameters()
        def jobInstance1 = new JobInstance(1, jobParameters1, "job1")
        def jobParameters2 = new JobParameters()
        def jobInstance2 = new JobInstance(2, jobParameters2, "job1")
        
		def jobExecutionMock = new JobExecution(1)
		jobExecutionMock.with {
			status = BatchStatus.COMPLETED
			startTime = new Date()
			endTime = new Date()
			jobInstance = jobInstance1
		}
        def jobExecutionMock2 = new JobExecution(2)
		jobExecutionMock2.with {
			status = BatchStatus.FAILED
			startTime = new Date()
			endTime = new Date()
			jobInstance = jobInstance2
        }
		
		def executionList = [jobExecutionMock, jobExecutionMock2]

        jobServiceMock.demand.countJobInstances(1) {String jobName ->
            assert "job1" == jobName
            return 2
        }
        jobServiceMock.demand.listJobInstances(1) {String jobName, int offset, int max ->
            assert "job1" == jobName
            assert 1 == offset
            assert 1 == max
            return [jobInstance1]
        }
        jobServiceMock.demand.getJobExecutionsForJobInstance(1) {String jobName, Long id ->
            assert "job1" == jobName
            assert 1 == id
            return executionList
        }

        service.jobService = jobServiceMock.createMock()

        def jobInstanceUiModel = service.getJobInstanceModels("job1", [offset: 1, max: 1])

        assert jobInstanceUiModel
        assert 2 == jobInstanceUiModel.resultsTotalCount
        assert 1 == jobInstanceUiModel.results.size()

        jobServiceMock.verify()
    }

	
    @Test
    void testJobInstanceModel() {
        def jobParameters1 = new JobParameters()
        def jobInstance1 = new JobInstance(1, jobParameters1, "job1")
        def jobParameters2 = new JobParameters()
        def jobInstance2 = new JobInstance(2, jobParameters2, "job1")
        
		def jobExecutionMock = new JobExecution(1)
		jobExecutionMock.with {
			status = BatchStatus.COMPLETED
			startTime = new Date()
			endTime = new Date()
			jobInstance = jobInstance1
		}
        def jobExecutionMock2 = new JobExecution(2)
		jobExecutionMock2.with {
			status = BatchStatus.FAILED
			startTime = new Date()
			endTime = new Date()
			jobInstance = jobInstance2
        }
		
        def executionList = [jobExecutionMock, jobExecutionMock2]

        jobServiceMock.demand.getJobExecutionsForJobInstance(1..1) {String name, Long id ->
            return executionList
        }

        def jobParameters = new JobParameters()
        def jobInstance = new JobInstance(1, jobParameters, "testJob")
		
		service.jobService = jobServiceMock.createMock()

        JobInstanceModel jobInstanceModel = service.jobInstanceModel(jobInstance)

        assert jobInstanceModel
        assert 1 == jobInstanceModel.id
        assert 2 == jobInstanceModel.jobExecutionCount
        assert 2 == executionList.size()
        assert jobParameters.parameters.size() == jobInstanceModel.jobParameters.size()
        assert BatchStatus.COMPLETED == jobInstanceModel.lastJobExecutionStatus

        jobServiceMock.verify()
    }

    @Test
    void testGetJobExecutionModels() {
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

        jobServiceMock.demand.getJobExecutionsForJobInstance(1) {String jobName, Long id ->
            assert "job1" == jobName
            assert 1 == id
            return [jobExecution]
        }

        service.jobService = jobServiceMock.createMock()

        def jobExecutionUiModel = service.getJobExecutionModels("job1", 1, [:])

        assert jobExecutionUiModel
        assert 1 == jobExecutionUiModel.resultsTotalCount
        assert 1 == jobExecutionUiModel.results.size()
        
        jobServiceMock.verify()
    }

    @Test
    void testGetJobExecutionModels_withParams() {
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

        JobExecution jobExecution2 = new JobExecution(jobInstance, 2)
        jobExecution2.startTime = new Date()
        jobExecution2.endTime = new Date(jobExecution2.startTime.time + 10000)
        jobExecution2.status = BatchStatus.COMPLETED
        jobExecution2.exitStatus = ExitStatus.COMPLETED

        StepExecution stepExecution2 = new StepExecution("step1", jobExecution2)
        stepExecution2.startTime = new Date()
        stepExecution2.endTime = new Date(stepExecution2.startTime.time + 20000)
        jobExecution2.addStepExecutions([stepExecution2])

        jobServiceMock.demand.getJobExecutionsForJobInstance(1) {String jobName, Long id ->
            assert "job1" == jobName
            assert 1 == id
            return [jobExecution, jobExecution2]
        }

        service.jobService = jobServiceMock.createMock()

        def jobExecutionUiModel = service.getJobExecutionModels("job1", 1, [offset: 1, max: 1])

        assert jobExecutionUiModel
        assert 2 == jobExecutionUiModel.resultsTotalCount
        assert 1 == jobExecutionUiModel.results.size()
        
        jobServiceMock.verify()
    }
	
	@Test
	void testJobExecutionModel() {

		def jobServiceMock = mockFor(JobService)
		JobInstance jobInstance = new JobInstance(1, null, "simpleJob")
		JobExecution jobExecution = new JobExecution(jobInstance, 1)
		jobExecution.startTime = new Date()
		jobExecution.endTime = dateWithDuration(new Date(), 10000)
		jobExecution.status = BatchStatus.COMPLETED
		jobExecution.exitStatus = ExitStatus.COMPLETED

		StepExecution stepExecution = new StepExecution("step1", jobExecution)
		stepExecution.startTime = new Date()
		stepExecution.endTime = dateWithDuration(stepExecution.startTime, 20000)
		jobExecution.addStepExecutions([stepExecution])
		
		service.jobService = jobServiceMock.createMock()

		JobExecutionModel jobExecutionModel = service.jobExecutionModel(jobExecution)

		assert jobExecutionModel
		assert jobExecution.id == jobExecutionModel.id
		assert jobExecution.jobInstance.id == jobExecutionModel.instanceId
		assert jobExecution.jobInstance.jobName == jobExecutionModel.jobName
		assert jobExecution.startTime == jobExecutionModel.startDateTime
		assert jobExecution.endTime == dateWithDuration(jobExecutionModel.startDateTime, jobExecutionModel.duration)
		assert jobExecution.status == jobExecutionModel.status
		assert jobExecution.exitStatus == jobExecutionModel.exitStatus

		/*assert jobExecution.stepExecutions.collect {
		  StepExecutionModel.fromService(jobServiceMock.createMock() as JobService, it)
		} == jobExecutionModel.stepExecutions*/

		jobServiceMock.verify()
	}
	
    @Test
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

        jobServiceMock.demand.getStepExecutions(1) {Long id ->
            assert 1 == id
            return [stepExecution]
        }

        service.jobService = jobServiceMock.createMock()

        def stepExecutionUiModel = service.getStepExecutionModels(1L, [:])

        assert stepExecutionUiModel
        assert 1 == stepExecutionUiModel.resultsTotalCount
        assert 1 == stepExecutionUiModel.results.size()

        jobServiceMock.verify()
    }

    @Test
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

        jobServiceMock.demand.getStepExecutions(1) {Long id ->
            assert 1 == id
            return [stepExecution, stepExecution2]
        }

        service.jobService = jobServiceMock.createMock()

        def stepExecutionUiModel = service.getStepExecutionModels(1L, [offset: 1, max: 1])

        assert stepExecutionUiModel
        assert 2 == stepExecutionUiModel.resultsTotalCount
        assert 1 == stepExecutionUiModel.results.size()

		StepExecutionModel stepExecutionModel = stepExecutionUiModel.results[0]
		assert stepExecutionModel
		assert stepExecution2.stepName == stepExecutionModel.name
		assert stepExecution2.startTime == stepExecutionModel.startDateTime
		assert stepExecution2.endTime == dateWithDuration(stepExecutionModel.startDateTime, stepExecutionModel.duration)
		assert stepExecution2.status == stepExecutionModel.status
		assert stepExecution2.readCount == stepExecutionModel.reads
		assert stepExecution2.writeCount == stepExecutionModel.writes
		assert stepExecution2.skipCount == stepExecutionModel.skips
		assert stepExecution2.exitStatus == stepExecutionModel.exitStatus
		
        jobServiceMock.verify()
    }
	

	private Date dateWithDuration(Date date, long duration) {
		new Date(date.time + duration)
	}
}
