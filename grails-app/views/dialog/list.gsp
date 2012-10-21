<%@page import="org.codehaus.groovy.grails.commons.*" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
		<jqui:resources theme="smoothness" />
        <title>
        	<g:if test="${title}">${title}</g:if>
	        <g:else><g:message code="${name?name:controllerName}.list.title" default="${name?name:controllerName}.list.title" /></g:else>
        </title>
        
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
		"sAjaxSource": "${createLink(controller:controllerName,action: jsonlist ? jsonlist :'jsonlist',params:jsonlistparams?jsonlistparams:[:])}",
		"sPaginationType": "full_numbers",
		"bFilter": ${bFilter ? true : false},
		"bJQueryUI": true,
		"aoColumnDefs": [ 
			{ "bSortable": false, "aTargets": [ ${dc.listProperties.size()} ,"nonsortable"] }
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
    			<h1>
    				<g:if test="${title}">${title}</g:if>
    				<g:else><g:message code="${name?name:controllerName}.list.title" default="${name?name:controllerName}.list.title" /></g:else>
    			</h1>
   			</div>
	      	<div class="datatable">
	      
	   			<table cellpadding="0" cellspacing="0" border="0" class="display${rowreordering?' rowreordering':''}" id="detailTable_${dc.getName().replace(".","_").replace("class ","")}">
					<thead>
				
						<tr>
						<g:each in="${dc.listProperties}" var="property">
						<g:if test="${dc.declaredFields.findAll {it.name=='sortableProperties'}.size()>0}">						 
					 	<th class="${dc.sortableProperties?.contains(property)?'sortable':'nonsortable'}"><g:message code="${controllerName}.${property}.label" default="${controllerName}.${property}.label" /></th>
					 	</g:if>
					 	<g:else>
					 	<th class="${new DefaultGrailsDomainClass(dc).hasPersistentProperty(property)?'sortable':'nonsortable'}"><g:message code="${controllerName}.${property}.label" default="${controllerName}.${property}.label" /></th>
					 	</g:else>		
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
