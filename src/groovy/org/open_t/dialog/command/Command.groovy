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
			if (list."${property.name}" && (property.name !="id") && (property.name !="version")) {
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