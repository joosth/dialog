<%@page import="org.codehaus.groovy.grails.commons.*" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
				
        <title>
        	<g:if test="${title}">${title}</g:if>
	        <g:else><g:message code="${name?name:controllerName}.list.title" default="${name?name:controllerName}.list.title" /></g:else>
        </title>
        <r:script>
        // Dialog plugin -- list
       $(document).ready(function() {
        
        //Create Id for the table
        var tableId="detailTable_" + "${dc.getName().replace(".","_").replace("class ","")}";
        var wrapperId="tableWrapper_${dc.getName().replace(".","_").replace("class ","")}";
        var domainClass="${dc.name.substring(dc.name.lastIndexOf('.')+1)}"

		dialog.dataTableHashList[tableId]=$("#"+tableId).dataTable( {
		//"sDom": '<"H"lfr>t<"F"ip>',
		//	"sDom": '<"toolbar"><"H"lfr>t<"F"ip>',
		
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
		"sAjaxSource": "${createLink(controller:controllerName,action: jsonlist ? jsonlist :'jsonlist',params:jsonlistparams?jsonlistparams:[:])}",
		<g:if test="${sPaginationType}" >	
			"sPaginationType": "${sPaginationType}",
		</g:if>
		<g:else>
		"sPaginationType": "bootstrap",
		</g:else>
		
		"bFilter": ${bFilter ? true : false},
		"bJQueryUI": false,
		"aoColumnDefs": [ 
			{ "bSortable": false, "aTargets": [ ${dc.listProperties.size()} ,"nonsortable"] },
            { "sClass": "actions" , "aTargets": [ -1 ] }
		] ,
		"oLanguage": dialog.messages.datatables.oLanguage,		
    	"fnInitComplete": function() {
    		 
   		<g:if test="${toolbar}" >	
			$('#'+wrapperId).find('.toolbar').prepend('${toolbar}');
		</g:if>
		<g:else>					    		    							    							
    		$('#'+wrapperId).find('.toolbar').prepend('<div style="float:left;margin-right:10px;" class="btn-group"><span class="btn" onclick="dialog.formDialog(null,\'${controllerName}\',{ domainclass : \''+domainClass+'\'}, null)"><g:message code="list.new" default="New" /></span></div>');
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
		
		
		$("#"+tableId).bind("dialog-refresh",dialog.datatables.refreshDatatableEvent);
		
		$("#"+tableId).addClass("dialog-events");
		       
});
        
        </r:script>

    </head>    
    <body>
    			<h3>
    				<g:if test="${title}">${title}</g:if>
    				<g:else><g:message code="${name?name:controllerName}.list.title" default="${name?name:controllerName}.list.title" /></g:else>
    			</h3>
   		  	<div class="datatable" id="tableWrapper_${dc.getName().replace(".","_").replace("class ","")}">
	   			<table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered display${rowreordering?' rowreordering':''}" id="detailTable_${dc.getName().replace(".","_").replace("class ","")}">
					<thead>				
						<tr>
						<g:each in="${dc.listProperties}" var="property">
						<g:if test="${dc.declaredFields.findAll {it.name=='sortableProperties'}.size()>0}">						 
					 	<th class="${dc.sortableProperties?.contains(property)?'sortable':'nonsortable'} ${controllerName}-${property}"><g:message code="${controllerName}.${property}.label" default="${controllerName}.${property}.label" /></th>
					 	</g:if>
					 	<g:else>
					 	<th class="${(property=='id' || new DefaultGrailsDomainClass(dc).hasPersistentProperty(property))?'sortable':'nonsortable'} ${controllerName}-${property}"><g:message code="${controllerName}.${property}.label" default="${controllerName}.${property}.label" /></th>
					 	</g:else>		
						</g:each>
						<th width="50px"><g:message code="dialog.list.actions.label" default="Actions" /></th>				
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
						<th><g:message code="dialog.list.actions.label" default="Actions" /></th>				
						</tr>
					</tfoot>
				</table>
			</div>   		
    </body>
</html>