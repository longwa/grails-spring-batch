package grails.plugins.springbatch

import grails.plugins.Plugin
import grails.plugins.springbatch.springbatchadmin.patch.PatchedSimpleJobServiceFactoryBean
import grails.spring.BeanBuilder
import groovy.sql.Sql
import groovy.util.logging.Slf4j
import org.grails.spring.DefaultRuntimeSpringConfiguration
import org.springframework.batch.core.configuration.support.JobLoader
import org.springframework.batch.core.scope.StepScope
import org.springframework.batch.core.scope.JobScope
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.core.io.FileSystemResource

import javax.sql.DataSource
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

@Slf4j
@SuppressWarnings("GroovyUnusedDeclaration")
class SpringBatchGrailsPlugin extends Plugin {
    def grailsVersion = "3.0.0 > *"

    def title = 'Grails Spring Batch Plugin'
    def description = 'Adds spring batch support including job configuration using Spring Bean DSL and configuration reloading'
    def license = "APACHE"
    def author = "John Engelman"
    def authorEmail = "john.r.engelman@gmail.com"

    def developers = [
        [name: "Daniel Bower", email: "daniel.bower@infinum.com"],
        [name: "Aaron Long", email: "longwa@gmail.com"],
    ]
    def issueManagement = [system: 'github', url: 'https://github.com/longwa/grails-spring-batch/issues']
    def scm = [url: 'https://github.com/longwa/grails-spring-batch']

    def watchedResources = [
        "file:./src/main/resources/batch/**/*BatchConfig.groovy",
    ]

    @Override
    Closure doWithSpring() {
        { ->
            // Database is required
            if (!config.plugin.springBatch.database) {
                log.warn "Configuration for 'plugin.springBatch.database' missing, using H2 by default..."
                config.plugin.springBatch.database = 'h2'
            }

            // Load the spring batch beans
            Closure loadRequired = loadRequiredSpringBatchBeans.clone() as Closure
            loadRequired.delegate = delegate
            loadRequired.call(config)

            Closure loadConfig = loadBatchConfig.clone() as Closure
            loadConfig.delegate = delegate
            xmlns(batch: "http://www.springframework.org/schema/batch")
            loadConfig.call()

            def loadJmx = config.plugin.springBatch.jmx.enable
            def loadRemoteJmx = config.plugin.springBatch.jmx.remote.enable

            if (loadJmx) {
                String jmxExportName = config.plugin.springBatch.jmx.name ?: 'jobOperator'
                Closure loadJmxClosure = loadSpringBatchJmx.clone() as Closure
                loadJmxClosure.delegate = delegate
                loadJmxClosure.call(jmxExportName)
            }

            if (loadRemoteJmx) {
                Integer jmxRemoteRmiPort = config.plugin.springBatch.jmx.remote.rmi.port ?: 1099
                String jmxRemoteExportName = config.plugin.springBatch.jmx.remote.name ?: 'springBatch'
                Closure loadRemoteJmxClosure = loadSpringBatchRemoteJmx.clone() as Closure
                loadRemoteJmxClosure.delegate = delegate
                loadRemoteJmxClosure.call(jmxRemoteRmiPort, jmxRemoteExportName)
            }
        }
    }

    @Override
    void doWithApplicationContext() {
        def conf = config.plugin.springBatch
        String dataSourceName = conf.dataSource ?: 'dataSource'
        def database = conf.database ?: 'h2'

        def loadTables = conf.loadTables
        if (loadTables) {
            if (database) {
                def ds = applicationContext.getBean(dataSourceName, DataSource)
                def sql = new Sql(ds)
                try {
                    sql.withTransaction { Connection conn ->
                        Statement statement = conn.createStatement()

                        String script = "org/springframework/batch/core/schema-drop-${database}.sql"
                        String text = applicationContext.getClassLoader().getResourceAsStream(script).text
                        text.split(";").each { line ->
                            if (line.trim()) {
                                statement.execute(line.trim())
                            }
                        }

                        script = "org/springframework/batch/core/schema-${database}.sql"
                        text = applicationContext.getClassLoader().getResourceAsStream(script).text
                        text.split(";").each { line ->
                            if (line.trim()) {
                                statement.execute(line.trim())
                            }
                        }

                        statement.close()
                        conn.commit()
                    }
                }
                finally {
                    sql.close()
                }
            }
            else {
                log.error("Must specify plugin.springBatch.database variable if plugin.springBatch.loadTables = true")
                throw new RuntimeException("Must specify plugin.springBatch.database variable if plugin.springBatch.loadTables = true")
            }
        }
    }

    @Override
    void onChange(Map<String, Object> event) {
        FileSystemResource fileSystemResource = event.source as FileSystemResource

        def springConfig = new DefaultRuntimeSpringConfiguration()
        def bb = new BeanBuilder(applicationContext, springConfig, grailsApplication.classLoader)
        bb.importBeans(fileSystemResource)

        // Setup step scope proxies
        def stepScope = new StepScope()
        def jobScope = new JobScope()

        ConfigurableListableBeanFactory beanFactory = event.ctx.getBeanFactory()
        stepScope.postProcessBeanFactory(beanFactory)
        jobScope.postProcessBeanFactory(beanFactory)
        bb.registerBeans(applicationContext as BeanDefinitionRegistry)

        // This forces the job loader to reload the beans defined in the file that changed
        // This will probably actually reload all spring batch jobs
        JobLoader jobLoader = new DefaultJobLoader(event.ctx.jobRegistry)
        jobLoader.reload(new ReloadApplicationContextFactory(event.ctx))
    }

    def loadBatchConfig = { ->
        loadBeans "classpath:batch/**/*BatchConfig.groovy"
    }

    def loadRequiredSpringBatchBeans = { config ->
        def conf = config.plugin.springBatch

        String dbType = conf.database ?: "h2"
        String tablePrefixVal = conf.tablePrefix ? (conf.tablePrefix + '_') : 'BATCH_'
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
        jobLauncher(SimpleJobLauncher) {
            jobRepository = ref("jobRepository")
            taskExecutor = { SimpleAsyncTaskExecutor executor -> }
        }

        /*
         * Additional Job Launcher to support synchronous scheduling
         */
        syncJobLauncher(SimpleJobLauncher) {
            jobRepository = ref("jobRepository")
            taskExecutor = { SyncTaskExecutor executor -> }
        }

        jobExplorer(JobExplorerFactoryBean) {
            dataSource = ref(dataSourceBean)
            tablePrefix = tablePrefixVal
        }

        jobRegistry(MapJobRegistry) {}

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
        springBatchExporter(MBeanExporter) { bean ->
            beans = [
                (serviceName): ref("jobOperator")
            ]
            assembler = { InterfaceBasedMBeanInfoAssembler interfaceBasedMBeanInfoAssembler ->
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
