import grails.plugins.springbatch.springbatchadmin.patch.PatchedSimpleJobServiceFactoryBean
import grails.plugins.springbatch.ReloadApplicationContextFactory
import grails.plugins.springbatch.ReloadableJobRegistryBeanPostProcessor
import groovy.sql.Sql
import org.springframework.batch.core.configuration.support.JobLoader
import org.springframework.batch.core.scope.StepScope
import org.springframework.batch.core.scope.JobScope
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory

import java.sql.Connection
import java.sql.Statement

import org.springframework.batch.core.configuration.support.DefaultJobLoader
import org.springframework.batch.core.configuration.support.MapJobRegistry
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean
import org.springframework.batch.core.launch.support.SimpleJobLauncher
import org.springframework.batch.core.launch.support.SimpleJobOperator
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.core.task.SyncTaskExecutor
import org.springframework.jmx.export.MBeanExporter
import org.springframework.jmx.export.assembler.InterfaceBasedMBeanInfoAssembler
import org.springframework.jmx.support.ConnectorServerFactoryBean
import org.springframework.remoting.rmi.RmiRegistryFactoryBean

import org.springframework.batch.core.repository.dao.AbstractJdbcBatchMetadataDao


class SpringBatchGrailsPlugin {
    def version = "2.5.4"
    def grailsVersion = "2.5 > *"
    def title = "Grails Spring Batch Plugin"
    def author = "John Engelman"
    def authorEmail = "john.r.engelman@gmail.com"
    def description = 'Adds the Spring Batch framework to application. Allows for job configuration using Spring Bean DSL. See documentation at https://github.com/johnrengelman/grails-spring-batch for details.'

    def documentation = "https://github.com/johnrengelman/grails-spring-batch"
    def license = "APACHE"
    def developers = [
        [name: "Daniel Bower", email: "daniel.bower@infinum.com"],
        [name: "Aaron Long",   email: "longwa@gmail.com"],
    ]
    def issueManagement = [ system: "JIRA", url: "https://github.com/johnrengelman/grails-spring-batch/issues" ]
    def scm = [ url: "https://github.com/johnrengelman/grails-spring-batch" ]

    def watchedResources = [
        "file:./grails-app/batch/**/*BatchConfig.groovy",
        "file:./plugins/*/grails-app/batch/**/*BatchConfig.groovy",
    ]

    def doWithSpring = {
        def conf = application.config.plugin.springBatch

        // Database is required
        if (!conf.database) {
            log.warn "'plugin.springBatch.database' not configured, using H2 by default..."
            conf.database = 'h2'
        }

        // Load the spring batch beans
        def loadRequired = loadRequiredSpringBatchBeans.clone()
        loadRequired.delegate = delegate
        loadRequired.call(application.config)

        def loadConfig = loadBatchConfig.clone()
        loadConfig.delegate = delegate
        xmlns(batch:"http://www.springframework.org/schema/batch")
        loadConfig.call()

        def loadJmx = conf.jmx.enable
        def loadRemoteJmx = conf.jmx.remote.enable

        if(loadJmx) {
            def jmxExportName = conf.jmx.name ?: 'jobOperator'
            def loadJmxClosure = loadSpringBatchJmx.clone()
            loadJmxClosure.delegate = delegate
            loadJmxClosure(jmxExportName)
        }
        if(loadRemoteJmx) {
            def jmxRemoteRmiPort = conf.jmx.remote.rmi.port ?: 1099
            def jmxRemoteExportName = conf.jmx.remote.name ?: 'springBatch'
            def loadRemoteJmxClosure = loadSpringBatchRemoteJmx.clone()
            loadRemoteJmxClosure.delegate = delegate
            loadRemoteJmxClosure(jmxRemoteRmiPort, jmxRemoteExportName)
        }
    }

    def doWithApplicationContext = { applicationContext ->
        def conf = application.config.plugin.springBatch
        String dataSourceName = conf.dataSource ?: 'dataSource'
        def database = conf.database ?: 'h2'

        def loadTables = conf.loadTables
        if(loadTables) {
            if(database) {
                def ds = applicationContext.getBean(dataSourceName)
                def sql = new Sql(ds)
                sql.withTransaction { Connection conn ->
                    Statement statement = conn.createStatement()
                    def script = "org/springframework/batch/core/schema-drop-${database}.sql"
                    def text = applicationContext.classLoader.getResourceAsStream(script).text
                    text.split(";").each { line ->
                        if(line.trim()) {
                            statement.execute(line.trim())
                        }
                    }

                    script = "org/springframework/batch/core/schema-${database}.sql"
                    text = applicationContext.classLoader.getResourceAsStream(script).text
                    text.split(";").each { line ->
                        if(line.trim()) {
                            statement.execute(line.trim())
                        }
                    }
                    statement.close()
                    conn.commit()
                }
                sql.close()
            } else {
                log.error("Must specify plugin.springBatch.database variable if plugin.springBatch.loadTables = true")
                throw new RuntimeException("Must specify plugin.springBatch.database variable if plugin.springBatch.loadTables = true")
            }
        }
    }

