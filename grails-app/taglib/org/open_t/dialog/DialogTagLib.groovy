/*
 * Dialog
 *
 * Copyright 2009-2017, Open-T B.V., and individual contributors as indicated
 * by the @author tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License
 * version 3 published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see http://www.gnu.org/licenses
 */
package org.open_t.dialog

import org.springframework.web.servlet.support.RequestContextUtils as RCU
import grails.util.GrailsNameUtils
import java.text.SimpleDateFormat
/**
 * Tag library for Dialog plugin.
 *
 *
 * @author Joost Horward
 */
class DialogTagLib {

    def dialogService
    def listService

    static namespace = "dialog"

    /**
     * Element to place in HTML page's <head> section
     * Initializes namespace dialog for datatable
     * Sets base URL to be used.
     * @param request The HTTPServletRequest
     */
    def head = {
        out <<
            """
            <script type="text/javascript">
                var dialog = {};
                dialog.options = {
                    "refreshPage": false
                };
                dialog.baseUrl = "${request.contextPath}";
                dialog.pluginUrl = "${resource(plugin: "dialog")}";
                dialog.language = "${g.message(code: "language.code", default: "en")}";

                dialog.messages = {};
                dialog.messages.ok = "${message(code: "dialog.messages.ok")}";
                dialog.messages.delete = "${message(code: "dialog.messages.delete")}";
                dialog.messages.cancel = "${message(code: "dialog.messages.cancel")}";

                dialog.messages.uploading = "${message(code: "dialog.messages.uploading")}";
                dialog.messages.uploadcompleted = "${message(code: "dialog.messages.uploadcompleted")}";

                dialog.messages['new'] = "${message(code: "dialog.messages.new")}";
                dialog.messages.confirmdelete = "${message(code: "dialog.messages.confirmdelete")}";
                dialog.messages.confirmdeleteTitle = "${message(code: "dialog.messages.confirmdeleteTitle")}";

                dialog.messages.moment = {};
                dialog.messages.moment.inputDateFormat = "${message(code: "dialog.moment.inputDateFormat")}";
                dialog.messages.moment.inputTimeFormat = "${message(code: "dialog.moment.inputTimeFormat")}";

                dialog.messages.datetimepicker = {};
                dialog.messages.datetimepicker.tooltips = {
                    today: "${message(code: "dialog.datetimepicker.today")}",
                    clear: "${message(code: "dialog.datetimepicker.clear")}",
                    close: "${message(code: "dialog.datetimepicker.close")}",
                    selectMonth: "${message(code: "dialog.datetimepicker.selectMonth")}",
                    prevMonth: "${message(code: "dialog.datetimepicker.prevMonth")}",
                    nextMonth: "${message(code: "dialog.datetimepicker.nextMonth")}",
                    selectYear: "${message(code: "dialog.datetimepicker.selectYear")}",
                    prevYear: "${message(code: "dialog.datetimepicker.prevYear")}",
                    nextYear: "${message(code: "dialog.datetimepicker.nextYear")}",
                    selectDecade: "${message(code: "dialog.datetimepicker.selectDecade")}",
                    prevDecade: "${message(code: "dialog.datetimepicker.prevDecade")}",
                    nextDecade: "${message(code: "dialog.datetimepicker.nextDecade")}",
                    prevCentury: "${message(code: "dialog.datetimepicker.prevCentury")}",
                    nextCentury: "${message(code: "dialog.datetimepicker.nextCentury")}"
                }
                dialog.messages.maskedinput = {};
                dialog.messages.maskedinput.date = "${message(code: "dialog.maskedinput.date")}";
                dialog.messages.maskedinput.time = "${message(code: "dialog.maskedinput.time")}";
                dialog.messages.datatables = {
                    "language": {
                        "decimal":        "${message(code: "dialog.datatables.decimal")}",
                        "emptyTable":     "${message(code: "dialog.datatables.emptyTable")}",
                        "info":           "${message(code: "dialog.datatables.info")}",
                        "infoEmpty":      "${message(code: "dialog.datatables.infoEmpty")}",
                        "infoFiltered":   "${message(code: "dialog.datatables.infoFiltered")}",
                        "infoPostFix":    "${message(code: "dialog.datatables.infoPostFix")}",
                        "thousands":      "${message(code: "dialog.datatables.thousands")}",
                        "lengthMenu":     "${message(code: "dialog.datatables.lengthMenu")}",
                        "loadingRecords": "${message(code: "dialog.datatables.loadingRecords")}",
                        "processing":     "${message(code: "dialog.datatables.processing")}",
                        "search":         "${message(code: "dialog.datatables.search")}",
                        "zeroRecords":    "${message(code: "dialog.datatables.zeroRecords")}",
                        "paginate": {
                            "first":      "${message(code: "dialog.datatables.paginate.first")}",
                            "last":       "${message(code: "dialog.datatables.paginate.last")}",
                            "next":       "${message(code: "dialog.datatables.paginate.next")}",
                            "previous":   "${message(code: "dialog.datatables.paginate.previous")}"
                        },
                        "aria": {
                            "sortAscending":  "${message(code: "dialog.datatables.aria.sortAscending")}",
                            "sortDescending": "${message(code: "dialog.datatables.aria.sortDescending")}"
                        }
                    }
                };
                dialog.messages.validation = {};
                dialog.messages.validation.invalidTime="${message(code: "dialog.validation.invalidTime")}";
                dialog.messages.validation.invalidDateTime="${message(code: "dialog.validation.invalidDateTime")}";
                var CKEDITOR_BASEPATH = dialog.baseUrl+"/assets/ext/ckeditor/";
            </script>
            """
    }

    /**
     * dialog:last
     * This tag should be after any other dialog modules
     */
    def last = {
        out <<
            """
            <script type="text/javascript">
                \$(function() {
                    \$(document).trigger("dialog-init", {});
                    \$(".dialog-open-events").filter(".dialog-open-first").filter(":not(.dialog-opened)").trigger("dialog-open", {"page": true});
                    \$(".dialog-open-events").filter(":not(.dialog-open-first)").filter(":not(.dialog-opened)").trigger("dialog-open", {"page": true}).addClass("dialog-opened");
                });
            </script>
            """
    }

