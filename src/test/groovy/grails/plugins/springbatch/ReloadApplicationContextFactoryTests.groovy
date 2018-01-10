package grails.plugins.springbatch

import org.grails.spring.GrailsApplicationContext
import org.springframework.context.ConfigurableApplicationContext
import spock.lang.Specification

class ReloadApplicationContextFactoryTests extends Specification {
    void construct() {
        when:
        new ReloadApplicationContextFactory(null)

        then:
        thrown(AssertionError)
    }

    void createApplicationContext() {
        def appContextMock = new GrailsApplicationContext()
        def factory = new ReloadApplicationContextFactory(appContextMock)

        when:
        ConfigurableApplicationContext configAppContext = factory.createApplicationContext()

        then:
        assert configAppContext == appContextMock
    }
}
