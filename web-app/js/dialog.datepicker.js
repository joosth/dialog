/*
* Datepicker module for xml-forms plugin
*  
* Grails xml-forms plug-in
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

if (!window.dialog) {
	window.dialog={};	
}

if (!window.dialog.datepicker) {
	window.dialog.datepicker={};	
}


dialog.datepicker.open =function open (e,params) {     
    $(this).find(".datepicker").each (function (i) {
        var id=this.id;
        var yearRange="c-10:c+10";
        if ($(this).attr('yearRange')) {
            yearRange=$(this).attr('yearRange');
        }
        
        var updateElementId = $(this).attr('id').replace("entry-","")+'_date';
        $(this).datepicker({    changeMonth: true, 
                                changeYear:true,
                                altField:"#"+updateElementId,
                                altFormat:"yy-mm-dd'T'00:00:00",
                                yearRange:yearRange
                            });
    });
};

$(function() {
    $.datepicker.setDefaults( window.dialog.messages.datepicker.regional );
	$("body").on("dialog-open",window.dialog.datepicker.open);
});
