/*
* Autocomplete module for dialog plugin
*
* Grails Dialog plug-in
* Copyright 2013 Open-T B.V., and individual contributors as indicated
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
dialog.autocomplete = {};


dialog.autocomplete.open =function(e,params) {
	$(e.target).find('input.autocomplete').each( function(index) {
			var curMatch = $(this);
			var jsonUrl = curMatch.attr("jsonUrl");
            var entryName=curMatch.attr("name");
			var name=entryName.replace("-entry","");

            var submitName=curMatch.attr("submitName") ? curMatch.attr("submitName") : name+'.id';

			curMatch.autocomplete({source:jsonUrl,
									minLength:0,
				select: function( event, ui ) {
					$( this ).val( ui.item.label );
					$('[name="'+ submitName+'"]' ).val( ui.item.value );
					$('[name="'+ submitName+'"]' ).attr("label", ui.item.label );
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
				   var currentValue=$( this ).val();
				   var label=$('[name="'+ submitName+'"]' ).attr("label");
				   $(this).trigger("change");
				   $('[name="'+ submitName+'"]' ).trigger("change",this);
				   return false;
			   },

				focus: function( event, ui ) {
					$( this ).val( ui.item.label );
					$('#'+name+'-container' ).removeClass("ac-selected");
					$('#'+name+'-container' ).removeClass("ac-idle");
					$('#'+name+'-container' ).addClass("ac-selecting");
					return false;
				}
			}).data( "autocomplete" )._renderItem = function( ul, item ) {
					var desc = item.description ? item.description : "";
				return $( "<li></li>" )
			.data( "item.autocomplete", item )
			.append( "<a>" + item.label + "<br><span class=\"autocomplete-description\">" + desc + "</span></a>" )
			.appendTo( ul );
			};

			curMatch.blur(function() {
				   var currentValue=$( this ).val();
				   var label=$('[name="'+ submitName+'"]' ).attr("label");

				   if (currentValue==="" || currentValue==="-") {
					$('[name="'+ submitName+'"]' ).val("null");
				   } else {
					   $( this ).val( label );
				   }

				   $('#'+name+'-container' ).addClass("ac-selected");
				   $('#'+name+'-container' ).removeClass("ac-idle");
				   $('#'+name+'-container' ).removeClass("ac-selecting");

				});

		}
		);
	return false;
};



$(function() {
	$("body").on("dialog-open",dialog.autocomplete.open);

});