/*
* Datetimepicker module for dialog plugin
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

if (!window.dialog.datetimepicker) {
    window.dialog.datetimepicker = {};
}

/**
 * Initialize datepicker entry element
 * @param e
 * @param params
 */
dialog.datetimepicker.openDate = function openDate (e, params) {

    var dateEntryElementId = $(this).attr('id').replace(/\./g, "\\.").replace(/\[/g, "\\[").replace(/\]/g, "\\]");
    var timeEntryElementId = dateEntryElementId.replace("-date", "-time");
    var updateElementId = dateEntryElementId.replace("entry-", "update-").replace("-date", "");
    var widgetType = $("#" + timeEntryElementId).length > 0 ? "DATETIME" : "DATE"
    
    //Check browser support for HTML5 date widget..
    if (Modernizr.inputtypes.date) {

        //HTML5 date widget
        $(this).on("change", function() {
            var dateValue = $(this).val();
            var timeValue = (widgetType == "DATETIME" ? dialog.datetimepicker.getTimeValue(timeEntryElementId) : false);
            var timeFormat = (widgetType == "DATETIME" ? dialog.datetimepicker.getTimeFormat(timeEntryElementId) : false);
            dialog.datetimepicker.updateValue(widgetType, updateElementId, dateValue, "YYYY-MM-DD", timeValue, timeFormat);
        });
    }
    else {

        //bootstrap datepicker widget
        var dateValue = moment($(this).val(), "YYYY-MM-DD");
        if (dateValue) {
            $(this).val(dateValue.format(window.dialog.messages.moment.inputDateFormat));
        }
        else {
            $(this).val("");
        }

        $(this).datetimepicker({
            format: window.dialog.messages.moment.inputDateFormat,
            icons: {
                time: "fa fa-clock-o",
                date: "fa fa-calendar",
                up: "fa fa-arrow-up",
                down: "fa fa-arrow-down",
                previous: "fa fa-chevron-left",
                next: "fa fa-chevron-right",
                today: "fa fa-dot-circle-o",
                clear: "fa fa-trash",
                close: "fa fa-times"
            },
            locale: moment.locale(),
            showTodayButton: true,
            showClear: true,
            tooltips: window.dialog.messages.datetimepicker.tooltips
        }).on("dp.change", function(e) {
            var dateValue = e.date;
            var timeValue = (widgetType == "DATETIME" ? dialog.datetimepicker.getTimeValue(timeEntryElementId) : false);
            var timeFormat = (widgetType == "DATETIME" ? dialog.datetimepicker.getTimeFormat(timeEntryElementId) : false);
            dialog.datetimepicker.updateValue(widgetType, updateElementId, dateValue, false, timeValue, timeFormat);
        }).mask(window.dialog.messages.maskedinput.date);
    }
}

/**
 * Initialize time entry element
 * @param e
 * @param params
 */
dialog.datetimepicker.openTime = function openTime (e, params) {

    var timeEntryElementId = $(this).attr('id').replace(/\./g, "\\.").replace(/\[/g, "\\[").replace(/\]/g, "\\]");
    var dateEntryElementId = timeEntryElementId.replace("-time", "-date");
    var updateElementId = timeEntryElementId.replace("entry-", "update-").replace("-time", "");
    var widgetType = $("#" + dateEntryElementId).length > 0 ? "DATETIME" : "TIME"
    
    //Check browser support for HTML5 date widget..
    if (Modernizr.inputtypes.time) {

        //HTML5 date widget
        $(this).on("change", function() {
            var timeValue = $(this).val();
            var dateValue = (widgetType == "DATETIME" ? dialog.datetimepicker.getDateValue(dateEntryElementId) : false);
            var dateFormat = (widgetType == "DATETIME" ? dialog.datetimepicker.getDateFormat(dateEntryElementId) : false);
            dialog.datetimepicker.updateValue(widgetType, updateElementId, dateValue, dateFormat, timeValue, "HH:mm");
        });
    }
    else {

        //bootstrap timepicker widget
        var dateValue = moment($(this).val(), "HH:mm:ss");
        if (dateValue) {
            $(this).val(dateValue.format(window.dialog.messages.moment.inputTimeFormat));
        }
        else {
            $(this).val("");
        }

        $(this).datetimepicker({
            format: window.dialog.messages.moment.inputTimeFormat,
            icons: {
                time: "fa fa-clock-o",
                date: "fa fa-calendar",
                up: "fa fa-arrow-up",
                down: "fa fa-arrow-down",
                previous: "fa fa-chevron-left",
                next: "fa fa-chevron-right",
                today: "fa fa-dot-circle-o",
                clear: "fa fa-trash",
                close: "fa fa-times"
            },
            locale: moment.locale(),
            showClear: true,
            toolbarPlacement: "bottom",
            tooltips: window.dialog.messages.datetimepicker.tooltips
        }).on("dp.change", function(e) {
            var timeValue = e.date;
            var dateValue = (widgetType == "DATETIME" ? dialog.datetimepicker.getDateValue(dateEntryElementId) : false);
            var dateFormat = (widgetType == "DATETIME" ? dialog.datetimepicker.getDateFormat(dateEntryElementId) : false);
            dialog.datetimepicker.updateValue(widgetType, updateElementId, dateValue, dateFormat, timeValue, false);
        }).mask(window.dialog.messages.maskedinput.time);
    }
}

