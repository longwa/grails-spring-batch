<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'jobInstanceModel.label', default: 'Spring Batch Job Instance')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
		<div id="show-jobInstanceModel" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>

			<ol class="property-list example">
				<li class="fieldcontain">
					<span id="name-label" class="property-label">Job Name</span>
						<span class="property-value" aria-labelledby="name-label">
							<g:link controller="springBatchJob" action="show" id="${jobInstance.jobName}">${jobInstance.jobName}</g:link></span>
				</li>
				<li class="fieldcontain">
					<span id="id-label" class="property-label">Job Instance Id</span>
						<span class="property-value" aria-labelledby="id-label">${jobInstance.id}</span>
				</li>
				<li class="fieldcontain">
					<span id="executionCount-label" class="property-label">Job Executions for Job Instance Count</span>
						<span class="property-value" aria-labelledby="executionCount-label">${jobInstance.jobExecutionCount}</span>
				</li>
				<li class="fieldcontain">
					<span id="status-label" class="property-label">Last Job Execution Status</span>
						<span class="property-value" aria-labelledby="status-label">${jobInstance.lastJobExecutionStatus}</span>
				</li>
			</ol>

			<table>
				<thead>
					<tr>
						<g:sortableColumn property="id" title="${message(code: 'jobExecutionModel.id.label', default: 'Execution Id')}" />
						<g:sortableColumn property="startDateTime" title="${message(code: 'jobExecutionModel.startDateTime.label', default: 'Start Date Time')}" />
						<g:sortableColumn property="duration" title="${message(code: 'jobExecutionModel.duration.label', default: 'Duration')}" />
						<g:sortableColumn property="status" title="${message(code: 'jobExecutionModel.status.label', default: 'Status')}" />
						<g:sortableColumn property="exitCode" title="${message(code: 'jobExecutionModel.exitStatus.exitCode.label', default: 'Exit Code')}" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${modelInstances}" status="i" var="jobExecutionModelInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:link controller="springBatchJobExecution" action="show" id="${jobExecutionModelInstance.id}">${fieldValue(bean: jobExecutionModelInstance, field: "id")}</g:link></td>
						<td><g:formatDate date="${jobExecutionModelInstance.startDateTime}" /></td>
						<td>${fieldValue(bean: jobExecutionModelInstance, field: "duration")}</td>
						<td>${fieldValue(bean: jobExecutionModelInstance, field: "status")}</td>
						<td>${fieldValue(bean: jobExecutionModelInstance, field: "exitStatus.exitCode")}</td>
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${modelInstances.resultsTotalCount}" id="$jobInstanceId" params="[jobName:jobName]" />
			</div>
		</div>
	</body>
</html>
