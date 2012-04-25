if (grailsAppName != 'spring-batch') { //don't do this when compiling just the plugin.
    eventCompileStart = {
        ant.copy(todir:"$classesDirPath/batch", preservelastmodified:true) {
            fileset(dir:"${basedir}/grails-app/batch", includes:"**/*BatchConfig.groovy")
        }
    }
}