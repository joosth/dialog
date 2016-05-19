/*
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

/**
 * Capitalize a string
 */
String.prototype.capitalize = function() {
    return this.charAt(0).toUpperCase() + this.slice(1);
};

/**
 * Removed leading and trailing spaces
 * @param value
 * @returns
 */
dialog.trim = function trim(value) {
	  value = value.replace(/^\s+/,'');
	  value = value.replace(/\s+$/,'');
	  return value;
};

/**
 * Pack params in an url-friendly String
 * @param params
 * @returns {String}
 */
dialog.obj2ParamStr = function obj2ParamStr(params) {
	var paramStr="";
	 if (params) {
		 var sep = "?";
		 for (key in params) {
			 paramStr=paramStr+sep+key+"="+params[key];
			 sep="&";
		 }
	 }
	 return paramStr;
};

/**
 * Show a confirmation dialog
 */
dialog.confirm = function confirm(message, title, callback, data) {

	var dx = data;

    var dialogHTML =
        "<div class='modal fade' tabindex='-1' role='dialog'>" +
            "<div class='modal-dialog'>" +
                "<div class='modal-content'>" +
                    "<div class='modal-header'>" +
                        "<button type='button' class='close' data-dismiss='modal' aria-label='Close'><span aria-hidden='true'>&times;</span></button>" +
                        "<h4 class='modal-title'>" + title + "</h4>" +
                    "</div>" +
                    "<div class='modal-body'>" +
                        "<p>" + message + " ?</p>" +
                    "</div>" +
                    "<div class='modal-footer'>" +
                        "<button id='cancel' type='button' class='btn btn-default' data-dismiss='modal'>" + window.dialog.messages.cancel + "</button>" +
                        "<button id='confirm' type='button' class='btn btn-primary'>" + window.dialog.messages.ok + "</button>" +
                    "</div>" +
                "</div>" +
            "</div>" +
        "</div>"

    var theDialog = $(dialogHTML).on("show.bs.modal", function (event) {

        var confirmButton = $(this).find(".modal-footer button#confirm");

        confirmButton.click( function () {
            theDialog.modal("hide");
            if(callback) {
                callback(dx);
            }
        });
    }).on("hidden.bs.modal", function (event) {
        theDialog.data("bs.modal", null);
        theDialog.remove();
    }).modal();
};

/**
 * Show dialog
 * @param id
 * @param controllerName
 * @param options
 * @param urlParams
 * @returns {Boolean}
 */
