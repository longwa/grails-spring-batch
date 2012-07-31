package grails.plugins.springbatch.ui

class SpringBatchUiUtilities {

    public static long getDuration(Date start, Date end) {
        if(!start) {
            throw new IllegalArgumentException("Must provide start")
        }
        return end?.time ? end.time - start.time : new Date().time - start.time
    }

    public static List paginate(int offset, int max, Closure c) {
        def list = c.call() as List
        return paginateInternal(list, max, offset)
    }

    private static List paginateInternal(List list, int max, int offset=0 ) {
        ((max as Integer) <= 0 || (offset as Integer) < 0) ? [] : list.subList( Math.min( offset as Integer, list.size() ), Math.min( (offset as Integer) + (max as Integer), list.size() ) )
    }
}
