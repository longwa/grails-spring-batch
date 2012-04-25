eventCompileStart = {
    ant.copy(todir:"$classesDirPath/batch", preservelastmodified:true) {
        fileset(dir:"${basedir}/grails-app/batch", includes:"**/*BatchConfig.groovy")
    }
}