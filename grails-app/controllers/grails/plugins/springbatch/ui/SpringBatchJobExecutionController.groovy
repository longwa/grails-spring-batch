package grails.plugins.springbatch.ui

class SpringBatchJobExecutionController {

    def springBatchUiService

    def index() {
        redirect(action: "list")
    }

    def list(Long id, String jobName) {
        if(!id || !jobName) {
            //TODO Add flash error
            redirect(controller: "springBatchJob", action: "index")
        } else {
            params.offset = params.offset ?: 0
            params.max = params.max ?: 10
            springBatchUiService.getJobExecutionUiModel(jobName, id, params)
        }
    }
}
