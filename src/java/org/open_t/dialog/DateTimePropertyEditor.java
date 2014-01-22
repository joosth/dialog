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
package org.open_t.dialog;

import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.grails.web.binding.StructuredPropertyEditor;
/**
 * DateTime property editor
 * Will accept date and time separated by 'T' as well as separate time and date components
 *
 * @author Joost Horward
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class DateTimePropertyEditor extends PropertyEditorSupport implements StructuredPropertyEditor {

	SimpleDateFormat dateTimeFormat;

    /**
     * Constructor
     */
	public DateTimePropertyEditor(String format) {
		this.dateTimeFormat = new SimpleDateFormat(format);
	}

    /**
     *
     * @return The list of required fields of the date property
     */
    @Override
	public List getRequiredFields() {
		List requiredFields = new ArrayList();
		requiredFields.add("date");
		return requiredFields;
	}

    /**
     *
     * @return The list of optional fields of the date property
     */
    @Override
	public List getOptionalFields() {
		List optionalFields = new ArrayList();
		optionalFields.add("time");
		return optionalFields;
	}

    /**
     * Assemble and bind a property value from the specified fieldValues and the given type
     * @param type The type
     * @param fieldValues Map of type:value pairs
     * @return The Date
     * @throws IllegalArgumentException
     */
    @Override
	public Object assemble(Class type, Map fieldValues) throws IllegalArgumentException {
		if (!fieldValues.containsKey("date")) {
			throw new IllegalArgumentException("Can't populate a date/time without a date");
		}

		String date = (String) fieldValues.get("date");

		try {
			if (date.isEmpty()) {
				throw new IllegalArgumentException("Can't populate date/time without a date");
			}
			String time = (String) fieldValues.get("time");
			if (time == null || time.isEmpty()) {
				time = "00:00";
			}
            if (date.contains("T")) {
               date=date.split("T")[0];
            }
			String dateTime = date + "T" + time;
			return dateTimeFormat.parse(dateTime);
		}
		catch (Exception nfe) {
			throw new IllegalArgumentException("Unable to parse structured DateTime from request for date.");
		}
	}
}
