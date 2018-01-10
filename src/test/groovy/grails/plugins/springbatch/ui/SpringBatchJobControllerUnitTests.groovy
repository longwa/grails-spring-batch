package grails.plugins.springbatch.ui

import grails.plugins.springbatch.SpringBatchService
import grails.plugins.springbatch.model.JobModel
import grails.testing.web.controllers.ControllerUnitTest
import org.junit.Before
import org.junit.Test

class SpringBatchJobControllerUnitTests implements ControllerUnitTest<SpringBatchJobController> {

    def springBatchUiServiceMock
	def springBatchServiceMock

    @Before
    void setUp() {
        springBatchUiServiceMock = mockFor(SpringBatchUiService)
		springBatchServiceMock = mockFor(SpringBatchService)
    }

    @Test
    void testList() {
        springBatchUiServiceMock.demand.getJobModels(1..1) {Map params ->
            return new PagedResult<JobModel>(resultsTotalCount:0, results:[])
        }
        springBatchServiceMock.demand.ready(1..1) {Map params ->
            return true
        }
        controller.springBatchUiService = springBatchUiServiceMock.createMock()
        controller.springBatchService = springBatchServiceMock.createMock()

        def results = controller.list()

        assert results.modelInstances.results.size() == 0
        assert results.modelInstances.resultsTotalCount == 0

        springBatchUiServiceMock.verify()

    }
}
