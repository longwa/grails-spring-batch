<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title><g:message code="batch.stepExecution.show.label" args="[stepExecution.name]" /></title>
	</head>
	<body>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
		<div id="list-stepExecutionModel" class="content scaffold-list" role="main">
			<h1><g:message code="batch.stepExecution.show.label" args="[stepExecution.name]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:if test="${flash.error}">
			<div class="errors" role="status">${flash.error}</div>
			</g:if>
			
			<ol class="property-list example">
				<li class="fieldcontain">
					<span id="startDateTime-label" class="property-label"><g:message code="batch.stepExecution.startDateTime.label"/></span>
						<span class="property-value" aria-labelledby="startDateTime-label">${stepExecution.startDateTime}</span>
				</li>
				<li class="fieldcontain">
					<span id="duration-label" class="property-label"><g:message code="batch.stepExecution.duration.label"/></span>
						<span class="property-value" aria-labelledby="duration-label"><batch:durationPrint duration="${stepExecution.duration}"/></span>
				</li>
				<li class="fieldcontain">
					<span id="status-label" class="property-label"><g:message code="batch.stepExecution.status.label"/></span>
						<span class="property-value" aria-labelledby="status-label">${stepExecution.status}</span>
				</li>
				<li class="fieldcontain">
					<span id="exitStatus-label" class="property-label"><g:message code="batch.stepExecution.exitStatus.exitCode.label"/></span>
						<span class="property-value" aria-labelledby="exitStatus-label">${stepExecution.exitStatus.exitCode}</span>
				</li>
				
				<li class="fieldcontain">
					<span id="reads-label" class="property-label"><g:message code="batch.stepExecution.reads.label"/></span>
						<span class="property-value" aria-labelledby="reads-label">${stepExecution.reads}</span>
				</li>
				<li class="fieldcontain">
					<span id="readSkipCount-label" class="property-label"><g:message code="batch.stepExecution.readSkipCount.label"/></span>
						<span class="property-value" aria-labelledby="readSkipCount-label">${stepExecution.readSkipCount}</span>
				</li>
				<li class="fieldcontain">
					<span id="writes-label" class="property-label"><g:message code="batch.stepExecution.writes.label"/></span>
						<span class="property-value" aria-labelledby="writes-label">${stepExecution.writes}</span>
				</li>
				<li class="fieldcontain">
					<span id="writeSkipCount-label" class="property-label"><g:message code="batch.stepExecution.writeSkipCount.label"/></span>
						<span class="property-value" aria-labelledby="writeSkipCount-label">${stepExecution.writeSkipCount}</span>
				</li>
				<li class="fieldcontain">
					<span id="skips-label" class="property-label"><g:message code="batch.stepExecution.skips.label"/></span>
						<span class="property-value" aria-labelledby="skips-label">${stepExecution.skips}</span>
				</li>
				<li class="fieldcontain">
					<span id="commitCount-label" class="property-label"><g:message code="batch.stepExecution.commitCount.label"/></span>
						<span class="property-value" aria-labelledby="commitCount-label">${stepExecution.commitCount}</span>
				</li>
				<li class="fieldcontain">
					<span id="filterCount-label" class="property-label"><g:message code="batch.stepExecution.filterCount.label"/></span>
						<span class="property-value" aria-labelledby="filterCount-label">${stepExecution.filterCount}</span>
				</li>
				<li class="fieldcontain">
					<span id="processSkipCount-label" class="property-label"><g:message code="batch.stepExecution.processSkipCount.label"/></span>
						<span class="property-value" aria-labelledby="processSkipCount-label">${stepExecution.processSkipCount}</span>
				</li>
				<li class="fieldcontain">
					<span id="lastUpdated-label" class="property-label"><g:message code="batch.stepExecution.lastUpdated.label"/></span>
						<span class="property-value" aria-labelledby="lastUpdated-label">${stepExecution.lastUpdated}</span>
				</li>
				<g:if test="${stepExecution.failureExceptions}">
					<li class="fieldcontain">
						<span id="exceptions-label" class="property-label"><g:message code="batch.stepExecution.exceptions.label"/></span>
							<span class="property-value" aria-labelledby="exceptions-label">${stepExecution.exceptions}</span>
					</li>
				</g:if>
				
				<g:if test="${stepExecution.exitStatus.exitDescription}">
					<li class="fieldcontain">
						<span id="exceptions2-label" class="property-label"><g:message code="batch.stepExecution.exceptions.label"/></span>
							<span class="property-value" aria-labelledby="exceptions2-label">${stepExecution.exitStatus.exitDescription}</span>
					</li>
				</g:if>
				
			</ol>
			
			<h2><g:message code="batch.stepExecution.previousStepExecutions.label"/></h2>
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
						<td>${fieldValue(bean: stepExecutionModelInstance, field: "id")}</td>
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
				<g:paginate total="${modelInstances.resultsTotalCount}" id="$stepExecution.id" params="${[jobExecutionId:stepExecution?.jobExecutionId]}" />
			</div>
		</div>
	</body>
</html>
