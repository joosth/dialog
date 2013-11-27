<%@page import="org.codehaus.groovy.grails.commons.*" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="list.${listConfig.name}.title" /></title>
        <r:script>
          
        	// Datatables list
			$(document).ready(function() {	        
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
				"sAjaxSource": "${createLink(controller:listConfig.controller,action: jsonlist ? jsonlist :listConfig.action,params:jsonlistparams?jsonlistparams:[:])}",
				"sPaginationType": "bootstrap",	
				"bFilter": ${listConfig.bFilter ? true : false},
				"bJQueryUI": false,
				"aoColumnDefs": [ 
					{ "bSortable": false, "aTargets": [ ${listConfig.columns.size()} ,"nonsortable"] },
                    { "sClass": "actions" , "aTargets": [ -1 ] }
				] ,
				
				"oLanguage": {      		
                  "sProcessing":   "${message(code:'dialog.datatables.sProcessing')}",        
                  "sLengthMenu":   "${message(code:'dialog.datatables.sLengthMenu')}",
                  "sZeroRecords":  "${message(code:'dialog.datatables.sZeroRecords')}",
                  "sInfo":         "${message(code:'dialog.datatables.sInfo')}",
                  "sInfoEmpty":    "${message(code:'dialog.datatables.sInfoEmpty')}",
                  "sInfoFiltered": "${message(code:'dialog.datatables.sInfoFiltered')}",
                  "sInfoPostFix":  "${message(code:'dialog.datatables.sInfoPostFix')}",
                  "sSearch":       "${message(code:'dialog.datatables.sSearch')}",
                  "oPaginate": {
                    "sFirst":    "${message(code:'dialog.datatables.oPaginate.sFirst')}",
                    "sPrevious": "${message(code:'dialog.datatables.oPaginate.sPrevious')}",
                    "sNext":     "${message(code:'dialog.datatables.oPaginate.sNext')}",
                    "sLast":     "${message(code:'dialog.datatables.oPaginate.sLast')}"
                  },
                  "sNew":"${message(code:'dialog.datatables.sNew')}"

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
