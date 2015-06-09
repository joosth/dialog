/*
* TinyMCE module for dialog plugin
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
dialog.tinymce = {};

dialog.tinymce.open =function open (e,params) {
		var theme = $(this).attr('theme');
		var plugins=$(this).attr('plugins');


		$(this).tinymce({theme : theme,
            //mode : "textareas",
            plugins : plugins,
            media_external_list_url:dialog.baseUrl+"/"+params.controllerName+"/medialist/"+params.id,
            external_image_list_url:dialog.baseUrl+"/"+params.controllerName+"/imagelist/"+params.id,
            theme_advanced_buttons1_add : "media,fullscreen",

           	valid_elements : "*[*]",
       		verify_html : false,
       		force_p_newlines : false,
       		forced_root_block : false,

       		cleanup: false,

    	});

}


dialog.tinymce.close =function close (e,params) {
	 for(id in tinyMCE.editors){
         tinyMCE.execCommand('mceRemoveControl', true, id);
     		tinyMCE.remove(id);
       }
}


$(function() {
	$(document).on("dialog-open","td.tinymce textarea",dialog.tinymce.open);
	$(document).on("dialog-close","td.tinymce textarea",dialog.tinymce.close);

	$("td.tinymce textarea").tinymce({
		valid_elements : "*[*]",
		verify_html : false,
		cleanup: false,
		apply_source_formatting : false,
		extended_valid_elements : 'g:applyLayout',
        custom_elements : 'g:applyLayout' // Notice the ~ prefix to force a span element for the element

	});
});