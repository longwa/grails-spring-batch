<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title><g:message code="batch.job.show.label"/> ${job.name}</title>
	</head>
	<body>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link action="list" >List</g:link></li>
				<g:if test="job.launchable}"><li><g:link action="launch" id="${job.name}">
					<g:message code="batch.job.launch.label"/></g:link></li></g:if>
				<li><g:link action="stopAllExecutions" id="${job.name}">
					<g:message code="batch.job.stopall.label"/></g:link></li>
			</ul>
		</div>
		<div id="show-jobModel" class="content scaffold-show" role="main">
			<h1><g:message code="batch.job.show.label" /> ${job.name}</h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:if test="${flash.error}">
			<div class="errors" role="status">${flash.error}</div>
			</g:if>

			<ol class="property-list example">
				<li class="fieldcontain">
					<span id="jobInstanceCount-label" class="property-label">Job Instance Count</span>
						<span class="property-value" aria-labelledby="jobInstanceCount-label">${job.jobInstanceCount}</span>
				</li>
				<li class="fieldcontain">
					<span id="executionCount-label" class="property-label">Job Execution Count</span>
						<span class="property-value" aria-labelledby="executionCount-label">${job.executionCount}</span>
				</li>
				<li class="fieldcontain">
					<span id="incrementable-label" class="property-label">Incrementable</span>
						<span class="property-value" aria-labelledby="incrementable-label">${job.incrementable}</span>
				</li>
			</ol>
			
			<h2>Instances</h2>
			<table>
				<thead>
					<tr>
						<g:sortableColumn property="id" title="${message(code: 'jobInstanceModel.id.label', default: 'Id')}" />
						<g:sortableColumn property="jobExecutionCount" title="${message(code: 'jobInstanceModel.jobExecutionCount.label', default: 'Execution Count')}" />
						<g:sortableColumn property="lastJobExecutionStatus" title="${message(code: 'jobInstanceModel.lastJobExecutionStatus.label', default: 'Last Execution Status')}" />
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
