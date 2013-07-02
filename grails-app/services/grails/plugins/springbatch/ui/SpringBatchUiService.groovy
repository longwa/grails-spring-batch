package grails.plugins.springbatch.ui

import org.springframework.batch.admin.service.JobService
import org.springframework.batch.core.launch.JobOperator

class SpringBatchUiService {

    static transactional = false

    JobService jobService
	JobOperator jobOperator
	
    List<JobModel> getJobModels(Map params = [:]) {
        int jobCount = jobService.countJobs()
        def offset = params.offset ?: 0
        def max = params.max ?: jobCount
        def jobs = jobService.listJobs(offset, max)

        def jobModelList = jobs.collect {
            JobModel.fromService(jobService, it)
        }
        jobModelList
    }

    Map getJobUiModel(Map params = [:]) {
        def model = [:]
        model.modelInstances = getJobModels(params)
        model.modelTotal = jobService.countJobs()
        model
    }

    List<JobInstanceModel> getJobInstanceModels(String jobName, Map params = [:]) {
        def jobInstanceCount = jobService.countJobInstances(jobName)
        def offset = params.offset ?: 0
        def max = params.max ?: jobInstanceCount
        def jobInstances = jobService.listJobInstances(jobName, offset, max)

        def jobInstanceModelList = jobInstances.collect {
            JobInstanceModel.fromService(jobService, it)
        }
        jobInstanceModelList
    }

    Map getJobInstanceUiModel(String jobName, Map params = [:]) {
        def model = [:]
        model.modelInstances = getJobInstanceModels(jobName, params)
        model.modelTotal = jobService.countJobInstances(jobName)
        model.jobName = jobName
        model
    }

    List<JobExecutionModel> getJobExecutionModels(String jobName, Long jobInstanceId, Map params = [:]) {
        def jobExecutions = jobService.getJobExecutionsForJobInstance(jobName, jobInstanceId)
        int jobExecutionCount = jobExecutions.size()
        int offset = params.offset?.toInteger() ?: 0
        int max = params.max?.toInteger() ?: jobExecutionCount

        def jobExecutionModelList = SpringBatchUiUtilities.paginate(offset, max) {
            jobExecutions.collect {
                JobExecutionModel.fromService(jobService, it)
            }
        }
        jobExecutionModelList
    }
	
	void stopAllJobExecutions(String jobName){
		Set<Long> executions = jobOperator.getRunningExecutions(jobName);
		log.info("Attempting to stop ${executions.size()} job executions for $jobName")
		
		executions.each{
			try{
				jobOperator.stop(it)
			}catch(Exception e){
				log.debug(e)
			}
		}
	}

    Map getJobExecutionUiModel(String jobName, Long jobInstanceId, Map params = [:]) {
        def model = [:]
        model.modelInstances = getJobExecutionModels(jobName, jobInstanceId, params)
        model.modelTotal = jobService.getJobExecutionsForJobInstance(jobName, jobInstanceId).size()
        model.jobName = jobName
        model.jobInstanceId = jobInstanceId
        model
    }

    List<StepExecutionModel> getStepExecutionModels(Long jobExecutionId, Map params=[:]) {
        def stepExecutions = jobService.getStepExecutions(jobExecutionId)
        int stepExecutionCount = stepExecutions.size()
        int offset = params.offset?.toInteger() ?: 0
        int max = params.max?.toInteger() ?: stepExecutionCount

        def stepExecutionModelList = SpringBatchUiUtilities.paginate(offset, max) {
            stepExecutions.collect {
                StepExecutionModel.fromService(jobService, it)
            }
        }
        stepExecutionModelList
    }

    Map getStepExecutionUiModel(Long jobExecutionId, Map params = [:]) {
        def model = [:]
        model.modelInstances = getStepExecutionModels(jobExecutionId, params)
        model.modelTotal = jobService.getStepExecutions(jobExecutionId).size()
        model.jobExecutionId = jobExecutionId
        model
    }
}
