package grails.plugins.springbatch.ui

class SpringBatchJobInstanceController {

    def springBatchUiService

    static defaultAction = 'list'

    def list(String id) {
        if(!id) {
            //TODO Add flash error
            redirect(controller: "springBatchJob")
        } else {
            params.offset = params.offset ?: 0
            params.max = params.max ?: 10
            springBatchUiService.getJobInstanceUiModel(id, params)
        }
    }
	
	def stopAllExecutions(String id) {
		if(!id) {
			render 'No job selected to stop'
		} else {
			springBatchUiService.stopAllJobExecutions(id)
			redirect(action:'list')
		}
	}
}
