package grails.plugins.springbatch.ui

import grails.test.mixin.TestFor
import org.junit.Before
import org.junit.Test

@TestFor(SpringBatchJobInstanceController)
class SpringBatchJobInstanceControllerUnitTests {

    def springBatchUiServiceMock

    @Before
    public void setUp() {
        springBatchUiServiceMock = mockFor(SpringBatchUiService)
    }

    @Test
    public void testIndex() {
        controller.index()

        assert response.redirectUrl.endsWith("/springBatchJobInstance/list")
    }

    @Test
    public void testList() {
        springBatchUiServiceMock.demand.getJobInstanceUiModel(1..1) {String id, Map params ->
            assert "testJob" == id
            assert 0 == params.offset
            assert 10 == params.max
            return [modelInstances: [], modelTotal: 0]
        }
        controller.springBatchUiService = springBatchUiServiceMock.createMock()

        def results = controller.list("testJob")

        assert results.modelInstances == []
        assert results.modelTotal == 0

        assert params.max == 10
        assert params.offset == 0
        springBatchUiServiceMock.verify()

    }

    @Test
    public void testListWithParams() {
        springBatchUiServiceMock.demand.getJobInstanceUiModel(1..1) {String id, Map params ->
            assert "testJob" == id
            assert 5 == params.max
            assert 10 == params.offset
            return [modelInstances: [], modelTotal: 0]
        }
        controller.springBatchUiService = springBatchUiServiceMock.createMock()

        params.max = 5
        params.offset = 10
        def results = controller.list("testJob")

        assert results.modelInstances == []
        assert results.modelTotal == 0

        assert params.max == 5
        assert params.offset == 10
        springBatchUiServiceMock.verify()
    }

    @Test
    public void testListNullId() {
        controller.list(null)

        assert response.redirectUrl.endsWith("/springBatchJob/index")
    }

    @Test
    public void testListBlankId() {
        controller.list("")

        assert response.redirectUrl.endsWith("/springBatchJob/index")
    }
}
