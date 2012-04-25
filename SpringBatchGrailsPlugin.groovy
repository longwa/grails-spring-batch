import org.springframework.beans.factory.config.MethodInvokingFactoryBean
import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.commons.GrailsClass

class SpringBatchGrailsPlugin {
    // the plugin version
    def version = "0.2"
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
Provides the Spring Batch framework and convention based Jobs. See documentation at https://github.com/johnrengelman/grails-spring-batch for details.
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

    def doWithWebDescriptor = { xml ->

    }

    def doWithSpring = {
        jobRepository(org.springframework.batch.core.repository.support.JobRepositoryFactoryBean) {
            dataSource = ref("dataSource")
            transactionManager = ref("transactionManager")
            isolationLevelForCreate: "SERIALIZABLE"
            tablePrefix: "batch_"
        }

        jobLauncher(org.springframework.batch.core.launch.support.SimpleJobLauncher){
            jobRepository = ref("jobRepository")
            taskExecutor = { org.springframework.core.task.SimpleAsyncTaskExecutor executor -> }
        }
        jobExplorer(org.springframework.batch.core.explore.support.JobExplorerFactoryBean) {
            dataSource = ref("dataSource")
        }

    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->

    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

}