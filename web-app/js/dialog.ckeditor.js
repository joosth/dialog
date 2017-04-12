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
dialog.ckeditor = {};


dialog.ckeditor.init = function(e,params) {
	CKEDITOR.stylesSet.add('my_custom_style', [
	                                           //{ name: 'My Custom Block', element: 'h3', styles: { color: 'blue'} },
	                                           //{ name: 'My Custom Inline', element: 'span', attributes: {'class': 'mine'} },

                                               { name: 'Alert (info)', element: 'div', attributes: {'class':'alert alert-info'} },
                                               { name: 'Alert (danger)', element: 'div', attributes: {'class':'alert alert-danger'} },
                                               { name: 'Alert (warning)', element: 'div', attributes: {'class':'alert alert-warning'} },
                                               { name: 'Alert (success)', element: 'div', attributes: {'class':'alert alert-success'} },
	                                           { name: 'Important label', element: 'span', attributes: {'class':'label label-important'} },

	                                           { name: 'CSS code', element: 'pre', attributes: {'data-lang':'text/css'} },
	                                           { name: 'Groovy code', element: 'pre', attributes: {'data-lang':'text/x-groovy'} },
	                                           { name: 'HTML code', element: 'pre', attributes: {'data-lang':'text/html'} },
	                                           { name: 'Java code', element: 'pre', attributes: {'data-lang':'text/x-java'} },
	                                           { name: 'JavaScript code', element: 'pre', attributes: {'data-lang':'text/javascript'} },
	                                           { name: 'Less code', element: 'pre', attributes: {'data-lang':'text/x-less'} },
	                                           { name: 'Shell code', element: 'pre', attributes: {'data-lang':'text/x-sh'} },
	                                           { name: 'XML code', element: 'pre', attributes: {'data-lang':'application/xml'} }



	                                         ]);
CKEDITOR.config.stylesSet = 'my_custom_style';
CKEDITOR.config.enterMode = CKEDITOR.ENTER_BR;
CKEDITOR.config.protectedSource.push( /<g:[.\s\S]*>[.\s\S]*<\/g:[.\s\S]*>/gi );	 // Grails Tags
CKEDITOR.config.protectedSource.push( /<c:[.\s\S]*>[.\s\S]*<\/c:[.\s\S]*>/gi );	 // Catviz Tags
CKEDITOR.config.protectedSource.push( /<g:[.\s\S]*\/>/gi );	 // Grails Tags (Self-closing)
CKEDITOR.config.protectedSource.push( /<c:[.\s\S]*\/>/gi );	 // Catviz Tags (Self-closing)
CKEDITOR.config.format_pre = { element : 'div', attributes: {'class':'well'}};
CKEDITOR.config.contentsCss=dialog.baseUrl+'/static/bundle-bundle_dialog-bootstrap_head.css'


dialog.ckeditor.open =function open (e,params) {
    CKEDITOR.config.filebrowserImageBrowseUrl = dialog.baseUrl+"/"+params.controllerName+"/filemap/"+params.id;
    var toolbar = $(this).attr("toolbar");
    if (!toolbar) { toolbar="Compact" ; };
    var height = $(this).attr("height");
    if (!height) { height="auto"; };
    CKEDITOR.replace( this.id, {
        toolbar:toolbar,
        height:height
    });
    $(this).addClass("dialog-submit-events");
    $(this).addClass("dialog-close-events");
};

dialog.ckeditor.submit =function submit (e,params) {
	for ( instance in CKEDITOR.instances )
        CKEDITOR.instances[instance].updateElement();
	return false

}

dialog.ckeditor.close =function close (e,params) {
    CKEDITOR.instances[this.id].destroy(true);
	return false
};


$(function() {
    $(document).on("dialog-init",dialog.ckeditor.init);
	$(document).on("dialog-open",".ckeditor textarea",dialog.ckeditor.open);
	$(document).on("dialog-submit",".ckeditor textarea",dialog.ckeditor.submit);
	$(document).on("dialog-close",".ckeditor textarea",dialog.ckeditor.close);
});
