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

import java.beans.PropertyEditorSupport
import java.text.SimpleDateFormat

import org.codehaus.groovy.grails.web.util.WebUtils
import org.springframework.web.servlet.support.RequestContextUtils as RCU

/**
 * Date property editor
 * Displays and accepts date in the xs:date format
 *
 * @author Joost Horward
 */
class DateEditor extends PropertyEditorSupport {

	String getAsText() {
		Date d = super.getValue()
		log.debug "GetAsText: ${d}"
		if (d) {
			return d.format("yyyy-MM-dd'T'HH:mm:ss")
		}
		return null
	}

	void setAsText(String value) {
		log.debug "SetAsText: ${value}"
		if (value=="") {
			setValue(null)
		} else {
			if (!value.contains('T')) {
				value+="T00:00:00"
			}
			def df=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

			df.parse(value)
			setValue(df.parse(value))
		}
	}
}
