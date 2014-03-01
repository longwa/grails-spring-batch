package grails.plugins.springbatch.ui

import grails.plugins.springbatch.model.JobModel;
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin

import org.junit.Test
import org.springframework.batch.admin.service.JobService

@TestMixin(GrailsUnitTestMixin)
class JobModelUnitTests {

    @Test
    void testFromService() {
        def jobServiceMock = mockFor(JobService)
        jobServiceMock.demand.countJobExecutionsForJob(1..1) {String name ->
            return 5
        }
        jobServiceMock.demand.isLaunchable(1..1) {String name ->
            return true
        }
        jobServiceMock.demand.isIncrementable(1..1) {String name ->
            return false
        }

        JobModel model = JobModel.fromService(jobServiceMock.createMock() as JobService, "testJob")

        assert model
        assert "testJob" == model.name
        assert 5 == model.executionCount
        assert model.launchable
        assert !model.incrementable

        jobServiceMock.verify()
    }
}
