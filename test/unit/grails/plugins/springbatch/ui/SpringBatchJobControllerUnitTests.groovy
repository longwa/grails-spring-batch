package grails.plugins.springbatch.ui

import grails.plugins.springbatch.model.JobModel
import grails.test.mixin.*

import org.junit.Before
import org.junit.Test

@TestFor(SpringBatchJobController)
class SpringBatchJobControllerUnitTests {

    def springBatchUiServiceMock

    @Before
    void setUp() {
        springBatchUiServiceMock = mockFor(SpringBatchUiService)
    }

    @Test
    void testList() {
        springBatchUiServiceMock.demand.getJobModels(1..1) {Map params ->
            return new PagedResult<JobModel>(resultsTotalCount:0, results:[])
        }
        controller.springBatchUiService = springBatchUiServiceMock.createMock()

        def results = controller.list()

        assert results.modelInstances.results.size() == 0
        assert results.modelInstances.resultsTotalCount == 0

        springBatchUiServiceMock.verify()

    }
}
