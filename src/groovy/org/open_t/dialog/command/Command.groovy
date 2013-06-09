package org.open_t.dialog.command

import grails.validation.Validateable

import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

@Validateable
class Command  {
	def id
	def version

	def getFrom (list) {
		DefaultGrailsDomainClass defaultDomainClass = new DefaultGrailsDomainClass( this.class )
		def props=defaultDomainClass.getProperties()
		props.each { property ->
			//println "Properties: ${list.properties}"
			//if ((property.name !="id") && (property.name !="version") && list."${property.name}") {
			if ((property.name !="id") && (property.name !="version") && list.properties.containsKey(property.name)) {
				this."${property.name}"=list."${property.name}"
			}
		}
		return this
	}

	def getAllFrom (list) {
		DefaultGrailsDomainClass defaultDomainClass = new DefaultGrailsDomainClass( this.class )
		def props=defaultDomainClass.getProperties()
		props.each { property ->
			try {
				if (list."${property.name}") {
					this."${property.name}"=list."${property.name}"
				}
			}
			catch (Exception e) {
				// If the property does not exist in list we silently ignore it.
			}
		}
		return this
	}

	def storeTo(list) {
		DefaultGrailsDomainClass defaultDomainClass = new DefaultGrailsDomainClass( this.class )
		def props=defaultDomainClass.getProperties()
		props.each { property ->
			if ((property.name !="id") && (property.name !="version")){
				list."${property.name}"=this."${property.name}"
			}
		}
	}
}
