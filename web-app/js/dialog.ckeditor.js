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

if (!window.dialog) {
    window.dialog = {};
}

if (!window.dialog.ckeditor) {
    window.dialog.ckeditor = {};
}

dialog.ckeditor.init = function (e, params) {

    CKEDITOR.config.language = dialog.language;

    CKEDITOR.config.enterMode = CKEDITOR.ENTER_BR;

    CKEDITOR.config.protectedSource.push( /<g:[.\s\S]*>[.\s\S]*<\/g:[.\s\S]*>/gi );	 // Grails Tags
    CKEDITOR.config.protectedSource.push( /<c:[.\s\S]*>[.\s\S]*<\/c:[.\s\S]*>/gi );	 // Catviz Tags
    CKEDITOR.config.protectedSource.push( /<g:[.\s\S]*\/>/gi );	 // Grails Tags (Self-closing)
    CKEDITOR.config.protectedSource.push( /<c:[.\s\S]*\/>/gi );	 // Catviz Tags (Self-closing)

    CKEDITOR.config.contentsCss = dialog.baseUrl + "/static/bundle-bundle_dialog-bootstrap_head.css";

    CKEDITOR.config.toolbar = "Compact";
    CKEDITOR.config.toolbar_Compact =
        [
            { name: "tools",        items: ["Maximize", "ShowBlocks"] },
            { name: "clipboard",    items: ["Cut", "Copy", "Paste", "PasteText", "PasteFromWord", "-", "Undo", "Redo"] },
            { name: "editing",      items: ["Find", "Replace", "-", "SelectAll", "-", "SpellChecker", "Scayt"] },
            { name: "paragraph",    items: ["NumberedList", "BulletedList", "-", "Outdent", "Indent", "-", "Blockquote", "-", "JustifyLeft", "JustifyCenter", "JustifyRight", "JustifyBlock"] },
            "/",
            { name: "basicstyles",  items: ["Bold", "Italic", "Underline", "TextColor", "-", "RemoveFormat"] },
            { name: "styles",       items: ["Format"] },
            { name: "links",        items: ["Link", "Unlink", "Anchor"] },
            { name: "insert",       items: ["Image", "Table", "HorizontalRule", "Smiley", "SpecialChar"] },
            { name: "document",     items: ["Source" , "-", "About"] }
        ];
};

dialog.ckeditor.open = function (e, params) {

    // Determine the URL for the filemap. As this is global all ckeditors in the same dialog share this URL.
    var controllerName = $(this).attr("imageBrowserControllerName");
    if (!controllerName) {
        controllerName = params.controllerName;
    }
    var id = $(this).attr("imageBrowserId");
    if (!id) {
        id = params.id;
    }
    CKEDITOR.config.filebrowserImageBrowseUrl = dialog.baseUrl + "/" + controllerName + "/filemap/" + id;

    var toolbar = $(this).attr("toolbar");
    if (!toolbar) {
        toolbar = "Compact";
    }

    var height = $(this).attr("height");
    if (!height) {
        height = "auto";
    }

    CKEDITOR.replace( this.id, {
        toolbar: toolbar,
        height: height
    });

    $(this).addClass("dialog-submit-events");
    $(this).addClass("dialog-close-events");
};

dialog.ckeditor.submit = function (e, params) {

    // TODO this updates ALL
    for (instance in CKEDITOR.instances) {
        CKEDITOR.instances[instance].updateElement();
    }

    return false;
};

dialog.ckeditor.close = function (e, params) {

    CKEDITOR.instances[this.id].destroy(true);

    return false;
};


$(function() {
    $(document).on("dialog-init", dialog.ckeditor.init);
    $(document).on("dialog-open", ".ckeditor textarea", dialog.ckeditor.open);
    $(document).on("dialog-submit", ".ckeditor textarea", dialog.ckeditor.submit);
    $(document).on("dialog-close", ".ckeditor textarea", dialog.ckeditor.close);
});
