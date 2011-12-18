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
package org.open_t.dialog;
import java.text.*
import java.util.*;

import java.beans.PropertyEditorSupport

/**
 * Date property editor
 * Displays and accepts date in the xs:date format
 * 
 * @author Joost Horward
 */

public class DateEditor extends PropertyEditorSupport{
	
	String getAsText() {
		def locale = new Locale('nl')
		Date d = (Date) super.getValue()
		if (d) {
			return d.format("yyyy-MM-dd'T'HH:mm:ss")
		} else {
			return null
		}
	}
	
	void setAsText(String value) {
		if (value=="") {
			setValue(null)
		} else {
			def locale = new Locale('nl')
			if (!value.contains('T')) {
				value+="T00:00:00"
			}
			def df=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
			
			df.parse(value)            
			setValue(df.parse(value))
		}
	}
	
	
	
}