    /**
     * This generates a Bootstrap form-group containing a label, control and helpblock.
     * It is mostly for internal use, the dialog elements input, select etc. use this to wrap themselves in.
     *
     *  @param object The domain object
     *  @param propertyName The property of the domain object
     *
     */
    def row = { attrs, body ->
        if (attrs.norow) {
            out << body()
        } else {
            def object = attrs.object
            def propertyName = attrs.propertyName
            def persistentEntity=grailsApplication.mappingContext.getPersistentEntity(attrs.object.getClass().name)
            def property
            if (persistentEntity) {
                property=persistentEntity.getPropertyByName(attrs.propertyName)
            } else {
                property=attrs.object?.getMetaClass()?.properties?.find { it.name == attrs.propertyName }
            }
            def domainPropertyName=persistentEntity?.getDecapitalizedName()
            def naturalName = GrailsNameUtils.getNaturalName(propertyName)
            def cssClass = attrs.class ? attrs.class : ""
            def errors = ""
            if (attrs.object.hasErrors()) {
                if(attrs.object.errors.getFieldError(propertyName)) {
                    errors = g.message(code: "${domainPropertyName}.${propertyName}.error", default: attrs.object.errors.getFieldError(propertyName).defaultMessage)
                    cssClass += " error"
                }
            }

            //begin row
            out << """<div class="form-group object-${domainPropertyName} property-${domainPropertyName}-${propertyName} property-${propertyName} ${cssClass}">"""

            //label
            if (attrs.noLabel != "true") {
                out << """<label for="${attrs.propertyName}"${attrs.vertical != "true" ? " class='col-sm-3 control-label'" : ""}>${g.message(code: "${domainPropertyName}.${propertyName}.label", default: "${naturalName}")}</label>"""
            }

            //control en help
            if (attrs.vertical != "true") {
                out << """<div class="col-sm-${attrs.noLabel != "true" ? "9" : "12"}">"""
            }
            out << body()
            if (attrs.noHelp != "true") {
                if (g.message(code: "${domainPropertyName}.${propertyName}.help", default: "")) {
                    out << """<span id="help-${attrs.propertyName}" class="help-block small">${g.message(code: "${domainPropertyName}.${propertyName}.help", default: "Help!")}</span>"""
                }
            }
            if (attrs.noErrors!="true"){
    			out <<"""<span class="small error-message">${errors}</span>"""
    		}

            if (attrs.vertical != "true") {
                out << "</div>"
            }

            //end row
            out << "</div>"
        }
    }

    /**
     * Provid a "simple" row that has no relation to a domain class
     *
     * @param label The label to show
     * @param name The name that acts as the key for looking up the label and help title [name].label and [name].help)
     * @param error Error message to show
     * @param class CSS class to apply to the <tr> element
     */
    def simplerow = { attrs, body ->
        def cssClass = attrs.class ? attrs.class : ""
        def error = attrs.error ? attrs.error : ""
        def errors = ""
        if (attrs.error) {
            cssClass += " error"
        }
        def label = attrs.label ?: g.message(code: "${attrs.name}.label", default: "${attrs.name}")

		if (attrs.separator == "true"){
			out <<"""<hr/>"""
		}

        //begin row
        out << """<div class="form-group ${cssClass}">"""

        //label
        if (attrs.noLabel != "true") {
            out << """<label for="${attrs.name}"${attrs.vertical != "true" ? " class='col-sm-3 control-label'" : ""}>${label}</label>"""
        }

        //control en help
        if (attrs.vertical != "true") {
            out << """<div class="col-sm-${attrs.noLabel != "true" ? "9" : "12"}">"""
        }
        out << body()

        if (attrs.noHelp != "true") {
            def help = attrs.help ? attrs.help : g.message(code: "${attrs.name}.help", default: "");
            if (help) {
                out << """<span id="help-${attrs.name}" class="help-block small">${help}</span>"""
            }
        }
        if (attrs.vertical != "true") {
            out << "</div>"
        }

        //end row
        out << "</div>"
    }


    /**
     * Text input field tag
     * Any extra attributes are copied over to the HTML &lt;input&gt; element.
     * The only attributes not copied over are:  object,propertyName,mode,class
     *
     * @param mode Contains 'edit' (generate edit field) or 'show' (generate read-only output)
     * @param propertyName The property of the domain object
     * @param object The domain object
     * @param type The type of input to be used (default: text)
     * @param class The CSS class to be supplied to the enclosing row
     */
    def textField = { attrs ->
        out << row (attrs) {
            switch (attrs.mode) {
                case "show":
                    return """<p class="form-control-static">${fieldValue(bean: attrs.object, field: attrs.propertyName)}</p>"""
                    break

                case "edit":
                    def name = attrs.propertyName
                    def value = null
                    if (attrs.value) {
                        value = attrs.value
                    } else {
                        value = fieldValue(bean: attrs.object, field: attrs.propertyName)
                    }

                    // Copy all extra attributes, skip the ones that are only meaningful for textField or are handled manually
                    def copiedAttrs = ""
                    def skipAttrs = ["object", "propertyName", "mode", "class", "type", "value"]
                    attrs.each { attrKey, attrValue ->
                        if (!skipAttrs.contains(attrKey)) {
                            copiedAttrs += """ ${attrKey}="${attrValue}" """
                        }
                    }
                    def inputType = "text"
                    if (attrs.type) inputType = attrs.type

                    return """<input type="${inputType}" name="${name}" value="${value}" id="${name}" class="form-control" ${copiedAttrs} />"""
                    break
            }
        }
    }

    /**
     * Date input field tag
     * This generates a date input element
     * The format to be used is fixed yyyy-MM-ddTHH:mm:ssZ in the update field
     *
     * @param mode Contains 'edit' (generate edit field) or 'show' (generate read-only output)
     * @param propertyName The property of the domain object
     * @param object The domain object
     * @param class The CSS class to be supplied to the enclosing row
     * @param minDate The minimum selectable date. When empty, there is no minimum.
     * @param maxDate The maximum selectable date. When empty, there is no maximum.
     */
    def date = { attrs ->

        out << row (attrs) {
            def value = attrs.object."${attrs.propertyName}"

            switch (attrs.mode) {
                case "show":
                    return """<p class="form-control-static">${listService.getDisplayString(value)}</p>"""
                    break

                case "edit":
                    def entryValue = value ? new SimpleDateFormat("yyyy-MM-dd").format(value) : ""
                    def updateValue = value ? new SimpleDateFormat("yyyy-MM-dd'T00:00:00Z'").format(value) : ""

                    // min & max
                    def minDate = DateTimeUtil.determineDate(attrs.minDate)
                    if (value && minDate && value.clearTime().before(minDate)) {
                        log.info "Value '${value.clearTime()}' is before minimum date '${minDate}', value will be used as minimum date."
                        minDate = value.clearTime()
                    }
                    def maxDate = DateTimeUtil.determineDate(attrs.maxDate)
                    if (minDate && maxDate && minDate.after(maxDate)) {
                        log.warn "Minimum date '${minDate}' is after maximum date '${maxDate}', maximum date will not be used! Is your snippet configuration correct?"
                        maxDate = null
                    }
                    if (value && maxDate && value.clearTime().after(maxDate)) {
                        log.info "Value '${value.clearTime()}' is after maximum date '${maxDate}', value will be used as maximum date."
                        maxDate = value.clearTime()
                    }
                    def uuid=UUID.randomUUID().toString()
                    def html =
                        """
                        <input id="entry-${uuid}-date" name="entry-${attrs.propertyName}-date" type="date" class="form-control datepicker dialog-open-events" value="${entryValue}" ${minDate ? """ min="${minDate.format("yyyy-MM-dd")}" """ : ""} ${maxDate ? """ max="${maxDate.format("yyyy-MM-dd")}" """ : ""} />
                        <input id="update-${uuid}" name="${attrs.propertyName}" type="hidden" value="${updateValue}" useTimeZone="true" />
                        """
                    return html
                    break
            }
        }
    }

