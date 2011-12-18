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
import java.text.*;
import java.util.*;

import java.beans.PropertyEditorSupport

/**
 * Big Decimal property editor
 * Accepts both '.' and ',' as decimal separators
 * Always display the decimal separator as ','
 * TODO make this configurable
 *
 * @author Joost Horward
 */
public class BigDecimalEditor extends PropertyEditorSupport{
	
	String getAsText() {
		if( super.getValue()) {
			def s = String.format("%01.2f",(BigDecimal) super.getValue())
			s=s.replace( '.', ',')
			return s 
		} else {
			return ""
		}
	}
	
	void setAsText(String value) {
		if (value=="") {
			setValue(null)
		} else {
			def s=value.replace( ',', '.')  			      
			setValue(new BigDecimal(s))
		}
	}
	
	
	
}