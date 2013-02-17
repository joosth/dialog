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
	var submitName = (options != null && options["submitname"] != null) ? options["submitname"] : "submit"+dialogName;
	
	
	var refreshTableKey = (options != null && options["refresh"] != null) ? options["refresh"] : "NO_REFRESH";
	
	// if true, form submit will be used instead of AJAX
	var submitForm = (options != null && options["submitform"] != null) ? options["submitform"] : false;
	
	// if true, form will not be submitted at all
	var noSubmit = (options != null && options["nosubmit"] != null) ? options["nosubmit"] : false;
	
	var domainClass = (options != null && options["domainclass"] != null) ? options["domainclass"] : controllerName.capitalize();
	
	 theUrl=dialog.baseUrl+'/'+controllerName+'/'+dialogName+'/'+urlId	 	
	 
	 var dialogHTML = $.ajax({
		  url: theUrl,
		  async: false,
		  cache: false
		 }).error(function(event, jqXHR, ajaxSettings, thrownError) { 
				window.location.reload();
			}).responseText;
	 
	 var formelements=$(dialogHTML).find('form')
	 if (formelements.length==0) {
		 window.location.reload()
	 } else {
	 
	 var theWidth=$(dialogHTML).css("width").replace("px","");
	 var theHeight=$(dialogHTML).css("height").replace("px","");
	 if (theHeight=="0") theHeight="auto";
	 
     //var theWidth=600;
	 
	 var theDialog=$(dialogHTML).dialog({ 
		 modal:true,
		 width:theWidth,
		 height:theHeight,
		 buttons: { 
		 	"OK": function(e,ui) {
	        	$(this).trigger("dialog-submit",{event:e,ui:ui,'this':this,id:id,controllerName:controllerName});

		 		if (submitForm) {
		 			theDialog.find("form").attr("action",dialog.baseUrl+"/"+controllerName+"/"+submitName+"/"+urlId);
		        	theDialog.find("form").submit();
		        	$( this ).dialog( "close" );
		 		} else {
		 		if (!noSubmit) {
				 	var formData=theDialog.find("form").serialize();
				 	$.post(dialog.baseUrl+"/"+controllerName+"/"+submitName+"/"+urlId,formData, function(data) 
				 		{
				 		var jsonResponse = data.result;
				 		
				 		$(".dialog-events").trigger("dialog-refresh",{dc:domainClass,id:id,jsonResponse:jsonResponse})
				 		$(".dialog-events").trigger("dialog-message",{message:jsonResponse.message})
				 					 		
				 		if(jsonResponse.success){
					 		theDialog.dialog("close");
					 		if (jsonResponse.nextDialog) {
					 			dialog.formDialog(jsonResponse.nextDialog.id,jsonResponse.nextDialog.controllerName,jsonResponse.nextDialog.options,jsonResponse.nextDialog.urlParams)
					 		}
					 	} else  {
					 		for (key in jsonResponse.errorFields) {
					 			var errorField=jsonResponse.errorFields[key]
					 			$("#"+errorField).parent().addClass("errors")				 			
					 		}
					 		theDialog.find("div.errors").html(jsonResponse.message)
					 		theDialog.find("div.errors").show();				 		
					 	
				 		}
				 	});
		 		
		 		} else {
		 			$( this ).dialog( "close" );
		 		}
		 		}
        	},

       	Cancel: function() {
	        		$( this ).dialog( "close" );
	        	}
       	},
        open: function(event, ui) {
        	
        	$(this).trigger("dialog-open",{event:event,ui:ui,'this':this,id:id,controllerName:controllerName});
        	
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
        		    if (e.keyCode == 13 && e.target.nodeName!="TEXTAREA") {
        		    	$(this).parents('.ui-dialog').first().find('.ui-button').first().click();
               		 return false;
        		    }
        		});
        	

        	
         	// Initialize date picker input elements
       		$(this).find(".datepicker").datepicker({ dateFormat: "yy-mm-dd" , changeMonth: true, changeYear:true});
       		$(this).find(".dialogtabs").tabs();
       		$(this).find(".altselect").altselect();       	
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
    				"sPaginationType": "bootstrap",
    				"bFilter": false,
    				"bJQueryUI": false,
    				/*
    			    sDom explanation:
    			    l - Length changing
    			    f - Filtering input
    			    t - The table!
    			    i - Information
    			    p - Pagination
    			    r - pRocessing
    			    < and > - div elements
    			    <"class" and > - div with a class
    			    Examples: <"wrapper"flipt>, <lf<t>ip>
    			*/
    			
    			"sDom": '<"toolbar"lf>rtip',
    				
    				"oLanguage": {
    			     	 "sUrl": dialog.dataTablesLanguageUrl, 
    			    	},
			    	"fnInitComplete": function() {
			    		if ( $(this).hasClass("rowreordering")) {
		       				dialog.dataTableHashList[tableId].rowReordering(       				
		       				{
		       					 sURL:dialog.baseUrl+positionUrl,
		                         sRequestType: "POST"
				
		       				});       				
		       			};
		       			// Add NEW button ("parent()" is the div with class dataTables_wrapper)		       			
		       			if (id != null && (!newButton || newButton!="false")) {
		       				//curMatch.parent().find('div.dataTables_length').prepend('<span class="list-toolbar-button ui-widget-content ui-state-default"><span onclick="dialog.formDialog(null,\''+controller+'\', { refresh : \''+tableId+'\'}, { parentId : '+id+'})">New</span></span>&nbsp;');
		       				curMatch.parent().find('div.toolbar').prepend('<div style="float:left;margin-right:10px;" class="btn-group"><span class="btn" onclick="dialog.formDialog(null,\''+controller+'\', { refresh : \''+tableId+'\'}, { parentId : '+id+'})">New</span></span>&nbsp;');
		       			}
    			    		
			    	}
    			    	
    				
    			});
       			/*
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
       			*/
       		});
       		
       		// get z-index of dialog so we can put cluetips above it
       		var parentZIndex=parseInt($(this.parentNode).css('z-index'));
       		
       		/*$(this).find(".help").cluetip({
       			splitTitle: '|',  
       			cluezIndex: parentZIndex+1
      	    	});
       		*/
       		$(this).find(".help").tooltip({});
       		
       		
       		
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
        	$(this).trigger("dialog-close",{event:event,ui:ui,'this':this})       	
            theDialog.dialog("destroy").remove();
         }
       });
	 
	 }
	 return false;
}


