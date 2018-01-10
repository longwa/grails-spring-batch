package grails.plugins.springbatch.ui

import spock.lang.Specification

class SpringBatchUiUtilitiesUnitTests extends Specification {

    void testGetDurationWithEnd() {
        when:
        def start = new Date(1000)
        def end = new Date(5000)

        then:
        SpringBatchUiUtilities.getDuration(start, end) == 4000
    }

    void testGetDurationToNow() {
        when:
        def start = new Date()
        Thread.sleep(10)

        then:
        10 <= SpringBatchUiUtilities.getDuration(start, null)
    }

    void testGetDurationNullStart() {
        when:
        SpringBatchUiUtilities.getDuration(null, null)

        then:
        thrown(IllegalArgumentException)
    }

    void testPaginate() {
        when:
        def pagedList = SpringBatchUiUtilities.paginate(1, 2) {
            return (1..5)
        }

        then:
        pagedList
        2 == pagedList.size()
        [2,3] == pagedList
    }

    void testPaginate_MaxLimitedToListSize() {
        when:
        def pagedList = SpringBatchUiUtilities.paginate(0, 6) {
            return (1..5)
        }

        then:
        pagedList
        5 == pagedList.size()
        (1..5) == pagedList
    }

    void testPaginate_MaxLimitedToListSize2() {
        when:
        def pagedList = SpringBatchUiUtilities.paginate(1, 6) {
            return (1..5)
        }

        then:
        pagedList
        4 == pagedList.size()
        (2..5) == pagedList
    }

    void testPaginate_OffsetLimitedToListSize() {
        when:
        def pagedList = SpringBatchUiUtilities.paginate(6, 1) {
            return (1..5)
        }

        then:
        !pagedList
        0 == pagedList.size()
        [] == pagedList
    }

    void testPaginate_NegativeOffset() {
        when:
        def pagedList = SpringBatchUiUtilities.paginate(-1, 1) {
            return (1..5)
        }

        then:
        !pagedList
        0 == pagedList.size()
        [] == pagedList
    }

    void testPaginate_NegativeMax() {
        when:
        def pagedList = SpringBatchUiUtilities.paginate(0, -1) {
            return (1..5)
        }

        then:
        !pagedList
        0 == pagedList.size()
        [] == pagedList
    }

    void testPaginate_ZeroMax() {
        when:
        def pagedList = SpringBatchUiUtilities.paginate(0, 0) {
            return (1..5)
        }

        then:
        !pagedList
        0 == pagedList.size()
        [] == pagedList
    }
}
