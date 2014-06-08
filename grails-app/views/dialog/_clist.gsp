<%@page import="org.codehaus.groovy.grails.commons.*" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="list.${listConfig.name}.title" /></title>
	</head>
	<body>
		<h3><g:message code="list.${listConfig.name}.title" /></h3>

		<div class="row-fluid">
		<div class="span12">
   		<div class="datatable dialog-events" id="tableWrapper_${listConfig.name}">
			<table iDisplayLength="10" datatable-type="master" bFilter="${listConfig.bFilter ? true : false}" jsonUrl="${createLink(base:'/',controller:listConfig.controller,action: jsonlist ? jsonlist :listConfig.action,params:jsonlistparams?jsonlistparams:[:]).substring(1)}" cellpadding="0" cellspacing="0" border="0" class="detailTable datatable table table-striped table-bordered display${listConfig.rowreordering?' rowreordering':''}" id="detailTable_${listConfig.name}"
                   positionUrl="/${listConfig.controller}/${position ? position :'position'}"
                   rowreordering="${listConfig.rowreordering}"
                   toolbar="${listConfig.toolbar?:''}"
                   newButton="${listConfig.newButton}"
                   >
				<thead>
					<tr>
						<g:each in="${listConfig.columns}" var="column">
							<th class="${column.sortable?'sortable':'nonsortable'} ${listConfig.name}-${column.name}"><g:message code="list.${listConfig.name}.${column.name}.label" /></th>
						</g:each>
						<th width="50px"><g:message code="dialog.list.actions.label" default="Actions" /></th>
					</tr>
				</thead>
				<tbody>
					<tr><td colspan="${listConfig.columns.size()}" class="dataTables_empty">Loading data from server</td></tr>
				</tbody>
				<tfoot>
				<tr>
					<g:each in="${listConfig.columns}" var="column">
							<th class="${column.sortable?'sortable':'nonsortable'} ${listConfig.name}-${column.name}"><g:message code="list.${listConfig.name}.${column.name}.label" /></th>
					</g:each>
				<th><g:message code="dialog.list.actions.label" default="Actions" /></th></tr>
				</tfoot>
			</table>
		</div>
		</div>
		</div>
    </body>
</html>
