grails.project.source.level = 1.6

springBatchVersion = '3.0.8.RELEASE'
springBatchAdminVersion = '1.3.1.RELEASE'

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.fork = [
    // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
    // compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

    // configure settings for the test-app JVM, uses the daemon by default
    test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
    // configure settings for the run-app JVM
    run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the run-war JVM
    war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the Console UI JVM
    console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {
    inherits 'global'
    log 'warn'

    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        def excludes = {
            excludes 'junit', 'spring-aop', 'spring-core', 'spring-oxm', 'spring-test', 'spring-tx', 'slf4j-log4j12', 'log4j'
        }

        compile "org.springframework.batch:spring-batch-core:${springBatchVersion}",
                "org.springframework.batch:spring-batch-infrastructure:${springBatchVersion}",
                "org.springframework.batch:spring-batch-admin-resources:${springBatchAdminVersion}",
                "org.springframework.batch:spring-batch-admin-manager:${springBatchAdminVersion}",
               excludes

        test "org.springframework.batch:spring-batch-test:${springBatchVersion}", excludes

        test "org.grails:grails-datastore-test-support:1.0.2-grails-2.4"
    }

    plugins {
        build(":release:3.1.2", ":rest-client-builder:2.1.1") {
            export = false
        }

        runtime(":hibernate4:4.3.10") {
            export = false
        }
    }
}
