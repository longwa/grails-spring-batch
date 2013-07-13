grails.project.work.dir = 'target'
grails.project.source.level = 1.6

springBatchVersion = '2.1.9.RELEASE'
springBatchAdminVersion = '1.2.2.RELEASE'

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

        compile "org.springframework.batch:spring-batch-core:${springBatchVersion}",
                "org.springframework.batch:spring-batch-infrastructure:${springBatchVersion}",
                "org.springframework.batch:spring-batch-admin-resources:${springBatchAdminVersion}",
                "org.springframework.batch:spring-batch-admin-manager:${springBatchAdminVersion}",
               excludes

        test "org.springframework.batch:spring-batch-test:${springBatchVersion}", excludes
    }

    plugins {
        build(':release:2.2.1', ':rest-client-builder:1.0.2') {
            export = false
        }

        compile ":platform-core:1.0.RC5"
        compile(":codenarc:0.18.1") {
            export = false
        }

        runtime(":hibernate:$grailsVersion") {
            export = false
        }
        test(":code-coverage:1.2.6") {
            export = false
        }
    }
}
