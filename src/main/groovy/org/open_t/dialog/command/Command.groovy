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

import org.grails.core.DefaultGrailsDomainClass
/**
 * Base class to derive dialog command classes from.
 */
//@Validateable
class Command  implements Validateable {
	def id=0
	def version=0

    /**
     * Populate properties from given map, ignoring id and value
     *
     * @param map The map of values to fetch
     * @return The command object
     */
	def getFrom (map) {
		//DefaultGrailsDomainClass defaultDomainClass = new DefaultGrailsDomainClass( this.class )
		def props=this.getMetaClass().properties
		props.each { property ->
            // Allow both maps and groovy objects as source map
            def keyPresent = (map instanceof Map) ? map.containsKey(property.name) : map.properties.containsKey(property.name)
			if ((property.name !="id") && (property.name !="version") && (property.name !="class") && keyPresent) {
				this."${property.name}"=map."${property.name}"
			}
		}
		return this
	}


    /**
     * setProperties to allow properties to be set in the same fashion as domain objects
     */
    def setProperties(map) {
        getFrom(map)
    }

    /**
     * Populate properties from given map
     * Silently skip values that are not in the domain class
     *
     * @param map The map of values to fetch
     * @return The command object
     */
	def getAllFrom (map) {
		DefaultGrailsDomainClass defaultDomainClass = new DefaultGrailsDomainClass( this.class )
		def props=defaultDomainClass.getProperties()
		props.each { property ->
			try {
				if (map."${property.name}") {
					this."${property.name}"=map."${property.name}"
				}
			}
			catch (Exception e) {
				// If the property does not exist in map we silently ignore it.
			}
		}
		return this
	}

    /**
     * Store properties in map
     * Excludes id and version
     *
     * @param map The map to store the values in
     */

	def storeTo(map) {
		DefaultGrailsDomainClass defaultDomainClass = new DefaultGrailsDomainClass( this.class )
		def props=defaultDomainClass.getProperties()
		props.each { property ->
			if ((property.name !="id") && (property.name !="version")&& (property.name !="class")){
				map."${property.name}"=this."${property.name}"
			}
		}
	}
}
