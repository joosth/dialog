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
dialog.codemirror = {};
dialog.codemirror.editors = {};

dialog.codemirror.openpre =function open (e,params) {
    // TODO check if this is still correct (catviz?)
    CodeMirror.colorize([this]);
};

dialog.codemirror.open =function open (e,params) {
    // TODO
	$(e.target).find("pre").each( function() {
		CodeMirror.colorize([this]);
	});

    var id=$(this).attr("id");
    var width=$(this).attr("width");
    var height=$(this).attr("height");
    if (id){
        var mode = $(this).attr('codeMirrorMode');
        var textarea = document.getElementById(id);

        // Create editor based on codeMirrorMode attribute.
        if (mode=='text/html') {
            dialog.codemirror.editors[id] = CodeMirror.fromTextArea(textarea, {
                mode: 'text/html',
                lineNumbers: true,
                extraKeys: {
                    "'>'": function(cm) { cm.closeTag(cm, '>'); },
                    "'/'": function(cm) { cm.closeTag(cm, '/'); }
                }
          });

        }
        //
        if (mode=='text/x-groovy') {
            dialog.codemirror.editors[id] = CodeMirror.fromTextArea(textarea, {
                mode: 'text/x-groovy',
                lineNumbers: true,
                matchBrachets:true

          });
        }
        dialog.codemirror.editors[id].setSize(width,height);
    }
    
    $(this).addClass("dialog-submit-events");
    $(this).addClass("dialog-close-events");

}

dialog.codemirror.submit =function submit (e,params) {
    var id=$(this).attr("id");
    if (id) {
        dialog.codemirror.editors[id].save();
    }
}


dialog.codemirror.close =function close (e,params) {
    var id=$(this).attr("id");
    if (id) {
        dialog.codemirror.editors[id].toTextArea();
        delete dialog.codemirror.editors[id];
    }
};


$(function() {
	$(document).on("dialog-open",".codemirror textarea", dialog.codemirror.open);
    $(document).on("dialog-open","pre.codemirror", dialog.codemirror.openpre);
	$(document).on("dialog-submit",".codemirror textarea",dialog.codemirror.submit);
	$(document).on("dialog-close",".codemirror textarea",dialog.codemirror.close);
});
