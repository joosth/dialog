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

/**
 * Tag library for Dialog plugin
 *
 * @author Joost Horward
 */
class DialogTagLib {

	def dialogService
	def listService
	def grailsApplication

	static namespace = 'dialog'

	/**
	 * Element to place in HTML page's <head> section
	 * Initializes namespace dialog and hashlist for datatable
	 * Sets base URL to be used.
	 * @param request The HTTPServletRequest
	 */
	def head = {
		out << """<script type="text/javascript">
        	var dialog={};
        	dialog.dataTableHashList = {};
			dialog.options = {
				"refreshPage":false
			};
        	dialog.baseUrl="${request.contextPath}";
			dialog.pluginUrl="${resource(plugin:'dialog')}";
			dialog.language="${g.message(code:'language.code',default:'en')}";
			dialog.dataTablesLanguageUrl="${resource(plugin:'dialog',dir:'js/jquery')}/dataTables/localisation/dataTables.${g.message(code:'language.code',default:'en')}.txt";

			dialog.messages={};
			dialog.messages.ok="${message(code:'dialog.messages.ok')}";
			dialog.messages.cancel="${message(code:'dialog.messages.cancel')}";
			dialog.messages.upload="${message(code:'dialog.messages.upload')}";
			dialog.messages.dropfileshere="${message(code:'dialog.messages.dropfileshere')}";
			dialog.messages.confirmdelete="${message(code:'dialog.messages.confirmdelete')}";
			dialog.messages.confirmdeleteTitle="${message(code:'dialog.messages.confirmdeleteTitle')}";

            dialog.messages.datepicker = {};
	        dialog.messages.datepicker.regional = {
		        closeText: "${message(code:'dialog.datepicker.closeText')}",
				prevText: "${message(code:'dialog.datepicker.prevText')}",
				nextText: "${message(code:'dialog.datepicker.nextText')}",
				currentText: "${message(code:'dialog.datepicker.currentText')}",
				monthNames: ${message(code:'dialog.datepicker.monthNames')},

				monthNamesShort: ${message(code:'dialog.datepicker.monthNamesShort')},
				dayNames: ${message(code:'dialog.datepicker.dayNames')},
				dayNamesShort: ${message(code:'dialog.datepicker.dayNamesShort')},
				dayNamesMin: ${message(code:'dialog.datepicker.dayNamesMin')},
				weekHeader: "${message(code:'dialog.datepicker.weekHeader')}",
				dateFormat: "${message(code:'dialog.datepicker.dateFormat')}",
				firstDay: ${message(code:'dialog.datepicker.firstDay')},
				isRTL: ${message(code:'dialog.datepicker.isRTL')},
				showMonthAfterYear: ${message(code:'dialog.datepicker.showMonthAfterYear')},
				yearSuffix: "${message(code:'dialog.datepicker.yearSuffix')}"
            };
        </script>
		"""

	}

	/**
	 * This tag generates a 2-cell row in the dialog table. The first cell contains the property's label, the second one contains the edit element or display value.
	 * It is mostly for internal use, the dialog elements input, select etc. use this to wrap themselves in.
	 *
	 *  @param object The domain object
	 *  @param propertyName The property of the domain object
	 *
	 */


