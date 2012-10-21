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

dialog.ckeditor.open =function open (e,params) {
 	CKEDITOR.config.filebrowserImageBrowseUrl = dialog.baseUrl+"/"+params.controllerName+"/filemap/"+params.id;
	CKEDITOR.config.enterMode = CKEDITOR.ENTER_BR;
	CKEDITOR.config.protectedSource.push( /<g:[.\s\S]*>[.\s\S]*<\/g:[.\s\S]*>/gi );	 // Grails Tags 
	CKEDITOR.config.protectedSource.push( /<c:[.\s\S]*>[.\s\S]*<\/c:[.\s\S]*>/gi );	 // Catviz Tags 
	CKEDITOR.config.protectedSource.push( /<g:[.\s\S]*\/>/gi );	 // Grails Tags (Self-closing) 
	CKEDITOR.config.protectedSource.push( /<c:[.\s\S]*\/>/gi );	 // Catviz Tags (Self-closing)
	
	CKEDITOR.config.toolbar = 'Compact';
	CKEDITOR.config.toolbar_Compact =
		[
		 	{ name: 'tools', items : [ 'Maximize', 'ShowBlocks' ] },
			{ name: 'clipboard', items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
			{ name: 'editing', items : [ 'Find','Replace','-','SelectAll','-','SpellChecker', 'Scayt' ] },	
			{ name: 'paragraph', items : [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote',
			                   			'-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'] },
   			'/',
			{ name: 'basicstyles', items : [ 'Bold','Italic','Underline', 'TextColor','-','RemoveFormat' ] },			
			{ name: 'styles', items : [ 'Format' ] },		
			{ name: 'links', items : [ 'Link','Unlink','Anchor' ] },
			{ name: 'insert', items : [ 'Image','Table','HorizontalRule','Smiley','SpecialChar' ] },			
			{ name: 'document', items : [ 'Source' , '-','About' ]},
		];
	
	
	$(e.target).find("td.ckeditor textarea").each( function() {
		var curMatch = $(this);
		var toolbar = $(this).attr("toolbar");
		if (!toolbar) { toolbar="Basic" };
		CKEDITOR.replace( this.id, {
			toolbar:toolbar
		});
	});
	return false

}

dialog.ckeditor.submit =function submit (e,params) {
	for ( instance in CKEDITOR.instances )
		    CKEDITOR.instances[instance].updateElement();
	return false

}

dialog.ckeditor.close =function close (e,params) {
	$(e.target).find("td.ckeditor textarea").each( function() {
		var curMatch = $(this);
		CKEDITOR.instances[this.id].destroy(true);
	});
	return false
}


$(function() {
	$("body").on("dialog-open",dialog.ckeditor.open);
	$("body").on("dialog-submit",dialog.ckeditor.submit);
	$("body").on("dialog-close",dialog.ckeditor.close);
});