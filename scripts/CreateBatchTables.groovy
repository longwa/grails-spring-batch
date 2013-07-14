import grails.util.GrailsNameUtils
import groovy.sql.Sql

import java.sql.Connection
import java.sql.Statement

includeTargets << grailsScript('_GrailsBootstrap')

target(createBatchTables: "Installs the Spring Batch tables into database") {
    depends(classpath, checkVersion, bootstrap, loadApp)
    String db = config.plugin.springBatch.database
    if(!db) {
        errorMessage "\nERROR: must specify the database type [db2, derby, h2, hsqldb, mysql, oracle10g, postgresql, sqlserver, sybase]"
        exit 1
    }

    def selectedDs = config.plugin.springBatch.dataSource
    if(!selectedDs) {
        selectedDs = "dataSource"
    }
    def dsConfig = config."${selectedDs}"
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
    sql.withTransaction { Connection conn ->
        Statement statement = conn.createStatement()
        scriptContents.split(';').each { line ->
            if(line.trim()) {
                statement.execute(line.trim())
            }
        }
        statement.close()
        conn.commit()
    }
    sql.close()
    printMessage "Done loading spring batch tables"
}

setDefaultTarget(createBatchTables)

printMessage = { String message -> event('StatusUpdate', [message]) }
errorMessage = { String message -> event('StatusError', [message]) }