	def row = { attrs,body ->
		def object=attrs.object;
		def domainPropertyName=object.getClass().getName()
		def domainClass = new DefaultGrailsDomainClass( object.class )
		domainPropertyName=domainClass.propertyName
		def propertyName=attrs.propertyName;
		def property=domainClass.getPropertyByName(propertyName)
		def naturalName=property.naturalName;
		def cssClass=attrs.class ? attrs.class : ""
		def errors=""
		if (attrs.object.hasErrors()) {
			if(attrs.object.errors.getFieldError(propertyName)) {
				errors=g.message(code:"${domainPropertyName}.${propertyName}.error", default:attrs.object.errors.getFieldError(propertyName).defaultMessage)
				cssClass+=" error"
			}
		}
		if (attrs.vertical == "true") {
			out <<"""
			<tr class="prop object-${domainPropertyName} property-${domainPropertyName}-${propertyName} property-${propertyName} ${cssClass}">
				<td valign="top" class="name">
					<label for="name">${g.message(code:"${domainPropertyName}.${propertyName}.label", default:"${naturalName}")}</label>
				</td>
				<td>&nbsp;
				</td>
				<td>
					<p align=right><span class="help-icon help action" title="${g.message(code:"${domainPropertyName}.${propertyName}.help",default:'Help!')}" href="#">&nbsp;</span></p>
				</td>
			</tr>
			<tr class="prop object-${domainPropertyName} property-${domainPropertyName}-${propertyName} property-${propertyName} ${attrs.class}">
				<td valign="top" colspan="3" class="value ${attrs.class}">"""
			out << body()
			out << """
				</td>
			</tr>"""
		} else {
			int colspan=1
			if (attrs.noLabel=="true") { colspan+=1 }
			if (attrs.noHelp=="true") { colspan+=1 }
			if (attrs.noErrors=="true") { colspan+=1 }

			out <<"""<tr class="prop object-${domainPropertyName} property-${domainPropertyName}-${propertyName} property-${propertyName} ${cssClass}">"""
			if (attrs.noLabel!="true"){
			out << """<td valign="top" class="name">
					<label for="name">${g.message(code:"${domainPropertyName}.${propertyName}.label", default:"${naturalName}")}</label>
					</td>
					"""
			}
			out <<"""<td colspan="${colspan}" valign="top" class="value ${attrs.class}">"""
			out << body()

			out << """</td>"""
			if (attrs.noHelp!="true"){
				if (g.message(code:"${domainPropertyName}.${propertyName}.help",default:'')) {
					out << """<td>&nbsp;<span class="help-icon help action" title="${g.message(code:"${domainPropertyName}.${propertyName}.help",default:'Help!')}" href="#">&nbsp;</span></td>"""
				}
			}

			if (attrs.noErrors!="true"){
				out <<"""<td class="text-error">${errors}</td>"""
			}
			out <<"""</tr>"""
		}
	}

	def simplerow = { attrs,body ->
		def cssClass=attrs.class ? attrs.class : ""
		def error=attrs.error ? attrs.error : ""
		def errors=""
		if (attrs.error) {
				cssClass+=" error"
			}
		def label=attrs.label?:g.message(code:"${attrs.name}.label", default:"${attrs.name}")

		if (attrs.vertical == "true") {
			out <<"""
			<tr class="prop ${cssClass}">
				<td valign="top" class="name">
					<label for="name">${label}</label>
				</td>
				<td>&nbsp;
				</td>
				<td>
					<p align=right><span class="help-icon help action" title="${g.message(code:"${attrs.name}.help",default:'Help!')}" href="#">&nbsp;</span></p>
				</td>
			</tr>
			<tr class="prop ${attrs.class}">
				<td valign="top" colspan="3" class="value ${attrs.class}">"""
			out << body()
			out << """
				</td>
			</tr>"""
		} else {
			out <<"""<tr class="prop ${cssClass}">
					<td valign="top" class="name">
					<label for="name">${label}</label>
					</td>
					<td valign="top" class="value ${attrs.class}">"""
			out << body()

			def helptext="&nbsp;"
			def helpTitle=attrs.help?:g.message(code:"${attrs.name}.help")
			if (attrs.help || g.message(code:"${attrs.name}.help",default:'UNKNOWN')!='UNKNOWN') {
				helptext="""<span class="help-icon help action" title="${helpTitle}" href="#">&nbsp;</span>"""
			}
			out << """</td><td>${helptext}</td><td>${error}</td></tr>"""
		}

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

		out << row ("class":attrs.class,object:attrs.object,propertyName:attrs.propertyName) {


			switch(attrs.mode) {
				case "show":
					"""${fieldValue(bean: attrs.object, field: attrs.propertyName)}"""
					break

				case "edit":
					def name=attrs.propertyName
					def value=null
					if (attrs.value) {
						value=attrs.value
					} else {
						value=fieldValue(bean: attrs.object, field: attrs.propertyName)
					}

					// Copy all extra attributes, skip the ones that are only meaningful for textField or are handled manually
					def copiedAttrs=""
					def skipAttrs=['object','propertyName','mode','class','type','value']
					attrs.each { attrKey, attrValue ->
						 if (!skipAttrs.contains(attrKey))
						 {
							 copiedAttrs+=""" ${attrKey}="${attrValue}" """
						 }
					}
					def inputType="text"
					if (attrs.type) inputType=attrs.type

					"""<input type="${inputType}" name="${name}" value="${value}" id="${name}" ${copiedAttrs}  />"""

					break
			}
		}
	}

