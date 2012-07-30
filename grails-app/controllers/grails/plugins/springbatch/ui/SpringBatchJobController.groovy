package grails.plugins.springbatch.ui

class SpringBatchJobController {

    def springBatchUiService

    def index() {
        redirect(action:'list')
    }

    def list() {
        params.offset = params.offset ?: 0
        params.max = params.max ?: 10
        springBatchUiService.getJobUiModel(params)
    }
}
