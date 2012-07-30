<%@ page import="grails.plugins.springbatch.ui.JobModel" %>
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
			</ul>
		</div>
		<div id="list-jobModel" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>

                        <g:sortableColumn property="name" title="${message(code: 'jobModel.name.label', default: 'Name')}" />

						<g:sortableColumn property="executionCount" title="${message(code: 'jobModel.executionCount.label', default: 'Execution Count')}" />
					
						<g:sortableColumn property="incrementable" title="${message(code: 'jobModel.incrementable.label', default: 'Is Incrementable')}" />
					
						<g:sortableColumn property="launchable" title="${message(code: 'jobModel.launchable.label', default: 'Is Launchable')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${modelInstances}" status="i" var="modelInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link controller="springBatchJobInstance" action="list" id="${modelInstance.name}">${fieldValue(bean: modelInstance, field: "name")}</g:link></td>

                        <td>${fieldValue(bean: modelInstance, field: "executionCount")}</td>
					
						<td><g:formatBoolean boolean="${modelInstance.incrementable}" /></td>
					
						<td><g:formatBoolean boolean="${modelInstance.launchable}" /></td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${modelTotal}" />
			</div>
		</div>
	</body>
</html>