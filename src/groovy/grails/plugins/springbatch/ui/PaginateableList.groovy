package grails.plugins.springbatch.ui

@Category(List)
class PaginateableList {
    List paginate(max, offset=0 ) {
        ((max as Integer) <= 0 || (offset as Integer) < 0) ? [] : this.subList( Math.min( offset as Integer, this.size() ), Math.min( (offset as Integer) + (max as Integer), this.size() ) )
    }
}
