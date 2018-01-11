package grails.plugins.springbatch.ui

import grails.plugins.springbatch.SpringBatchService
import grails.plugins.springbatch.model.JobModel
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class SpringBatchJobControllerUnitTests extends Specification implements ControllerUnitTest<SpringBatchJobController> {
    void setup() {
        controller.springBatchService = Mock(SpringBatchService)
        controller.springBatchUiService = Mock(SpringBatchUiService)
    }

    void testList() {
        when:
        def results = controller.list()

        then:
        results.modelInstances.results.size() == 0
        results.modelInstances.resultsTotalCount == 0

        and:
        1 * controller.springBatchUiService.getJobModels(_) >> {
            return new PagedResult<JobModel>(resultsTotalCount:0, results:[])
        }
    }
}
