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
           		<div class="datatable dialog-open-events" id="tableWrapper_${listConfig.name}">
        			<table pageLength="10" datatable-type="master" filter="${listConfig.filter ? true : false}" jsonUrl="${createLink(base:'/',controller:listConfig.controller,action: jsonlist ? jsonlist :listConfig.action,params:jsonlistparams?jsonlistparams:[:]).substring(1)}" cellpadding="0" cellspacing="0" border="0" class="dialog-open-events detailTable datatable table table-striped table-bordered display${listConfig.rowreordering?' rowreordering':''}" id="detailTable_${listConfig.name}"
                           positionUrl="/${listConfig.controller}/${position ? position :'position'}"
                           rowreordering="${listConfig.rowreordering}"
                           toolbar="${listConfig.toolbar?:''}"
                           newButton="${listConfig.newButton}"
                           autoWidth="${listConfig.autoWidth}"
                           >
        				<thead>
        					<tr>
        						<g:each in="${listConfig.columns}" var="column">
        							<th class="${column.sortable?'sortable':'nonsortable'} ${listConfig.name}-${column.name}"><g:message code="list.${listConfig.name}.${column.name}.label" /></th>
        						</g:each>
        						<th><g:message code="dialog.list.actions.label" default="Actions" /></th>
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
