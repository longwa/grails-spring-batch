package grails.plugins.springbatch

import org.springframework.batch.core.configuration.support.ApplicationContextFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ConfigurableApplicationContext

class ReloadApplicationContextFactory implements ApplicationContextFactory {

    ApplicationContext applicationContext

    ReloadApplicationContextFactory(ApplicationContext applicationContext) {
        assert applicationContext
        this.applicationContext = applicationContext
    }

    ConfigurableApplicationContext createApplicationContext() {
        return applicationContext
    }
}
