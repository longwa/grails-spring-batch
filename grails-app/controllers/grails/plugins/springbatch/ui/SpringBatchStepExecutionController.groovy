package grails.plugins.springbatch.ui

class SpringBatchStepExecutionController {

    def springBatchUiService

    static defaultAction = 'list'

    def list(Long id) {
        if(!id) {
            //TODO Add flash error
            redirect(controller: "springBatchJob", action: "index")
        } else {
            params.offset = params.offset ?: 0
            params.max = params.max ?: 10
            springBatchUiService.getStepExecutionUiModel(id, params)
        }
    }
}