dialog.formDialog = function formDialog(id,controllerName, options ,urlParams) {

    var urlId = id + dialog.obj2ParamStr(urlParams);

    var dialogName = (options != null && options["dialogname"] != null) ? options["dialogname"] : "dialog";
    var submitName = (options != null && options["submitname"] != null) ? options["submitname"] : "submit"+dialogName;

    // if true, form submit will be used instead of AJAX
    var submitForm = (options != null && options["submitform"] != null) ? options["submitform"] : false;

    // if true, form will not be submitted at all
    var noSubmit = (options != null && options["nosubmit"] != null) ? options["nosubmit"] : false;

    var domainClass = (options != null && options["domainclass"] != null) ? options["domainclass"] : controllerName.capitalize();

    theUrl = dialog.baseUrl + '/' + controllerName + '/' + dialogName + '/' + urlId;

    var errorMessage = null;
    var dialogHTML = $.ajax({
        url: theUrl,
        async: false,
        cache: false
    }).error(function(event, jqXHR, ajaxSettings, thrownError) {
        // If the error is authentication, reload the window to show the login dialog.
        if (event.status >= 400 && event.status < 500) {
            window.location.reload();
        } else {
            // If it is not authentication, something actually went wrong.
            // Store the errormessage so we can show it and bail out lateron
            errorMessage = event.getResponseHeader('X-Dialog-Error-Message');
        }
    }).responseText;

    // If an error occurred, show it and bail out.
    if (errorMessage) {
        $(".dialog-message-events").trigger("dialog-message",{message:errorMessage,alertType:'danger'});
        return;
    }

    var formelements=$(dialogHTML).find("form");
    if (formelements.length==0) {
        window.location.reload();
    } else {

        var theDialog = $(dialogHTML).on("show.bs.modal", function (event) {
            $(this).drags({handle:".modal-header"});
            // This will trigger all modules that want to receive open events; the second parameter is the params object that will be received by the event handler
            $(this).find('[data-toggle="tooltip"]').tooltip();

            var cancelButton = $(this).find(".modal-footer button#cancel");
            var saveButton = $(this).find(".modal-footer button#save");

            saveButton.click( function () {
                theDialog.trigger("dialog-submit", { "this": this, id: id, controllerName: controllerName } );
                theDialog.find(".dialog-submit-events").trigger("dialog-submit", { "this": this, id: id, controllerName: controllerName } );

                if (submitForm) {
                    theDialog.find("form").attr("action", dialog.baseUrl + "/" + controllerName + "/" + submitName + "/" + urlId);
                    theDialog.find("form").submit();
                    theDialog.modal("hide");
                } else if (!noSubmit) {
                    var formData = theDialog.find("form").serialize();
                    // Clear out error messages from any previous attempt
                    theDialog.find("div.errors").html("");
                    theDialog.find("p.error-message").html("");
                    theDialog.find(".error").removeClass("error");

                    $.post(dialog.baseUrl + "/" + controllerName + "/" + submitName + "/" + urlId, formData, function(data) {
                        var jsonResponse = data.result;

                        $(".dialog-refresh-events").trigger("dialog-refresh", { dc: domainClass, id: id, jsonResponse: jsonResponse } );

                        if (jsonResponse.success) {
                            $(".dialog-message-events").trigger("dialog-message", { message: jsonResponse.message } );
                            theDialog.modal("hide");
                            if (jsonResponse.nextDialog) {
                                dialog.formDialog(jsonResponse.nextDialog.id, jsonResponse.nextDialog.controllerName, jsonResponse.nextDialog.options, jsonResponse.nextDialog.urlParams);
                            }
                        } else {
                            for (fieldName in jsonResponse.errorFields) {
                                var errorMessage=jsonResponse.errorFields[fieldName];
                                $(".property-"+fieldName).addClass("has-error");
                                $(".property-"+fieldName).find("span.error-message").html(errorMessage);
                            }
                            theDialog.find("div.errors").html(jsonResponse.message);
                            theDialog.find("div.errors").show();
                        }
                    },"json");
                } else {
                    theDialog.modal("hide");
                }
            });
        }).on("shown.bs.modal", function(event) {
            if ($(this).find(".tab-pane").length>0) {
                $(this).find(".tab-pane").filter(".active").find(".dialog-open-events").not(".dialog-opened").trigger("dialog-open", { "this": this, id: id, controllerName: controllerName }).addClass("dialog-opened");
            } else {
                $(this).find(".dialog-open-events").not(".dialog-opened").trigger("dialog-open", { "this": this, id: id, controllerName: controllerName }).addClass("dialog-opened");
            }
        }).on("shown.bs.tab", function (event) {
            var targetRef=$(event.target).attr("href");
            var target=$(targetRef);
            $(target).find(".dialog-open-events").not(".dialog-opened").trigger("dialog-open", { "this": this, id: id, controllerName: controllerName }).addClass("dialog-opened");
            $(target).find("input[type!='hidden'], select, textarea").filter(":first").focus();
        }).on("hidden.bs.modal", function (event) {
            $(this).trigger("dialog-close", { event: event, "this": this } );
            $(this).find(".dialog-close-events").trigger("dialog-close", { event: event, "this": this } );
            theDialog.data("bs.modal", null);
            theDialog.remove();
        }).modal({animation:false});

    }
    return false;
};

