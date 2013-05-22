<%@page import="org.codehaus.groovy.grails.commons.*" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="list.${listConfig.name}.title" /></title>
        <r:script>
        	// Datatables list
			$(function() {		        
	        	//Create Id for the table
	        	var tableId="detailTable_${listConfig.name}";
	        	var wrapperId="tableWrapper_${listConfig.name}";	        
				dialog.dataTableHashList[tableId]=$("#"+tableId).dataTable( {			
				/*
				    sDom explanation:
				    l - Length changing
				    f - Filtering input
				    t - The table!
				    i - Information
				    p - Pagination
				    r - pRocessing
				    < and > - div elements
				    <"class" and > - div with a class
				    Examples: <"wrapper"flipt>, <lf<t>ip>
				*/							
				"sDom": '<"toolbar"lf><"processing"r>tip',
				"bProcessing": true,
				"bServerSide": true,		
				"sAjaxSource": "${createLink(controller:listConfig.controller,action: jsonlist ? jsonlist :'jsonlist',params:jsonlistparams?jsonlistparams:[:])}",
				"sPaginationType": "bootstrap",	
				"bFilter": ${listConfig.bFilter ? true : false},
				"bJQueryUI": false,
				"aoColumnDefs": [ 
					{ "bSortable": false, "aTargets": [ ${listConfig.columns.size()} ,"nonsortable"] }
				] ,
				
				"oLanguage": {
		     	 "sUrl": "${resource(plugin:'dialog',dir:'js/jquery')}/dataTables/localisation/dataTables.${g.message(code:'datatables.language',default:'en')}.txt"
		    	},
		    	"fnInitComplete": function() {
		    		 
	   			<g:if test="${listConfig.toolbar}" >	
					$('#'+wrapperId).find('.toolbar').prepend('${listConfig.toolbar}');
				</g:if>
				<g:else>
					<g:if test="${listConfig.newButton}" >    		    							    							
    					$('#'+wrapperId).find('.toolbar').prepend('<div style="float:left;margin-right:10px;" class="btn-group"><span class="btn" onclick="dialog.formDialog(null,\'${controllerName}\',{ }, null)"><g:message code="list.new" default="New" /></span></div>');
    				</g:if>
				</g:else>
				<g:if test="${listConfig.rowreordering}">
					dialog.dataTableHashList[tableId].rowReordering(       				
	       			{
	       				sURL:"${resource(dir:listConfig.controller,file: position ? position :'position')}",
	                    sRequestType: "POST"
	       			});       	
				</g:if>
				}
				});	
				$("#"+tableId).bind("dialog-refresh",dialog.datatables.refreshDatatableEvent);
				$("#"+tableId).addClass("dialog-events");	       
			});
        
		</r:script>
	</head>    
	<body>	
		<h3><g:message code="list.${listConfig.name}.title" /></h3>
		
		<div class="row-fluid">
		<div class="span12">
   		<div class="datatable dialog-events" id="tableWrapper_${listConfig.name}">	      
			<table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered display${rowreordering?' rowreordering':''}" id="detailTable_${listConfig.name}" >
				<thead>
					<tr>
						<g:each in="${listConfig.columns}" var="column">											 
							<th class="${column.sortable?'sortable':'nonsortable'}"><g:message code="list.${listConfig.name}.${column.name}.label" /></th>		
						</g:each>
						<th width="50px"><g:message code="list.actions.label" default="Actions" /></th>
					</tr>
				</thead>
				<tbody>
					<tr><td colspan="${listConfig.columns.size()}" class="dataTables_empty">Loading data from server</td></tr>
				</tbody>
				<tfoot>
				<tr>
					<g:each in="${listConfig.columns}" var="column">
							<th class="${column.sortable?'sortable':'nonsortable'}"><g:message code="list.${listConfig.name}.${column.name}.label" /></th>
					</g:each>
				<th><g:message code="list.actions.label" default="Actions" /></th></tr>
				</tfoot>
			</table>
		</div>
		</div>
		</div>		
    </body>
</html>
