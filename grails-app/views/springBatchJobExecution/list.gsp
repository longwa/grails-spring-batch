<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'jobExecutionModel.label', default: 'Spring Batch Job Execution')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
		<div id="list-jobExecutionModel" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
						<g:sortableColumn property="id" title="${message(code: 'jobExecutionModel.id.label', default: 'Instance Id')}" />
						<g:sortableColumn property="instanceId" title="${message(code: 'jobExecutionModel.instanceId.label', default: 'Instance Id')}" />
						<g:sortableColumn property="name" title="${message(code: 'jobExecutionModel.name.label', default: 'Name')}" />
						<g:sortableColumn property="startDateTime" title="${message(code: 'jobExecutionModel.startDateTime.label', default: 'Start Date Time')}" />
						<g:sortableColumn property="duration" title="${message(code: 'jobExecutionModel.duration.label', default: 'Duration')}" />
						<g:sortableColumn property="status" title="${message(code: 'jobExecutionModel.status.label', default: 'Status')}" />
						<g:sortableColumn property="exitCode" title="${message(code: 'jobExecutionModel.exitStatus.exitCode.label', default: 'Exit Code')}" />
						<th/>
					</tr>
				</thead>
				<tbody>
				<g:each in="${modelInstances}" status="i" var="jobExecutionModelInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td>${fieldValue(bean: jobExecutionModelInstance, field: "id")}</td>
						<td>${fieldValue(bean: jobExecutionModelInstance, field: "instanceId")}</td>
						<td>${fieldValue(bean: jobExecutionModelInstance, field: "name")}</td>
						<td><g:formatDate date="${jobExecutionModelInstance.startDateTime}" /></td>
						<td>${fieldValue(bean: jobExecutionModelInstance, field: "duration")}</td>
						<td>${fieldValue(bean: jobExecutionModelInstance, field: "status")}</td>
						<td>${fieldValue(bean: jobExecutionModelInstance, field: "exitStatus.exitCode")}</td>
						<td><g:link controller="springBatchStepExecution" action="list" id="${jobExecutionModelInstance.id}">${message(code: 'jobExecutionModel.stepExecutions.label', default: 'Step Executions')}</g:link></td>
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${modelTotal}" id="$jobInstanceId" params="[jobName:jobName]" />
			</div>
		</div>
	</body>
</html>
