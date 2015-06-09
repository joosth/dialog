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
    window.dialog = {};
}

if (!window.dialog.datepicker) {
    window.dialog.datepicker = {};
}


dialog.datepicker.open = function open (e, params) {

    var updateElementId = $(this).attr('id').replace("entry-", "") + '_date';

    //Check browser support for HTML5 date widget..
    if (Modernizr.inputtypes.date) {
        // Increase size of date field
        $(this).removeClass('input-small');
        $(this).addClass('input-medium');

        $(this).parent().find('.input-mini').removeClass("input-mini").addClass("input-medium");
        //HTML5 date widget
        $(this).on('change', function() {
            var dateValue = $.datepicker.parseDate("yy-mm-dd", $(this).val());
            if (dateValue) {
                $("#" + updateElementId).val( $.datepicker.formatDate("yy-mm-dd'T'00:00:00", dateValue) );
            }
            else {
                $("#" + updateElementId).val('');
            }
        });
    }
    else {
        //jQuery UI Datepicker with mask

        //var dateValue = $.datepicker.parseDate("yy-mm-dd", $(this).val());
        var dateVal=$("#"+updateElementId).val();
        dateVal=dateVal.substring(0,10);
        var dateValue = $.datepicker.parseDate("yy-mm-dd", dateVal);
        if (dateValue) {
            $(this).val( $.datepicker.formatDate($.datepicker._defaults.dateFormat, dateValue) );
        }
        else {
            $(this).val('');
        }

        var yearRange = $(this).attr('yearRange') ? $(this).attr('yearRange') : "c-10:c+10";

        $(this).datepicker({
            altField: "#" + updateElementId,
            altFormat: "yy-mm-dd'T'00:00:00",
            changeMonth: true,
            changeYear: true,
            yearRange: yearRange
        }).mask(dialog.messages.datepicker.mask);
    }
};

$(function() {
    $.datepicker.setDefaults( window.dialog.messages.datepicker.regional );
    $(document).on("dialog-open",".datepicker", window.dialog.datepicker.open);
});
