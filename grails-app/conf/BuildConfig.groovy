grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsCentral()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenCentral()
        //mavenLocal()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.5'

        compile ('org.springframework.batch:spring-batch-core:2.1.8.RELEASE') {
            excludes 'junit',
                    'spring-aop',
                    'spring-core', // Use spring-core from Grails.
                    'spring-oxm',
                    'spring-test',
                    'spring-tx',
                    'slf4j-log4j12',
                    'log4j'
        }

        compile ('org.springframework.batch:spring-batch-infrastructure:2.1.8.RELEASE') {
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
              ":release:2.0.0") {
            export = false
        }
        compile ":platform-core:1.0.M1"
    }
}
