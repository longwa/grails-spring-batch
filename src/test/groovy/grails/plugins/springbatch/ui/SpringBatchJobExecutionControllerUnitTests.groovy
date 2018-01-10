package grails.plugins.springbatch.ui

import grails.plugins.springbatch.model.JobExecutionModel
import grails.plugins.springbatch.model.StepExecutionModel
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class SpringBatchJobExecutionControllerUnitTests extends Specification implements ControllerUnitTest<SpringBatchJobExecutionController> {
    void setup() {
        controller.springBatchUiService = Mock(SpringBatchUiService)
    }

    void testShow() {
        when:
        def results = controller.show(1L)

        then:
		results.modelInstances.results.size() == 0
		results.modelInstances.resultsTotalCount == 0

        and:
        1 * controller.springBatchUiService.jobExecutionModel(_) >> {
            return new JobExecutionModel()
        }
        1 * controller.springBatchUiService.getStepExecutionModels(_, _)  >> {
            return new PagedResult<StepExecutionModel>(resultsTotalCount:0, results:[])
        }
    }
}
