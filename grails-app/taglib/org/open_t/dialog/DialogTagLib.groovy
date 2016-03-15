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

package org.open_t.dialog

import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.springframework.web.servlet.support.RequestContextUtils as RCU

/**
 * Tag library for Dialog plugin
 *
 * @author Joost Horward
 */
class DialogTagLib {

    def dialogService
    def listService
    def grailsApplication

    static namespace = "dialog"

    /**
     * Element to place in HTML page's <head> section
     * Initializes namespace dialog and hashlist for datatable
     * Sets base URL to be used.
     * @param request The HTTPServletRequest
     */
    def head = {
        def ckeditorBasePath = g.resource(plugin: "jquery-dialog", dir: "/js/ckeditor/")
        out <<
            """
            <script type="text/javascript">
                var dialog = {};
                dialog.dataTableHashList = {};
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
                dialog.messages.upload = "${message(code: "dialog.messages.upload")}";
                dialog.messages.dropfileshere = "${message(code: "dialog.messages.dropfileshere")}";
                dialog.messages.new = "${message(code: "dialog.messages.new")}";
                dialog.messages.confirmdelete = "${message(code: "dialog.messages.confirmdelete")}";
                dialog.messages.confirmdeleteTitle = "${message(code: "dialog.messages.confirmdeleteTitle")}";

                dialog.messages.datepicker = {};
                dialog.messages.datepicker.regional = {
                    closeText: "${message(code: "dialog.datepicker.closeText")}",
                    prevText: "${message(code: "dialog.datepicker.prevText")}",
                    nextText: "${message(code: "dialog.datepicker.nextText")}",
                    currentText: "${message(code: "dialog.datepicker.currentText")}",
                    monthNames: ${message(code: "dialog.datepicker.monthNames")},

                    monthNamesShort: ${message(code: "dialog.datepicker.monthNamesShort")},
                    dayNames: ${message(code: "dialog.datepicker.dayNames")},
                    dayNamesShort: ${message(code: "dialog.datepicker.dayNamesShort")},
                    dayNamesMin: ${message(code: "dialog.datepicker.dayNamesMin")},
                    weekHeader: "${message(code: "dialog.datepicker.weekHeader")}",
                    dateFormat: "${message(code: "dialog.datepicker.dateFormat")}",
                    firstDay: ${message(code: "dialog.datepicker.firstDay")},
                    isRTL: ${message(code: "dialog.datepicker.isRTL")},
                    showMonthAfterYear: ${message(code: "dialog.datepicker.showMonthAfterYear")},
                    yearSuffix: "${message(code: "dialog.datepicker.yearSuffix")}"
                };
                dialog.messages.datepicker.mask="${message(code: "dialog.datepicker.mask")}";
                dialog.messages.timepicker = {};
                dialog.messages.timepicker.regional = {
                    currentText: "${message(code: "dialog.timepicker.currentText")}",
                    closeText: "${message(code: "dialog.timepicker.closeText")}",
                    amNames: ${message(code: "dialog.timepicker.amNames")},
                    pmNames: ${message(code: "dialog.timepicker.pmNames")},
                    timeFormat: "${message(code: "dialog.timepicker.timeFormat")}",
                    timeSuffix: "${message(code: "dialog.timepicker.timeSuffix")}",
                    timeOnlyTitle: "${message(code: "dialog.timepicker.timeOnlyTitle")}",
                    timeText: "${message(code: "dialog.timepicker.timeText")}",
                    hourText: "${message(code: "dialog.timepicker.hourText")}",
                    minuteText: "${message(code: "dialog.timepicker.minuteText")}",
                    secondText: "${message(code: "dialog.timepicker.secondText")}",
                    millisecText: "${message(code: "dialog.timepicker.millisecText")}",
                    microsecText: "${message(code: "dialog.timepicker.microsecText")}",
                    timezoneText: "${message(code: "dialog.timepicker.timezoneText")}",
                    isRTL: ${message(code: "dialog.timepicker.isRTL")}
                };
                dialog.messages.timepicker.mask="${message(code: "dialog.timepicker.mask")}";
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
                var CKEDITOR_BASEPATH = "${ckeditorBasePath}";
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
                    \$(document).trigger("dialog-init", {});\n\
                    \$(".dialog-open-events").trigger("dialog-open", {"page":true});
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

        def object = attrs.object
        def domainPropertyName = object.getClass().getName()
        def domainClass = new DefaultGrailsDomainClass(object.class)
        domainPropertyName = domainClass.propertyName
        def propertyName = attrs.propertyName
        def property = domainClass.getPropertyByName(propertyName)
        def naturalName = property.naturalName
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
            out << """<label for="${attrs.propertyName}"${attrs.vertical != "true" ? " class='col-sm-2 control-label'" : ""}>${g.message(code: "${domainPropertyName}.${propertyName}.label", default: "${naturalName}")}</label>"""
        }

        //control en help
        if (attrs.vertical != "true") {
            out << """<div class="col-sm-${attrs.noLabel != "true" ? "10" : "12"}">"""
        }
        out << body()
        if (attrs.noHelp != "true") {
            if (g.message(code: "${domainPropertyName}.${propertyName}.help", default: "")) {
                out << """<span id="help-${attrs.propertyName}" class="help-block">${g.message(code: "${domainPropertyName}.${propertyName}.help", default: "Help!")}</span>"""
            }
        }
        if (attrs.vertical != "true") {
            out << "</div>"
        }

        //end row
        out << "</div>"
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

        //begin row
        out << """<div class="form-group ${cssClass}">"""
        
        //label
        if (attrs.noLabel != "true") {
            out << """<label for="${attrs.name}"${attrs.vertical != "true" ? " class='col-sm-2 control-label'" : ""}>${label}</label>"""
        }

        //control en help
        if (attrs.vertical != "true") {
            out << """<div class="col-sm-${attrs.noLabel != "true" ? "10" : "12"}">"""
        }
        out << body()
        if (attrs.noHelp != "true") {
            if (g.message(code: "${attrs.name}.help", default: "")) {
                out << """<span id="help-${attrs.name}" class="help-block">${g.message(code: "${attrs.name}.help", default: "Help!")}</span>"""
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
     * This generates an text input element that pops up a calendar
     * The format to be used is fixed yyyy-MM-ddTHH:mm:ssZ in the update field
     *
     * @param mode Contains 'edit' (generate edit field) or 'show' (generate read-only output)
     * @param propertyName The property of the domain object
     * @param object The domain object
     * @param class The CSS class to be supplied to the enclosing row
     */
    def date = { attrs ->

        def value = attrs.object."${attrs.propertyName}"

        out << row (attrs) {

            switch (attrs.mode) {
                case "show":
                    return """<p class="form-control-static">${listService.getDisplayString(value)}</p>"""
                    break

                case "edit":
                    def dateValue = value ? value.format("yyyy-MM-dd'T00:00:00Z'") : ""
                    def inputValue = value ? value.format("yyyy-MM-dd") : ""

                    return
                        """
                        <input id="entry-${attrs.propertyName}" name="entry-${attrs.propertyName}" type="date" class="dialog-open-events datepicker form-control" value="${inputValue}" />
                        <input id="update-${attrs.propertyName}" name="${attrs.propertyName}" type="hidden" class="datetimeISO" value="${dateValue}" />
                        """
                    break
            }
        }
    }

    /**
     * Date input field tag
     * This generates an text input element that pops up a calendar plus a text input element for the time in hh:mm format
     * Currently the format to be used for the date is fixed yyyy-MM-dd
     * It uses the DateTimePropertyEditor to process the text
     * Generates a hidden field which triggers the use of the structured property editor
     *
     * @param mode Contains 'edit' (generate edit field) or 'show' (generate read-only output)
     * @param propertyName The property of the domain object
     * @param object The domain object
     * @param class The CSS class to be supplied to the enclosing row
     */

    def dateTime = { attrs ->

        def value = attrs.object."${attrs.propertyName}"

        out << row (attrs) {

            switch (attrs.mode) {
                case "show":
                    return """<p class="form-control-static">${listService.getDisplayString(value)}</p>"""
                    break

                case "edit":
                    def dateValue = value ? value.format("yyyy-MM-dd'T'HH:mm:ss'Z'") : ""
                    def inputValue = value ? value.format("yyyy-MM-dd") : ""
                    def timeFormat = "HH:mm"
                    def timeAttrs=[id:"time-${attrs.propertyName}",name:"time-${attrs.propertyName}",type:"time",value:formatDate(date:attrs.object."${attrs.propertyName}",format:timeFormat),class:"time timepicker dialog-open-events form-control"]

                    return
                        """
                        <input id="entry-${attrs.propertyName}" name="entry-${attrs.propertyName}" type="date" class="dialog-open-events datepicker ignore-validation form-control" value="${inputValue}" />
                        <input id="update-${attrs.propertyName}" name="${attrs.propertyName}" type="hidden" class="datetimeISO" value="${dateValue}" />
                        """ +
                        g.field(timeAttrs)
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
                    newAttrs += [name: attrs.propertyName, value: attrs.object."${attrs.propertyName}", cols: cols, rows: rows , id: "id${new Random().nextInt(10000000)}"]

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
        newAttrs.value = xmltext ? dialogService.prettyPrint(xmltext) : ""
        newAttrs.name = attrs.propertyName
        if (newAttrs["class"]) {
            newAttrs["class"] += " dialog-open-events"
        } else {
            newAttrs["class"] = "dialog-open-events"
        }

        out << row (attrs) {

            switch (attrs.mode) {
                case "show":
                    String s = xmltext ? dialogService.prettyPrint(xmltext) : ""
                    return """${g.textArea(newAttrs) {s.encodeAsHTML()}}"""
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

        def optionValues = []
        def domainClass = new DefaultGrailsDomainClass(attrs.object.class)
        def property = domainClass.getPropertyByName(attrs.propertyName)

        out << row (attrs) {

            switch (attrs.mode) {
                case "show":
                    return """<p class="form-control-static">${fieldValue(bean: attrs.object, field: attrs.propertyName)}</p>"""
                    break

                case "edit":
                    if (attrs.from) {
                        optionValues = attrs.from
                    } else if (attrs.sort) {
                        optionValues = property.getType().findAll([sort: attrs.sort, order: "asc"]) {}
                    }
                    else {
                        optionValues = property.getType().findAll([sort: "name", order: "asc"]) {}
                    }

                    def value = attrs.object."${attrs.propertyName}"
                    def valueId = value ? value.id : null

                    if (property.isOptional()) {
                        return g.select(name: attrs.propertyName + ".id", value: valueId, from: optionValues, optionKey: "id", class: "form-control", noSelection:["null": "-"])
                    }
                    else {
                        return g.select(name: attrs.propertyName + ".id", value: valueId, from: optionValues, optionKey: "id", class: "form-control")
                    }
                    break

                case "autocomplete":
                    if (attrs.from) {
                        optionValues = attrs.from
                    }

                    def value = attrs.object."${attrs.propertyName}"
                    def valueLabel = value ? value.acLabel : ""
                    def valueDescription = value ? value.acDescription : ""
                    def valueId = value ? value.id : null

                    def dc = new DefaultGrailsDomainClass(property.getType())
                    def domainPropertyName = dc.getPropertyName()
                    def acAction = attrs.acAction ? attrs.acAction : "autocomplete"
                    def jsonUrl = "${request.contextPath}/${domainPropertyName}/${acAction}"

                    def descriptionText = ""
                    if (attrs.subtitle == "true") {
                        descriptionText = """<p id="${attrs.propertyName}-description" class="autocomplete-description">${valueDescription}</p>"""
                    }
                    def containerClass = value ? "ac-selected" : "ac-idle"

                    //input + hidden field
                    return
                        """
                        <input name="${attrs.propertyName}-entry" value="${valueLabel}" type="text" class="autocomplete dialog-open-events" jsonUrl="${jsonUrl}" class="form-control" />
                        ${descriptionText}
                        <input name="${attrs.propertyName}.id" value="${valueId}" type="hidden" label="${valueLabel}" />
                        """
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
                    def domainClass = new DefaultGrailsDomainClass(attrs.object.class)
                    def property=domainClass.getPropertyByName(attrs.propertyName)
                    def cp = domainClass.constrainedProperties[attrs.propertyName]

                    def optionValues = []
                    if (attrs.from) {
                        optionValues = attrs.from
                    } else {
                        optionValues = attrs.object.constraints."${attrs.propertyName}".inList
                    }

                    def opts = [name: attrs.propertyName, value: attrs.object."${attrs.propertyName}", from: optionValues, class: "form-control"]
                    if (attrs["class"]) opts.class += " " + attrs["class"]
                    if (attrs["optionKey"]) opts.put("optionKey", attrs["optionKey"])
                    if (attrs["optionValue"]) opts.put("optionValue", attrs["optionValue"])
                    if (attrs["multiple"]) opts.put("multiple", attrs["multiple"])
                    if (attrs["style"]) opts.put("style", attrs["style"])

                    if (property.isOptional()) {
                        // TODO: yes. ""  for strings, null for int's
                        opts.put("noSelection", ["": "-"])
                        //opts.put("noSelection", ["null": "-"])
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
        def defaultDomainClass = new DefaultGrailsDomainClass(attrs.object.class)
        def domainPropertyName = defaultDomainClass.propertyName

        out << """<div><ul class="nav nav-tabs" role="tablist">"""

        this.pageScope.dialogTabNames.eachWithIndex { name, i ->
            def defaultTabLabel = g.message(code: "dialog.tab.${name}", default: name)
            def tabLabel = g.message(code: "dialog.tab.${domainPropertyName}.${name}", default: defaultTabLabel)
            out << """<li role="presentation" class="${i == 0 ? "active" : "" }"><a href="#${prefix}${name}" aria-controls="${prefix}${name}" role="tab" data-toggle="tab">${tabLabel}</a></li>"""
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

        def prefix = "dialog_" + attrs.object.getClass().getName() + "_" + attrs.object.id + "_"

        out << """<div role="tabpanel" class="tab-pane ${this.pageScope.dialogTabNames.size() == 1 ? "active" : "" }" id="${prefix}${attrs.name}">"""
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
            defaultName = new DefaultGrailsDomainClass(attrs.object.class).getPropertyName()
        }

        def name = attrs.name ? attrs.name : defaultName
        def title = attrs.title ? attrs.title : g.message(code: "form.${name}.title", default: name)

        out <<
            """
            <div class="modal fade" id="${name}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
                <div class="modal-dialog modal-lg" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                            <h4 class="modal-title" id="myModalLabel">${title}</h4>
                        </div>
                        <div class="modal-body">
                            <form class="ajaxdialogform${attrs.vertical != true ? " form-horizontal" : ""}" name="${name}" method="post" action="${attrs.action}">
            """

        if (attrs.error) {
            out << """<div class="errors text-error">${attrs.error ? attrs.error : ""}</div>"""
        } else {
            out << """<div class="errors" style="display: none;"></div>"""
        }

        def message = g.message(code: "form.${name}.message", default: "")
        if (message) {
            out << """<div class="dialog-message">${message}</div>"""
        }

        // Add Hidden field with the id of the parent DomainObject (belongsTo)
        // REMARK: Currently it will only work if belongto has only 1 relation
        if (!(attrs.noBelongsTo && (attrs.noBelongsTo == true || attrs.noBelongsTo == "true"))) {
            if (attrs.object) {
                def defaultDomainClass = new DefaultGrailsDomainClass(attrs.object.class)
                Map belongToMap = defaultDomainClass.getStaticPropertyValue(GrailsDomainClassProperty.BELONGS_TO, Map.class)
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
            def defaultDomainClass = new DefaultGrailsDomainClass(attrs.object.class)
            Map belongToMap = defaultDomainClass.getStaticPropertyValue(GrailsDomainClassProperty.BELONGS_TO, Map.class)
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
            def domainClass = new DefaultGrailsDomainClass(attrs.domainClass)
            if (domainClass.hasProperty("listConfig")) {
                listConfig = attrs.listConfig?:attrs.domainClass?.listConfig
            }
        }
        if (listConfig) {
            controllerName = listConfig.controller
            listProperties = listConfig.columns.collect { it.name }
            prefix = "detailTable_"+listConfig.name
        } else {

            def domainClass = new DefaultGrailsDomainClass(attrs.domainClass)

            controllerName = attrs.controllerName ?: domainClass.getPropertyName()
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
            def domainClass = new DefaultGrailsDomainClass(attrs.object.class)
            controller = domainClass.getPropertyName()
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
     * Displays an upload control in the dialog
     */
    def upload = { attrs, body ->

        def copiedAttrs = ""
        def skipAttrs = ["object", "propertyName", "mode", "class", "type", "value"]
        attrs.each { attrKey, attrValue ->
            if (!skipAttrs.contains(attrKey)) {
                copiedAttrs+=""" ${attrKey}="${attrValue}" """
            }
        }

        out << """<div class="upload dialog-open-events" ${copiedAttrs}>"""
        out << body()
        out << """</div>"""
    }

    /**
     * Header element for upload
     * Only needed for full-page dialogs containing an upload.
     *
     * @param action The action to use for the JSON data source (default: fileupload)
     * @param object The domain object
     * @param propertyName The property of the domain object
     * @param url The URL of the JSON data source (object and action are ignored)
     */
    def uploadHead = { attrs ->

        def action = attrs.action ?: "fileupload"
        def domainClass = new DefaultGrailsDomainClass(attrs.object.class)
        def property = domainClass.getPropertyByName(attrs.propertyName)
        def dc = new DefaultGrailsDomainClass(property.getType())
        def domainPropertyName = dc.getPropertyName()
        def url = attrs.url ?: "${request.contextPath}/${domainPropertyName}/${action}"

        out <<
            """
            <script  type="text/javascript">
                \$(function() {
                    var uploader = new qq.FileUploader({
                    element: document.getElementById("file-uploader"),
                    // path to server-side upload script
                    action: '${url}',
                    params: {
                    },
                    onComplete: function(id, fileName, responseJSON) {
                        \$("#form").append('<input type=\"hidden\" name=\"filename\" value=\"' + fileName + '\" />');
                    },
                    template:
                        '<div class="qq-uploader">' +
                            '<div class="qq-upload-drop-area"><span>${message(code:'cmis.uploader.dropfileshere')}</span></div>' +
                            '<div class="qq-upload-button">${message(code:'cmis.uploader.uploadafile')}</div>' +
                            '<ul class="qq-upload-list"></ul>' +
                        '</div>'
                    });
                });
            </script>
            """
    }

    /**
     * Displays a dropdown menu in the menu bar. The key for the message is dropdown.code.label, with code replaced by the code attribute.
     */
    def dropdown = { attrs, body ->

        out <<
            """
            <li class="dropdown ${attrs.class ?: ""}">
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
        def label = attrs.label ?: g.message(code: code + ".label")
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
                onclick = """onclick="dialog.formDialog('null', '${attrs.controller}', {'dialogname': '${attrs.action}', 'nosubmit': ${nosubmit}}, {${params}}, null)" """
            } else {
                onclick = """onclick="${attrs.onclick}" """
            }