    def onChange = { event ->
        if(event.source instanceof Class && event.source.name.endsWith("BatchConfig")) {
            Class configClass = event.source

            // Get an instance of the changed class file as a script
            Script script = (Script) configClass.newInstance()

            // Create a new script binding so we can assign a delegate to it
            Binding scriptBinding = new Binding()

            // Allows the script file to delegate the beans DSL to the onChangeEvent
            scriptBinding.beans = { Closure closure -> delegate.beans(closure) }
            script.binding = scriptBinding

            // Execute the script to get the new beans that were defined
            def scriptBeans = script.run()
            scriptBeans.registerBeans(event.ctx)

            // Setup step scope proxies
            def stepScope = new StepScope()
            def jobScope = new JobScope()

            ConfigurableListableBeanFactory beanFactory = event.ctx.getBeanFactory()
            stepScope.postProcessBeanFactory(beanFactory)
            jobScope.postProcessBeanFactory(beanFactory)

            // This forces the job loader to reload the beans defined in the file that changed
            // This will probably actually reload all spring batch jobs
            JobLoader jobLoader = new DefaultJobLoader(event.ctx.jobRegistry)
            jobLoader.reload(new ReloadApplicationContextFactory(event.ctx))
        }
    }

    def loadBatchConfig = { ->
        loadBeans 'classpath*:/batch/*BatchConfig.groovy'
    }

    def loadRequiredSpringBatchBeans = { config ->
        def conf = config.plugin.springBatch

        String dbType = conf.database ?: "h2"
        String tablePrefixVal = conf.tablePrefix ? (conf.tablePrefix + '_' ) : 'BATCH_'
        String dataSourceBean = conf.dataSource ?: 'dataSource'
        Integer maxVarCharLengthVal = conf.maxVarCharLength ?: AbstractJdbcBatchMetadataDao.DEFAULT_EXIT_MESSAGE_LENGTH

        jobRepository(JobRepositoryFactoryBean) {
            dataSource = ref(dataSourceBean)
            transactionManager = ref("transactionManager")
            tablePrefix = tablePrefixVal
            databaseType = dbType
            maxVarCharLength = maxVarCharLengthVal
            isolationLevelForCreate = conf.isolation ?: "ISOLATION_READ_COMMITTED"
        }

        /*
         * Async launcher to use by default
         */
        jobLauncher(SimpleJobLauncher){
            jobRepository = ref("jobRepository")
            taskExecutor = { SimpleAsyncTaskExecutor executor -> }
        }

        /*
         * Additional Job Launcher to support synchronous scheduling
         */
        syncJobLauncher(SimpleJobLauncher){
            jobRepository = ref("jobRepository")
            taskExecutor = { SyncTaskExecutor executor -> }
        }

        jobExplorer(JobExplorerFactoryBean) {
            dataSource = ref(dataSourceBean)
            tablePrefix = tablePrefixVal
        }

        jobRegistry(MapJobRegistry) { }

        //Use a custom bean post processor that will unregister the job bean before trying to initializing it again
        //This could cause some problems if you define a job more than once, you'll probably end up with 1 copy
        //of the last definition processed instead of getting a DuplicateJobException
        //Had to do this to get reloading to work
        jobRegistryPostProcessor(ReloadableJobRegistryBeanPostProcessor) {
            jobRegistry = ref("jobRegistry")
        }

        jobOperator(SimpleJobOperator) {
            jobRepository = ref("jobRepository")
            jobLauncher = ref("jobLauncher")
            jobRegistry = ref("jobRegistry")
            jobExplorer = ref("jobExplorer")
        }
        jobService(PatchedSimpleJobServiceFactoryBean) {
            jobRepository = ref("jobRepository")
            jobLauncher = ref("jobLauncher")
            jobLocator = ref("jobRegistry")
            dataSource = ref(dataSourceBean)
            tablePrefix = tablePrefixVal
        }
    }

    def loadSpringBatchJmx = { exportName ->
        def serviceName = "spring:service=batch,bean=${exportName}".toString()
        springBatchExporter(MBeanExporter) {bean ->
            beans = [
                (serviceName): ref("jobOperator")
            ]
            assembler = {InterfaceBasedMBeanInfoAssembler interfaceBasedMBeanInfoAssembler ->
                interfaceMappings = [
                    (serviceName): "org.springframework.batch.core.launch.JobOperator"
                ]
            }
        }
    }

    def loadSpringBatchRemoteJmx = { sbRmiPort, exportName ->
        sbRmiRegistry(RmiRegistryFactoryBean) {
            port = sbRmiPort
        }
        sbRmiServerConnector(ConnectorServerFactoryBean) {
            objectName = "connector:name=rmi"
            serviceUrl = "service:jmx:rmi://localhost/jndi/rmi://localhost:$sbRmiPort/$exportName".toString()
            threaded = "true"
        }
    }
}
