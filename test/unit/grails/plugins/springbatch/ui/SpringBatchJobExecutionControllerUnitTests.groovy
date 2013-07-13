package grails.plugins.springbatch.ui

import grails.test.mixin.TestFor
import org.junit.Before
import org.junit.Test

@TestFor(SpringBatchJobExecutionController)
class SpringBatchJobExecutionControllerUnitTests {

    def springBatchUiServiceMock

    @Before
    void setUp() {
        springBatchUiServiceMock = mockFor(SpringBatchUiService)
    }

    @Test
    void testList() {
        springBatchUiServiceMock.demand.getJobExecutionUiModel(1..1) {String name, Long id, Map params ->
            assert "jobName" == name
            assert 1 == id
            assert 0 == params.offset
            assert 10 == params.max
            return [modelInstances: [], modelTotal: 0]
        }
        controller.springBatchUiService = springBatchUiServiceMock.createMock()

        def results = controller.list(1, "jobName")

        assert results.modelInstances == []
        assert results.modelTotal == 0

        assert params.max == 10
        assert params.offset == 0
        springBatchUiServiceMock.verify()

    }

    @Test
    void testListWithParams() {
        springBatchUiServiceMock.demand.getJobExecutionUiModel(1..1) {String name, Long id, Map params ->
            assert "jobName" == name
            assert 1 == id
            assert 5 == params.max
            assert 10 == params.offset
            return [modelInstances: [], modelTotal: 0]
        }
        controller.springBatchUiService = springBatchUiServiceMock.createMock()

        params.max = 5
        params.offset = 10
        def results = controller.list(1, "jobName")

        assert results.modelInstances == []
        assert results.modelTotal == 0

        assert params.max == 5
        assert params.offset == 10
        springBatchUiServiceMock.verify()
    }

    @Test
    void testListNoId() {
        controller.list(null, "testJob")

        assert response.redirectUrl.endsWith("/springBatchJob/list")
    }

    @Test
    void testListNullJobName() {
        controller.list(1, null)

        assert response.redirectUrl.endsWith("/springBatchJob/list")
    }

    @Test
    void testListBlankJobName() {
        controller.list(1, "")

        assert response.redirectUrl.endsWith("/springBatchJob/list")
    }
}
