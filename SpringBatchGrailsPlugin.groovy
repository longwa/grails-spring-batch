import grails.plugins.springbatch.job.JobArtefactHandler
import grails.plugins.springbatch.step.StepArtefactHandler
import grails.plugins.springbatch.tasklet.TaskletArtefactHandler
import grails.plugins.springbatch.tasklet.GrailsTaskletClass
import org.springframework.beans.factory.config.MethodInvokingFactoryBean
import grails.plugins.springbatch.step.GrailsStepClass
import grails.plugins.springbatch.job.GrailsJobClass

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
//    def scm = [ url: "http://svn.grails-plugins.codehaus.org/browse/grails-plugins/" ]

    def watchedResources = [
            "file:./grails-app/batch/**/*Job.groovy",
            "file:./plugins/*/grails-app/batch/**/*Job.groovy",
            "file:./grails-app/batch/**/*Step.groovy",
            "file:./plugins/*/grails-app/batch/**/*Step.groovy",
            "file:./grails-app/batch/**/*Tasklet.groovy",
            "file:./plugins/*/grails-app/batch/**/*Tasklet.groovy"
    ]

    def artefacts = [new JobArtefactHandler(), new StepArtefactHandler(), new TaskletArtefactHandler()]

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

        log.debug("Batch Tasklet Classes: ${application.taskletClasses}")
        application.batchTaskletClasses.each {taskletClass ->
            configureTaskletBeans.delegate = delegate
            configureTaskletBeans(taskletClass)
        }
        log.debug("Batch Step Classes: ${application.stepClasses}")
            application.batchStepClasses.each {stepClass ->
            configureStepBeans.delegate = delegate
            configureStepBeans(stepClass)

        }
        log.debug("Batch Job Classes: ${application.batchJobClasses}")
        application.batchJobClasses.each {batchJobClass ->
            configureBatchJobBeans.delegate = delegate
            configureBatchJobBeans(batchJobClass)
        }
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
//        application.stepClasses.each {GrailsStepClass stepClass ->
//            def tasklet = application.getArtefact(TaskletArtefactHandler.TYPE, stepClass.taskletClass.canonicalName)
//            def taskletBean = applicationContext.getBean("${tasklet.propertyName}")
//            def step = applicationContext.getBean("${stepClass.propertyName}")
//            log.info("Wiring tasklet ${taskletBean} to step ${step}")
//            step.setTasklet(taskletBean)
//        }
//        application.batchJobClasses.each {GrailsJobClass jobClass ->
//            def steps = jobClass.steps
//            def job = applicationContext.getBean("${jobClass.propertyName}")
//            steps.each {step ->
//                def stepClass = application.getArtefact(StepArtefactHandler.TYPE, step.canonicalName)
//                def stepBean = applicationContext.getBean("${stepClass.propertyName}")
//                log.info("Wiring step ${stepBean} to job ${job}")
//                job.addStep(stepBean)
//            }
//        }
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

    def configureTaskletBeans = {GrailsTaskletClass taskletClass ->
        def fullName = taskletClass.fullName
        def propertyName = taskletClass.propertyName

        "${fullName}Class"(MethodInvokingFactoryBean) {
            targetObject = ref("grailsApplication", true)
            targetMethod = "getArtefact"
            arguments = [TaskletArtefactHandler.TYPE, fullName]
        }

        "${propertyName}"(ref("${fullName}Class")) {bean ->
            bean.factoryMethod = "newInstance"
            bean.autowire = "byName"
            bean.scope = "prototype"
        }
    }

    def configureStepBeans = {GrailsStepClass stepClass ->
        def fullName = stepClass.fullName
        def propertyName = stepClass.propertyName

        def taskletClass = stepClass.getTaskletClass();

        "${fullName}Class"(MethodInvokingFactoryBean) {
            targetObject = ref("grailsApplication", true)
            targetMethod = "getArtefact"
            arguments = [StepArtefactHandler.TYPE, fullName]
        }

        "${propertyName}"(ref("${fullName}Class")) {bean ->
            bean.factoryMethod = "newInstance"
            bean.autowire = "byName"
            bean.scope = "prototype"
            jobRepository = ref("jobRepository")
            transactionManager = ref("transactionManager")
            tasklet = ref(GrailsNameUtils.getPropertyName(taskletClass))
        }
    }

    def configureBatchJobBeans = {GrailsJobClass jobClass ->
        def fullName = jobClass.fullName
        def propertyName = jobClass.propertyName

        def stepClasses = jobClass.steps

        "${fullName}Class"(MethodInvokingFactoryBean) {
            targetObject = ref("grailsApplication", true)
            targetMethod = "getArtefact"
            arguments = [JobArtefactHandler.TYPE, fullName]
        }

        "${propertyName}"(ref("${fullName}Class")) {bean ->
            bean.factoryMethod = "newInstance"
            bean.autowire = "byName"
            bean.scope = "prototype"
            jobRepository = ref("jobRepository")
            steps = stepClasses.collect { ref(GrailsNameUtils.getPropertyName(it)) }
        }
    }
}