	/**
	* Date input field tag
	* This generates an text input element that pops up a calendar
	* Currently the format to be used is fixed yyyy-MM-dd
	* It uses the DateTimePropertyEditor to process the text
	* Generates a hidden field which triggers the use of the structured property editor
	*
	* @param mode Contains 'edit' (generate edit field) or 'show' (generate read-only output)
	* @param propertyName The property of the domain object
	* @param object The domain object
	* @param class The CSS class to be supplied to the enclosing row
	*/

	def date = { attrs ->

		out << row ("class":attrs.class,object:attrs.object,propertyName:attrs.propertyName) {

			switch(attrs.mode) {
				case "show":
					def value=attrs.object."${attrs.propertyName}"

					return listService.getDisplayString(value)

					break

				case "edit":
					def hiddenAttrs=[name:attrs.propertyName,value:'struct']
					out << g.hiddenField(hiddenAttrs)
					def dateValue
                    def submitDateValue
                    def dateFormat=g.message(code:"dialog.date.format",default:"yyyy-MM-dd")
					if (attrs.object."${attrs.propertyName}") {
						dateValue=formatDate(date:attrs.object."${attrs.propertyName}",format:dateFormat)
                        submitDateValue=formatDate(date:attrs.object."${attrs.propertyName}",format:"yyyy-MM-dd'T'HH:mm:ss")
					}

					out << g.textField(name:'entry-'+attrs.propertyName,value:dateValue,class:'datepicker')
                    out << g.hiddenField(name:attrs.propertyName+'_date',value:submitDateValue)
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
		out << row (object:attrs.object,propertyName:attrs.propertyName) {

			switch(attrs.mode) {
				case "show":
					def value=attrs.object."${attrs.propertyName}"
					return listService.getDisplayString(value)
					break

				case "edit":
					def dateValue=null
                    def submitDateValue
                    def dateFormat=g.message(code:"dialog.date.format",default:"yyyy-MM-dd")
                    if (attrs.object."${attrs.propertyName}") {
						dateValue=formatDate(date:attrs.object."${attrs.propertyName}",format:dateFormat)
                        submitDateValue=formatDate(date:attrs.object."${attrs.propertyName}",format:"yyyy-MM-dd'T'HH:mm:ss")
					}

					def hiddenAttrs=[name:attrs.propertyName,value:'struct']
					out << g.hiddenField(hiddenAttrs)

                    out << g.textField(name:'entry-'+attrs.propertyName,value:dateValue,class:'datepicker')
                    out << g.hiddenField(name:attrs.propertyName+'_date',value:submitDateValue)

					out << "&nbsp;"

                    def timeFormat=g.message(code:"dialog.time.format",default:"HH:mm")
					def timeAttrs=[name:attrs.propertyName+'_time',value:formatDate(date:attrs.object."${attrs.propertyName}",format:timeFormat),class:'time']
					out << g.textField(timeAttrs)

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

		def copiedAttrs=""
		def skipAttrs=['object','propertyName','mode','type','value']
		def newAttrs=attrs.findAll { attrKey, attrValue -> !skipAttrs.contains(attrKey)}

		out << row (attrs) {
			switch(attrs.mode) {
				case "show":
					"""${fieldValue(bean: attrs.object, field: attrs.propertyName)}"""
					break

				case "edit":
					// Hack to assign unique ID's and keep tinyMCE happy
					newAttrs+=[name:attrs.propertyName,value:attrs.object."${attrs.propertyName}",cols:40,rows:5,id:"id${new Random().nextInt(10000000)}"]

					"""${g.textArea(newAttrs)}"""
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
		def skipAttrs=['object','propertyName','mode','type','value']
		def newAttrs=attrs.findAll { attrKey, attrValue -> !skipAttrs.contains(attrKey)}
		newAttrs.cols=attrs.cols ?: 80
		newAttrs.rows=attrs.rows ?: 20
		def xmltext=attrs.object."${attrs.propertyName}"
		newAttrs.value = xmltext ? dialogService.prettyPrint(xmltext) : ""
		newAttrs.name=attrs.propertyName

	out << row (attrs) {
		switch(attrs.mode) {
			case "show":
				String s = xmltext ? dialogService.prettyPrint(xmltext) : ""
				return """${g.textArea(newAttrs) {s.encodeAsHTML()}}"""
				break

			case "edit":
				"""${g.textArea(newAttrs)}"""
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

		out << row (class:attrs.class,object:attrs.object,propertyName:attrs.propertyName) {
			switch(attrs.mode) {
				case "show":
					def value=fieldValue(bean: attrs.object, field: attrs.propertyName)
					g.message(code:"dialog.checkBox.${value}.label".toString(), default:value.toString())
					break

				case "edit":
					"""${g.checkBox(name:attrs.propertyName,value:attrs.object."${attrs.propertyName}")}"""
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
		out << row (class:attrs.class,object:attrs.object,propertyName:attrs.propertyName) {
			switch(attrs.mode) {
				case "show":
					"""${fieldValue(bean: attrs.object, field: attrs.propertyName)}"""
					break

				case "edit":
					def optionValues=[]
					def domainClass = new DefaultGrailsDomainClass( attrs.object.class )
					def property=domainClass.getPropertyByName(attrs.propertyName)
					if (attrs.from) {
						optionValues=attrs.from
					} else {
						//optionValues= attrs.object."${attrs.propertyName}".class.findAll([sort:'name',order:'asc'])
						if (attrs.sort)
							optionValues= property.getType().findAll([sort:attrs.sort,order:'asc'])
						else
							optionValues= property.getType().findAll([sort:'name',order:'asc'])
					}



					def value=attrs.object."${attrs.propertyName}"
					def valueId=value ? value.id : null

					if (property.isOptional())
						"""${g.select(name:attrs.propertyName+'.id',value:valueId,from:optionValues,optionKey:'id',noSelection:['null': '-'] )}"""
					else
						"""${g.select(name:attrs.propertyName+'.id',value:valueId,from:optionValues,optionKey:'id')}"""
					break
				case "autocomplete":
					def optionValues=[]
					def domainClass = new DefaultGrailsDomainClass( attrs.object.class )
					def property=domainClass.getPropertyByName(attrs.propertyName)
					if (attrs.from) {
						optionValues=attrs.from
					}
					def value=attrs.object."${attrs.propertyName}"
					def valueLabel=value ? value.acLabel : ""
					def valueDescription=value ? value.acDescription : ""

					def valueId=value ? value.id : null

					def dc = new DefaultGrailsDomainClass( property.getType())
					def domainPropertyName=dc.getPropertyName()

					def acAction=attrs.acAction ? attrs.acAction : "autocomplete"
					def jsonUrl="${request.contextPath}/${domainPropertyName}/${acAction}"

					def descriptionText=""
					if (attrs.subtitle=="true") {
						descriptionText="""<p id="${attrs.propertyName}-description" class="autocomplete-description">${valueDescription}</p>"""
					}
					def containerClass = value ? "ac-selected" : "ac-idle"
					// input+hidden field
					"""<input name="${attrs.propertyName}-entry" value="${valueLabel}" type="text" class="autocomplete" jsonUrl="${jsonUrl}" />
						${descriptionText}
						<input name="${attrs.propertyName}.id" value="${valueId}" type="hidden" label="${valueLabel}"/>"""
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

		out << row (object:attrs.object,propertyName:attrs.propertyName, vertical:attrs.vertical) {
			def multiple = attrs.multiple ? attrs.multiple : "no"
			def cssClass=attrs.class ? attrs.class : ""
			multiple=""
			def optionKey = attrs.optionKey ? attrs.optionKey : null
			multiple=null
			optionKey=""

			switch(attrs.mode) {
				case "show":
					"""${fieldValue(bean: attrs.object, field: attrs.propertyName)}"""
					break

				case "edit":
					def domainClass = new DefaultGrailsDomainClass( attrs.object.class )
					def property=domainClass.getPropertyByName(attrs.propertyName)

					def cp = domainClass.constrainedProperties[attrs.propertyName]

					def optionValues=[]
					if (attrs.from) {
						optionValues=attrs.from
					} else {
						optionValues=attrs.object.constraints."${attrs.propertyName}".inList
					}

					def opts=[name:attrs.propertyName,value:attrs.object."${attrs.propertyName}",from:optionValues]
					if (attrs["class"]) opts.put("class",attrs["class"])
					if (attrs["optionKey"]) opts.put("optionKey",attrs["optionKey"])
					if (attrs["optionValue"]) opts.put("optionValue",attrs["optionValue"])
					if (attrs["multiple"]) opts.put("multiple",attrs["multiple"])
					if (attrs["style"]) opts.put("style", attrs["style"])

					if (property.isOptional()) {
						// TODO: yes. ''  for strings, null for int's
						opts.put("noSelection",['': '-'])
						//opts.put("noSelection",['null': '-'])
					}
					"""${g.select(opts)}"""
					break
			}
		}
	}

	/**
	* tabs tag - create a &lt;tabs&gt; enclosure for &lt;tab&gt; elements
	*
	* @param names Contains a comma separated string containing the names of the tabs
	* @param object The domain object
	*/
	def tabs = { attrs,body ->

		out << """<div id="dialogtabs" class="dialogtabs" >
			<ul>"""
			def prefix="dialog_"+attrs.object.getClass().getName()+"_"+attrs.object.id+"_"
			prefix=prefix.replace(".","_")
			def names=attrs.names.split(",")
			def defaultDomainClass = new DefaultGrailsDomainClass( attrs.object.class )
			def domainPropertyName=defaultDomainClass.propertyName

			for (name in names) {
				def defaultTabLabel=g.message(code:"dialog.tab.${name}", default:name)
				def tabLabel=g.message(code:"dialog.tab.${domainPropertyName}.${name}", default:defaultTabLabel)
				out <<"""
				<li>
					<a href="#${prefix}${name}">${tabLabel}</a>
				</li>
				"""
			}
		out <<"""</ul>"""
		out << body()
		out << "</div>"
	}

	/**
	* tab tag - create a &lt;tab&gt; element
	*
	* @param name The name of this tab
	* @param object The domain object
	*/
	def tab = { attrs,body ->
		def prefix="dialog_"+attrs.object.getClass().getName()+"_"+attrs.object.id+"_"
		prefix=prefix.replace(".","_")
		out << """<div id="${prefix}${attrs.name}">
				<table class="dialog-form-table"><tbody>"""
				out << body()
				out << "</tbody></table></div>"
		}

	/**
	* form tag - create a &lt;form&gt;
	*
	* @param name The name of this form
	* @param object The domain object
	* @param width The CSS width of this dialog (default: 600px)
	* @param heigt The CSS height of this dialog (default: auto)
	* @param title The title of this dialog
	*/
	def form = { attrs,body ->
		def width = attrs.width ? attrs.width : "600px"
		def height = attrs.height ? attrs.height : "auto"

		def defaultName="form"
		if (attrs.object) {
			defaultName = new DefaultGrailsDomainClass( attrs.object.class).getPropertyName()
		}

		def name = attrs.name ? attrs.name : defaultName
		def title=attrs.title?attrs.title:g.message(code:"form.${name}.title", default:name)




		out << """<div aid="dialog" style="width:${width};height:${height};" title="${title}" id="${name}">
		<form class="ajaxdialogform" name="${name}" method="post" action="${attrs.action}" test="test" >"""

		if (attrs.error) {
			out << """<div class="errors text-error">${attrs.error?attrs.error:''}</div>"""
		} else {
			out << """<div class="errors" style="display:none;"></div>"""
		}

		def message=g.message(code:"form.${name}.message",default:'')
		if (message) {
			out << """<div class="dialog-message">${message}</div>"""
		}


		// Add Hidden field with the id of the parent DomainObject (belongsTo)
		// REMARK: Currently it will only work if belongto has only 1 relation
		if (!(attrs.noBelongsTo && (attrs.noBelongsTo==true || attrs.noBelongsTo=="true"))) {
			if (attrs.object) {
				def defaultDomainClass = new DefaultGrailsDomainClass( attrs.object.class )
				Map belongToMap = defaultDomainClass.getStaticPropertyValue(GrailsDomainClassProperty.BELONGS_TO, Map.class)
				if (belongToMap?.size() == 1) {
					belongToMap.each { key, value ->
						out << '<input id="' + key + '.id" type="hidden" name="' + key + '.id" value="'+ attrs.object."${key}"?.id +'" />'
					}
				}
		}
		}
		out << body()
		out << "</form></div>"
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
		def cssClass=attrs.class ? "pageform ${attrs.class}" : "pageform"
		def formClass=attrs.formClass ? attrs.formClass:""

		out << """<div class="pageform row"><div class="${cssClass}" id="${name}">
			<div><h3>${g.message(code:"page.${name}.title", default:"${name}")}</h3></div>
			<p>${g.message(code:"page.${name}.help", default:"")}</p>

		<form class="${formClass} form-horizontal" name="${name}" method="post" action="${action}" >"""
		if (attrs.error) {
			out << """<div class="errors">${attrs.error?attrs.error:''}</div>"""
		} else {
			out << """<div class="errors" style="display:none;"></div>"""
		}

		// Add Hidden field with the id of the parent DomainObject (belongsTo)
		// REMARK: Currently it will only work if belongto has only 1 relation
		if (attrs.object) {
			def defaultDomainClass = new DefaultGrailsDomainClass( attrs.object.class )
			Map belongToMap = defaultDomainClass.getStaticPropertyValue(GrailsDomainClassProperty.BELONGS_TO, Map.class)
			if (belongToMap?.size() == 1) {
				belongToMap.each { key, value ->
					out << '<input id="' + key + '.id" type="hidden" name="' + key + '.id" value="'+ attrs.object."${key}"?.id +'" />'
				}
			}
		}

		out << body()
		out << "</form></div></div>"

	}

	/**
	 * Displays a set of navigation buttons for a page form. The button actions are the same as their names, and the labels are messages with key navigation.${name}.
	 */
	def navigation = {attrs,body ->
		if (grailsApplication.config.dialog?.bootstrap) {
			out << """<div class="navigation navigation-form-actions">"""
			def buttons=attrs.buttons.split(",")
			buttons.each { name ->
					out << """<button type="submit" name="${name}" class="${name} btn" value="${g.message(code:'navigation.'+name,default:name)}">${g.message(code:'navigation.'+name,default:name)}</button>"""
			}
			out << """</div>"""
		} else {
			out << """<div class="navigation">
				<ul class="clearfix">"""

			def buttons=attrs.buttons.split(",")
			buttons.each { name ->
				out << """<li><button type="submit" name="${name}" class="${name}" value="${g.message(code:'navigation.'+name,default:name)}">${g.message(code:'navigation.'+name,default:name)}</button></li>"""
			}

			out << """	</ul>
			</div>"""
		}
	}

	/**
	 * table tag - create a &lt;table&gt; to contain form rows
	 */
	def table = { attrs,body ->
		out << """<table class="dialog-form-table"><tbody>"""
		out << body()
		out << "</tbody></table>"
	}

	/**
	 * detailTable tag - create a detail table in master/detail view
	 * @param domainClass detail class name
	 * @param object master object
	 * @param property property that links detail with the master
	 */
	def detailTable = { attrs ->

		def copiedAttrs=""
		def skipAttrs=['object','propertyName','mode','class','type','value']
		attrs.each { attrKey, attrValue ->
			if (!skipAttrs.contains(attrKey)) {
				copiedAttrs+=""" ${attrKey}="${attrValue}" """
			}
		}
		def controllerName
		def listProperties
		def prefix
		def listConfig
		if (attrs.domainClass) {
			def domainClass = new DefaultGrailsDomainClass( attrs.domainClass)
			if (domainClass.hasProperty('listConfig')) {
				listConfig=attrs.listConfig?:attrs.domainClass?.listConfig
			}
		}
		if (listConfig) {
			controllerName=listConfig.controller
			listProperties=	listConfig.columns.collect { it.name }
			prefix="detailTable_"+listConfig.name
		} else {

			def domainClass = new DefaultGrailsDomainClass( attrs.domainClass)

			controllerName=attrs.controllerName?:domainClass.getPropertyName()
			listProperties=attrs.domainClass.listProperties
			prefix="detailTable_"+attrs.domainClass
			prefix=prefix.replace(".","_")
			prefix=prefix.replace("class ","")
		}

		def optionalParams = '?objectId='+attrs.object.id+'&objectClass='+attrs.object.getClass().getName()+'&property='+attrs.property
		def jsonUrl='/'+controllerName+'/jsonlist'+optionalParams
		def positionUrl='/'+controllerName+'/position'+optionalParams
		def cssClass="detailTable"
		if (attrs.rowreordering) {
			cssClass+=" rowreordering"
		}

		out << """<div class="datatable">
					<table id="${prefix}" ${copiedAttrs} class="${cssClass} table table-striped table-bordered table-hover" jsonUrl="${jsonUrl}" positionUrl="${positionUrl}"><thead><tr>"""
			if (listConfig) {
				listConfig.columns.each { column ->
					out << """<th class="${column.sortable?'sortable':'nonsortable'} ${listConfig.name}-${column.name}">${g.message(code:"list.${listConfig.name}.${column.name}.label")}</th>"""
				}
			} else {
				listProperties.each { propertyName ->
					out << """<th class="${controllerName}-${propertyName}">${g.message(code:"${controllerName}.${propertyName}.label", default:"${propertyName}")}</th>"""
				}
			}
		out << """<th class='nonsortable list-actions ${controllerName}-actions'>${g.message(code:"dialog.list.actions.label", default:"Actions")}</th></tr></thead><tbody>"""
		out <<"""</tbody></table>"""

	}

	/**
	* filesTable tag
	* @param object domain class instance
	*/

	def filesTable = { attrs ->
        def controller
        def prefix
        if (attrs.controller) {
            controller=attrs.controller
            prefix=controller
        } else {
            def domainClass = new DefaultGrailsDomainClass( attrs.object.class )
            controller=domainClass.getPropertyName()
            prefix="filesTable_"+domainClass.name
        }
        def id=attrs.id?:attrs.object.id
		prefix=prefix.replace(".","_")
		prefix=prefix.replace("class ","")

        def actions=attrs.actions?:"none"


		def jsonUrl='/'+controller+'/filelist/'+id+'?actions='+actions

		def cssClass="detailTable"

		out << """<table id="${prefix}" class="${cssClass} table table-striped table-bordered table-hover xxx" jsonUrl="${jsonUrl}" newButton="false"><thead><tr>"""
		out << """<th>${g.message(code:"filestable.filename.label")}</th>"""
		out << """<th>${g.message(code:"filestable.size.label")}</th>"""
		out << """<th>${g.message(code:"filestable.date.label")}</th>"""
		out << """<th class='nonsortable list-actions'>${g.message(code:"filestable.actions.label")}</th>"""
		out << "</tr></thead><tbody>"
		out <<"""</tbody></table>"""

	}

    /**
     * Displays an upload control in the dialog
     */
	def upload = { attrs,body ->
		def copiedAttrs=""
		def skipAttrs=['object','propertyName','mode','class','type','value']
		attrs.each { attrKey, attrValue ->
			 if (!skipAttrs.contains(attrKey))
			 {
				 copiedAttrs+=""" ${attrKey}="${attrValue}" """
			 }
		}

		out <<"""<div class="upload" ${copiedAttrs}>"""
		out << body()
		out << """</div>"""
	}

	// Only needed for full-page dialogs containing an upload.
	def uploadHead = { attrs ->

		def html="""
		 <script  type="text/javascript">
		\$(function() {
			var uploader = new qq.FileUploader({
				  element: document.getElementById('file-uploader'),
				  // path to server-side upload script
				  action: cmis.baseUrl+'/cmisDocument/fileupload',
				  params: {
					  },
				  onComplete: function(id, fileName, responseJSON){
					  \$("#form").append('<input type=\"hidden\" name=\"filename\" value=\"'+fileName+'\" />');
				  },
				  template: '<div class="qq-uploader">' +
				'<div class="qq-upload-drop-area"><span>${message(code:'cmis.uploader.dropfileshere')}</span></div>' +
				'<div class="qq-upload-button">${message(code:'cmis.uploader.uploadafile')}</div>' +
				'<ul class="qq-upload-list"></ul>' +
				 '</div>'
			   });
			   });
		</script>
		"""

		out << html
	}

    /**
     * Displays a dropdown menu in the menu bar. The key for the message is dropdown.code.label, with code replaced by the code attribute.
     */
	def dropdown = { attrs,body ->
		out << """ <li class="dropdown">
		              			<a class="dropdown-toggle" data-toggle="dropdown" href="#">
					 			${g.message(code:'dropdown.'+attrs.code+'.label')}
								<b class="caret"></b>
								</a>
								<ul class="dropdown-menu">
		"""
		out << body()
		out <<"""</ul></li>"""
	}

    /**
     *  Displays a submenu in a dropdown menu. Should be used within a <dialog:dropdown> element.
     *  The key for the message is dropdown.code.label, with code replaced by the code attribute.
     */
	def submenu = { attrs,body ->
        def icon=""
		if (attrs.icon) {
			icon="""<i class="${attrs.icon}"></i> """
		}

		out << """ <li class="dropdown-submenu">
		              			<a class="dropdown-toggle" data-toggle="dropdown" href="#">
					 			${icon}${g.message(code:'dropdown.'+attrs.code+'.label')}
								</a>
								<ul class="dropdown-menu">
		"""
		out << body()
		out <<"""</ul></li>"""
	}

    /**
     * Displays a menu item in a dropdown menu. Should be used within a <dialog:dropdown> or <dialog:submenu> element.
     * The key for the label message is menu.code.label, with code replaced by the code attribute. The key for the help message is menu.code.help, with code replaced by the code attribute.
     */
	def menuitem = { attrs,body ->
		def icon=""
		if (attrs.icon) {
			icon="""<i class="${attrs.icon}"></i> """
		}
		def code=null
		if (attrs.code) {
			code='menu.'+attrs.code
		} else {
			code='menu.'+attrs.controller+'.'+attrs.action
		}
		def label=attrs.label?:g.message(code:code+'.label')
		def help=g.message(code:code+'.help',default:'')

		def onclick=""
		def link=""
		if (attrs.onclick) {
			if (attrs.onclick=="dialog") {
				def nosubmit=attrs.nosubmit?true:false
				def params=""
				if (attrs.params) {
				 params=attrs.params.collect {key,value -> "'${key}':'${value}'"}.join(',')
				}
				onclick="""onclick="dialog.formDialog('null','${attrs.controller}', {'dialogname':'${attrs.action}','nosubmit':${nosubmit}},{${params}} ,null)" """

			} else {
				onclick="""onclick="${attrs.onclick}" """
			}

			link="""<a href="#" title="${help}">${icon}${label}</a>"""
		} else {
			link=g.link(controller:attrs.controller,action:attrs.action,params:attrs.params,title:help) {icon+ label }
		}

		out << """<li ${onclick}class="menu-item" >  ${link}</li>"""
	}

	def treeselect = { attrs, body ->
		out << row ("class":attrs.class,object:attrs.object,propertyName:attrs.propertyName) {
			def action=attrs.action?:"treeJSON"
			def domainClass = new DefaultGrailsDomainClass( attrs.object.class )
			def property=domainClass.getPropertyByName(attrs.propertyName)
			def dc = new DefaultGrailsDomainClass( property.getType())
			def domainPropertyName=dc.getPropertyName()
			def url=attrs.url?:"${request.contextPath}/${domainPropertyName}/${action}"
			def attributes=""
			if (attrs.width)  { attributes+=""" treeDialogWidth="${attrs.width}" """	}
			if (attrs.height) { attributes+=""" treeDialogHeight="${attrs.height}" """ }
			if (attrs.root)   { attributes+=""" treeRoot="${attrs.root}" """ }

			def value=attrs.object."${attrs.propertyName}"

			"""<span id="treeselect-${attrs.propertyName}-span" treeUrl="${url}" ${attributes} >
				<span>${value?:''}</span>
					<a href="#" onclick="dialog.tree.treeSelect('treeselect-${attrs.propertyName}');" class="btn btn-small">...</a>
				<input id="treeselect-field-input" type="hidden" name="${attrs.propertyName}.id" value="${value?.id}" />
			</span>"""
		}
	}
}
