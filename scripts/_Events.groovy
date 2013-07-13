if (grailsAppName != 'spring-batch') { //don't do this when compiling just the plugin.
    eventCompileStart = {
        if (new File("${basedir}/grails-app/batch").exists()) {
            ant.copy(todir:"$classesDirPath/batch", preservelastmodified:true) {
                fileset(dir:"${basedir}/grails-app/batch", includes:"**/*BatchConfig.groovy")
            }
        }
    }
}