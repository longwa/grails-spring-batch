package grails.plugins.springbatch.ui

class SpringBatchJobInstanceController {

    def springBatchUiService

    def index() {
        redirect(action: 'list')
    }

    def list(String id) {
        if(!id) {
            //TODO Add flash error
            redirect(controller: "springBatchJob", action: "index")
        } else {
            params.offset = params.offset ?: 0
            params.max = params.max ?: 10
            springBatchUiService.getJobInstanceUiModel(id, params)
        }
    }
}
