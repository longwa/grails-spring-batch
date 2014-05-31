<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'jobModel.label', default: 'Spring Batch Job')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:if test="${ready}">
						<g:link controller="springBatchJob" action="disableLaunching">
							<g:message code="batch.disableLaunching"/></g:link></g:if>
					<g:else>
						<g:link controller="springBatchJob" action="enableLaunching">
							<g:message code="batch.enableLaunching"/></g:link></g:else>
				</li>
			</ul>
		</div>
		<div id="list-jobModel" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:if test="${flash.error}">
			<div class="errors" role="status">${flash.error}</div>
			</g:if>
			<table>
				<thead>
					<tr>
						<g:sortableColumn property="name" title="${message(code: 'jobModel.name.label', default: 'Name')}" />
						<g:sortableColumn property="currentlyRunning" title="${message(code: 'batch.job.currentlyRunning.label', default: 'Running')}" />
						<g:sortableColumn property="executionCount" title="${message(code: 'jobModel.executionCount.label', default: 'Execution Count')}" />
						<td>Most Recent Execution</td>
						<g:sortableColumn property="launchable" title="${message(code: 'jobModel.launchable.label', default: 'Is Launchable')}" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${modelInstances}" status="i" var="modelInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:link controller="springBatchJob" action="show" id="${modelInstance.name}">${fieldValue(bean: modelInstance, field: "name")}</g:link></td>
						<td>${fieldValue(bean: modelInstance, field: "currentlyRunning")}</td>
						<td>${fieldValue(bean: modelInstance, field: "executionCount")}</td>
						<td><g:if test="${modelInstance.mostRecentJobExecution}">
								<g:link controller="springBatchJobExecution" action="show" id="${modelInstance.mostRecentJobExecution.id}">
									${modelInstance.mostRecentJobExecution.startDateTime} - ${modelInstance.mostRecentJobExecution.status} - ${modelInstance.mostRecentJobExecution.exitStatus.exitCode} 
								</g:link>
							</g:if>
						</td>
						<td><g:if test="modelInstance.launchable}">
							<g:link action="launch" id="${modelInstance.name}" 
								class="launchJobButton" params="[a: 'l']">
							<g:message code="batch.job.launch.label"/></g:link></g:if></td>
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${modelInstances.resultsTotalCount}" />
			</div>
		</div>
	</body>
</html>