    /**
     * Time input field tag
     * This generates a time input element
     * The format to be used is fixed HH:mm:ss in the update field
     *
     * @param mode Contains 'edit' (generate edit field) or 'show' (generate read-only output)
     * @param propertyName The property of the domain object
     * @param object The domain object
     * @param class The CSS class to be supplied to the enclosing row
     * @param minTime The minimum selectable time. When empty, there is no minimum.
     * @param maxTime The maximum selectable time. When empty, there is no maximum.
     */
    def time = { attrs ->

        out << row (attrs) {
            def value = attrs.object."${attrs.propertyName}"

            switch (attrs.mode) {
                case "show":
                    return """<p class="form-control-static">${listService.getDisplayString(value)}</p>"""
                    break

                case "edit":
                    def entryValue = value ? new SimpleDateFormat("HH:mm:ss").format(value) : ""
                    def updateValue = value ? new SimpleDateFormat("yyyy-MM-dd'T00:00:00Z'").format(value) : ""

                    // min & max
                    def minTime = DateTimeUtil.determineTime(attrs.minTime)
                    if (value && minTime && value.before(minTime)) {
                        log.info "Value '${value}' is before minimum time '${minTime}', value will be used as minimum time."
                        minTime = value
                    }
                    def maxTime = DateTimeUtil.determineTime(attrs.maxTime)
                    if (minTime && maxTime && minTime.after(maxTime)) {
                        log.warn "Minimum time '${minTime}' is after maximum time '${maxTime}', maximum time will not be used! Is your snippet configuration correct?"
                        maxTime = null
                    }
                    if (value && maxTime && value.after(maxTime)) {
                        log.info "Value '${value}' is after maximum time '${maxTime}', value will be used as maximum time."
                        maxTime = value
                    }
                    def uuid=UUID.randomUUID().toString()
                    def html =
                        """
                        <input id="entry-${uuid}-time" name="entry-${attrs.propertyName}-time" type="time" class="form-control timepicker dialog-open-events" value="${entryValue}" ${minTime ? """ min="${minTime.format("HH:mm")}" """ : ""} ${maxTime ? """ max="${maxTime.format("HH:mm")}" """ : ""} />
                        <input id="update-${uuid}" name="${attrs.propertyName}" type="hidden" value="${updateValue}" useTimeZone="true" />
                        """
                    return html
                    break
            }
        }
    }

    /**
     * DateTime input field tag
     * This generates a date input element plus a time input element
     * The format to be used is fixed yyyy-MM-ddTHH:mm:ssZ in the update field
     *
     * @param mode Contains 'edit' (generate edit field) or 'show' (generate read-only output)
     * @param propertyName The property of the domain object
     * @param object The domain object
     * @param class The CSS class to be supplied to the enclosing row
     * @param minDate The minimum selectable date. When empty, there is no minimum.
     * @param maxDate The maximum selectable date. When empty, there is no maximum.
     * @param minTime The minimum selectable time. When empty, there is no minimum.
     * @param maxTime The maximum selectable time. When empty, there is no maximum.
     */
    def dateTime = { attrs ->

        out << row (attrs) {
            def value = attrs.object."${attrs.propertyName}"

            switch (attrs.mode) {
                case "show":
                    return """<p class="form-control-static">${listService.getDisplayString(value)}</p>"""
                    break

                case "edit":
                    def timeValue = value ?  new SimpleDateFormat("HH:mm:ss").format(value) : null
                    def entryValueDate = value ? new SimpleDateFormat("yyyy-MM-dd").format(value) : ""

                    def entryValueTime = value ? new SimpleDateFormat("HH:mm:ss").format(value) : ""
                    def updateValue = value ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(value)  : ""

                    // min & max date
                    def minDate = DateTimeUtil.determineDate(attrs.minDate)
                    if (value && minDate && value.clearTime().before(minDate)) {
                        log.info "Value '${value.clearTime()}' is before minimum date '${minDate}', value will be used as minimum date."
                        minDate = value.clearTime()
                    }
                    def maxDate = DateTimeUtil.determineDate(attrs.maxDate)
                    if (minDate && maxDate && minDate.after(maxDate)) {
                        log.warn "Minimum date '${minDate}' is after maximum date '${maxDate}', maximum date will not be used! Is your snippet configuration correct?"
                        maxDate = null
                    }
                    if (value && maxDate && value.clearTime().after(maxDate)) {
                        log.info "Value '${value.clearTime()}' is after maximum date '${maxDate}', value will be used as maximum date."
                        maxDate = value.clearTime()
                    }
                    // min & max time
                    def minTime = DateTimeUtil.determineTime(attrs.minTime)
                    if (timeValue && minTime && timeValue.before(minTime)) {
                        log.info "Value '${timeValue}' is before minimum time '${minTime}', value will be used as minimum time."
                        minTime = timeValue
                    }
                    def maxTime = DateTimeUtil.determineTime(attrs.maxTime)
                    if (minTime && maxTime && minTime.after(maxTime)) {
                        log.warn "Minimum time '${minTime}' is after maximum time '${maxTime}', maximum time will not be used! Is your snippet configuration correct?"
                        maxTime = null
                    }
                    if (timeValue && maxTime && timeValue.after(maxTime)) {
                        log.info "Value '${timeValue}' is after maximum time '${maxTime}', value will be used as maximum time."
                        maxTime = timeValue
                    }
                    def uuid=UUID.randomUUID().toString()
                    def html =
                        """
                        <div class="row row-no-margin-top">
                            <div class="col-md-7">
                                <input id="entry-${uuid}-date" name="entry-${attrs.propertyName}-date" type="date" class="form-control datepicker dialog-open-events" value="${entryValueDate}" ${minDate ? """ min="${minDate.format("yyyy-MM-dd")}" """ : ""} ${maxDate ? """ max="${maxDate.format("yyyy-MM-dd")}" """ : ""} />
                            </div>
                            <div class="col-md-5">
                                <input id="entry-${uuid}-time" name="entry-${attrs.propertyName}-time" type="time" class="form-control timepicker dialog-open-events" value="${entryValueTime}" ${minTime ? """ min="${minTime.format("HH:mm")}" """ : ""} ${maxTime ? """ max="${maxTime.format("HH:mm")}" """ : ""} />
                            </div>
                        </div>
                        <input id="update-${uuid}" name="${attrs.propertyName}" type="hidden" value="${updateValue}" useTimeZone="true" />
                        """
                    return html
                    break
            }
        }
    }

