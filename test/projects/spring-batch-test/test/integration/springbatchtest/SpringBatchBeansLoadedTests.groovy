package springbatchtest

import org.junit.Test
import groovy.sql.Sql
import org.springframework.batch.core.Job

class SpringBatchBeansLoadedTests extends GroovyTestCase {

    def grailsApplication
    def dataSource

    @Test
    public void testRequiredBeansLoaded() {
        def requiredBeans = ['jobRepository', 'jobOperator', 'jobLauncher', 'jobExplorer', 'jobRegistry', 'jobRegistryPostProcessor']
        requiredBeans.each {bean ->
            assert grailsApplication.mainContext.getBean(bean)
        }
    }

    @Test
    public void testJobConfigurationLoaded() {
        def jobBeans = ['simpleJob', 'jobStart', 'printStartMessage']
        jobBeans.each {bean ->
            assert grailsApplication.mainContext.getBean(bean)
        }
        assert grailsApplication.mainContext.getBean('simpleJob') instanceof Job
    }

    @Test
    public void testJmxBeanLoaded() {
        def sbMBean = grailsApplication.mainContext.getBean("springBatchExporter")
        assert sbMBean

        //TODO check that the JobOperator interface is exposed here
    }

    @Test
    public void testSpringBatchTablesCreated() {
        def sql = new Sql(dataSource)
        def tables = ['BATCH_JOB_EXECUTION', 'BATCH_JOB_EXECUTION_CONTEXT', 'BATCH_JOB_INSTANCE', 'BATCH_JOB_PARAMS',
            'BATCH_STEP_EXECUTION', 'BATCH_STEP_EXECUTION_CONTEXT']

        tables.each {table ->
            def query = "select count(*) from $table;".toString()
            assert sql.execute(query)
        }
    }
}
