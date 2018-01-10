package grails.plugins.springbatch.ui

import grails.plugins.springbatch.model.JobExecutionModel
import grails.plugins.springbatch.model.StepExecutionModel
import grails.testing.web.controllers.ControllerUnitTest
import org.junit.Before
import org.junit.Test

class SpringBatchJobExecutionControllerUnitTests implements ControllerUnitTest<SpringBatchJobExecutionController> {
    def springBatchUiServiceMock

    @Before
    void setUp() {
        springBatchUiServiceMock = mockFor(SpringBatchUiService)
    }

    @Test
    void testShow() {
		springBatchUiServiceMock.demand.jobExecutionModel(1..1) {String id->
			return new JobExecutionModel()
		}
		springBatchUiServiceMock.demand.getStepExecutionModels(1..1) {Long id, Map params ->
			return new PagedResult<StepExecutionModel>(resultsTotalCount:0, results:[])
		}
        
        controller.springBatchUiService = springBatchUiServiceMock.createMock()

        def results = controller.show(1,)
		
		assert results.modelInstances.results.size() == 0
		assert results.modelInstances.resultsTotalCount == 0

        springBatchUiServiceMock.verify()

    }

}
