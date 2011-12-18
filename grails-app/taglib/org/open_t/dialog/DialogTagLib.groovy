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

/**
 * Tag library for Dialog plugin
 *
 * @author Joost Horward
 */

import org.codehaus.groovy.grails.commons.*
class DialogTagLib {
	def dialogService
	static namespace = 'dialog'

	
	def row = { attrs,body ->
		def object=attrs.object;
		def domainPropertyName=object.getClass().getName()
		def domainClass = new DefaultGrailsDomainClass( object.class )
		domainPropertyName=domainClass.propertyName
		def propertyName=attrs.propertyName;
		def property=domainClass.getPropertyByName(propertyName)
		def naturalName=property.naturalName;
		
		if (attrs.vertical == "true") {
			out <<"""
			<tr class="prop object-${domainPropertyName} property-${domainPropertyName}-${propertyName} property-${propertyName} ${attrs.class}">
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
			out <<"""<tr class="prop object-${domainPropertyName} property-${domainPropertyName}-${propertyName} property-${propertyName} ${attrs.class}">
					<td valign="top" class="name">
					<label for="name">${g.message(code:"${domainPropertyName}.${propertyName}.label", default:"${naturalName}")}</label>
					</td>
					<td valign="top" class="value ${attrs.class}">"""
			out << body()
			
			out << """</td><td>&nbsp;<span class="help-icon help action" title="${g.message(code:"${domainPropertyName}.${propertyName}.help",default:'Help!')}" href="#">&nbsp;</span>
				</td></tr>"""
		}
	

		

	
	}
	
	
	def textField = { attrs ->
		
		out << row ("class":attrs.class,object:attrs.object,propertyName:attrs.propertyName) {
			
			
			switch(attrs.mode) {
				case "show":
					"""${fieldValue(bean: attrs.object, field: attrs.propertyName)}"""
					break
				
				case "edit":
					def name=attrs.propertyName
					def value=fieldValue(bean: attrs.object, field: attrs.propertyName)
					
					// Copy all extra attributes, skip the ones that are only meaningful for textField or are handled manually
					def copiedAttrs=""
					def skipAttrs=['object','propertyName','mode','class','type']
					attrs.each { attrKey, attrValue ->						 
						 if (!skipAttrs.contains(attrKey))
						 {
							 copiedAttrs+=""" ${attrKey}="${attrValue}" """
						 }
					}
					def inputType="text";
					if (attrs.type) inputType=attrs.type;
					
					"""<input type="${inputType}" name="${name}" value="${value}" id="${name}" ${copiedAttrs}  />"""
					
					break
			}
		}
	}
	
	def date = { attrs ->
	
		out << row (object:attrs.object,propertyName:attrs.propertyName) {
			
			
			switch(attrs.mode) {
				case "show":
					"""${fieldValue(bean: attrs.object, field: attrs.propertyName)}"""
					break
				
				case "edit":
					def dateValue="${formatDate(date:attrs.object."${attrs.propertyName}",format:"yyyy-MM-dd")}"
					"""${g.textField(name:attrs.propertyName,value:dateValue,class:'datepicker')}"""
					break
			}
		}
	}
	
	
	def textArea = { attrs ->
		out << row (class:attrs.class,object:attrs.object,propertyName:attrs.propertyName) {
			switch(attrs.mode) {
				case "show":
					"""${fieldValue(bean: attrs.object, field: attrs.propertyName)}"""
					break
				
				case "edit":
					"""${g.textArea(name:attrs.propertyName,value:attrs.object."${attrs.propertyName}",cols:40,rows:5)}"""
					break
			}
		}
	}
	
	def xml = { attrs ->
	out << row (class:attrs.class,object:attrs.object,propertyName:attrs.propertyName) {
		switch(attrs.mode) {
			case "show":
				def xmltext=attrs.object."${attrs.propertyName}"
				String s = xmltext ? dialogService.prettyPrint(xmltext) : ""
				return "<textarea cols=\"80\" rows=\"25\">"+s.encodeAsHTML()+"</textarea>"
				break
			
			case "edit":
				"""${g.textArea(name:attrs.propertyName,value:attrs.object."${attrs.propertyName}",cols:40,rows:5)}"""
				break
		}
	}
	}
	
	
	def checkBox = { attrs ->
	
		out << row (object:attrs.object,propertyName:attrs.propertyName) {
			switch(attrs.mode) {
				case "show":
					"""${fieldValue(bean: attrs.object, field: attrs.propertyName)}"""
					break
				
				case "edit":
					"""${g.checkBox(name:attrs.propertyName,value:attrs.object."${attrs.propertyName}")}"""
					break
			}
		}
	}
	
	
	def domainObject = { attrs ->
	
		out << row (object:attrs.object,propertyName:attrs.propertyName) {
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
			}
		}
	}
	
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
					
					def opts=[name:attrs.propertyName,value:attrs.object."${attrs.propertyName}",from:optionValues,noSelection:['null': '-']]
					if (attrs["class"]) opts.put("class",attrs["class"])
					if (attrs["optionKey"]) opts.put("optionKey",attrs["optionKey"])
					if (attrs["multiple"]) opts.put("multiple",attrs["multiple"])
					if (attrs["style"]) opts.put("style", attrs["style"])
					
					if (property.isOptional()) {
						// yes. ''  for strings, null for int's
						opts.put("noSelection",['': '-'])
					}
					"""${g.select(opts)}"""
					break
			}
		}
	}
/*
	<g:select class="multiselect" name="importSchema" from="${org.workflow4people.Namespace.list(sort:'prefix')}" multiple="yes" optionKey="id" value="${namespaceInstance?.importSchema}" />
	*/
	def tabs = { attrs,body ->
	
		out << """<div id="dialogtabs" class="dialogtabs" >
			<ul>"""
			def prefix="dialog_"+attrs.object.getClass().getName()+"_"+attrs.object.id+"_"
			prefix=prefix.replace(".","_")
			def names=attrs.names.split(",")
			for (name in names) {
				out <<"""
				<li>
					<a href="#${prefix}${name}">${name}</a>
				</li>
				"""
			}
		out <<"""</ul>"""
		out << body()
		out << "</div>"
	}

	def tab = { attrs,body ->
	def prefix="dialog_"+attrs.object.getClass().getName()+"_"+attrs.object.id+"_"
	prefix=prefix.replace(".","_")
	out << """<div id="${prefix}${attrs.name}">
			<table ><tbody>"""
		
	out << body()
	out << "</tbody></table></div>"
}

	
	def form = { attrs,body ->
		def width = attrs.width ? attrs.width : "600px";
		
		out << """<div aid="dialog" style="width:${width};" title="${attrs.title}">
		<form class="ajaxdialogform">
		<div class="errors" style="display:none;"></div>"""

		// Add Hidden field with the id of the parent DomainObject (belongsTo)
		// REMARK: Currently it will only work if belongto has only 1 relation
		if (attrs.object) {
			def defaultDomainClass = new DefaultGrailsDomainClass( attrs.object.class )
			Map belongToMap = defaultDomainClass.getStaticPropertyValue(GrailsDomainClassProperty.BELONGS_TO, Map.class)
			if (belongToMap?.size() == 1) {
				belongToMap.each { key, value ->
					out << '<input id="' + key + '.id" type="hidden" name="' + key + '.id" value="'+ attrs.object."${key}"?.id +'">'
				}
			}
		}
		
		out << body()
		out << "</form></div>"
	}

	def table = { attrs,body ->
	
		out << """<table><tbody>"""
		out << body()
		out << "</tbody></table>"
	}
	
	def detailTable = { attrs ->
		
		def domainClass = new DefaultGrailsDomainClass( attrs.domainClass)

		def domainPropertyName=domainClass.getPropertyName()
		
		//attrs.domainClass = detail class name
		//attrs.object      = master object
		//attrs.property    = property that links detail with the master
		
		def prefix="detailTable_"+attrs.domainClass
		prefix=prefix.replace(".","_")
		prefix=prefix.replace("class ","")
		
		def optionalParams = '?objectId='+attrs.object.id+'&objectClass='+attrs.object.getClass().getName()+'&property='+attrs.property
		def jsonUrl='/'+domainPropertyName+'/jsonlist'+optionalParams
		out << """<div>
					<table id="${prefix}" class="detailTable" jsonUrl=${jsonUrl}><thead><tr>"""
			attrs.domainClass.listProperties.each { propertyName ->

				def property=domainClass.getPropertyByName(propertyName)
				def naturalName=property.naturalName;
			
				out << """<th>${g.message(code:"${domainPropertyName}.${propertyName}.label", default:"${naturalName}")}</th>"""
			}
		out << "<th>Actions</th></tr></thead><tbody>"
		out <<"""</tbody></table>"""
	
	}
	
	
	
	
}

