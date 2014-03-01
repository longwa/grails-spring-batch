<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'jobModel.label', default: 'Spring Batch Job')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link action="list" >List</g:link></li>
				<g:if test="job.launchable}"><li><g:link action="launch" id="${job.name}">Launch Job</g:link></li></g:if>
				<li><g:link action="stopAllExecutions" id="${job.name}">Stop All Executions</g:link></li>
			</ul>
		</div>
		<div id="show-jobModel" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>

			<ol class="property-list example">
				<li class="fieldcontain">
					<span id="name-label" class="property-label">Name</span>
						<span class="property-value" aria-labelledby="name-label">${job.name}</span>
				</li>
				<li class="fieldcontain">
					<span id="jobInstanceCount-label" class="property-label">Job Instance Count</span>
						<span class="property-value" aria-labelledby="jobInstanceCount-label">${job.jobInstanceCount}</span>
				</li>
				<li class="fieldcontain">
					<span id="executionCount-label" class="property-label">Job Execution Count</span>
						<span class="property-value" aria-labelledby="executionCount-label">${job.executionCount}</span>
				</li>
				<li class="fieldcontain">
					<span id="launchable-label" class="property-label">Launchable</span>
						<span class="property-value" aria-labelledby="launchable-label">${job.launchable}</span>
				</li>
				<li class="fieldcontain">
					<span id="incrementable-label" class="property-label">Incrementable</span>
						<span class="property-value" aria-labelledby="incrementable-label">${job.incrementable}</span>
				</li>
			</ol>

			<table>
				<thead>
					<tr>
						<g:sortableColumn property="id" title="${message(code: 'jobInstanceModel.id.label', default: 'ID')}" />
						<g:sortableColumn property="jobExecutionCount" title="${message(code: 'jobInstanceModel.jobExecutionCount.label', default: 'Job Execution Count')}" />
						<g:sortableColumn property="lastJobExecutionStatus" title="${message(code: 'jobInstanceModel.lastJobExecutionStatus.label', default: 'Last Job Execution Status')}" />
						<th/>
					</tr>
				</thead>
				<tbody>
				<g:each in="${jobModelInstances}" status="i" var="jobInstanceModelInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:link controller="springBatchJobInstance" action="show" id="${jobInstanceModelInstance.id}" params="[jobName: job.name]">${fieldValue(bean: jobInstanceModelInstance, field: "id")}</g:link></td>
						<td>${fieldValue(bean: jobInstanceModelInstance, field: "jobExecutionCount")}</td>
						<td>${fieldValue(bean: jobInstanceModelInstance, field: "lastJobExecutionStatus")}</td>
						<td><g:if test="${jobInstanceModelInstance.stoppable }"><g:link action="stopAllExecutions" id="${job.name}">${message(code: 'jobInstanceModel.jobExecutions.stop.label', default: "Stop")}</g:link></g:if></td>
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${jobModelInstances.resultsTotalCount}" id="$job.name" />
			</div>
		</div>
	</body>
</html>
