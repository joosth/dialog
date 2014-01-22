/*
 * Grails Dialog plug-in
 * Copyright 2014 Open-T B.V., and individual contributors as indicated
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