    /**
     * Text area field tag
     *
     * @param mode Contains 'edit' (generate textarea field) or 'show' (generate read-only output)
     * @param propertyName The property of the domain object
     * @param object The domain object
     * @param class The CSS class to be supplied to the enclosing row
     */
    def textArea = { attrs ->
        def copiedAttrs = ""
        def skipAttrs = ["object", "propertyName", "mode", "type", "value"]
        def newAttrs = attrs.findAll { attrKey, attrValue -> !skipAttrs.contains(attrKey) }
        if (newAttrs["class"]) {
            newAttrs["class"] += " form-control dialog-open-events"
        } else {
            newAttrs["class"] = " form-control dialog-open-events"
        }

        def rows = attrs.rows ?: 5
        def cols = attrs.cols ?: 40

        out << row (attrs) {

            switch(attrs.mode) {
                case "show":
                    return """<p class="form-control-static">${fieldValue(bean: attrs.object, field: attrs.propertyName)}</p>"""
                    break

                case "edit":
                    // Hack to assign unique ID's and keep tinyMCE happy
                    def uuid=UUID.randomUUID().toString()
                    newAttrs += [name: attrs.propertyName, value: attrs.object."${attrs.propertyName}", cols: cols, rows: rows , id: uuid]

                    return g.textArea(newAttrs)
                    break
            }
        }
    }

    /**
     * XML editing text area field tag
     *
     * @param mode Contains 'edit' (generate textarea field) or 'show' (generate read-only output)
     * @param propertyName The property of the domain object
     * @param object The domain object
     * @param class The CSS class to be supplied to the enclosing row
     */
    def xml = { attrs ->
        def skipAttrs = ["object", "propertyName", "mode", "type", "value"]
        def newAttrs = attrs.findAll { attrKey, attrValue -> !skipAttrs.contains(attrKey) }
        newAttrs.cols = attrs.cols ?: 80
        newAttrs.rows = attrs.rows ?: 20
        def xmltext = attrs.object."${attrs.propertyName}"
        def prettyXmlText = xmltext
        if (xmltext) {
            try {
                prettyXmlText=dialogService.prettyPrint(xmltext)
            } catch (Exception e) {
                // Do nothing if XML parsing fails
            }
        }
        newAttrs.value = prettyXmlText
        newAttrs.name = attrs.propertyName
        if (newAttrs["class"]) {
            newAttrs["class"] += " dialog-open-events"
        } else {
            newAttrs["class"] = "dialog-open-events"
        }
        def uuid=UUID.randomUUID().toString()
        newAttrs['id']=uuid

        out << row (attrs) {

            switch (attrs.mode) {
                case "show":
                    String s = prettyXmlText
                    return """${g.textArea(newAttrs) {s?.encodeAsHTML()}}"""
                    break

                case "edit":
                    return g.textArea(newAttrs)
                    break
            }
        }
    }

    /**
     * Checkbox tag
     *
     * @param mode Contains 'edit' (generate textarea field) or 'show' (generate read-only output)
     * @param propertyName The property of the domain object
     * @param object The domain object
     * @param class The CSS class to be supplied to the enclosing row
     */

    def checkBox = { attrs ->
        out << row (attrs) {
            switch (attrs.mode) {
                case "show":
                    def value = fieldValue(bean: attrs.object, field: attrs.propertyName)
                    return """<p class="form-control-static">""" + g.message(code: "dialog.checkBox.${value}.label".toString(), default: value.toString()) + "</p>"
                    break

                case "edit":
                    def cb =
                        """
                        <div class="checkbox">
                            <label>
                            """ +
                            g.checkBox(name: attrs.propertyName, value: attrs.object."${attrs.propertyName}") +
                            """
                            </label>
                        </div>
                        """
                    return cb
                    break
            }
        }
    }

    /**
     * domainObject tag - shows a select box that allows to select an object from a domain class
     *
     * @param mode Contains 'edit' (generate select field) or 'show' (generate read-only output) or 'autocomplete'
     * @param propertyName The property of the domain object
     * @param object The domain object
     * @param class The CSS class to be supplied to the enclosing row
     * @param from A list of values to be used in lieu of all objects in the domain class
     * @param sort The property to sort the domain class items in the list by (default: name)
     */
    def domainObject = { attrs ->
        def nullable=false
        def constrainedProperty

        if (attrs.object.metaClass.hasProperty("constraintsMap")) {
            constrainedProperty=attrs.object.getClass().constraintsMap[attrs.propertyName]
            nullable=constrainedProperty.isNullable()
        }

        def propertyClass=dialogService.getAssociationClass(attrs.object,attrs.propertyName)

        def optionValues = []

        if (attrs.containsKey('optional')) {
            nullable=(attrs.optional=="true")
        }
        out << row (attrs) {

            switch (attrs.mode) {
                case "show":
                    return """<p class="form-control-static">${fieldValue(bean: attrs.object, field: attrs.propertyName)}</p>"""
                    break

                case "edit":
                    if (attrs.from) {
                        optionValues = attrs.from
                    } else if (attrs.sort) {
                        optionValues = propertyClass.findAll([sort: attrs.sort, order: "asc"]) {}
                    }
                    else {
                        optionValues = propertyClass.findAll([sort: "name", order: "asc"]) {}
                    }

                    def value = attrs.object."${attrs.propertyName}"
                    def valueId = value ? value.id : null

                    if (nullable) {
                        return g.select(name: attrs.propertyName + ".id", value: valueId, from: optionValues, optionKey: "id", class: "form-control", noSelection:["null": "-"])
                    }
                    else {
                        return g.select(name: attrs.propertyName + ".id", value: valueId, from: optionValues, optionKey: "id", class: "form-control")
                    }
                    break

                case "autocomplete":
                    def domainPropertyName=GrailsNameUtils.getPropertyName(propertyClass.simpleName)

                    def acAction = attrs.acAction ? attrs.acAction : "autocomplete"
                    def jsonUrl = "${request.contextPath}/${domainPropertyName}/${acAction}"
                    attrs.jsonUrl=jsonUrl
                    attrs.mode="edit" // select edit mode of edit control
                    attrs.norow="true"
                    def value = attrs.object."${attrs.propertyName}"

                    if(value) {
                        def valueId = value ? value.id : null

                        attrs.from = [[key:valueId, value:value]]
                        attrs.value = valueId

                        attrs.optionKey = "key"
                        attrs.optionValue = "value"
                    }
                    return select(attrs)
                    break

            }
        }
    }

