grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsCentral()
    }
    dependencies {

        runtime 'mysql:mysql-connector-java:5.1.5'

        compile ('org.springframework.batch:spring-batch-core:2.1.8.RELEASE',
                 'org.springframework.batch:spring-batch-infrastructure:2.1.8.RELEASE',
                 'org.springframework.batch:spring-batch-admin-resources:1.2.1.RELEASE',
                 'org.springframework.batch:spring-batch-admin-manager:1.2.1.RELEASE') {
            excludes 'junit',
                    'spring-aop',
                    'spring-core', // Use spring-core from Grails.
                    'spring-oxm',
                    'spring-test',
                    'spring-tx',
                    'slf4j-log4j12',
                    'log4j'
        }

        test ('org.springframework.batch:spring-batch-test:2.1.8.RELEASE') {
            excludes 'junit',
                    'spring-aop',
                    'spring-core', // Use spring-core from Grails.
                    'spring-oxm',
                    'spring-test',
                    'spring-tx',
                    'slf4j-log4j12',
                    'log4j'
        }
    }

    plugins {
        build(":tomcat:$grailsVersion",
              ":release:2.0.3") {
            export = false
        }
        compile ":platform-core:1.0.M6"
        compile(":codenarc:0.17") {
            export = false
        }

        runtime(":hibernate:$grailsVersion") {
            export = false
        }
        test(":code-coverage:1.2.5") {
            export = false
        }
    }
}