dialog.deleteDialog = function deleteDialog(id,controllerName, options ,urlParams) {
	var urlId=id+dialog.obj2ParamStr(urlParams);
	 
	 var dialogHTML = '<div title="Confirm delete"><form><div class="errors" style="display:none;"></div><div>Are you sure you want to delete '+controllerName+' '+id+' ?</div></form></div>'

	 var domainClass = (options != null && options["domainclass"] != null) ? options["domainclass"] : controllerName.capitalize();

	 var theDialog=$(dialogHTML).dialog({ 
		 modal:true,
		 width:400,
		 height:170,
		 buttons: { 
		 	"Delete": function(e) {
			 	var formData=theDialog.find("form").serialize();
			 	$.post(dialog.baseUrl+"/"+controllerName+"/delete/"+urlId,formData, function(data) 
			 		{
			 		var result=data.result			 		

			 		
			 		$(".dialog-events").trigger("dialog-refresh",{dc:domainClass,id:id,jsonResponse:result})
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
			//curTable.fnReloadAjax();
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
		var msg='<div id="alertmessage" class="alert alert-success fade"><button type="button" class="close" data-dismiss="alert">×</button><div>'+eventData.message+'</div></div>'
		$("#statusmessage").html(msg);
		$("#alertmessage").addClass("in");
	}
}

// TODO this seems to double deleteDialog ??
dialog.deleteFile = function deleteFile(id,controllerName, filename,options) {
	
	 
	 var dialogHTML = '<div title="Confirm delete"><form><div class="errors" style="display:none;"></div><div>Are you sure you want to delete '+filename+' from '+controllerName+' '+id+' ?</div></form></div>'	 
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

$(function() {
	
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

	
	
	// Initialize date picker input elements
 	$(".datepicker").datepicker({ dateFormat: "yyyy-MM-dd'T'HH:mm:ss" , changeMonth: true, changeYear:true});
  	
  	$("a.confirm").live("click",function(){
  	return confirm('Are you sure?')
  	});
  	
  	if($.cluetip) {
  		$(".help").cluetip({splitTitle: '|',cluezIndex:10000});
  	}
	/* Helper for the left menu, when clicking a li the enclosed a's href  will be called */

	$("div.dialog-menu ul ul li a").each(function (index) {
		var curMatch = $(this);
		curMatch.parent().click(function(){
			document.location = curMatch[0].href;
		});
	});
	
	$("#statusmessage").bind("dialog-message",dialog.statusMessage);	
	$("#statusmessage").addClass("dialog-events");
	var test=function (e) {
		alert ('test')
		return false
	}
	//$("body").on("daialog-open",test)

    
});

//$.fn.dataTableExt.oApi.fnReloadAjax = function ( oSettings, sNewSource, fnCallback, bStandingRedraw )
//{
//    if ( typeof sNewSource != 'undefined' && sNewSource != null )
//    {
//        oSettings.sAjaxSource = sNewSource;
//    }
//    this.oApi._fnProcessingDisplay( oSettings, true );
//    var that = this;
//    var iStart = oSettings._iDisplayStart;
//    var aData = [];
//  
//    this.oApi._fnServerParams( oSettings, aData );
//      
//    oSettings.fnServerData( oSettings.sAjaxSource, aData, function(json) {
//        /* Clear the old information from the table */
//        that.oApi._fnClearTable( oSettings );
//          
//        /* Got the data - add it to the table */
//        var aData =  (oSettings.sAjaxDataProp !== "") ?
//            that.oApi._fnGetObjectDataFn( oSettings.sAjaxDataProp )( json ) : json;
//          
//        for ( var i=0 ; i<aData.length ; i++ )
//        {
//            that.oApi._fnAddData( oSettings, aData[i] );
//        }
//          
//        oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
//        that.fnDraw();
//          
//        if ( typeof bStandingRedraw != 'undefined' && bStandingRedraw === true )
//        {
//            oSettings._iDisplayStart = iStart;
//            that.fnDraw( false );
//        }
//          
//        that.oApi._fnProcessingDisplay( oSettings, false );
//          
//        /* Callback user function - for event handlers etc */
//        if ( typeof fnCallback == 'function' && fnCallback != null )
//        {
//            fnCallback( oSettings );
//        }
//    }, oSettings );
//};
