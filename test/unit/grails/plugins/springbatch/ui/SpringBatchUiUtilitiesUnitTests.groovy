package grails.plugins.springbatch.ui

import org.junit.Test

class SpringBatchUiUtilitiesUnitTests {

    @Test
    public void testGetDurationWithEnd() {
        def start = new Date(1000)
        def end = new Date(5000)

        assert 4000 == SpringBatchUiUtilities.getDuration(start, end)
    }

    @Test
    public void testGetDurationToNow() {
        def start = new Date()
        Thread.sleep(10)
        assert 10 <= SpringBatchUiUtilities.getDuration(start, null)
    }

    @Test(expected=IllegalArgumentException)
    public void testGetDurationNullStart() {
        SpringBatchUiUtilities.getDuration(null, null)
    }

    @Test
    public void testPaginate() {
        def pagedList = SpringBatchUiUtilities.paginate(1, 2) {
            return (1..5)
        }

        assert pagedList
        assert 2 == pagedList.size()
        assert [2,3] == pagedList
    }
}
