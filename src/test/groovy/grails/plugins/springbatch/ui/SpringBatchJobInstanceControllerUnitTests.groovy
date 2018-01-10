package grails.plugins.springbatch.ui

import grails.plugins.springbatch.model.JobExecutionModel
import grails.plugins.springbatch.model.JobInstanceModel
import grails.test.mixin.*

import org.junit.Before
import org.junit.Test

@TestFor(SpringBatchJobInstanceController)
class SpringBatchJobInstanceControllerUnitTests {

    def springBatchUiServiceMock

    @Before
    void setUp() {
        springBatchUiServiceMock = mockFor(SpringBatchUiService)
    }

    @Test
    void testShow() {
        springBatchUiServiceMock.demand.jobInstanceModel(1..1) {String id->
			return new JobInstanceModel()
        }
		springBatchUiServiceMock.demand.getJobExecutionModels(1..1) {String jobName, Long id, Map params ->
            return new PagedResult<JobExecutionModel>(resultsTotalCount:0, results:[])
        }
        
        controller.springBatchUiService = springBatchUiServiceMock.createMock()

        def results = controller.show("testJob", 1L)

		assert results.modelInstances.results.size() == 0
		assert results.modelInstances.resultsTotalCount == 0

        springBatchUiServiceMock.verify()

    }

    @Test
    void testListNullId() {
        controller.show('myJob', null)

        assert response.redirectUrl.endsWith("/springBatchJob/show/myJob")
    }

    @Test
    void testListNullJobName() {
        controller.show(null, null)

        assert response.redirectUrl.endsWith("/springBatchJob/list")
    }

    @Test
    void testListBlankJobName() {
        controller.show("", null)

        assert response.redirectUrl.endsWith("/springBatchJob/list")
    }
}
