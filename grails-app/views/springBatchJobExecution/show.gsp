<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title><g:message code="batch.jobExecution.show.label" args="[jobExecution.jobName, jobExecution.instanceId, jobExecution.id]" /></title>
	</head>
	<body>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link controller="springBatchJobInstance" action="show" id="${jobExecution.instanceId}" params="[jobName:jobExecution.jobName]">
					<g:message code="batch.jobExecution.backToJobInstance.label"/></g:link></li>
				<li><g:link action="restart" id="${jobExecution.id}">
					<g:message code="batch.jobExecution.restart.label"/></g:link></li>
				<li><g:link action="stop" id="${jobExecution.id}">
					<g:message code="batch.jobExecution.stop.label"/></g:link></li>
			</ul>
		</div>
		<div id="list-stepExecutionModel" class="content scaffold-list" role="main">
			<h1><g:message code="batch.jobExecution.show.label" args="[jobExecution.jobName, jobExecution.instanceId, jobExecution.id]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:if test="${flash.error}">
			<div class="errors" role="status">${flash.error}</div>
			</g:if>
			
			<ol class="property-list example">
				<li class="fieldcontain">
					<span id="startDateTime-label" class="property-label"><g:message code="batch.jobExecution.startDateTime.label"/></span>
						<span class="property-value" aria-labelledby="startDateTime-label">${jobExecution.startDateTime}</span>
				</li>
				<li class="fieldcontain">
					<span id="duration-label" class="property-label"><g:message code="batch.jobExecution.duration.label"/></span>
						<span class="property-value" aria-labelledby="duration-label"><batch:durationPrint duration="${jobExecution.duration}"/></span>
				</li>
				<li class="fieldcontain">
					<span id="status-label" class="property-label"><g:message code="batch.jobExecution.status.label"/></span>
						<span class="property-value" aria-labelledby="status-label">${jobExecution.status}</span>
				</li>
				<li class="fieldcontain">
					<span id="exitStatus-label" class="property-label"><g:message code="batch.jobExecution.exitStatus.label"/></span>
						<span class="property-value" aria-labelledby="exitStatus-label">${jobExecution.exitStatus.exitCode}</span>
				</li>
			</ol>
			
			<h2><g:message code="batch.jobExecution.steps.label"/></h2>
			<table>
				<thead>
					<tr>
						<g:sortableColumn property="id" title="${message(code: 'batch.stepExecution.id.label', default: 'ID')}" />
						<g:sortableColumn property="name" title="${message(code: 'batch.stepExecution.name.label', default: 'Name')}" />
						<g:sortableColumn property="startDateTime" title="${message(code: 'batch.stepExecution.startDateTime.label', default: 'Start Date Time')}" />
						<g:sortableColumn property="duration" title="${message(code: 'batch.stepExecution.duration.label', default: 'Duration')}" />
						<g:sortableColumn property="status" title="${message(code: 'batch.stepExecution.status.label', default: 'Status')}" />
						<g:sortableColumn property="reads" title="${message(code: 'batch.stepExecution.reads.label', default: 'Reads')}" />
						<g:sortableColumn property="writes" title="${message(code: 'batch.stepExecution.writes.label', default: 'Writes')}" />
						<g:sortableColumn property="skips" title="${message(code: 'batch.stepExecution.skips.label', default: 'Skips')}" />
						<g:sortableColumn property="exitCode" title="${message(code: 'batch.stepExecution.exitStatus.exitCode.label', default: 'Exit Code')}" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${modelInstances}" status="i" var="stepExecutionModelInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:link controller="springBatchStepExecution" action="show" id="${stepExecutionModelInstance.id}" params="${[jobExecutionId:jobExecution.id]}">${fieldValue(bean: stepExecutionModelInstance, field: "id")}</g:link></td>
						<td>${fieldValue(bean: stepExecutionModelInstance, field: "name")}</td>
						<td><g:formatDate date="${stepExecutionModelInstance.startDateTime}" /></td>
						<td><batch:durationPrint duration="${stepExecutionModelInstance?.duration}"/></td>
						<td>${fieldValue(bean: stepExecutionModelInstance, field: "status")}</td>
						<td>${fieldValue(bean: stepExecutionModelInstance, field: "reads")}</td>
						<td>${fieldValue(bean: stepExecutionModelInstance, field: "writes")}</td>
						<td>${fieldValue(bean: stepExecutionModelInstance, field: "skips")}</td>
						<td>${fieldValue(bean: stepExecutionModelInstance, field: "exitStatus.exitCode")}</td>
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${modelInstances.resultsTotalCount}" id="$jobExecution.id" />
			</div>
		</div>
	</body>
</html>