/**
 * Show a delete dialog
 * @param id
 * @param controllerName
 * @param options
 * @param urlParams
 */
dialog.deleteDialog = function deleteDialog(id, controllerName, options, urlParams) {
    var urlId = id + dialog.obj2ParamStr(urlParams);
    var controllerTitle = controllerName.charAt(0).toUpperCase() + controllerName.slice(1);
    var dialogHTML =
        "<div class='modal fade' tabindex='-1' role='dialog'>" +
            "<div class='modal-dialog'>" +
                "<div class='modal-content'>" +
                    "<div class='modal-header'>" +
                        "<button type='button' class='close' data-dismiss='modal' aria-label='Close'><span aria-hidden='true'>&times;</span></button>" +
                        "<h4 class='modal-title'>" + dialog.messages.confirmdeleteTitle + "</h4>" +
                    "</div>" +
                    "<div class='modal-body'>" +
                        "<p>" + dialog.messages.confirmdelete + " " + controllerTitle + " " + id + " ?</p>" +
                    "</div>" +
                    "<div class='modal-footer'>" +
                        "<button id='cancel' type='button' class='btn btn-default' data-dismiss='modal'>" + window.dialog.messages.cancel + "</button>" +
                        "<button id='delete' type='button' class='btn btn-danger'>" + window.dialog.messages.delete + "</button>" +
                    "</div>" +
                "</div>" +
            "</div>" +
        "</div>"

    var domainClass = (options != null && options["domainclass"] != null) ? options["domainclass"] : controllerName.capitalize();

    var theDialog = $(dialogHTML).on("show.bs.modal", function (event) {
        $(this).drags({handle:".modal-header"});
        var deleteButton = $(this).find(".modal-footer button#delete");

        deleteButton.click( function () {
            var formData = theDialog.find("form").serialize();
            $.post(dialog.baseUrl + "/" + controllerName + "/delete/" + urlId, formData, function(data) {
                var result = data.result;

                $(".dialog-refresh-events").trigger("dialog-refresh", { dc: domainClass, id: id, jsonResponse: result } );
                $(".dialog-message-events").trigger("dialog-message", { message: result.message } );

                if (result.success) {
                    theDialog.modal("hide");
                } else  {
                    theDialog.find("div.errors").html(result.message);
                    theDialog.find("div.errors").show();
                }
            },"json");
        });
    }).on("hidden.bs.modal", function (event) {
        theDialog.data("bs.modal", null);
        theDialog.remove();
    }).modal();
};


/**
 * Show status message event handler
 * @param event
 * @param eventData
 * alert Types from http://getbootstrap.com/2.3.2/components.html#alerts :
 * - success (default,green)
 * - error
 * - info (blue)
 */
dialog.statusMessage = function statusMessage(event,eventData) {
	if (eventData.message) {
        var alertType="success";
        if (eventData.alertType!==undefined) {
            alertType=eventData.alertType;
        }
		var msg='<div id="alertmessage" class="alert alert-'+alertType+' fade"><button type="button" class="close" data-dismiss="alert">×</button><div>'+eventData.message+'</div></div>';
		$("#statusmessage").html(msg);
		$("#alertmessage").addClass("in");
		// TODO This is more annoying than helpful. Maybe make this configurable.
		//setTimeout( function() {$("#alertmessage").alert("close")}, 2000 );
	}
};

/**
 * Delete file dialog
 * @param id
 * @param controllerName
 * @param filename
 * @param options
 */
