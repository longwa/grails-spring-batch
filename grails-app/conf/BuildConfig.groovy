grails.project.work.dir = 'target'
grails.project.source.level = 1.6

grails.project.dependency.resolution = {

    inherits 'global'
    log 'warn'

    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        runtime('mysql:mysql-connector-java:5.1.22') {
            export = false
        }

        def excludes = {
            excludes 'junit', 'spring-aop', 'spring-core', 'spring-oxm', 'spring-test', 'spring-tx', 'slf4j-log4j12', 'log4j'
        }

        compile 'org.springframework.batch:spring-batch-core:2.1.8.RELEASE',
                'org.springframework.batch:spring-batch-infrastructure:2.1.8.RELEASE',
                'org.springframework.batch:spring-batch-admin-resources:1.2.1.RELEASE',
                'org.springframework.batch:spring-batch-admin-manager:1.2.1.RELEASE',
               excludes

        test 'org.springframework.batch:spring-batch-test:2.1.8.RELEASE', excludes
    }

    plugins {
        build(':release:2.1.0', ':rest-client-builder:1.0.2') {
            export = false
        }

        compile ":platform-core:1.0.RC2"
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
