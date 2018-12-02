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
    var searching=curMatch.attr("filter")=="true";
    var toolbar=curMatch.attr("toolbar") || "";
    var pageLength=parseInt(curMatch.attr("pageLength")) || 5;
    var rowReorder = curMatch.hasClass("rowreordering");
    var autoWidth = curMatch.attr("autoWidth")=="true";

    curMatch.on('init.dt', function () {
        if (rowReorder) {
            var curTable = curMatch.dataTable().api();
            curTable.on('row-reorder', function (e, details, edit) {
                for (var i = 0, ien = details.length ; i < ien ; i++) {
                    var row = curTable.row(details[i].node);
                    $.ajax({
                        type: "POST",
                        cache: false,
                        url: dialog.baseUrl + positionUrl,
                        data: { id: row.id(), fromPosition: details[i].oldData, toPosition: details[i].newData },
                        dataType: "json"
                    });
                }
            });
        }

        curMatch.parent().find('div.toolbar').prepend(toolbar);

        // Add NEW button
        var newString = dialog.messages['new'];
        if (!newString) {
            newString = "new";
        }
        if (!newButton || newButton != "false") {
            if (datatableType == "detail") {
                // only show detail table if parent is present
                if (params != null && (params.id != null)) {
                    curMatch.closest("div.dataTables_wrapper").find('div.toolbar div:first').prepend('<div style="float: left; margin-right: 20px;" class="btn-group"><label><span class="btn btn-default btn-sm" onclick="dialog.formDialog(null,\''+controller+'\', { refresh : \''+tableId+'\'}, { parentId : \''+params.id+'\'})">'+newString+'</span></label></div>');
                }
            } else {
                curMatch.closest("div.dataTables_wrapper").find('div.toolbar div:first').prepend('<div style="float: left; margin-right: 20px;" class="btn-group"><label><span class="btn btn-default btn-sm" onclick="dialog.formDialog(null,\''+controller+'\', { refresh : \''+tableId+'\'}, {})">'+newString+'</span></label></div>');
            }
        }
    }).DataTable({
        "autoWidth": autoWidth,
        "processing": true,
        "serverSide": true,
        "ajax": dialog.baseUrl + jsonUrl,
        "pagingType": "full_numbers",
        "searching": searching,
        "columnDefs": [
                { "targets": [-1, "nonsortable"], "orderable": false },
                { "targets": ["_all"], "orderable": true },
                { "targets": [-1], "className": "actions" }
            ] ,
        "pageLength": pageLength,
        "lengthMenu": [[5, 10, 25, 50, 100], [5, 10, 25, 50, 100]],
        "dom": "<'row toolbar'<'col-sm-6'l><'col-sm-6'f>>" +
            "<'row'<'col-sm-12'tr>>" +
            "<'row'<'col-sm-5'i><'col-sm-7'p>>",
        "language": dialog.messages.datatables.language,
        "rowReorder": rowReorder
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
	        	// TODO all tables are refreshed which is a little crude.

                $(".detailTable,table.datatable").each( function( index, element ) {
    		          dialog.datatables.refreshDataTable(element,lastPage);
                });
		}
	}
};


dialog.datatables.refreshDataTable = function (element, lastPage) {
	var curTable = $(element).dataTable().api();
	if (typeof(curTable) !== 'undefined' && curTable != null) {
		if (lastPage == false) {
			curTable.draw(false);
		} else {
			curTable.page('last').draw(false);
		}
	}
};

/**
 * Initialize simple HTML datatable
 */
dialog.datatables.openHtmlDatatable = function (e,params) {
    var pageLength=parseInt($(this).attr("pageLength")) || 5;

    /* Mark elements that need initialization after they escape pagination */
    $(this).find(".dialog-open-events").addClass("datatables-reinit");

    $(this).DataTable({
        "pageLength": pageLength,
        "lengthMenu": [[5, 10, 25], [5, 10, 25]],
        "dom": "<'row toolbar'<'col-sm-6'l><'col-sm-6'f>>" +
            "<'row'<'col-sm-12'tr>>" +
            "<'row'<'col-sm-5'i><'col-sm-7'p>>",
        "columnDefs": [
                { "targets": [-1, "nonsortable"], "orderable": false },
                { "targets": ["_all"], "orderable": true }
            ]
    });
    /* Unmark elements that are shown initially */
    $(this).find(".dialog-open-events").removeClass("datatables-reinit");
    return false;
}

dialog.datatables.htmlDraw= function (e) {
    var curTable = $(e.currentTarget).dataTable().api();
    var page=curTable.page.info().page;

    if (page!=0) {
        $(this).find(".dialog-open-events").filter(".datatables-reinit").trigger("dialog-open", { "this": this }).removeClass("datatables-reinit");
    }

    return false;
}



$(function() {
	$(document).on("dialog-open",".detailTable,table.datatable",dialog.datatables.open);
    $(document).on("dialog-open","table.html-datatable",dialog.datatables.openHtmlDatatable);
    $(document).on("draw.dt","table.html-datatable",dialog.datatables.htmlDraw);
});