dialog.deleteFile = function deleteFile(id,controllerName, filename,options) {

    var dialogHTML =
        "<div class='modal fade' tabindex='-1' role='dialog'>" +
            "<div class='modal-dialog'>" +
                "<div class='modal-content'>" +
                    "<div class='modal-header'>" +
                        "<button type='button' class='close' data-dismiss='modal' aria-label='Close'><span aria-hidden='true'>&times;</span></button>" +
                        "<h4 class='modal-title'>Confirm delete</h4>" +
                    "</div>" +
                    "<div class='modal-body'>" +
                        "<p>Are you sure you want to delete " + filename + " from " + controllerName + " " + id + " ?</p>" +
                    "</div>" +
                    "<div class='modal-footer'>" +
                        "<button id='cancel' type='button' class='btn btn-default' data-dismiss='modal'>" + window.dialog.messages.cancel + "</button>" +
                        "<button id='delete' type='button' class='btn btn-danger'>" + window.dialog.messages.delete + "</button>" +
                    "</div>" +
                "</div>" +
            "</div>" +
        "</div>"

    var domainClass = (options != null && options["domainclass"] != null) ? options["domainclass"] : controllerName.capitalize();

    var theDialog = $(dialogHTML).on("show.bs.modal", function (event) {
        $(this).drags({handle:".modal-header"});
        var deleteButton = $(this).find(".modal-footer button#delete");

        deleteButton.click( function () {
            var formData = theDialog.find("form").serialize();
            $.post(dialog.baseUrl + "/" + controllerName + "/deletefile/" + id + "?filename=" + filename, formData, function(data) {
                var result = data.result;

                $(".dialog-refresh-events").trigger("dialog-refresh", { dc: domainClass, id: id } );
                $(".dialog-message-events").trigger("dialog-message", { message: result.message } );

                if (result.success) {
                    theDialog.modal("hide");
                } else  {
                    theDialog.find("div.errors").html(result.message);
                    theDialog.find("div.errors").show();
                }
            });
        });
    }).on("hidden.bs.modal", function (event) {
        theDialog.data("bs.modal", null);
        theDialog.remove();
    }).modal()
};

// https://github.com/jschr/bootstrap-modal/issues/39
// Draggable without JQuery UI
(function($) {
    $.fn.drags = function(opt) {

        opt = $.extend({handle:"",cursor:"move"}, opt);

        if(opt.handle === "") {
            var $el = this;
        } else {
            var $el = this.find(opt.handle);
        }

        return $el.css('cursor', opt.cursor).on("mousedown", function(e) {
            if(opt.handle === "") {
                var $drag = $(this).addClass('draggable');
            } else {
                var $drag = $(this).addClass('active-handle').parent().addClass('draggable');
            }
            var z_idx = $drag.css('z-index'),
                drg_h = $drag.outerHeight(),
                drg_w = $drag.outerWidth(),
                pos_y = $drag.offset().top + drg_h - e.pageY,
                pos_x = $drag.offset().left + drg_w - e.pageX;

            $drag.parents().on("mousemove", function(e) {
                $('.draggable').offset({
                    top:e.pageY + pos_y - drg_h,
                    left:e.pageX + pos_x - drg_w
                }).on("mouseup", function() {
                    $(this).removeClass('draggable').css('z-index', z_idx);
                });
            });
            e.preventDefault(); // disable selection
        }).on("mouseup", function() {
            if(opt.handle === "") {
                $(this).removeClass('draggable');
            } else {
                $(this).removeClass('active-handle').parent().removeClass('draggable');
            }
        });

    }
})(jQuery);

$(function() {
    moment.locale(window.dialog.language);

	$("#statusmessage").bind("dialog-message",dialog.statusMessage);
	$("#statusmessage").addClass("dialog-message-events");

    // Deconflict button() function of twitter bootstrap, see http://stackoverflow.com/questions/13809847/button-classes-not-added-in-jquery-ui-bootstrap-dialog
    var btn = $.fn.button.noConflict(); // reverts $.fn.button to jqueryui btn
    $.fn.btn = btn;

    // Show help text when ? is clicked
    $(document).on("click", ".help-action",function(e) {
        $(this).closest(".modal").find(".help-block").toggle();
    });
});
