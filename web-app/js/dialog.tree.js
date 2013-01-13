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

dialog.tree.treeSelect=function treeSelect(id) {	
	var dialogHTML = '<div title="Select"><form><div class="errors" style="display:none;"></div><div><div id="'+id+'-tree" style="overflow:auto;" class="tree" ><ul id="tree" class="filetree treeview" /></div></div></form></div>'
	
	var currentValue = $('#'+id+'-input').attr("value");	
	var treeRoot=$('#'+id+'-span').attr("treeRoot");
	var treeUrl=$('#'+id+'-span').attr("treeUrl");
	
	/* get with and height from span attributes, default to 450x250 */
	var treeDialogWidth=$('#'+id+'-span').attr("treeDialogWidth");
	treeDialogWidth=treeDialogWidth?treeDialogWidth:450;
	var treeDialogHeight=$('#'+id+'-span').attr("treeDialogHeight");	
	treeDialogHeight=treeDialogHeight?treeDialogHeight:250;
	
	var theDialog=$(dialogHTML).dialog({ 
		modal:false,
		width:treeDialogWidth,
		height:treeDialogHeight,
		buttons: { 
			"OK": function(e) {			 	
				var selectedElement=$('#'+id+'-tree').jstree('get_selected');
				var title=selectedElement.attr("title");
				var selectedId=selectedElement.attr('id')
				// Set the hidden field
				$('#'+id+'-input').attr("value",selectedId);
				$('#'+id+'-span span').html(title)				
				$( this ).dialog( "close" );			 	
		 	},
		 	Cancel: function() {
	        	$( this ).dialog( "close" );
        	},
		 },
		 close: function(event, ui) {      
        		theDialog.dialog("destroy").remove();
         },
		 open: function(event, ui) {
			 
			 var popupTree=$("#"+id+"-tree").jstree({
			        "plugins" : [  "json_data", "ui", "cookies", "dnd", "search", "types", "themes" ,"hotkeys"],			       
			        "json_data" : {
			            "ajax" : {
			                "url" : treeUrl,
			                cache: false,
			                
			                // This determines what is sent back to the server.
			                "data" : function (n) {
			                	// this determines the root of the tree.
			                    return { id : n.attr ? n.attr("id") : treeRoot ,currentValue:currentValue,treeRoot:treeRoot};
			                }
			            }
			        }			    
		        });
		 }
	 });
}

$(function() {
	
});