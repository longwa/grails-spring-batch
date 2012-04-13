import grails.plugins.springbatch.job.JobArtefactHandler
import grails.plugins.springbatch.step.StepArtefactHandler
import grails.plugins.springbatch.tasklet.TaskletArtefactHandler
import grails.plugins.springbatch.tasklet.GrailsTaskletClass
import org.springframework.beans.factory.config.MethodInvokingFactoryBean
import grails.plugins.springbatch.step.GrailsStepClass
import grails.plugins.springbatch.job.GrailsJobClass
import grails.util.GrailsNameUtils
import org.springframework.batch.core.job.SimpleJob
import org.springframework.batch.core.step.tasklet.TaskletStep
import grails.plugins.springbatch.validator.GrailsValidatorClass
import grails.plugins.springbatch.validator.ValidatorArtefactHandler
import grails.plugins.springbatch.incrementor.IncrementorArtefactHandler
import org.codehaus.groovy.grails.commons.ArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsClass
import grails.plugins.springbatch.listener.JobListenerArtefactHandler

class SpringBatchGrailsPlugin {
    // the plugin version
    def version = "0.1"
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
Provides the Spring Batch framework and convention based Jobs.
'''

    // URL to the plugin's documentation
    def documentation = "http://johnrengelman.github.com/grails-spring-batch"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/johnrengelman/grails-spring-batch" ]

    def watchedResources = [
        "file:./grails-app/batch/**/*Job.groovy",
        "file:./plugins/*/grails-app/batch/**/*Job.groovy",
        "file:./grails-app/batch/**/*Step.groovy",
        "file:./plugins/*/grails-app/batch/**/*Step.groovy",
        "file:./grails-app/batch/**/*Tasklet.groovy",
        "file:./plugins/*/grails-app/batch/**/*Tasklet.groovy"
    ]

    def artefacts = [
        new JobArtefactHandler(), new StepArtefactHandler(), new TaskletArtefactHandler(),
        new ValidatorArtefactHandler(), new IncrementorArtefactHandler(), new JobListenerArtefactHandler()
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

        log.debug("Batch Job Listener Classes: ${application.batchJobListenerClasses}")
        application.batchJobListenerClasses.each {jobListenerClass ->
            configureBeans.delegate = delegate
            configureBeans(jobListenerClass, JobListenerArtefactHandler.TYPE)
        }

        log.debug("Batch Incrementor Classes: ${application.batchIncrementorClasses}")
        application.batchValidatorClasses.each {incrementorClass ->
            configureBeans.delegate = delegate
            configureBeans(incrementorClass, IncrementorArtefactHandler.TYPE)
        }

        log.debug("Batch Validator Classes: ${application.batchValidatorClasses}")
        application.batchValidatorClasses.each {validatorClass ->
            configureBeans.delegate = delegate
            configureBeans(validatorClass, ValidatorArtefactHandler.TYPE)
        }

        log.debug("Batch Tasklet Classes: ${application.batchTaskletClasses}")
        application.batchTaskletClasses.each {taskletClass ->
            configureBeans.delegate = delegate
            configureBeans(taskletClass, TaskletArtefactHandler.TYPE)
        }
        log.debug("Batch Step Classes: ${application.batchStepClasses}")
            application.batchStepClasses.each {stepClass ->
            configureBatchSteps.delegate = delegate
            configureBatchSteps(stepClass)

        }
        log.debug("Batch Job Classes: ${application.batchJobClasses}")
        application.batchJobClasses.each {batchJobClass ->
            configureBatchJobs.delegate = delegate
            configureBatchJobs(batchJobClass)
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

    def configureBeans = {GrailsClass clazz, String type ->
        def fullName = clazz.fullName
        def propertyName = clazz.propertyName

        "${fullName}Class"(MethodInvokingFactoryBean) {
            targetObject = ref("grailsApplication", true)
            targetMethod = "getArtefact"
            arguments = [type, fullName]
        }

        "${propertyName}"(ref("${fullName}Class")) {bean ->
            bean.factoryMethod = "newInstance"
            bean.autowire = "byName"
            bean.scope = "prototype"
        }
    }

    def configureBatchSteps = {GrailsStepClass stepClass ->
        def propertyName = stepClass.propertyName

        def taskletClass = stepClass.getTaskletClass();

        "${propertyName}"(TaskletStep) {bean ->
            bean.autowire = "byName"
            bean.scope = "prototype"
            jobRepository = ref("jobRepository")
            transactionManager = ref("transactionManager")
            tasklet = ref(GrailsNameUtils.getPropertyName(taskletClass))
        }
    }

    def configureBatchJobs = {GrailsJobClass jobClass ->
        def propertyName = jobClass.propertyName

        def stepClasses = jobClass.steps
        def listenerClasses = jobClass.listeners

        def jobValidator = jobClass.getValidator() ? ref(GrailsNameUtils.getPropertyName(jobClass.getValidator())) : null
        def jobIncrementor = jobClass.getIncrementor() ? ref(GrailsNameUtils.getPropertyName(jobClass.getIncrementor())) : null

        "${propertyName}"(SimpleJob) {bean ->
            bean.autowire = "byName"
            bean.scope = "prototype"
            jobRepository = ref("jobRepository")
            steps = stepClasses.collect { ref(GrailsNameUtils.getPropertyName(it)) }
            restartable = jobClass.isRestartable()
            jobParametersValidator = jobValidator
            jobParametersIncrementer = jobIncrementor
            jobExecutionListeners = listenerClasses.collect { ref(GrailsNameUtils.getPropertyName(it)) }
        }
    }
}
