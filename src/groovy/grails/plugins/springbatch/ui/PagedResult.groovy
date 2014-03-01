package grails.plugins.springbatch.ui

import groovy.transform.Immutable

@Immutable
class PagedResult<T> {

	int resultsTotalCount = 0
	@Delegate Collection<T> results
}
