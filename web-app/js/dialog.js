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

String.prototype.capitalize = function() {
    return this.charAt(0).toUpperCase() + this.slice(1);
}

/*
 * Modal JQuery UI confirmation dialog
 */



dialog.confirm = function confirm(message,title,url) {
	var htmlMessage='<div id="dialog-confirm" title="'+title+'"><p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>'+message+'</p></div>';
	
	var confirmDialog=$(htmlMessage).dialog({
		resizable: true,
		width:600,
		//height:140,
		modal: true,
		buttons: {
			"OK": function() {								
				$( this ).dialog( "close" );
			},
			Cancel: function() {
				$( this ).dialog( "close" );
			}
		},
        close: function(event, ui) {      
            confirmDialog.remove();
            }
        });			
	
}


dialog.trim = function trim(value) {
	  value = value.replace(/^\s+/,'');
	  value = value.replace(/\s+$/,'');
	  return value;
	}

dialog.formDialog = function formDialog(id,controllerName, options ,urlParams) {
	var urlId=id+dialog.obj2ParamStr(urlParams);
	
	var dialogName = (options != null && options["dialogname"] != null) ? options["dialogname"] : "dialog";
	var submitName = (options != null && options["submitname"] != null) ? options["submitname"] : "submitdialog";
	var refreshTableKey = (options != null && options["refresh"] != null) ? options["refresh"] : "NO_REFRESH";
	var submitForm = (options != null && options["submitform"] != null) ? options["submitform"] : false;
	
	var domainClass = (options != null && options["domainclass"] != null) ? options["domainclass"] : controllerName.capitalize();
	
	 theUrl=dialog.baseUrl+'/'+controllerName+'/'+dialogName+'/'+urlId	 	
	 
	 var dialogHTML = $.ajax({
		  url: theUrl,
		  async: false,
		  cache: false
		 }).responseText;
	 
	 var formelements=$(dialogHTML).find('form')
	 if (formelements.length==0) {
		 window.location.reload()
	 } else {
	 
	 var theWidth=$(dialogHTML).css("width");
     //var theWidth=600;
	 
	 var theDialog=$(dialogHTML).dialog({ 
		 modal:true,
		 width:theWidth,
		 buttons: { 
		 	"OK": function(e) {
		 		if (submitForm) {
		 			theDialog.find("form").attr("action",dialog.baseUrl+"/"+controllerName+"/"+submitName+"/"+urlId);
		        	theDialog.find("form").submit();
		        	$( this ).dialog( "close" );
		 		} else {
		 		
			 	var formData=theDialog.find("form").serialize();
			 	$.post(dialog.baseUrl+"/"+controllerName+"/"+submitName+"/"+urlId,formData, function(data) 
			 		{
			 		var jsonResponse = data.result;
			 		
			 		$(".dialog-events").trigger("dialog-refresh",{dc:domainClass,id:id})
			 		$(".dialog-events").trigger("dialog-message",{message:jsonResponse.message})
			 					 		
			 		if(jsonResponse.success){
				 		theDialog.dialog("close");
				 	} else  {
				 		for (key in jsonResponse.errorFields) {
				 			var errorField=jsonResponse.errorFields[key]
				 			$("#"+errorField).parent().addClass("errors")				 			
				 		}
				 		theDialog.find("div.errors").html(jsonResponse.message)
				 		theDialog.find("div.errors").show();				 		
				 	
			 		}
			 	});	
		 		}
        	},

       	Cancel: function() {
	        		$( this ).dialog( "close" );
	        	}
       	},
        open: function(event, ui) { 
        	/*
        	 * First attempt to have a default submit on <enter>
        	 $(this).find("form").first().unbind('submit');
        	 $(this).find("form").first().submit(function(){
             //simulate click on create button
        		 $(this).parents('.ui-dialog').first().find('.ui-button').first().click();
        		 return false;
        	 });
        	 */
        	
        
        	
        	 $(this).keyup(function(e) {
        		    if (e.keyCode == 13) {
        		    	$(this).parents('.ui-dialog').first().find('.ui-button').first().click();
               		 return false;
        		    }
        		});
        	

        	 /*
        	$(this).find("td.tinymce textarea").tinymce({theme : "advanced",
                mode : "textareas",
                plugins : "media",
                media_external_list_url:dialog.baseUrl+"/js/medialist.js",               
                external_image_list_url:dialog.baseUrl+"/"+controllerName+"/imagelist/"+id,
                theme_advanced_buttons1_add : "media"
        	});
        	*/

        	 
        	$(this).find("td.tinymce textarea").each( function() {
        		var curMatch = $(this);
       			var theme = curMatch.attr('theme');
       			var plugins=curMatch.attr('plugins');

        		$(this).tinymce({theme : theme,
                    //mode : "textareas",
                    plugins : plugins,
                    media_external_list_url:dialog.baseUrl+"/"+controllerName+"/medialist/"+id,               
                    external_image_list_url:dialog.baseUrl+"/"+controllerName+"/imagelist/"+id,
                    theme_advanced_buttons1_add : "media"
            	});
            	        		
        	});
        	
        	
        	
         	// Initialize date picker input elements
       		$(this).find(".datepicker").datepicker({ dateFormat: "yy-mm-dd" , changeMonth: true, changeYear:true});
       		$(this).find(".dialogtabs").tabs();
       		$(this).find(".altselect").altselect();
       		
       		$(this).find('.upload').each(function (index) {
       			var theAction=$(this).attr("action");
       			var uploader = new qq.FileUploader({
       			    // pass the dom node (ex. $(selector)[0] for jQuery users)
       			    element: $(this)[0],
       			    // path to server-side upload script
       			    action: $(this).attr("action"),
       			    params: {identifier: $(this).attr("identifier"),direct: $(this).attr("direct"),sFileName:$(this).attr("sFileName")},
       			    onProgress:function(id, fileName, loaded, total){
       			    	$(".qq-upload-list").show();       			    	
       			    },
       				
       				onComplete: function(id, fileName, responseJSON){
       					var upload=fileName+"|"+responseJSON.path+"|"+responseJSON.mimetype+"|"+responseJSON.identifier+"|"+responseJSON.fileCategory+"|"+responseJSON.direct+"|"+responseJSON.sFileName;
       					// Preventing the submit-time copy is a bad idea for new entries
       					//if (!$(this).attr("direct") || $(this).attr("direct")=="false") {
       						$(this.element).append('<input type=\"hidden\" name=\"fileupload\" value=\"'+upload+'\" />');
       					//}
       					$(".dialog-events").trigger("dialog-refresh",{dc:domainClass,id:id});
       					$(".qq-upload-list").hide();
       				}
       			});       			
       		});
       		
       		//$(".refresh-image").bind("dialog-refresh",dialog.refreshImageEvent);
       		
       		
       		var dataTable = $(this).find('.detailTable');
       		
       		$(this).find('.detailTable').each(function (index) {
       			var curMatch = $(this);
       			var tableId = curMatch.attr('id');
       			var jsonUrl = curMatch.attr("jsonUrl");
       			var newButton=curMatch.attr("newButton");
       			var positionUrl = curMatch.attr("positionUrl");
       			var controller = jsonUrl.split('/')[1]; //extract controller name from json url
       			
       			dialog.dataTableHashList[tableId] = curMatch.dataTable({
    				"bProcessing": true,
    				"bServerSide": true,
    				"sAjaxSource": dialog.baseUrl+jsonUrl,
    				"sPaginationType": "full_numbers",
    				"bFilter": false,
    				"bJQueryUI": true
    			});
       			
       			if ( $(this).hasClass("rowreordering")) {
       				dialog.dataTableHashList[tableId].rowReordering(       				
       				{
       					 sURL:dialog.baseUrl+positionUrl,
                         sRequestType: "POST"
		
       				});       				
       			}
       			
       			
       			// Add NEW button ("parent()" is the div with class dataTables_wrapper)
       			if (id != null && (!newButton || newButton!="false")) {
       				curMatch.parent().find('div.dataTables_length').prepend('<span class="list-toolbar-button ui-widget-content ui-state-default"><span onclick="dialog.formDialog(null,\''+controller+'\', { refresh : \''+tableId+'\'}, { parentId : '+id+'})">New</span></span>&nbsp;');
       			}
       			
       		});
       		
       		// get z-index of dialog so we can put cluetips above it
       		var parentZIndex=parseInt($(this.parentNode).css('z-index'));
       		
       		$(this).find(".help").cluetip({
       			splitTitle: '|',  
       			cluezIndex: parentZIndex+1
      	    	});
       		
       		//$(".autocomplete").autocomplete({source:"/boekhouding/rekening/autocomplete"})
       		
       		$("input.autocomplete").each(function (index) {
       			var curMatch = $(this);
       			var jsonUrl = curMatch.attr("jsonUrl");
       			curMatch.autocomplete({source:jsonUrl,
       									minLength:0,
       				select: function( event, ui ) {
       					$( this ).val( ui.item.label );
       					var name=$( this ).attr("name");       					
       					name=name.replace("-entry","");
       					$('[name="'+ name+'.id"]' ).val( ui.item.value );
       					$('[name="'+ name+'.id"]' ).attr("label", ui.item.label );
       					if (ui.item.description) {
       						$('#'+name+'-description' ).html( ui.item.description);
       					}
       					$('#'+name+'-container' ).addClass("ac-selected");
       					$('#'+name+'-container' ).removeClass("ac-idle");
       					$('#'+name+'-container' ).removeClass("ac-selecting");
       					// nice idea! should use this.
       					//$( "#project-icon" ).attr( "src", "images/" + ui.item.icon );

       					return false;
       				},
       			   change: function(event, ui) {
       				  // var value=this.attributes["value"];
       				   //$( this ).val( value.nodeValue );
       				   
       				   var name=$( this ).attr("name");       					
       				   var currentValue=$( this ).val();
       				   
       				   name=name.replace("-entry","");       				   
       				   var label=$('[name="'+ name+'.id"]' ).attr("label");
       				   /*
       				   if (currentValue=="" || currentValue=="-") {
       					$('[name='+ name+'.id]' ).val("null");
       				   } else {
       					   $( this ).val( label );
       				   }
       				   
       				   $('#'+name+'-container' ).addClass("ac-selected");
       				   $('#'+name+'-container' ).removeClass("ac-idle");
       				   $('#'+name+'-container' ).removeClass("ac-selecting");
       				   */
       				   $(this).trigger("change");
       				   $('[name="'+ name+'.id"]' ).trigger("change",this);
       				   return false;
       			   },
       				
       				focus: function( event, ui ) {
       					$( this ).val( ui.item.label );
       					var name=$( this ).attr("name");       					
       					name=name.replace("-entry","");
       					$('#'+name+'-container' ).removeClass("ac-selected");
       					$('#'+name+'-container' ).removeClass("ac-idle");
       					$('#'+name+'-container' ).addClass("ac-selecting");       					
       					return false;
       				}
       			}).data( "autocomplete" )._renderItem = function( ul, item ) {       					
       					var desc = item.description ? item.description : ""
       				return $( "<li></li>" )
    				.data( "item.autocomplete", item )
    				.append( "<a>" + item.label + "<br><span class=\"autocomplete-description\">" + desc + "</span></a>" )
    				.appendTo( ul );
       			};
       			
       		}
       		);
       		$("input.autocomplete").blur(function() {
       			   var name=$( this ).attr("name");       					
				   var currentValue=$( this ).val();
				   
				   name=name.replace("-entry","");       				   
				   var label=$('[name="'+ name+'.id"]' ).attr("label");
				   
				   if (currentValue=="" || currentValue=="-") {
					$('[name="'+ name+'.id"]' ).val("null");
				   } else {
					   $( this ).val( label );
				   }
				   
				   $('#'+name+'-container' ).addClass("ac-selected");
				   $('#'+name+'-container' ).removeClass("ac-idle");
				   $('#'+name+'-container' ).removeClass("ac-selecting");
				   
       		});
       		
       		
       		$(this).find("input[type!='hidden'],select,textarea").filter(":first").focus();

       		
         },
        close: function(event, ui) {
        	//$(".tinymce").tinymce().remove();
        
       	 for(id in tinyMCE.editors){
           tinyMCE.execCommand('mceRemoveControl', true, id);
       		tinyMCE.remove(id);
         }
        var test="test"
        /*	$(this).find("td.tinymce textarea").each( function() {
        		//tinyMCE.remove(this)
        		tinyMCE.triggerSave();
        		tinyMCE.execCommand('mceFocus', false,"#content");
        		tinyMCE.execCommand('mceRemoveControl', false, "#content");
        	});
        	*/
               theDialog.dialog("destroy").remove();
               //dialogHTML=null;
               //var test=dialogHTML;
             }
         });
	 
	 }
}


