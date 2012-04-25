package springBatch

import org.junit.Test

class SpringBatchBeansLoadedTests extends GroovyTestCase {

    def grailsApplication

    @Test
    public void testRequiredBeansLoaded() {
        def requiredBeans = ['jobLauncher', 'jobRepository', 'jobExplorer']
        requiredBeans.each {bean ->
            assert grailsApplication.mainContext.getBean(bean)
        }
    }

    @Test
    public void testJobConfigurationLoaded() {
        def jobBeans = ['simpleJob', 'logStart', 'printStartMessage']
        jobBeans.each {bean ->
            assert grailsApplication.mainContext.getBean(bean)
        }
    }
}
