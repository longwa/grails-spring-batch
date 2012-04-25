import grails.util.GrailsNameUtils
import groovy.sql.Sql

includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript('_GrailsBootstrap')

target(main: "Installs the Spring Batch tables into database") {
    depends(classpath, checkVersion, bootstrap, loadApp)
    def argsList = argsMap.params
    String db = argsList[0]
    if(!db) {
        errorMessage "\nERROR: must specify the database type [db2, derby, h2, hsqldb, mysql, oracle10g, postgresql, sqlserver, sybase]"
        exit 1
    }

    def dsConfig = config.dataSource
    String dbDesc = dsConfig.jndiName ? "JNDI $dsConfig.jndiName" : "$dsConfig.username @ $dsConfig.url"
    def hyphenatedScriptName = GrailsNameUtils.getScriptName(scriptName)
    printMessage "Starting $hyphenatedScriptName for database $dbDesc"

    def schemaScript = "org/springframework/batch/core/schema-${db}.sql"
    def scriptResource = classLoader.rootLoader.getResource(schemaScript)
    printMessage "Loading tables for ${db} database"
    String scriptContents = scriptResource.openStream().text

    Sql sql = Sql.newInstance(
        dsConfig.url,
        dsConfig.username,
        dsConfig.password,
        dsConfig.driverClassName
    )
    sql.execute(scriptContents)
    sql.commit()
    sql.close()
    printMessage "Done loading spring batch tables"
}

setDefaultTarget(main)

printMessage = { String message -> event('StatusUpdate', [message]) }
errorMessage = { String message -> event('StatusError', [message]) }