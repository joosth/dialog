/*
* Datepicker module for dialog plugin
*
* Grails dialog plug-in
* Copyright 2013-2015 Open-T B.V., and individual contributors as indicated
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
    var dateEntryElementId = $(this).attr('id');
    var timeEntryElementId = $(this).attr('id')+"-time";
    var updateElementId = $(this).attr('id').replace("entry-", "update-");

    //Check browser support for HTML5 date widget..
    if (Modernizr.inputtypes.date) {
        // Increase size of date field
        $(this).removeClass('input-small');
        $(this).addClass('input-medium');

        $(this).parent().find('.input-mini').removeClass("input-mini").addClass("input-medium");

        // Need to copy value to update field on change
        $(this).on('change', function() {
            var dateValue = $(this).val();
            if (dateValue) {
                var timeValue=$("#"+timeEntryElementId).val();
                // needs more checking
                if (timeValue) {
                    $("#" + updateElementId).val( dateValue+'T'+timeValue+':00');
                } else {
                    $("#" + updateElementId).val( dateValue+'T00:00:00');
                }
            } else {
                $("#" + updateElementId).val('');
            }
        });
    }
    else {
        var dateVal=$("#"+updateElementId).val();
        $(this).val('');
        if (dateVal && dateVal!="null") {
            dateVal=dateVal.substring(0,10);
            var dateValue = $.datepicker.parseDate("yy-mm-dd", dateVal);
            if (dateValue) {
                $(this).val( $.datepicker.formatDate($.datepicker._defaults.dateFormat, dateValue) );
            }
        }

        var yearRange = $(this).attr('yearRange') ? $(this).attr('yearRange') : "c-10:c+10";

        $(this).datepicker({
            altField: "#" + updateElementId,
            altFormat: "yy-mm-dd'T'00:00:00",
            changeMonth: true,
            changeYear: true,
            yearRange: yearRange,
            onClose:function(dateText,obj) {
                var updateValue=$("#" + updateElementId).val();
                updateValue=updateValue.substring(0,updateValue.lastIndexOf('T'));
                var timeValue=$("#"+timeEntryElementId).val();
                // needs more checking
                if (timeValue) {
                    $("#" + updateElementId).val( updateValue+'T'+timeValue+':00');
                } else {
                    $("#" + updateElementId).val( updateValue+'T00:00:00');
                }
            }
        }).mask(dialog.messages.datepicker.mask);
    }
};

dialog.datepicker.openTime = function open (e, params) {
    var timeEntryElementId = $(this).attr('id').replace(/\./g, "\\.").replace(/\[/g, "\\[").replace(/\]/g, "\\]");
    // Note we don't use replace here as the name of the property may start with 'time' which would match.
    var dateEntryElementId = timeEntryElementId.substring(0,timeEntryElementId.lastIndexOf("-time"));
    var updateElementId = dateEntryElementId.replace("entry-", "update-");

    $(this).on('change', function() {
        var timeValue=$(this).val();
        var updateValue=$("#" + updateElementId).val();
        updateValue=updateValue.substring(0,updateValue.lastIndexOf('T'));
        $("#" + updateElementId).val( updateValue+'T'+timeValue+':00');
    });

};

$(function() {
    $.datepicker.setDefaults( window.dialog.messages.datepicker.regional );
    $(document).on("dialog-open",".datepicker", window.dialog.datepicker.open);
    $(document).on("dialog-open",'.timepicker', window.dialog.datepicker.openTime);
});
