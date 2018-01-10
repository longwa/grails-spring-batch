package grails.plugins.springbatch.ui

class DurationTagLibTest {
	void testDurationPrintBasic(){
		assert applyTemplate('<batch:durationPrint duration="30"/>') == '0:0:0.030'
	}

}