            link = """<a href="#" title="${help}">${icon}${label}</a>"""
        } else {
            def linkParams = [controller: attrs.controller, action: attrs.action, params: attrs.params, title: help]
            if (attrs.id) {
                linkParams["id"]=attrs.id
            }
            link = g.link(linkParams) { icon + label }
        }

        out << """<li ${onclick}class="menu-item ${attrs.class ?: ""}">${link}</li>"""
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

        out << row ("class": attrs.class, object: attrs.object, propertyName: attrs.propertyName) {
            def action = attrs.action ?: "treeJSON"
            def domainClass = new DefaultGrailsDomainClass(attrs.object.class)
            def property = domainClass.getPropertyByName(attrs.propertyName)
            def dc = new DefaultGrailsDomainClass(property.getType())
            def domainPropertyName = dc.getPropertyName()
            def url = attrs.url ?: "${request.contextPath}/${domainPropertyName}/${action}"
            def attributes = ""
            if (attrs.width) { attributes += """ treeDialogWidth="${attrs.width}" """ }
            if (attrs.height){ attributes += """ treeDialogHeight="${attrs.height}" """ }
            if (attrs.root) { attributes += """ treeRoot="${attrs.root}" """ }

            def value = attrs.object."${attrs.propertyName}"

            return
                """
                <span id="treeselect-${attrs.propertyName}-span" treeUrl="${url}" ${attributes}>
                    <span>${value ?: ""}</span>
                    <a href="#" onclick="dialog.tree.treeSelect('treeselect-${attrs.propertyName}');" class="btn btn-default btn-sm">...</a>
                    <input id="treeselect-field-input" type="hidden" name="${attrs.propertyName}.id" value="${value?.id}" />
                </span>
                """
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

        linkTagAttrs.params = linkParams

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
            writer << '<li class="prev">'
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
            linkTagAttrs.class = "step"

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
                writer << '<li>'
                writer << link(linkTagAttrs.clone()) {firststep.toString()}
                writer << '</li>'
                writer << '<li class="disabled"><span>...</span></li>'
            }

            // display paginate steps
            (beginstep..endstep).each { i ->
                if (currentstep == i) {
                    writer << "<li class=\"active\">"
                    writer << "<span>${i}</span>"
                    writer << "</li>";
                }
                else {
                    linkParams.offset = (i - 1) * max
                    writer << "<li>";
                    writer << link(linkTagAttrs.clone()) {i.toString()}
                    writer << "</li>";
                }
            }

            // display laststep link when endstep is not laststep
            if (endstep < laststep) {
                writer << '<li class="disabled"><span>...</span></li>'
                linkParams.offset = (laststep -1) * max
                writer << '<li>'
                writer << link(linkTagAttrs.clone()) { laststep.toString() }
                writer << '</li>'
            }
        }

        // display next link when not on laststep
        if (currentstep < laststep) {
            linkParams.offset = offset + max
            writer << '<li class="next">'
            writer << link(linkTagAttrs.clone()) {
                (attrs.next ? attrs.next : messageSource.getMessage("paginate.next", null, "&raquo;", locale))
            }
            writer << '</li>'
        }
        else {
            linkParams.offset = offset + max
            writer << '<li class="disabled">'
            writer << '<span>'
            writer << (attrs.next ? attrs.next : messageSource.getMessage("paginate.next", null, "&raquo;", locale))
            writer << '</span>'
            writer << '</li>'
        }

        writer << '</ul>'
    }
}