    /**
     * select tag - shows a select
     *
     * @param mode Contains 'edit' or 'show'
     * @param propertyName The property of the domain object
     * @param object The domain object
     * @param class The CSS class to be supplied to the enclosing row
     * @param from A list of values to be used
     * @param optionKey attribute to be supplied to the &lt;select&gt; element
     * @param optionValue attribute to be supplied to the &lt;select&gt; element
     * @param multiple attribute to be supplied to the &lt;select&gt; element
     * @param notOptional attribute to determine whether a dash is added as default
     * option or not.
     * @param style attribute to be supplied to the &lt;select&gt; element
     */
    def select = { attrs ->
        def multiple = attrs.multiple ? attrs.multiple : "no"
        def cssClass = attrs.class ? attrs.class : ""
        def optionKey = attrs.optionKey ? attrs.optionKey : null
        optionKey = ""

        out << row (attrs) {
            switch (attrs.mode) {
                case "show":
                    return """<p class="form-control-static">${fieldValue(bean: attrs.object, field: attrs.propertyName)}</p>"""
                    break

                case "edit":
                    def constrainedProperty=dialogService.getConstrainedProperty(attrs.object,attrs.propertyName)
                    def nullable=constrainedProperty?constrainedProperty.isNullable():false

                    def optionValues = []

                    if (attrs.from) {
                        optionValues = attrs.from
                    } else {
                        if (attrs.jsonUrl) {
                                def currentValue=attrs.object."${attrs.propertyName}"
                                if (currentValue) {
                                    optionValues=[currentValue]
                                }
                        } else {
                            if (constrainedProperty){
                                def inList=constrainedProperty.getInList()
                                if (inList) {
                                    optionValues=inList
                                }
                            }
                        }
                    }

                    def value=attrs.value?:attrs.object."${attrs.propertyName}"
                    def opts = [name: attrs.propertyName, value: value, from: optionValues, class: "form-control dialog-open-events select2"]
                    if (attrs["class"]) opts.class += " " + attrs["class"]

                    if (nullable && !attrs.notOptional) {
                        opts.put("noSelection", ["": "-"])
                    }

                    def copiedAttrs = ""
                    def skipAttrs = ["object", "propertyName", "mode", "class", "type", "value"]
                    attrs.each { attrKey, attrValue ->
                        if (!skipAttrs.contains(attrKey)) {
                            opts.put(attrKey,attrValue)
                        }
                    }

                    return g.select(opts)
                    break
            }
        }
    }

    /**
     * tabs tag - create a &lt;tabs&gt; enclosure for &lt;tab&gt; elements
     *
     * @param object The domain object
     */
    def tabs = { attrs, body ->
        this.pageScope.dialogTabNames = []
        def bodyText = body()

        def prefix = "dialog_" + attrs.object.getClass().getName() + "_" + attrs.object.id + "_"
        prefix = prefix.replace(".", "\\.").replace(":", "\\:")
        def domainPropertyName=GrailsNameUtils.getPropertyName(attrs.object.class.simpleName)

        out << """<div><ul class="nav nav-tabs" role="tablist">"""

        this.pageScope.dialogTabNames.eachWithIndex { name, i ->
            def defaultTabLabel = g.message(code: "dialog.tab.${name}", default: name)
            def eName=name.replace(" ","_")
            def tabLabel = g.message(code: "dialog.tab.${domainPropertyName}.${eName}", default: defaultTabLabel)
            out << """<li class="${i == 0 ? "active" : "" }"><a href="#${prefix}${eName}" aria-controls="${prefix}${eName}" role="tab" data-toggle="tab">${tabLabel}</a></li>"""
        }

        out << "</ul>"
        out << """<div class="tab-content">"""
        out << bodyText
        out << "</div></div>"
    }

    /**
     * tab tag - create a &lt;tab&gt; element
     *
     * @param name The name of this tab
     * @param object The domain object
     */
    def tab = { attrs, body ->
        this.pageScope.dialogTabNames += attrs.name
        def name=attrs.name.replace(" ","_")
        def prefix = "dialog_" + attrs.object.getClass().getName() + "_" + attrs.object.id + "_"
        out << """<div role="tabpanel" class="tab-pane ${this.pageScope.dialogTabNames.size() == 1 ? "active" : "" }" id="${prefix}${name}">"""
        out << body()
        out << "</div>"
    }

    /**
     * form tag - create a &lt;form&gt;
     *
     * @param name The name of this form
     * @param object The domain object
     * @param title The title of this dialog
     */
    def form = { attrs, body ->
        def defaultName = "form"
        if (attrs.object) {
            defaultName = GrailsNameUtils.getPropertyName(attrs.object.getClass().getSimpleName())
        }

        def name = attrs.name ? attrs.name : defaultName
        def title = attrs.title ? attrs.title : g.message(code: "form.${name}.title", default: name)

        out <<
            """
            <div class="modal" id="${name}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
                <div class="modal-dialog modal-lg" role="document" style="min-width:${attrs.width?:0};" >
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><i class="fa fa-times"></i></button>
                            <button type="button" class="close help-action" style="margin-right:10px;" aria-label="Help"><i class="fa fa-question"></i></button>
                            <h4 class="modal-title" id="myModalLabel">${title}</h4>
                        </div>
                        <div class="modal-body">
                            <form class="ajaxdialogform${attrs.vertical != true ? " form-horizontal" : ""}" name="${name}" method="post" action="${attrs.action}">
            """

        if (attrs.error) {
            out << """<div class="errors text-error alert alert-danger">${attrs.error ? attrs.error : ""}</div>"""
        } else {
            out << """<div class="errors alert alert-danger" style="display: none;"></div>"""
        }

        def message = g.message(code: "form.${name}.message", default: "")
        if (message) {
            out << """<div class="dialog-message">${message}</div>"""
        }

        // Add Hidden field with the id of the parent DomainObject (belongsTo)
        // REMARK: Currently it will only work if belongto has only 1 relation
        if (!(attrs.noBelongsTo && (attrs.noBelongsTo == true || attrs.noBelongsTo == "true"))) {
            if (attrs.object) {
                Map belongToMap = dialogService.getBelongsToMap(attrs.object)
                if (belongToMap?.size() == 1) {
                    belongToMap.each { key, value ->
                        out << g.hiddenField([name: key + ".id", value: attrs.object."${key}"?.id])
                    }
                }
            }
        }

        out << body()
        out <<
            """
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button id="cancel" type="button" class="btn btn-default" data-dismiss="modal">${g.message(code: "dialog.messages.cancel")}</button>
                            <button id="save" type="button" class="btn btn-primary">${g.message(code: "dialog.messages.ok")}</button>
                        </div>
                    </div>
                </div>
            </div>
            """
    }

    /**
    * pageform tag - create a &lt;pageform&gt;
    * this one is meant to be on a full page rather than in a popup dialog
    *
    * @param name The name of this tab
    * @param object The domain object
    * @param width The CSS width of this dialog (default: 600px)
    * @param title The title of this dialog
    */
    def pageform = { attrs,body ->
        def name = attrs.name ? attrs.name : "form"
        def action = attrs.action ? attrs.action : "submit${name}"
        def cssClass = attrs.class ? "pageform ${attrs.class}" : "pageform"
        def formClass = attrs.formClass ? attrs.formClass:""

        out <<
            """
            <div class="row ${cssClass}" id="${name}">
                <h3>${g.message(code:"page.${name}.title", default:"${name}")}</h3>
                <p>${g.message(code:"page.${name}.help", default:"")}</p>
                <form class="${formClass} form-horizontal" name="${name}" method="post" action="${action}">
            """
        if (attrs.error) {
            out << """<div class="errors">${attrs.error ? attrs.error: ""}</div>"""
        } else {
            out << """<div class="errors" style="display: none;"></div>"""
        }

        // Add Hidden field with the id of the parent DomainObject (belongsTo)
        // REMARK: Currently it will only work if belongto has only 1 relation
        if (attrs.object) {
            Map belongToMap = dialogService.getBelongsToMap(attrs.object)
            if (belongToMap?.size() == 1) {
                belongToMap.each { key, value ->
                    out << '<input id="${key}.id" type="hidden" name="${key}.id" value="' + attrs.object."${key}"?.id + '" />'
                }
            }
        }

        out << body()
        out <<
            """
                </form>
            </div>
            """
    }

