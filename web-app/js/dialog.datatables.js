/*
* Datatables module for dialog plugin
*
* Grails Dialog plug-in
* Copyright 2013 Open-T B.V., and individual contributors as indicated
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
    var curMatch = $(this);
    var tableId = curMatch.attr('id');
    var jsonUrl = curMatch.attr("jsonUrl");
    var newButton=curMatch.attr("newButton");
    var datatableType=curMatch.attr("datatable-type") || "detail";
    var positionUrl = curMatch.attr("positionUrl");
    var controller = jsonUrl.split('/')[1]; //extract controller name from json url
    var bFilter=curMatch.attr("bFilter")=="true";
    var toolbar=curMatch.attr("toolbar") || "";
    var iDisplayLength=curMatch.attr("iDisplayLength") || 5;

    dialog.dataTableHashList[tableId] = curMatch.dataTable({
        "bProcessing": true,
        "bServerSide": true,
        "sAjaxSource": dialog.baseUrl+jsonUrl,
        "sPaginationType": "bootstrap",
        "bFilter": bFilter,
        "bJQueryUI": false,
        "aoColumnDefs": [
                { "bSortable": false, "aTargets": [ -1 ,"nonsortable"] },
                { "bSortable": true, "aTargets": ["_all"] },
                { "sClass": "actions" , "aTargets": [ -1 ] }
            ] ,
        "iDisplayLength":iDisplayLength,
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
        "oLanguage": dialog.messages.datatables.oLanguage,
        "fnInitComplete": function() {
            if ( $(this).hasClass("rowreordering")) {
                dialog.dataTableHashList[tableId].rowReordering(
                {
                     sURL:dialog.baseUrl+positionUrl,
                     sRequestType: "POST"

                });
            };

           curMatch.parent().find('div.toolbar').prepend(toolbar);

            // Add NEW button ("parent()" is the div with class dataTables_wrapper)

            var newString=this.dataTableSettings[0].oLanguage.sNew;
            if (!newString) {
                newString="new";
            }
            if (!newButton || newButton!="false") {
                if (datatableType=="detail") {
                    // only show detail table if parent is present
                    if (params != null && (params.id != null)) {
                        curMatch.parent().find('div.toolbar').prepend('<div style="float:left;margin-right:10px;" class="btn-group"><span class="btn" onclick="dialog.formDialog(null,\''+controller+'\', { refresh : \''+tableId+'\'}, { parentId : '+params.id+'})">'+newString+'</span></span>&nbsp;');
                    }
                } else {
                    curMatch.parent().find('div.toolbar').prepend('<div style="float:left;margin-right:10px;" class="btn-group"><span class="btn" onclick="dialog.formDialog(null,\''+controller+'\', { refresh : \''+tableId+'\'}, {})">'+newString+'</span></span>&nbsp;');
                }
            }
        }
    });
    // refresh dialog on event
    $("#"+tableId).bind("dialog-refresh",dialog.datatables.refreshDatatableEvent);
    $("#"+tableId).addClass("dialog-refresh-events");
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
	        	// TODO this is a crude measure. Commented the check out so that all datatables will refresh.
	        	//if (eventData.dc=="ALL" || key.toLowerCase().indexOf(eventData.dc.toLowerCase())!=-1) {
	        		dialog.datatables.refreshDataTable(key,dialog.dataTableHashList,lastPage);
	        	//}
	        }
		}
	}
};


dialog.datatables.refreshDataTable = function (key, list, lastPage) {
	var curTable = list[key];
	if (typeof(curTable) !== 'undefined' && curTable != null) {
		if (lastPage == false) {
			curTable.fnDraw(false);
			//curTable.fnReloadAjax();
		} else {
			curTable.fnPageChange( 'last' );
		}
	}
};

$(function() {
	$(document).on("dialog-open",".detailTable,table.datatable",dialog.datatables.open);
});
