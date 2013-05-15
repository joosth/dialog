/*
* CodeMirror module for dialog plugin
*  
* Grails Dialog plug-in
* Copyright 2011 Open-T B.V., and individual contributors as indicated
* by the @author tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Affero General Public License
* version 3 published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.

* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see http://www.gnu.org/licenses
*/
dialog.datatables = {};


dialog.datatables.open =function open (e,params) {
	$(e.target).find('.detailTable').each( function(index) {	
		var curMatch = $(this);
		var tableId = curMatch.attr('id');
		var jsonUrl = curMatch.attr("jsonUrl");
		var newButton=curMatch.attr("newButton");
		var positionUrl = curMatch.attr("positionUrl");
		var controller = jsonUrl.split('/')[1]; //extract controller name from json url
		
		dialog.dataTableHashList[tableId] = curMatch.dataTable({
			"bProcessing": true,
			"bServerSide": true,
			"sAjaxSource": dialog.baseUrl+jsonUrl,
			"sPaginationType": "bootstrap",
			"bFilter": false,
			"bJQueryUI": false,
			"aoColumnDefs": [ 
								{ "bSortable": false, "aTargets": ["nonsortable"] }
							] ,
			"iDisplayLength":5,
			"aLengthMenu": [[5,10, 25, 50], [5,10, 25, 50 ]],
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
			
			"oLanguage": {
		     	 "sUrl": dialog.dataTablesLanguageUrl, 
		    	},
	    	"fnInitComplete": function() {
	    		if ( $(this).hasClass("rowreordering")) {
       				dialog.dataTableHashList[tableId].rowReordering(       				
       				{
       					 sURL:dialog.baseUrl+positionUrl,
                         sRequestType: "POST"
		
       				});       				
       			};
       			// Add NEW button ("parent()" is the div with class dataTables_wrapper)		       			
       			if (params.id != null && (!newButton || newButton!="false")) {
       				//curMatch.parent().find('div.dataTables_length').prepend('<span class="list-toolbar-button ui-widget-content ui-state-default"><span onclick="dialog.formDialog(null,\''+controller+'\', { refresh : \''+tableId+'\'}, { parentId : '+id+'})">New</span></span>&nbsp;');
       				curMatch.parent().find('div.toolbar').prepend('<div style="float:left;margin-right:10px;" class="btn-group"><span class="btn" onclick="dialog.formDialog(null,\''+controller+'\', { refresh : \''+tableId+'\'}, { parentId : '+params.id+'})">New</span></span>&nbsp;');
       			}
	    	}		    
		});
		// refresh dialog on event
		$("#"+tableId).bind("dialog-refresh",dialog.datatables.refreshDatatableEvent);
		$("#"+tableId).addClass("dialog-events");       			
	});
	return false;
}

/**
 * Respond to refresh event for a datatable
 * @param event
 * @param eventData
 */

dialog.datatables.refreshDatatableEvent = function refreshDatatableEvent(event,eventData) {
	if (dialog.options.refreshPage) {
		window.location.reload();
	} else {	
		var lastPage = eventData.id==null;
		
		if (eventData.dc!=null) {
	        var tableId="detailTable_" + eventData.dc.replace(".","_").replace("class ","");
	        
	        for(key in dialog.dataTableHashList) {
	        	// TODO this is a crude measure. All datatables will refresh. 
	        	// The logic between messages, dialogs and datatables needs to be fixed
	        	if (eventData.dc=="ALL" || key.toLowerCase().indexOf(eventData.dc.toLowerCase())!=-1) {
	        		dialog.datatables.refreshDataTable(key,dialog.dataTableHashList,lastPage)		
	        	}        	
	        }
		}
	}
}


dialog.datatables.refreshDataTable = function refreshDataTable(key, list, lastPage) {
	var curTable = list[key];
	if (typeof(curTable) !== 'undefined' && curTable != null) {
		if (lastPage == false) {
			curTable.fnDraw(false);
			//curTable.fnReloadAjax();
		} else {
			curTable.fnPageChange( 'last' );
		}
	}
}

$(function() {
	$("body").on("dialog-open",dialog.datatables.open);
	
});