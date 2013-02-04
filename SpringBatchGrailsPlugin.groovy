import grails.plugins.springbatch.ReloadApplicationContextFactory
import grails.plugins.springbatch.ReloadableJobRegistryBeanPostProcessor
import org.springframework.batch.core.configuration.support.DefaultJobLoader
import org.springframework.jmx.export.MBeanExporter
import org.springframework.jmx.export.assembler.InterfaceBasedMBeanInfoAssembler
import org.springframework.batch.core.launch.support.SimpleJobOperator
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean
import org.springframework.batch.core.launch.support.SimpleJobLauncher
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean
import org.springframework.batch.core.configuration.support.MapJobRegistry
import org.springframework.remoting.rmi.RmiRegistryFactoryBean
import org.springframework.jmx.support.ConnectorServerFactoryBean
import groovy.sql.Sql
import org.springframework.batch.admin.service.SimpleJobServiceFactoryBean

class SpringBatchGrailsPlugin {
    def version = "1.0.RC2"
    def grailsVersion = "2.0 > *"
    def title = "Grails Spring Batch Plugin"
    def author = "John Engelman"
    def authorEmail = "john.r.engelman@gmail.com"
    def description = 'Adds the Spring Batch framework to application. Allows for job configuration using Spring Bean DSL. See documentation at https://github.com/johnrengelman/grails-spring-batch for details.'

    def documentation = "https://github.com/johnrengelman/grails-spring-batch"
    def license = "APACHE"
    def issueManagement = [ system: "JIRA", url: "https://github.com/johnrengelman/grails-spring-batch/issues" ]
    def scm = [ url: "https://github.com/johnrengelman/grails-spring-batch" ]

    def watchedResources = [
        "file:./grails-app/batch/**/*BatchConfig.groovy",
        "file:./plugins/*/grails-app/batch/**/*BatchConfig.groovy",
    ]

    //From Platform Core
    def doWithConfigOptions = {
        'jmx.enable'(type: Boolean, defaultValue: false)
        'jmx.name'(type: String, defaultValue: 'jobOperator')
        'jmx.remote.enable'(type: Boolean, defaultValue: false)
        'jmx.remote.rmi.port'(type: Integer, defaultValue: 1099)
        'jmx.remote.name'(type: String, defaultValue: 'springBatch')
        'dataSource'(type: String, defaultValue: "dataSource")
        'tablePrefix'(type: String, defaultValue: "BATCH")
        'loadTables'(type: Boolean, defaultValue: false)
        'database'(type: String)
    }

    def doWithSpring = {
        def conf = application.config.plugin.springBatch

        def tablePrefix = conf.tablePrefix
        def dataSourceBean = conf.dataSource
        def loadRequired = loadRequiredSpringBatchBeans.clone()
        loadRequired.delegate = delegate
        loadRequired(dataSourceBean, tablePrefix)

        def loadConfig = loadBatchConfig.clone()
        loadConfig.delegate = delegate
        loadConfig()

        def loadJmx = conf.jmx.enable
        def loadRemoteJmx = conf.jmx.remote.enable

        if(loadJmx) {
            def jmxExportName = conf.jmx.name
            def loadJmxClosure = loadSpringBatchJmx.clone()
            loadJmxClosure.delegate = delegate
            loadJmxClosure(jmxExportName)
        }
        if(loadRemoteJmx) {
            def jmxRemoteRmiPort = conf.jmx.remote.rmi.port
            def jmxRemoteExportName = conf.jmx.remote.name
            def loadRemoteJmxClosure = loadSpringBatchRemoteJmx.clone()
            loadRemoteJmxClosure.delegate = delegate
            loadRemoteJmxClosure(jmxRemoteRmiPort, jmxRemoteExportName)
        }
    }

    def doWithApplicationContext = { applicationContext ->
        def conf = application.config.plugin.springBatch
        String dataSourceName = conf.dataSource
        def database = conf.database
        def loadTables = conf.loadTables
        if(loadTables) {
            if(database) {
                def ds = applicationContext.getBean(dataSourceName)
                def sql = new Sql(ds)

                def script = "org/springframework/batch/core/schema-drop-${database}.sql"
                def text = applicationContext.classLoader.getResourceAsStream(script).text
                text.split(";").each { statement ->
                    if(statement.trim()) {
                        sql.execute(statement)
                    }
                }

                script = "org/springframework/batch/core/schema-${database}.sql"
                text = applicationContext.classLoader.getResourceAsStream(script).text
                text.split(";").each { statement ->
                    if(statement.trim()) {
                        sql.execute(statement)
                    }
                }
                sql.commit()
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
            //Get an instance of the changed class file as a script
            Script script = (Script) configClass.newInstance()
            //Create a new script binding so we can assign a delegate to it
            Binding scriptBinding = new Binding()
            //Allows the script file to delegate the beans DSL to the onChangeEvent
            scriptBinding.beans = { Closure closure ->
                delegate.beans(closure)
            }
            script.binding = scriptBinding
            //Execute the script to get the new beans that were defined
            def beans = script.run()
            //Register new beans into the application context
            beans.registerBeans(event.ctx)
            //This forces the job loader to reload the beans defined in the file that changed
            //This will probably actually reload all spring batch jobs
            def jobLoader = new DefaultJobLoader(event.ctx.jobRegistry)
            jobLoader.reload(new ReloadApplicationContextFactory(event.ctx))
        }
    }

    def loadBatchConfig = { ->
        loadBeans 'classpath*:/batch/*BatchConfig.groovy'
    }

    def loadRequiredSpringBatchBeans = { def dataSourceBean, def tablePrefixValue ->
        jobRepository(JobRepositoryFactoryBean) {
            dataSource = ref(dataSourceBean)
            transactionManager = ref("transactionManager")
            isolationLevelForCreate: "SERIALIZABLE"
            tablePrefix: "${tablePrefixValue ? tablePrefixValue + '_' : ''}".toString()
        }
        jobLauncher(SimpleJobLauncher){
            jobRepository = ref("jobRepository")
            taskExecutor = { SimpleAsyncTaskExecutor executor -> }
        }
        jobExplorer(JobExplorerFactoryBean) {
            dataSource = ref(dataSourceBean)
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
        jobService(SimpleJobServiceFactoryBean) {
            jobRepository = ref("jobRepository")
            jobLauncher = ref("jobLauncher")
            jobLocator = ref("jobRegistry")
            dataSource = ref(dataSourceBean)
        }
    }

    def loadSpringBatchJmx = { exportName ->
        def serviceName = "spring:service=batch,bean=${exportName}".toString()
        springBatchExporter(MBeanExporter) {bean ->
            beans = [
                //TODO GRAILS-6557
//                "spring:service=batch,bean=jobOperator": {ProxyFactoryBean proxyFactoryBean ->
//                    target = ref("jobOperator")
//                    interceptorNames = "exceptionTranslator"
//                }
                (serviceName): ref("jobOperator")
            ]
            assembler = {InterfaceBasedMBeanInfoAssembler interfaceBasedMBeanInfoAssembler ->
                interfaceMappings = [
                    (serviceName): "org.springframework.batch.core.launch.JobOperator"
                ]
            }
        }
    }

    def loadSpringBatchRemoteJmx = {sbRmiPort, exportName ->
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
