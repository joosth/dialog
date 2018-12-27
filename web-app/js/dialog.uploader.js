/*
 * Dialog - Upload JavaScript.
 *
 * Copyright 2009-2017, Open-T B.V., and individual contributors as indicated
 * by the @author tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License
 * version 3 published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see http://www.gnu.org/licenses
 */
dialog.uploader = { globalProgress:0 };


/**
 * Handle the XHR ready state for DONE (i.e. ready state 4). Added functionality
 * for adding a file to a data table.
 *
 * @param file The file to handle.
 * @param options A map with several options that were provided to the uploader.
 * @param response The response object from the server.
 * @param wrapper The file upload wrapper.
 *
 * @since 12/04/2017
 */
dialog.uploader.handleReadyStateDone = function(file, options, response, wrapper) {
    if (!response.success || response.success == "false") {
        $(".dialog-message-events").trigger("dialog-message", {
            message: response.message,
            alertType: "danger"
        });
    } else {
        if (options.params.direct === "false") {
            var name = file.name;
            var size = file.size;
            var mimetype = response.data.mimetype;

            var uuid = response.data.uuid;
            $(wrapper).append("<input type=\"hidden\" name=\"fileupload\" value=\"" + uuid + "\" />");

            var dataTable = $(wrapper).find("#fileupload").dataTable();
            var deleteButton = "<a href=\"#\" class=\"btn btn-default btn-danger upload-delete-button\" row-id=\"" +
                uuid + "\" controller=\"" + options.controller + "\"><i class=\"fa fa-trash-o\"></i></a>";

            var rowId = dataTable.fnAddData([ name, mimetype, size, deleteButton ]);
            var row = dataTable.fnGetNodes(rowId);
            $(row).attr("id", uuid);
        } else {
            $(".dialog-message-events").trigger("dialog-message", { message: response.message, alertType: "success" });
        }
        $(wrapper).find(".upload-progress-text").html(dialog.messages.uploadcompleted.replace("{0}", file.name));
        $(".dialog-refresh-events").trigger("dialog-refresh", { dc: "ALL" });
    }

    $(".dialog-events").trigger("dialog-refresh", { });
    $(wrapper).find(".progress").delay(2000).fadeOut("slow");
    $(wrapper).find(".upload-progress-text").delay(2000).fadeOut("slow");
    $(wrapper).find(".upload-progress-row").delay(2000).fadeOut("slow");
};

/**
 * 12/04/2017 - Cleanup. Moved the XHR OnReadyStateChange event to the dialog.uploader.handleReadyStateDone
 * method for better overview.
 * 12/05/2017 - Enforce the user to only upload 1 file when the form can only have
 * a single file. This is done by uploading async.
 */
dialog.uploader.upload = function(file, options,fileNo,numFiles) {
    var xhr = new XMLHttpRequest();
    var wrapper = options.wrapper;


    if (fileNo==0) {
        dialog.uploader.globalProgress=0;

        $(wrapper).find(".upload-progress").css("width", "0%");
        $(wrapper).find(".upload-progress-percentage").html("0%");
        $(wrapper).find(".upload-progress-text").html(dialog.messages.uploading.replace("{0}", file.name));
    }

    xhr.upload.onprogress = function(e) {
        if (e.lengthComputable && (numFiles==1)) {
            var completed = Math.round(e.loaded * 100 / (e.total));
            var s = completed + "%";
            $(wrapper).find(".upload-progress").css("width", s);
            $(wrapper).find(".upload-progress-percentage").html(s);
        }
    };

    xhr.onreadystatechange = function() {
        switch (xhr.readyState) {
        case 0:
            /* TODO - Unsent */
            break;
        case 1:
            /* TODO - Opened */
            break;
        case 2:
            /* TODO - Headers received */
            break;
        case 3:
            /* TODO - Loading */
            break;
        case 4:
            var response = $.parseJSON(this.response);
            dialog.uploader.handleReadyStateDone(file, options, response, wrapper);
                var gp = dialog.uploader.globalProgress++;
                var completed = Math.round((gp+1) * 100 / (numFiles));
                var s = completed + "%";
                $(wrapper).find(".upload-progress").css("width", s);
                $(wrapper).find(".upload-progress-percentage").html(s);
            break;
        }
    };

    $(wrapper).find(".upload-progress-text").html(dialog.messages.uploading.replace("{0}", file.name));

    var url = dialog.baseUrl + "/" + options.controller + "/" + options.action;
    $.each(options.params, function(k, v) {
        if (url.indexOf("?") === -1) {
            url += "?" + k + "=" + v;
        } else {
            url += "&" + k + "=" + v;
        }
    });

    xhr.open("POST", url, true);
    xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
    xhr.setRequestHeader("X-File-Name", encodeURIComponent(file.name));

    var mimetype = "application/octet-stream";
    if (file.type) {
        mimetype = file.type;
    }

    xhr.setRequestHeader("Content-Type", mimetype);
    xhr.send(file);
};

