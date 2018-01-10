package grails.plugins.springbatch.ui

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class DurationTagLibTest extends Specification implements TagLibUnitTest<DurationTagLib> {
	void testDurationPrintBasic(){
		expect:
		applyTemplate('<batch:durationPrint duration="30"/>') == '0:0:0.030'
	}
}
