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
        	

        	 
        	 $(this).find("input[type!='hidden'],select,textarea").filter(":first").focus();
        	 
        	
         	// Initialize date picker input elements
       		$(this).find(".datepicker").datepicker({ dateFormat: "yy-mm-dd" , changeMonth: true, changeYear:true});
       		$(this).find(".dialogtabs").tabs();
       		//$(this).find(".altselect").altselect();
       		
       		var dataTable = $(this).find('.detailTable');
       		
       		$(this).find('.detailTable').each(function (index) {
       			var curMatch = $(this);
       			var tableId = curMatch.attr('id');
       			var jsonUrl = curMatch.attr("jsonUrl");
       			var controller = jsonUrl.split('/')[1]; //extract controller name from json url
       			
       			dialog.dataTableHashList[tableId] = curMatch.dataTable({
    				"bProcessing": true,
    				"bServerSide": true,
    				"sAjaxSource": dialog.baseUrl+jsonUrl,
    				"sPaginationType": "full_numbers",
    				"bFilter": false,
    				"bJQueryUI": true
    			});
       			
       			// Add NEW button ("parent()" is the div with class dataTables_wrapper)
       			if (id != null) {
       				curMatch.parent().find('div.dataTables_length').prepend('<span class="list-toolbar-button ui-widget-content ui-state-default"><span onclick="dialog.formDialog(null,\''+controller+'\', { refresh : \''+tableId+'\'}, { parentId : '+id+'})">New</span></span>&nbsp;');
       			}
       			
       		});
       		
       		// get z-index of dialog so we can put cluetips above it
       		var parentZIndex=parseInt($(this.parentNode).css('z-index'));
       		
       		$(this).find(".help").cluetip({
       			splitTitle: '|',  
       			cluezIndex: parentZIndex+1
      	    	});
                     },
        close: function(event, ui) {      
               theDialog.dialog("destroy").remove();
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
