package grails.plugins.springbatch.ui

class SpringBatchUiUtilities {

    public static long getDuration(Date start, Date end) {
        if(!start) {
            throw new IllegalArgumentException("Must provide start")
        }
        return end?.time ? end.time - start.time : new Date().time - start.time
    }

    public static List paginate(int offset, int max, Closure c) {
        def list = c.call()
        list.metaClass.mixin(PaginateableList)
        return list.paginate(max, offset)
    }
}