/**
 * Initialize the DataTable for the fileuploads right away.
 * @since 12/04/2017
 *
 * 12/05/2017 - Added the lengthChange and searching parameters to the initialization
 * of the data table to make it smaller.
 */
dialog.uploader.initializeFileuploadTable = function(event, data) {
    $(document).find("#fileupload").dataTable({ lengthChange: false, searching: false });
};

/**
 * Remove an upload from the uploads list.
 * @since 12/04/2017
 */
dialog.uploader.removeUpload = function(event, data) {
    /* Find the UUID for the row and fileupload. */
    var uuid = $(this).attr("row-id");
    var controller = $(this).attr("controller");

    /* Post the remove action to the backend. */
    var url = dialog.baseUrl + "/" + controller + "/removefile";
    $.post(url, { uuid: uuid })
        .done(function(data) {
            if (!data.success) {
                return;
            }

            /* Get the table and remove the row from the table. Only do this when
            the document was successfully removed in the backend. */
            var table = $(document).find("#fileupload").DataTable();
            var row = $("#" + uuid);
            table.row(row).remove().draw();
            /* Get and remove the hidden input field. */
            $("input[value=\"" + uuid + "\"]").remove();
        });
};

dialog.uploader.addDropHandler=function(catcher, options) {
    if(catcher) {
        catcher.addEventListener("drop", function (e) {
            if (e.preventDefault) e.preventDefault();
            $(this).removeClass("dropping");
            $(catcher).find(".progress").show();
            $(catcher).find(".upload-progress-text").show();
            $(catcher).find(".upload-progress-row").show();
            var files=e.dataTransfer.files;

            for (var i = 0, numFiles = files.length; i < numFiles; i++) {
                var file = files[i];
                dialog.uploader.upload(file,options,i,numFiles);
            }

            return false;
        },false);

        catcher.addEventListener("dragenter", function (e) {
            $(this).addClass("dropping");
            return false;
        },false);

        catcher.addEventListener("dragleave", function (e) {
            $(this).removeClass("dropping");
            return false;
        },false);

        catcher.addEventListener("dragover", function (e) {
            var files=e.dataTransfer.files;
            if (e.preventDefault) e.preventDefault();

            $(this).addClass("dropping");
            return false;
        },false);
    }

};

/**
 * 12/04/2017 - Cleanup. Added logic to clear the file input after uploading. This
 * prevented the user from uploading the file, deleting it and then trying to upload
 * it again.
 * 12/05/2017 - Added a check for single-attachment uploading.
 */
dialog.uploader.open = function open(e, params) {
    var upload = function(event, eventData) {
        var wrapper = event.data.wrapper;
        $(wrapper).find(".progress").show();
        $(wrapper).find(".upload-progress-text").show();
        $(wrapper).find(".upload-progress-row").show();

        var input = $(this).find("input[type=file]")[0];

        var files = input.files;
        var options = event.data;
        /* 12/05/2017 - Ensure that only the first file is uploaded when multiple
        are selected, but only one is allowed. */
        if (options.params.single == "true") {
            dialog.uploader.upload(files[0], options,1,1);
        } else {
            for (var i = 0, numFiles = files.length; i < numFiles; i++) {
                var file = files[i];
                dialog.uploader.upload(file, options,i,numFiles);
            }
        }

        /* 12/04/2017 - Clear the input files list. */
        input.value = "";
    };

    $(e.target).each(function() {
        var params = { };
        for (var att, i = 0, atts = this.attributes, n = atts.length; i < n; i++) {
            att = atts[i];
            if (att.nodeName.indexOf("param_") === 0) {
                params[att.nodeName.substr(6)] = att.nodeValue;
            }
        }

        var options = {
            wrapper: this,
            params: params,
            controller: $(this).attr("controller"),
            action: $(this).attr("action")
        };

        $(this).find('.upload-button').each(function() {
            $(this).bind("change", options, upload);
        });

        dialog.uploader.addDropHandler(this, options);
    });

    return true;
};

/**
 *
 */
dialog.uploader.dropZoneDragOver = function() {
    $(this).addClass("drop");
    return false;
};

/**
 *
 */
dialog.uploader.dropZoneDragLeave = function() {
    $(this).removeClass("drop");
    return false;
};

/**
 * 12/04/2017 - Added the removeUpload and initializeFileuploadTable events.
 */
$(function() {
    $(document).on("dialog-open", ".fileuploadTable", dialog.uploader.initializeFileuploadTable);
    $(document).on("dialog-open", ".upload-button-wrapper", dialog.uploader.open);
    $(document).on("dragover", ".upload-drop-zone", dialog.uploader.dropZoneDragOver);
    $(document).on("dragleave", ".upload-drop-zone", dialog.uploader.dropZoneDragLeave);
    $(document).on("click", ".upload-delete-button", dialog.uploader.removeUpload);
});
