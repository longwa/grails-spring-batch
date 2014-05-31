<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title><g:message code="batch.jobInstance.show.label" args="[jobInstance.jobName, jobInstance.id]"/></title>
	</head>
	<body>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link controller="springBatchJob" action="show" id="${jobInstance.jobName}">
					<g:message code="batch.jobInstance.backToJob.label"/></g:link></li>
			</ul>
		</div>
		<div id="show-jobInstanceModel" class="content scaffold-show" role="main">
			<h1><g:message code="batch.jobInstance.show.label" args="[jobInstance.jobName, jobInstance.id]"/></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:if test="${flash.error}">
			<div class="errors" role="status">${flash.error}</div>
			</g:if>

			<ol class="property-list example">
				<li class="fieldcontain">
					<span id="executionCount-label" class="property-label"><g:message code="batch.jobInstance.jobExecutionCount.label"/></span>
						<span class="property-value" aria-labelledby="executionCount-label">${jobInstance.jobExecutionCount}</span>
				</li>
				<li class="fieldcontain">
					<span id="status-label" class="property-label"><g:message code="batch.jobInstance.lastJobExecutionStatus.label"/></span>
						<span class="property-value" aria-labelledby="status-label">${jobInstance.lastJobExecutionStatus}</span>
				</li>
				<li class="fieldcontain">
					<span id="jobParameters-label" class="property-label"><g:message code="batch.jobInstance.jobParameters.label"/></span>
						<span class="property-value" aria-labelledby="jobParameters-label">${jobInstance.jobParameters}</span>
				</li>
			</ol>
			
			<h2><g:message code="batch.jobInstance.executions.label"/></h2>
			<table>
				<thead>
					<tr>
						<g:sortableColumn property="id" title="${message(code: 'batch.jobExecution.id.label', default: 'Id')}" />
						<g:sortableColumn property="startDateTime" title="${message(code: 'batch.jobExecution.startDateTime.label', default: 'Start Date Time')}" />
						<g:sortableColumn property="duration" title="${message(code: 'batch.jobExecution.duration.label', default: 'Duration')}" />
						<g:sortableColumn property="status" title="${message(code: 'batch.jobExecution.status.label', default: 'Status')}" />
						<g:sortableColumn property="exitCode" title="${message(code: 'batch.jobExecution.exitStatus.exitCode.label', default: 'Exit Code')}" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${modelInstances}" status="i" var="jobExecutionModelInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:link controller="springBatchJobExecution" action="show" id="${jobExecutionModelInstance.id}">${fieldValue(bean: jobExecutionModelInstance, field: "id")}</g:link></td>
						<td><g:formatDate date="${jobExecutionModelInstance.startDateTime}" /></td>
						<td><batch:durationPrint duration="${jobExecutionModelInstance.duration}"/></td>
						<td>${fieldValue(bean: jobExecutionModelInstance, field: "status")}</td>
						<td>${fieldValue(bean: jobExecutionModelInstance, field: "exitStatus.exitCode")}</td>
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${modelInstances.resultsTotalCount}" id="${jobInstance.id}" params="[jobName:jobName]" />
			</div>
		</div>
	</body>
</html>
