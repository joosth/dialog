/*
* Tree module for dialog plugin
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
dialog.tree = {};

dialog.tree.treeSelect = function treeSelect(id) {

    var dialogHTML =
        "<div class='modal fade' tabindex='-1' role='dialog'>" +
            "<div class='modal-dialog modal-lg'>" +
                "<div class='modal-content'>" +
                    "<div class='modal-body'>" +
                        "<div title='Select'>" +
                            "<form>" +
                                "<div class='errors' style='display: none;'></div>" +
                                "<div>" +
                                    "<div id='" + id + "-tree' style='overflow: auto;' class='tree'>" +
                                        "<ul id='tree' class='filetree treeview' />" +
                                    "</div>" +
                                "</div>" +
                            "</form>" +
                        "</div>" +
                    "</div>" +
                    "<div class='modal-footer'>" +
                        "<button id='cancel' type='button' class='btn btn-default' data-dismiss='modal'>" + window.dialog.messages.cancel + "</button>" +
                        "<button id='confirm' type='button' class='btn btn-primary'>" + window.dialog.messages.ok + "</button>" +
                    "</div>" +
                "</div>" +
            "</div>" +
        "</div>";

    var currentValue = $('#' + id + '-input').val();
    var treeRoot = $('#' + id + '-span').attr("treeRoot");
    var treeUrl = $('#' + id + '-span').attr("treeUrl");
    var treeTypes = JSON.parse($('#' + id + '-span').attr("treeTypes"));

    var theDialog = $(dialogHTML).on("show.bs.modal", function (event) {
        $(this).drags({ handle: ".modal-header" });

        var popupTree = $(this).find("#" + id + "-tree").jstree({
            "core": {
                "data": {
                    "url": treeUrl,
                    "cache": false,
                    "data": function (node) {
                        return { id : node.id != "#" ? node.id : treeRoot, currentValue: currentValue, treeRoot: treeRoot } ;
                    }
                },
                "dblclick_toggle": false,
                "multiple": false
            },
            "plugins": ["types"],
            "types": {
                "default": {
                    "valid_children": [],
                    "icon": "fa fa-fw fa-question-circle"
                }
            }
        });
        $.extend(popupTree.jstree(true).settings.types, treeTypes);

        var cancelButton = $(this).find(".modal-footer button#cancel");
        var confirmButton = $(this).find(".modal-footer button#confirm");

        confirmButton.click( function () {
            var selectedElements = popupTree.jstree(true).get_selected(true);
            if (selectedElements.length > 0) {
                var title = selectedElements[0].text;
                var underscoreIndex = selectedElements[0].id.indexOf("_");
                var selectedId = (underscoreIndex > -1) ? selectedElements[0].id.substring(underscoreIndex + 1) : selectedElements[0].id;
                // Set the hidden field
                $('#' + id + '-input').val(selectedId);
                $('#' + id + '-span span').html(title);
            }
            theDialog.modal("hide");
        });
    }).on("shown.bs.modal", function(event) {
        $(this).find(".dialog-open-events").not(".dialog-opened").trigger("dialog-open", { "this": this }).addClass("dialog-opened");
    }).on("hidden.bs.modal", function (event) {
        $(this).trigger("dialog-close", { event: event, "this": this } );
        $(this).find(".dialog-close-events").trigger("dialog-close", { event: event, "this": this } );
        theDialog.data("bs.modal", null);
        theDialog.remove();
    }).modal({ animation: false });
};

$(function() {

});
