/*
* CKEditor module for dialog plugin
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
dialog.fileuploader = {};

dialog.fileuploader.open =function open (e,params) {

	$(e.target).find('.upload').each(function (index) {
			var uploader = new qq.FileUploader({
			    // pass the dom node (ex. $(selector)[0] for jQuery users)
			    element: $(this)[0],
			    // path to server-side upload script
			    action: $(this).attr("action"),
			    params: {identifier: $(this).attr("identifier"),direct: $(this).attr("direct"),sFileName:$(this).attr("sFileName")},
			    onProgress:function(id, fileName, loaded, total){
			    	$(".qq-upload-list").show();
			    },

			 template: '<div class="qq-uploader well">' +
         '<div class="qq-upload-drop-area well"><span>'+dialog.messages.dropfileshere+'</span></div>' +
         '<div class="qq-upload-button btn btn-primary">'+dialog.messages.upload+'</div>' +
         '<ul class="qq-upload-list nav nav-list"></ul>' +
      '</div>',
      fileTemplate: '<li>' +
      '<span class="qq-upload-file"></span>' +
      '<span class="qq-upload-spinner"></span>' +
      '<span class="qq-upload-size"></span>' +
      '<a class="qq-upload-cancel" href="#">Cancel</a>' +
      '<span class="qq-upload-failed-text">Failed</span>' +
  '</li>',
				onComplete: function(id, fileName, responseJSON){
					var upload=fileName+"|"+responseJSON.path+"|"+responseJSON.mimetype+"|"+responseJSON.identifier+"|"+responseJSON.fileCategory+"|"+responseJSON.direct+"|"+responseJSON.sFileName;
					
					$(this.element).append('<input type=\"hidden\" name=\"fileupload\" value=\"'+upload+'\" />');
					if (responseJSON.success==true) {
						$(".dialog-events").trigger("dialog-refresh",{dc:"ALL",id:id});
					} else {
						$(".dialog-events").trigger("dialog-message",{message:responseJSON.message,alertType:"error"});
					}
				
				}
			});
		});

	return false;

};



$(function() {
	$("body").on("dialog-open",dialog.fileuploader.open);
	//$("body").trigger("dialog-open");
});
