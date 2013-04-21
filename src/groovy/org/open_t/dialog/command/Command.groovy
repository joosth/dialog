package org.open_t.dialog.command
import grails.validation.Validateable;
import org.codehaus.groovy.grails.commons.*

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