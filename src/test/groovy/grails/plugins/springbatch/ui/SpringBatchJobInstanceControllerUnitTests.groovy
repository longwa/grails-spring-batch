package grails.plugins.springbatch.ui

import grails.plugins.springbatch.model.JobExecutionModel
import grails.plugins.springbatch.model.JobInstanceModel
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class SpringBatchJobInstanceControllerUnitTests extends Specification implements ControllerUnitTest<SpringBatchJobInstanceController> {
    void setup() {
        controller.springBatchUiService = Mock(SpringBatchUiService)
    }

    void testShow() {
        when:
        def results = controller.show("testJob", 1L)

        then:
		results.modelInstances.results.size() == 0
		results.modelInstances.resultsTotalCount == 0

        and:
        1 * controller.springBatchUiService.jobInstanceModel(_) >> {
            return new JobInstanceModel()
        }
        1 * controller.springBatchUiService.getJobExecutionModels(_, _, _) >> {
            return new PagedResult<JobExecutionModel>(resultsTotalCount:0, results:[])
        }
    }

    void testListNullId() {
        when:
        controller.show('myJob', null)

        then:
        response.redirectUrl.endsWith("/springBatchJob/show/myJob")
    }

    void testListNullJobName() {
        when:
        controller.show(null, null)

        then:
        response.redirectUrl.endsWith("/springBatchJob/list")
    }

    void testListBlankJobName() {
        when:
        controller.show("", null)

        then:
        response.redirectUrl.endsWith("/springBatchJob/list")
    }
}
