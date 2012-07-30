
<%@ page import="grails.plugins.springbatch.ui.StepExecutionModel" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'stepExecutionModel.label', default: 'Spring Batch Step Execution')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
		<div id="list-stepExecutionModel" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>

                        <g:sortableColumn property="id" title="${message(code: 'stepExecutionModel.id.label', default: 'ID')}" />

                        <g:sortableColumn property="jobExecutionId" title="${message(code: 'stepExecutionModel.jobExecutionId.label', default: 'Job Execution Id')}" />

                        <g:sortableColumn property="name" title="${message(code: 'stepExecutionModel.name.label', default: 'Name')}" />

                        <g:sortableColumn property="startDateTime" title="${message(code: 'stepExecutionModel.startDateTime.label', default: 'Start Date Time')}" />

                        <g:sortableColumn property="duration" title="${message(code: 'stepExecutionModel.duration.label', default: 'Duration')}" />

                        <g:sortableColumn property="status" title="${message(code: 'stepExecutionModel.status.label', default: 'Status')}" />

                        <g:sortableColumn property="reads" title="${message(code: 'stepExecutionModel.reads.label', default: 'Reads')}" />

                        <g:sortableColumn property="writes" title="${message(code: 'stepExecutionModel.reads.label', default: 'Writes')}" />

                        <g:sortableColumn property="skips" title="${message(code: 'stepExecutionModel.skips.label', default: 'Skips')}" />

                        <g:sortableColumn property="exitCode" title="${message(code: 'stepExecutionModel.exitStatus.exitCode.label', default: 'Exit Code')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${modelInstances}" status="i" var="stepExecutionModelInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                        <td>${fieldValue(bean: stepExecutionModelInstance, field: "id")}</td>

                        <td>${fieldValue(bean: stepExecutionModelInstance, field: "jobExecutionId")}</td>

                        <td>${fieldValue(bean: stepExecutionModelInstance, field: "name")}</td>

                        <td><g:formatDate date="${stepExecutionModelInstance.startDateTime}" /></td>

                        <td>${fieldValue(bean: stepExecutionModelInstance, field: "duration")}</td>

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
				<g:paginate total="${modelTotal}" id="$jobExecutionId" />
			</div>
		</div>
	</body>
</html>