    /**
     * Displays a set of navigation buttons for a page form. The button actions are the same as their names, and the labels are messages with key navigation.${name}.
     */
    def navigation = { attrs, body ->
        out << """<div class="navigation navigation-form-actions">"""
        def buttons = attrs.buttons.split(",")
        // By default, the last button is the default.
        def defaultButton = attrs.default ?: buttons[buttons.size() - 1]
        out << """<button style="overflow: visible !important; height: 0 !important; width: 0 !important; margin: 0 !important; border: 0 !important; padding: 0 !important; display: block !important;" type="submit" value="${defaultButton}" />"""

        buttons.each { name ->
            out << """<button type="submit" name="${name}" class="btn btn-default pull-right" value="${g.message(code: "navigation." + name, default: name)}">${g.message(code: "navigation." + name, default: name)}</button>"""
        }
        out << """</div>"""
    }

    /**
     * table tag - create a &lt;table&gt;
     */
    def table = { attrs, body ->
        out <<
            """
            <table class="table table-striped table-bordered">
                <tbody>
                """ +
                body() +
                """
                </tbody>
            </table>
            """
    }

    /**
     * detailTable tag - create a detail table in master/detail view
     * @param domainClass detail class name
     * @param object master object
     * @param property property that links detail with the master
     */
    def detailTable = { attrs ->
        def copiedAttrs = ""
        def skipAttrs = ["object", "propertyName", "mode", "class", "type", "value"]
        attrs.each { attrKey, attrValue ->
            if (!skipAttrs.contains(attrKey)) {
                copiedAttrs += """ ${attrKey}="${attrValue}" """
            }
        }
        def controllerName
        def listProperties
        def prefix
        def listConfig
        if (attrs.domainClass) {
            if (attrs.listConfig) {
                listConfig = attrs.listConfig
            } else {
                if (dialogService.hasProperty(attrs.domainClass,"listConfig")) {
                    listConfig=attrs.domainClass.listConfig
                }
            }
        }
        if (listConfig) {
            controllerName = listConfig.controller
            listProperties = listConfig.columns.collect { it.name }
            prefix = "detailTable_"+listConfig.name
        } else {
            controllerName = attrs.controllerName ?: GrailsNameUtils.getPropertyName(attrs.domainClass?.getSimpleName())
            listProperties = attrs.domainClass.listProperties
            prefix = "detailTable_" + attrs.domainClass
            prefix = prefix.replace(".", "_")
            prefix = prefix.replace("class ", "")
        }

        def optionalParams = "?objectId=" + attrs.object.id + "&objectClass=" + attrs.object.getClass().getName() + "&property=" + attrs.property
        def jsonUrl = "/" + controllerName + "/jsonlist" + optionalParams
        def positionUrl = "/" + controllerName + "/position" + optionalParams
        def cssClass = "detailTable"
        if (attrs.rowreordering) {
            cssClass += " rowreordering"
        }

        out <<
            """
            <table id="${prefix}" ${copiedAttrs} class="table table-striped table-bordered table-hover dialog-open-events ${cssClass}" jsonUrl="${jsonUrl}" positionUrl="${positionUrl}">
                <thead>
                    <tr>
            """
        if (listConfig) {
            listConfig.columns.each { column ->
                out << """<th class="${column.sortable ? "sortable" : "nonsortable"} ${listConfig.name}-${column.name}">${g.message(code: "list.${listConfig.name}.${column.name}.label")}</th>"""
            }
        } else {
            listProperties.each { propertyName ->
                out << """<th class="${controllerName}-${propertyName}">${g.message(code: "${controllerName}.${propertyName}.label", default: "${propertyName}")}</th>"""
            }
        }
        out <<
            """
                        <th class="nonsortable list-actions ${controllerName}-actions">${g.message(code: "dialog.list.actions.label", default: "Actions")}</th>
                    </tr>
                </thead>
            </table>
            """
    }

    /**
     * filesTable tag
     * @param object domain class instance
     */
    def filesTable = { attrs ->
        def controller
        def prefix
        if (attrs.controller) {
            controller = attrs.controller
            prefix = controller
        } else {
            controller = GrailsNameUtils.getPropertyName(attrs.object.class.getSimpleName())
            prefix = "filesTable_" + domainClass.name
        }
        def id = attrs.id ?: attrs.object.id
        prefix = prefix.replace(".", "_")
        prefix = prefix.replace("class ", "")

        def actions = attrs.actions ?: "none"

        def jsonUrl = "/" + controller + "/filelist/" + id + "?actions=" + actions

        out <<
            """
            <table id="${prefix}" class="table table-striped table-bordered table-hover dialog-open-events detailTable" jsonUrl="${jsonUrl}" newButton="false">
                <thead>
                    <tr>
                        <th>${g.message(code: "filestable.filename.label")}</th>
                        <th>${g.message(code: "filestable.size.label")}</th>
                        <th>${g.message(code: "filestable.date.label")}</th>
                        <th class="nonsortable">${g.message(code: "filestable.actions.label")}</th>
                    </tr>
                </thead>
            </table>
            """
    }

    /**
     * A tag for adding a simple table that will contain fileuploads.
     * @since 11/30/2017
     */
    def fileuploadTable = { attrs ->
        out <<
            """
            <table id="fileupload" class="table table-striped table-bordered table-hover dialog-open-events fileuploadTable" newButton="false">
                <thead>
                    <tr>
                        <th>${g.message(code: "fileuploadtable.name.label")}</th>
                        <th>${g.message(code: "fileuploadtable.mimetype.label")}</th>
                        <th>${g.message(code: "fileuploadtable.size.label")}</th>
                        <th class="actions">${g.message(code: "fileuploadtable.actions.label")}</th>
                    </tr>
                </thead>
            </table>
            """
    }

    /**
     * Displays an upload control in the dialog.
     */
    def upload = { attrs, body ->
        def copiedAttrs = ""
        def skipAttrs = ["object", "propertyName", "mode", "class", "type", "value"]
        attrs.each { attrKey, attrValue ->
            if (!skipAttrs.contains(attrKey)) {
                copiedAttrs += """ ${attrKey}="${attrValue}" """
            }
        }
        out << """<div ${copiedAttrs} class="upload-button-wrapper dialog-open-events" controller="${attrs.controller}" action="${attrs.action?:'upload'}">"""
        out << body()
        out << """</div>"""
    }

