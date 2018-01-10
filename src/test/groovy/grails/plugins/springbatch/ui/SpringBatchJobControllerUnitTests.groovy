package grails.plugins.springbatch.ui

import grails.plugins.springbatch.SpringBatchService
import grails.plugins.springbatch.model.JobModel
import grails.test.mixin.*

import org.junit.Before
import org.junit.Test

@TestFor(SpringBatchJobController)
class SpringBatchJobControllerUnitTests {

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