dialog.deleteDialog = function deleteDialog(id,controllerName, options ,urlParams) {
	var urlId=id+dialog.obj2ParamStr(urlParams);
	 
	 var dialogHTML = '<div "title="Confirm delete"><form><div class="errors" style="display:none;"></div><div>Are you sure you want to delete '+controllerName+' '+id+' ?</div></form></div>'	 
	 var domainClass = (options != null && options["domainclass"] != null) ? options["domainclass"] : controllerName.capitalize();

	 var theDialog=$(dialogHTML).dialog({ 
		 modal:false,
		 width:400,
		 height:100,
		 buttons: { 
		 	"Delete": function(e) {
			 	var formData=theDialog.find("form").serialize();
			 	$.post(dialog.baseUrl+"/"+controllerName+"/delete/"+urlId,formData, function(data) 
			 		{
			 		var result=data.result			 		

			 		
			 		$(".dialog-events").trigger("dialog-refresh",{dc:domainClass,id:id})
			 		$(".dialog-events").trigger("dialog-message",{message:result.message})
			 		
			 		if(result.success){
				 		theDialog.dialog("close");
				 	} else  {				 		
				 		theDialog.find("div.errors").html(result.message)
				 		theDialog.find("div.errors").show();				 		
				 	}			 		
			 	});			 	
       	},
      	Cancel: function() {
	        		$( this ).dialog( "close" );
	        	}
      	},
       close: function(event, ui) {      
              theDialog.dialog("destroy").remove();
            }
        });
}