    // Upload progress bar
    def uploadProgressBar = { attrs, body ->
        attrs.name=attrs.name?:"uploadProgressBar"
        attrs.noLabel="true"

        out << """<div class="row upload-progress-row" style="display:none;">
                    <div class="col-md-12">
                        <div id="progress" class="progress" style="width:100%;display:none;margin-bottom:10px;">
                            <div id="list-upload-progress" class="progress-bar upload-progress" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">
                                <span id="list-upload-progress-percentage" class="upload-progress-percentage">0%</span>
                            </div>
                        </div>
                    </div>
                </div>"""
    }

    // Upload progress text
    def uploadProgressText = { attrs, body ->
        out << """<div class="row upload-progress-row" style="display:none;">
                    <div class="col-md-12">
                        <span id="list-upload-progress-text" class="upload-progress-text" style="width:100%;display:none;margin-bottom:10px;">&nbsp;</span>
                        </div>
                    </div>"""
    }
    def listToolBar = { attrs, body ->
        attrs.id=attrs.id?:"list-toolbar"
        out << """
        <div class="row" >
            <div class="col-md-3">
                <div id="${attrs.id}" class="btn-toolbar btn-toolbar-above-datatable" role="toolbar" >"""
                    out << body()
                  out << """
                </div>
            </div>
        </div>"""
    }

    // Upload button
    def uploadButton = { attrs, body ->
        out <<
            """
            <span href="#" class="btn btn-default btn-file upload-button">
                <span class="fa fa-upload" aria-hidden="true"></span> ${dialogService.getMessage('dialog.uploadButton.label')} <input type="file" multiple>
            </span>
            """
    }

    /**
     * Displays a dropdown menu in the menu bar. The key for the message is dropdown.code.label, with code replaced by the code attribute.
     */
    def dropdown = { attrs, body ->
        def idAttr = ''
        if ((attrs.id?:'') != '') {
            idAttr="id=\"${attrs.id?:''}\" "
        }

        out <<
            """
            <li ${idAttr?:''}class="dropdown ${attrs.class ?: ""}">
                <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">${g.message(code: "dropdown." + attrs.code + ".label")}<span class="caret"></span></a>
                <ul class="dropdown-menu">
        """
        out << body()
        out <<
            """
                </ul>
            </li>
            """
    }

    /**
     *  Displays a header in a dropdown menu. Should be used within a <dialog:dropdown> element.
     *  The key for the message is dropdown.code.label, with code replaced by the code attribute.
     */
    def dropdownHeader = { attrs, body ->
        def icon = ""
        if (attrs.icon) {
            icon = """<i class="${attrs.icon}"></i> """
        }
        out << """<li class="dropdown-header">${icon}${g.message(code: "dropdown." + attrs.code + ".label")}</li>"""
    }

    /**
     *  Displays a divider in a dropdown menu. Should be used within a <dialog:dropdown> element.
     *  The key for the message is dropdown.code.label, with code replaced by the code attribute.
     */
    def dropdownDivider = { attrs, body ->
        out << """<li role="separator" class="divider"></li>"""
    }

    /**
     *  Displays a submenu in a dropdown menu. Should be used within a <dialog:dropdown> element.
     *  The key for the message is dropdown.code.label, with code replaced by the code attribute.
     */
    def submenu = { attrs, body ->
        def icon = ""
        if (attrs.icon) {
            icon = """<i class="${attrs.icon}"></i> """
        }

        out <<
            """
            <li class="dropdown-submenu ${attrs.class ?: ""}">
                <a class="dropdown-toggle" data-toggle="dropdown" href="#">${icon}${g.message(code: "dropdown." + attrs.code + ".label")}</a>
                <ul class="dropdown-menu">
            """
        out << body()
        out <<
            """
                </ul>
            </li>
            """
    }

    /**
     * Displays a menu item in a dropdown menu. Should be used within a <dialog:dropdown> or <dialog:submenu> element.
     * The key for the label message is menu.code.label, with code replaced by the code attribute. The key for the help message is menu.code.help, with code replaced by the code attribute.
     *
     * @param code The code key for lookup of messages
     */
    def menuitem = { attrs, body ->
        def icon = ""
        if (attrs.icon) {
            icon = """<i class="${attrs.icon}"></i> """
        }
        def code = null
        if (attrs.code) {
            code = "menu." + attrs.code
        } else {
            code = "menu." + attrs.controller + "." + attrs.action
        }
        def label = g.message(code: code + ".label", default: attrs.label)
        def help = g.message(code: code + ".help", default: "")

        def onclick = ""
        def link = ""
        if (attrs.onclick) {
            if (attrs.onclick == "dialog") {
                def nosubmit = attrs.nosubmit ? true : false
                def params = ""
                if (attrs.params) {
                    params = attrs.params.collect { key, value -> "'${key}': '${value}'"}.join(",")
                }
                onclick = """onclick="dialog.formDialog('null', '${attrs.controller}', {'dialogname': '${attrs.action}', 'nosubmit': ${nosubmit}}, {${params}}, null);return false;" """
            } else {
                onclick = """onclick="${attrs.onclick}" """
            }

            link = """<a href="#" title="${help}">${icon}${label}</a>"""
        } else {
            def linkParams = [controller: attrs.controller, action: attrs.action, params: attrs.params, title: help]
            if (attrs.param_id) {
                linkParams["id"]=attrs.param_id
            }
            link = g.link(linkParams) { icon + label }
        }

        def copiedAttrs = ""
        def skipAttrs = ["class", "code", "label", "nosubmit", "params", "onclick", "icon", "controller", "action"]
        attrs.each { attrKey, attrValue ->
            if (!skipAttrs.contains(attrKey)) {
                copiedAttrs += """ ${attrKey}="${attrValue}" """
            }
        }

        out << """<li ${onclick}class="menu-item ${attrs.class ?: ""}"  ${copiedAttrs}>${link}</li>"""
    }

    /**
     * Select from a tree popup
     * This needs a JSON data source to provide information on tree nodes to the jstree component
     *
     * @param action The action to use for the JSON data source (default: treeJSON)
     * @param width The width of the dialog
     * @param height The height of the dialog
     * @param root The id of the root element to show in the tree
     * @param object The domain object
     * @param propertyName The property of the domain object
     * @param url The URL of the JSON data source (object and action are ignored)
     */
    def treeselect = { attrs, body ->
        out << row (attrs) {
            def action = attrs.action ?: "treeJSON"

            def domainPropertyName = GrailsNameUtils.getPropertyName(attrs.object."${attrs.propertyName}".getClass().getSimpleName())
            domainPropertyName=domainPropertyName.replaceAll('\\$.*','')

            def url = attrs.url ?: "${request.contextPath}/${domainPropertyName}/${action}"
            def attributes = ""
            if (attrs.width) { attributes += """ treeDialogWidth="${attrs.width}" """ }
            if (attrs.height){ attributes += """ treeDialogHeight="${attrs.height}" """ }
            if (attrs.root) { attributes += """ treeRoot="${attrs.root}" """ }

            def value = attrs.object."${attrs.propertyName}"

            def html =
                """
                <span id="treeselect-${attrs.propertyName}-span" treeUrl="${url}" treeTypes='""" + attrs.types + """' ${attributes}>
                    <span>${value ?: ""}</span>
                    <a href="#" onclick="dialog.tree.treeSelect('treeselect-${attrs.propertyName}');" class="btn btn-default btn-sm">...</a>
                    <input id="treeselect-${attrs.propertyName}-input" type="hidden" name="${attrs.propertyName}.id" value="${value?.id}" />
                </span>
                """
            return html
        }
    }

