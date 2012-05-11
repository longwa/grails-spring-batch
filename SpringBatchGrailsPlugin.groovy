import org.springframework.beans.factory.config.MethodInvokingFactoryBean
import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.commons.GrailsClass
import org.springframework.jmx.export.MBeanExporter
import org.springframework.aop.framework.ProxyFactoryBean
import org.springframework.jmx.export.assembler.InterfaceBasedMBeanInfoAssembler
import org.springframework.batch.core.launch.support.SimpleJobOperator
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean
import org.springframework.batch.core.launch.support.SimpleJobLauncher
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean
import org.springframework.batch.core.configuration.support.MapJobRegistry
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor
import org.springframework.remoting.rmi.RmiRegistryFactoryBean
import org.springframework.jmx.support.ConnectorServerFactoryBean
import org.springframework.batch.core.launch.JobOperator
import groovy.sql.Sql

class SpringBatchGrailsPlugin {
    // the plugin version
    def version = "v0.2.3"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Grails Spring Batch Plugin" // Headline display name of the plugin
    def author = "John Engelman"
    def authorEmail = "john.r.engelman@gmail.com"
    def description = '''\
Adds the Spring Batch framework to application. Allows for job configuration using Spring Bean DSL. See documentation at https://github.com/johnrengelman/grails-spring-batch for details.
'''

    // URL to the plugin's documentation
    def documentation = "https://github.com/johnrengelman/grails-spring-batch"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
    def issueManagement = [ system: "JIRA", url: "https://github.com/johnrengelman/grails-spring-batch/issues" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/johnrengelman/grails-spring-batch" ]

    def watchedResources = [
        "file:./grails-app/batch/**/*BatchConfig.groovy",
        "file:./plugins/*/grails-app/batch/**/*BatchConfig.groovy",
    ]

    //From Platform Core
    def doWithConfigOptions = {
        //TODO this only gets exposed in artefacts
        'jmx.enable'(type: Boolean, defaultValue: false)
        'jmx.remote.enable'(type: Boolean, defaultValue: false)
        'jmx.remote.rmi.port'(type: Integer, defaultValue: 1099)
        'dataSource'(type: String, defaultValue: "dataSource")
        'tablePrefix'(type: String, defaultValue: "BATCH")
        'loadTables'(type: Boolean, defaultValue: false)
        'database'(type: String)
    }

    //From Platform Core
    def doWithConfig = { config ->

    }

    def doWithWebDescriptor = { xml ->

    }

    def doWithSpring = {
        def conf = application.config.plugin.springBatch

        def tablePrefix = conf.tablePrefix ?: "BATCH" //TODO can I get the default values from doWithConfigOptions?
        def dataSourceBean = conf.dataSource ?: "dataSource" //TODO can I get the default values from doWithConfigOptions?
        def loadRequired = loadRequiredSpringBatchBeans.clone()
        loadRequired.delegate = delegate
        loadRequired.call(dataSourceBean, tablePrefix)

        def loadConfig = loadBatchConfig.clone()
        loadConfig.delegate = delegate
        loadConfig.call()

        def loadJmx = conf.jmx.enable ?: false //TODO can I get the default values from doWithConfigOptions?
        def loadRemoteJmx = conf.jmx.remote.enable ?: false //TODO can I get the default values from doWithConfigOptions?

        if(loadJmx) {
            def loadJmxClosure = loadSpringBatchJmx.clone()
            loadJmxClosure.delegate = delegate
            loadJmxClosure.call()
        }
        if(loadRemoteJmx) {
            def jmxRemoteRmiPort = conf.jmx.remote.rmi.port ?: 1099 //TODO can I get the default values from doWithConfigOptions?
            def loadRemoteJmxClosure = loadSpringBatchRemoteJmx.clone()
            loadRemoteJmxClosure.delegate = delegate
            loadRemoteJmxClosure.call(jmxRemoteRmiPort)
        }
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        def conf = application.config.plugin.springBatch
        String dataSourceName = conf.dataSource ?: "dataSource" //TODO can I get the default values from doWithConfigOptions?
        def database = conf.database
        def loadTables = conf.loadTables
        if(loadTables) {
            if(database) {
                def ds = applicationContext.getBean(dataSourceName)
                def sql = new Sql(ds)
                def schemaScript = "org/springframework/batch/core/schema-${database}.sql"
                def script = applicationContext.classLoader.getResourceAsStream(schemaScript).text
                sql.execute(script)
                sql.commit()
                sql.close()
            } else {
                log.error("Must specify plugin.springBatch.database variable if plugin.springBatch.loadTables = true")
                throw new RuntimeException("Must specify plugin.springBatch.database variable if plugin.springBatch.loadTables = true")
            }
        }
    }

    def onChange = { event ->
        //TODO need to figure out how to reload beans from the update config file...the event has the compiled class
    }

    def onConfigChange = { event ->

    }

    def loadBatchConfig = { ->
        loadBeans 'classpath*:/batch/*BatchConfig.groovy'
    }

    def loadRequiredSpringBatchBeans = { def dataSourceBean, def tablePrefixValue ->
        jobRepository(JobRepositoryFactoryBean) {
            dataSource = ref(dataSourceBean)
            transactionManager = ref("transactionManager")
            isolationLevelForCreate: "SERIALIZABLE"
            tablePrefix: "${tablePrefixValue}_".toString()
        }
        jobLauncher(SimpleJobLauncher){
            jobRepository = ref("jobRepository")
            taskExecutor = { SimpleAsyncTaskExecutor executor -> }
        }
        jobExplorer(JobExplorerFactoryBean) {
            dataSource = ref(dataSourceBean)
        }
        jobRegistry(MapJobRegistry) { }
        jobRegistryPostProcessor(JobRegistryBeanPostProcessor) {
            jobRegistry = ref("jobRegistry")
        }

        jobOperator(SimpleJobOperator) {
            jobRepository = ref("jobRepository")
            jobLauncher = ref("jobLauncher")
            jobRegistry = ref("jobRegistry")
            jobExplorer = ref("jobExplorer")
        }
    }

    def loadSpringBatchJmx = { ->

        springBatchExporter(MBeanExporter) {bean ->
            beans = [
                //TODO GRAILS-6557
//                "spring:service=batch,bean=jobOperator": {ProxyFactoryBean proxyFactoryBean ->
//                    target = ref("jobOperator")
//                    interceptorNames = "exceptionTranslator"
//                }
                "spring:service=batch,bean=jobOperator": ref("jobOperator")
            ]
            assembler = {InterfaceBasedMBeanInfoAssembler interfaceBasedMBeanInfoAssembler ->
                interfaceMappings = [
                    "spring:service=batch,bean=jobOperator": "org.springframework.batch.core.launch.JobOperator"
                ]
            }
        }
    }

    def loadSpringBatchRemoteJmx = {sbRmiPort ->
        sbRmiRegistry(RmiRegistryFactoryBean) {
            port = sbRmiPort
        }
        sbRmiServerConnector(ConnectorServerFactoryBean) {
            objectName = "connector:name=rmi"
            serviceUrl = "service:jmx:rmi://localhost/jndi/rmi://localhost:$sbRmiPort/springBatch"
            threaded = "true"
        }
    }

}
