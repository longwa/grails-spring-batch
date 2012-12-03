<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'jobInstanceModel.label', default: 'Spring Batch Job Instance')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
		<div id="list-jobInstanceModel" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
						<g:sortableColumn property="id" title="${message(code: 'jobInstanceModel.id.label', default: 'ID')}" />
						<th/>
						<g:sortableColumn property="jobExecutionCount" title="${message(code: 'jobInstanceModel.jobExecutionCount.label', default: 'Job Execution Count')}" />
						<g:sortableColumn property="lastJobExecutionStatus" title="${message(code: 'jobInstanceModel.lastJobExecutionStatus.label', default: 'Last Job Execution Status')}" />
						<g:sortableColumn property="jobParameters" title="${message(code: 'jobInstanceModel.jobParameters.label', default: 'Job Parameters')}" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${modelInstances}" status="i" var="jobInstanceModelInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td>${fieldValue(bean: jobInstanceModelInstance, field: "id")}</td>
						<td><g:link controller="springBatchJobExecution" action="list" id="${jobInstanceModelInstance.id}" params="[jobName: jobName]">${message(code: 'jobInstanceModel.jobExecutions.label', default: "Executions")}</g:link></td>
						<td>${fieldValue(bean: jobInstanceModelInstance, field: "jobExecutionCount")}</td>
						<td>${fieldValue(bean: jobInstanceModelInstance, field: "lastJobExecutionStatus")}</td>
						<td>${fieldValue(bean: jobInstanceModelInstance, field: "jobParameters")}</td>
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${modelTotal}" id="$jobName" />
			</div>
		</div>
	</body>
</html>