dialog.refreshDatatableEvent = function refreshDatatableEvent(event,eventData) {
	if (dialog.options.refreshPage) {
		window.location.reload();
	} else {	
		var lastPage = eventData.id==null;
		
		if (eventData.dc!=null) {
	        var tableId="detailTable_" + eventData.dc.replace(".","_").replace("class ","");
	        
	        for(key in dialog.dataTableHashList) {
	        	// TODO this is a crude measure. All datatables will refresh. 
	        	// The logic between messages, dialogs and datatables needs to be fixed
	        	if (key.toLowerCase().indexOf(eventData.dc.toLowerCase())!=-1) {
	        		dialog.refreshDataTable(key,dialog.dataTableHashList,lastPage)		
	        	}        	
	        }
		}
	}
}


dialog.refreshDataTable = function refreshDataTable(key, list, lastPage) {
	var curTable = list[key];
	if (typeof(curTable) !== 'undefined' && curTable != null) {
		if (lastPage == false) {
			curTable.fnDraw(false);
		} else {
			curTable.fnPageChange( 'last' );
		}
	}
}
/*
dialog.refreshImageEvent=function refreshImageEvent(event,eventData){
	//alert("we need a refresh!")
	this.src=this.src+"&amp;nocache="+new Date().getTime();
}
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
}

dialog.statusMessage = function statusMessage(event,eventData) {
	if (eventData.message) {	
		$("#statusmessage").html(eventData.message);
	}
}


dialog.deleteFile = function deleteDialog(id,controllerName, filename,options) {
	
	 
	 var dialogHTML = '<div "title="Confirm delete"><form><div class="errors" style="display:none;"></div><div>Are you sure you want to delete '+filename+' from '+controllerName+' '+id+' ?</div></form></div>'	 
	 var domainClass = (options != null && options["domainclass"] != null) ? options["domainclass"] : controllerName.capitalize();

	 var theDialog=$(dialogHTML).dialog({ 
		 modal:false,
		 width:400,
		 height:100,
		 buttons: { 
		 	"Delete": function(e) {
			 	var formData=theDialog.find("form").serialize();
			 	$.post(dialog.baseUrl+"/"+controllerName+"/deletefile/"+id+"?filename="+filename,formData, function(data) 
			 		{
			 		var result=data.result			 		

			 		
			 		//$(".dialog-events").trigger("dialog-refresh",{dc:domainClass,id:id})
			 		$(".dialog-events").trigger("dialog-refresh",{dc:domainClass,id:id})
			 		$(".dialog-events").trigger("dialog-message",{message:result.message})
			 		
			 		if(result.success){
				 		theDialog.dialog("close");
				 	} else  {				 		
				 		theDialog.find("div.errors").html(result.message)
				 		theDialog.find("div.errors").show();				 		
				 	}			 		
			 	});			 	
       	},
      	Cancel: function() {
	        		$( this ).dialog( "close" );
	        	}
      	},
       close: function(event, ui) {      
              theDialog.dialog("destroy").remove();
            }
        });
}




jQuery.fn.center = function () {
    this.css("position","absolute");
    this.css("top", ( $(window).height() - this.height() ) / 2+$(window).scrollTop() + "px");
    this.css("left", ( $(window).width() - this.width() ) / 2+$(window).scrollLeft() + "px");
    return this;
}

jQuery.fn.hcenter = function () {
    this.css("position","absolute");
    this.css("left", ( $(window).width() - this.width() ) / 2+$(window).scrollLeft() + "px");
    return this;
}
		
$(function() {
	// tinymce
	$("td.tinymce textarea").tinymce({});

	
	// Initialize date picker input elements
 	$(".datepicker").datepicker({ dateFormat: "yyyy-MM-dd'T'HH:mm:ss" , changeMonth: true, changeYear:true});
  	
  	$("a.confirm").live("click",function(){
  	return confirm('Are you sure?')
  	});
  	
	$(".help").cluetip({splitTitle: '|',cluezIndex:10000});
	
	/* Helper for the left menu, when clicking a li the enclosed a's href  will be called */

	$("div.dialog-menu ul ul li a").each(function (index) {
		var curMatch = $(this);
		curMatch.parent().click(function(){
			document.location = curMatch[0].href;
		});
	});
	
	$("#statusmessage").bind("dialog-message",dialog.statusMessage);	
	$("#statusmessage").addClass("dialog-events");
    
});