    /**
     * Creates next/previous links to support pagination for the current controller.<br/>
     *
     * @attr total REQUIRED The total number of results to paginate
     * @attr action the name of the action to use in the link, if not specified the default action will be linked
     * @attr controller the name of the controller to use in the link, if not specified the current controller will be linked
     * @attr id The id to use in the link
     * @attr params A map containing request parameters
     * @attr prev The text to display for the previous link (defaults to "Previous" as defined by default.paginate.prev property in I18n messages.properties)
     * @attr next The text to display for the next link (defaults to "Next" as defined by default.paginate.next property in I18n messages.properties)
     * @attr max The number of records displayed per page (defaults to 10). Used ONLY if params.max is empty
     * @attr maxsteps The number of steps displayed for pagination (defaults to 10). Used ONLY if params.maxsteps is empty
     * @attr offset Used only if params.offset is empty
     * @attr fragment The link fragment (often called anchor tag) to use
     */
    def paginate = { attrs ->
        def writer = out
        if (attrs.total == null) {
            throwTagError("Tag [paginate] is missing required attribute [total]")
        }
        def messageSource = grailsAttributes.messageSource
        def locale = RCU.getLocale(request)

        def total = attrs.int("total") ?: 0
        def action = (attrs.action ? attrs.action : (params.action ? params.action : "index"))
        def offset = params.int("offset") ?: 0
        def max = params.int("max")
        def maxsteps = (attrs.int("maxsteps") ?: 10)

        if (!offset) offset = (attrs.int("offset") ?: 0)
        if (!max) max = (attrs.int("max") ?: 10)

        def linkParams = [:]
        if (attrs.params) linkParams.putAll(attrs.params)
        linkParams.offset = offset - max
        linkParams.max = max
        if (params.sort) linkParams.sort = params.sort
        if (params.order) linkParams.order = params.order

        def linkTagAttrs = [action:action]
        if (attrs.namespace) {
            linkTagAttrs.namespace = attrs.namespace
        }
        if (attrs.controller) {
            linkTagAttrs.controller = attrs.controller
        }
        if (attrs.id != null) {
            linkTagAttrs.id = attrs.id
        }
        if (attrs.fragment != null) {
            linkTagAttrs.fragment = attrs.fragment
        }
        //add the mapping attribute if present
        if (attrs.mapping) {
            linkTagAttrs.mapping = attrs.mapping
        }

        if (attrs.uri) {
            linkTagAttrs.uri = attrs.uri
        }

        linkTagAttrs.params = linkParams
        linkTagAttrs.class = "step"

        def cssClasses = "pagination"
        if (attrs.class) {
            cssClasses = "pagination " + attrs.class
        }

        // determine paging variables
        def steps = maxsteps > 0
        int currentstep = (offset / max) + 1
        int firststep = 1
        int laststep = Math.round(Math.ceil(total / max))

        writer << "<ul class=\"${cssClasses}\">"

        // display previous link when not on firststep
        if (currentstep > firststep) {
            linkParams.offset = offset - max
            writer << '<li class="prev" data-offset="'+linkParams.offset+'">'
            writer << link(linkTagAttrs.clone()) {
                (attrs.prev ?: messageSource.getMessage("paginate.prev", null, "&laquo;", locale))
            }
            writer << '</li>'
        }
        else {
            writer << '<li class="prev disabled">'
            writer << '<span>'
            writer << (attrs.prev ?: messageSource.getMessage("paginate.prev", null, "&laquo;", locale))
            writer << '</span>'
            writer << '</li>'
        }

        // display steps when steps are enabled and laststep is not firststep
        if (steps && laststep > firststep) {


            // determine begin and endstep paging variables
            int beginstep = currentstep - Math.round(maxsteps / 2) + (maxsteps % 2)
            int endstep = currentstep + Math.round(maxsteps / 2) - 1

            if (beginstep < firststep) {
                beginstep = firststep
                endstep = maxsteps
            }
            if (endstep > laststep) {
                beginstep = laststep - maxsteps + 1
                if (beginstep < firststep) {
                    beginstep = firststep
                }
                endstep = laststep
            }

            // display firststep link when beginstep is not firststep
            if (beginstep > firststep) {
                linkParams.offset = 0
                writer << '<li data-offset="0">'
                writer << link(linkTagAttrs.clone()) {firststep.toString()}
                writer << '</li>'
                writer << '<li class="disabled"><span>...</span></li>'
            }

            // display paginate steps
            (beginstep..endstep).each { i ->
                if (currentstep == i) {
                    writer << "<li class=\"active hidden-xs\">"
                    writer << "<span>${i}</span>"
                    writer << "</li>";
                }
                else {
                    linkParams.offset = (i - 1) * max
                    writer << '<li data-offset="'+linkParams.offset+'" class=\"hidden-xs\">';
                    writer << link(linkTagAttrs.clone()) {i.toString()}
                    writer << "</li>";
                }
            }

            // display laststep link when endstep is not laststep
            if (endstep < laststep) {
                writer << '<li class="disabled"><span>...</span></li>'
                linkParams.offset = (laststep -1) * max
                writer << '<li data-offset="'+linkParams.offset+'">'
                writer << link(linkTagAttrs.clone()) { laststep.toString() }
                writer << '</li>'
            }
        }

        // display next link when not on laststep
        if (currentstep < laststep) {
            linkParams.offset = offset + max
            writer << '<li class="next" data-offset="'+linkParams.offset+'">'
            writer << link(linkTagAttrs.clone()) {
                (attrs.next ? attrs.next : messageSource.getMessage("paginate.next", null, "&raquo;", locale))
            }
            writer << '</li>'
        }
        else {
            linkParams.offset = offset + max
            writer << '<li class="disabled" data-offset="'+linkParams.offset+'"">'
            writer << '<span>'
            writer << (attrs.next ? attrs.next : messageSource.getMessage("paginate.next", null, "&raquo;", locale))
            writer << '</span>'
            writer << '</li>'
        }

        writer << '</ul>'
    }

    /**
     *  Displays a bootstrap alert
     *  The type can be info (default), warning, danger, success
     */
    def alert = { attrs, body ->
        def messageSource = grailsAttributes.messageSource
        def locale = RCU.getLocale(request)
        def alertType=attrs.type?:"info"
        out << """<div class="alert alert-${alertType} alert-dismissible" role="alert">"""
        out << '<button type="button" class="close" data-dismiss="alert" aria-label="' + messageSource.getMessage("dialog.alert.close", null, "close", locale) + '"><span aria-hidden="true">&times;</span></button>'
        out << body()
        out << "</div>"
   }

}
