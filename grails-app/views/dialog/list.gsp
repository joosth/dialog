<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
		<jqui:resources theme="smoothness" />
        <title><g:message code="${controllerName}.list.title" default="${controllerName}.list.title" /></title>
        
        <script type="text/javascript">
        $(function() {
        
        //Create Id for the table
        var tableId="detailTable_" + "${dc.getName().replace(".","_").replace("class ","")}";
        var domainClass="${dc.name.substring(dc.name.lastIndexOf('.')+1)}"

		dialog.dataTableHashList[tableId]=$("#"+tableId).dataTable( {
		//"sDom": '<"H"lfr>t<"F"ip>',
		//	"sDom": '<"toolbar"><"H"lfr>t<"F"ip>',
		//"sDom": '<"toolbar">frtip',
		"bProcessing": true,
		"bServerSide": true,		
		"sAjaxSource": "${resource(dir:controllerName,file: jsonlist ? jsonlist :'jsonlist')}",
		"sPaginationType": "full_numbers",
		"bFilter": ${bFilter ? true : false},
		"bJQueryUI": true,
		"aoColumnDefs": [ 
			{ "bSortable": false, "aTargets": [ ${dc.listProperties.size()} ] }
		] ,
		
		"oLanguage": {
     	 "sUrl": "${resource(plugin:'dialog',dir:'js/jquery')}/dataTables/localisation/dataTables.${g.message(code:'datatables.language',default:'en')}.txt"
    	},
    	"fnInitComplete": function() {
    		 
   		<g:if test="${toolbar}" >	
			$("div.datatable div.fg-toolbar div.dataTables_length").prepend('${toolbar}');
		</g:if>
		<g:else>
    		$("div.datatable div.fg-toolbar div.dataTables_length").prepend('<span class="list-toolbar-button ui-widget-content ui-state-default"><span onclick="dialog.formDialog(null,\'${controllerName}\',{ domainclass : \''+domainClass+'\'}, null)"><g:message code="list.new" default="New" /></span></span>&nbsp;');			    		    							    							
		</g:else>
		<g:if test="${rowreordering}">
			dialog.dataTableHashList[tableId].rowReordering(       				
       				{
       					 sURL:"${resource(dir:controllerName,file: position ? position :'position')}",
                         sRequestType: "POST"
       				});       	
		</g:if>
		
		
		}
		} );
		
		
		
		
		
		$("#"+tableId).bind("dialog-refresh",dialog.refreshDatatableEvent);
		
		$("#"+tableId).addClass("dialog-events");
		
		
		
		
		       
});
        </script>       
    </head>    
    <body>
    	<div class="body">
    		<div class="fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr table-title" >
    	
    			<h1><g:message code="${controllerName}.list.title" default="${controllerName}.list.title" /></h1>
   			</div>
	      	<div class="datatable">
	      
	   			<table cellpadding="0" cellspacing="0" border="0" class="display${rowreordering?' rowreordering':''}" id="detailTable_${dc.getName().replace(".","_").replace("class ","")}">
					<thead>
				
						<tr>
						<g:each in="${dc.listProperties}" var="property">
					 	<th><g:message code="${controllerName}.${property}.label" default="${controllerName}.${property}.label" /></th>		
						</g:each>
						<th width="50px"><g:message code="list.actions.label" default="Actions" /></th>
				
						</tr>
					</thead>
					<tbody>
						<tr>
							<td colspan="${dc.listProperties.size()}" class="dataTables_empty">Loading data from server</td>
						</tr>
					</tbody>
					<tfoot>
				
						<tr>
							<g:each in="${dc.listProperties}" var="property">
					 	<th><g:message code="${controllerName}.${property}.label" default="${controllerName}.${property}.label" /></th>		
						</g:each>
						<th><g:message code="list.actions.label" default="Actions" /></th>				
						</tr>
					</tfoot>
				</table>
			</div>   
   			<div id="statusmessage" style="margin:auto;text-align:center;">...</div>    
   		</div>
    </body>
</html>