dialog.datetimepicker.getDateValue = function getDateValue (entryElementId) {

    if (Modernizr.inputtypes.date || $("#" + entryElementId).data("DateTimePicker") === undefined) {
        return $("#" + entryElementId).val();
    }
    else {
        return $("#" + entryElementId).data("DateTimePicker").date();
    }
}

dialog.datetimepicker.getDateFormat = function getDateFormat (entryElementId) {

    if (Modernizr.inputtypes.date || $("#" + entryElementId).data("DateTimePicker") === undefined) {
        return "YYYY-MM-DD";
    }
    else {
        return $("#" + entryElementId).data("DateTimePicker").format();
    }
}

dialog.datetimepicker.getTimeValue = function getTimeValue (entryElementId) {

    if (Modernizr.inputtypes.time || $("#" + entryElementId).data("DateTimePicker") === undefined) {
        return $("#" + entryElementId).val();
    }
    else {
        return $("#" + entryElementId).data("DateTimePicker").date();
    }
}

dialog.datetimepicker.getTimeFormat = function getTimeFormat (entryElementId) {

    if (Modernizr.inputtypes.time || $("#" + entryElementId).data("DateTimePicker") === undefined) {
        return "HH:mm:ss";
    }
    else {
        return $("#" + entryElementId).data("DateTimePicker").format();
    }
}

dialog.datetimepicker.updateValue = function updateValue (type, updateElementId, inputDateValue, inputDateFormat, inputTimeValue, inputTimeFormat) {

    var dateValue = false;
    if (inputDateValue instanceof moment) {
        dateValue = inputDateValue;
    }
    else if (typeof inputDateValue == "string" && inputDateValue.length == inputDateFormat.length) {
        dateValue = moment(inputDateValue, inputDateFormat);
    }

    var timeValue = false;
    if (inputTimeValue instanceof moment) {
        timeValue = inputTimeValue;
    }
    else if (typeof inputTimeValue == "string" && inputTimeValue.length == inputTimeFormat.length) {
        timeValue = moment(inputTimeValue, inputTimeFormat);
    }

    var useTimeZone = $("#" + updateElementId).attr("useTimeZone") !== undefined ? $("#" + updateElementId).attr("useTimeZone") == "true" : false;

    switch (type) {

        case "DATETIME":
            if (dateValue && timeValue) {
                $("#" + updateElementId).val(dateValue.format("YYYY-MM-DD") + "T" + timeValue.format("HH:mm:ss") + (useTimeZone ? "Z" : ""));
            }
            else if (dateValue) {
                $("#" + updateElementId).val(dateValue.format("YYYY-MM-DD"));
            }
            else if (timeValue) {
                $("#" + updateElementId).val(timeValue.format("HH:mm:ss"));
            }
            else {
                $("#" + updateElementId).val("");
            }
            break;

        case "DATE":
            if (dateValue) {
                $("#" + updateElementId).val(dateValue.format("YYYY-MM-DD") + "T00:00:00" + (useTimeZone ? "Z" : ""));
            }
            else {
                $("#" + updateElementId).val("");
            }
            break;

        case "TIME":
            if (timeValue) {
                $("#" + updateElementId).val(timeValue.format("HH:mm:ss"));
            }
            else {
                $("#" + updateElementId).val("");
            }
            break;

        default:
            $("#" + updateElementId).val("");
            alert("Cannot determine DATE / TIME widget type!");
            break;
    }

    $("#" + updateElementId).trigger("change");
    $("#" + updateElementId).trigger("blur");
}

$(function() {
    $(document).on("dialog-open", ".datepicker", window.dialog.datetimepicker.openDate);
    $(document).on("dialog-open", ".timepicker", window.dialog.datetimepicker.openTime);
});
