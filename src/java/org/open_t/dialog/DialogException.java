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

import java.util.List;

/**
 * Dialog exceptions provide a way to throw an exception that will be handled with a dialog
 */
class DialogException extends RuntimeException {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    List<String> args;

	DialogException (String message) {
		super(message);
	}

	DialogException (String message,List<String> args) {
		super(message);
		this.args=args;
	}
}
