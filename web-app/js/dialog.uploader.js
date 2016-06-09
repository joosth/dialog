/*
* uploader module for dialog plugin
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
dialog.uploader = {};


dialog.uploader.upload=function(file,options) {
    var xhr  = new XMLHttpRequest();
    var wrapper=options.wrapper;

    $(wrapper).find(".upload-progress").css("width","0%");
    $(wrapper).find(".upload-progress-percentage").html("0%");
    $(wrapper).find(".upload-progress-text").html(dialog.messages.uploading.replace("[0]",file.name));

    xhr.upload.onprogress = function(e){
        if (e.lengthComputable){
            var completed=e.loaded*100/e.total;
            var s=completed+"%";
            $(wrapper).find(".upload-progress").css("width",s);
            $(wrapper).find(".upload-progress-percentage").html(s);
        }
    };

    xhr.onreadystatechange = function(){
        if (xhr.readyState == 4){
            var response=$.parseJSON(this.response);
            if (!response.success|| response.success=="false") {
                $(".dialog-message-events").trigger("dialog-message",{message:response.message,alertType:"danger"});
            } else {
                if (options.params.direct === "false") {
                    var upload= file.name + "|" + response.path + "|" + response.mimetype + "|" + file.size;
                    $(wrapper).append('<input type=\"hidden\" name=\"fileupload\" value=\"' + upload + '\" />');
                }

                $(wrapper).find(".upload-progress-text").html(dialog.messages.uploadcompleted.replace("[0]",file.name));
                $(".dialog-message-events").trigger("dialog-message",{message:response.message,alertType:"success"});
                $(".dialog-refresh-events").trigger("dialog-refresh", {dc:"ALL"} );
            }
            $(".dialog-events").trigger("dialog-refresh",{});

            $(wrapper).find(".progress").delay(2000).fadeOut("slow");
            $(wrapper).find(".upload-progress-text").delay(2000).fadeOut("slow");
            $(wrapper).find(".upload-progress-row").delay(2000).fadeOut("slow");
        }
    };

    $(wrapper).find(".upload-progress-text").html(dialog.messages.uploading.replace("[0]",file.name));

    var url=dialog.baseUrl+'/'+options.controller+'/'+options.action;

    $.each(options.params,function (k,v) {
        if (url.indexOf("?")===-1) {
            url+='?'+k+'='+v;
        } else {
            url+='&'+k+'='+v;
        }
    });

    xhr.open("POST", url, true);
    xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
    xhr.setRequestHeader("X-File-Name", encodeURIComponent(file.name));
    //xhr.setRequestHeader("Content-Length", file.size);

    var mimetype="application/octet-stream";
    if (file.type){
        mimetype=file.type;
    }

    xhr.setRequestHeader("Content-Type", mimetype);
    xhr.send(file);
};




dialog.uploader.addDropHandler=function(catcher, options) {
    //var catcher=document.getElementById(id);
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
                dialog.uploader.upload(file,options);
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


dialog.uploader.open =function open (e,params) {

    var upload=function(event,eventData) {
        var wrapper=event.data.wrapper;
        $(wrapper).find(".progress").show();
        $(wrapper).find(".upload-progress-text").show();
        $(wrapper).find(".upload-progress-row").show();

        var input=$(this).find('input[type=file]')[0];
        var files=input.files;
        for (var i = 0, numFiles = files.length; i < numFiles; i++) {
            var file = files[i];
            dialog.uploader.upload(file,event.data);
        }
    };


    $(e.target).each(function () {
        var params={};
        for (var att, i = 0, atts = this.attributes, n = atts.length; i < n; i++){
            att = atts[i];
            if (att.nodeName.indexOf("param_")===0) {
                params[att.nodeName.substr(6)]=att.nodeValue;
            }
        }

        var options= {
            wrapper:this,
            params:params,
            controller:$(this).attr("controller"),
            action:$(this).attr("action")
        };

        $(this).find('.upload-button').each (function () {
            $(this).bind("change",options,upload);
        });

        dialog.uploader.addDropHandler(this,options);
    });
    return true;
};

$(function() {
	$(document).on("dialog-open",".upload-button-wrapper",dialog.uploader.open);
});
