package grails.plugins.springbatch

import org.codehaus.groovy.grails.commons.spring.GrailsApplicationContext
import org.junit.Test
import org.springframework.context.ConfigurableApplicationContext

class ReloadApplicationContextFactoryTests {

    @Test(expected=AssertionError)
    void construct() {
        new ReloadApplicationContextFactory(null)
    }

    @Test
    void createApplicationContext() {
        def appContextMock = new GrailsApplicationContext()
        def factory = new ReloadApplicationContextFactory(appContextMock)
        ConfigurableApplicationContext configAppContext = factory.createApplicationContext()
        assert configAppContext == appContextMock
    }
}
