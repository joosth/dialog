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

    //var updateElementId = $(this).attr('id').replace("entry-", "") + '_date';
    var updateElementId = $(this).attr('id').replace("entry-", "update-");

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
            yearRange: yearRange
        }).mask(dialog.messages.datepicker.mask);
    }
};


dialog.datepicker.update = function update (entryDateElementId, entryTimeElementId, updateElementId, entryDateFormat, entryTimeFormat, entryDateValue, entryTimeValue) {
    var dateValue;
    try {
        entryDateValue  = entryDateValue.replace(/_/g, "");
        if (entryDateValue.length == (entryDateFormat.length + 2)) {
            dateValue = $.datepicker.parseDate(entryDateFormat, entryDateValue);
        }
    }
    catch (error) {
        //incorrect date.. leave dateValue undefined..
    }

    if (!dateValue) {
        $("#" + entryDateElementId).val('');
    }

    var timeValue;
    try {
        entryTimeValue = entryTimeValue.replace(/_/g, "");
        if (entryTimeValue.length == (entryTimeFormat.length)) {
            timeValue = $.datepicker.parseTime(entryTimeFormat, entryTimeValue);
        }
    }
    catch (error) {
        //incorrect time.. leave timeValue undefined..
    }

    if (!timeValue) {
        $("#" + entryTimeElementId).val('');
    }

    var updateValue = dateValue ? $.datepicker.formatDate("yy-mm-dd", dateValue) : "";
    updateValue += dateValue && timeValue ? "T" : "";
    updateValue += timeValue ? $.datepicker.formatTime("HH:mm:ss", timeValue) : "";

    if (updateValue) {
        $("#" + updateElementId).val(updateValue);
    }
    else {
        $("#" + updateElementId).val('');
    }
    $("#" + updateElementId).valid();
};


dialog.datepicker.openTime = function open (e, params) {
    var timeEntryElementId = $(this).attr('id').replace(/\./g, "\\.").replace(/\[/g, "\\[").replace(/\]/g, "\\]");
    var dateEntryElementId = timeEntryElementId.replace("-time", "");
    var updateElementId = timeEntryElementId.replace("entry-", "update-").replace("-time", "");

    //Check browser support for HTML5 date widget..
    if (Modernizr.inputtypes.time) {

        //HTML5 time widget
        $(this).on('change', function() {
            dialog.datepicker.update(dateEntryElementId, timeEntryElementId, updateElementId, "yy-mm-dd", "HH:mm", $("#" + dateEntryElementId).val(), $(this).val());
        });
    }
    else {

        // jQuery UI Timepicker with mask
        var timeValue = $.datepicker.parseTime("HH:mm", $(this).val());
        if (timeValue) {
            $(this).val( $.datepicker.formatTime($.timepicker._defaults.timeFormat, timeValue) );
        }
        else {
            $(this).val('');
        }

        $(this).on('change', function() {
            dialog.datepicker.update(dateEntryElementId, timeEntryElementId, updateElementId, "yy-mm-dd", "HH:mm", $("#" + dateEntryElementId).val(), $(this).val());
        });
    }
};



$(function() {
    $.datepicker.setDefaults( window.dialog.messages.datepicker.regional );
    $.timepicker.setDefaults( window.dialog.messages.timepicker.regional );
    $(document).on("dialog-open",".datepicker", window.dialog.datepicker.open);
    $(document).on("dialog-open",'.timepicker', window.dialog.datepicker.openTime);
});
