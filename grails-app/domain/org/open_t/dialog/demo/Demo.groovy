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
package org.open_t.dialog.demo

class Demo {

	static listProperties=['id','name','dateOfBirth','dateCreated','lastUpdated']
	static hasMany = [demoItem : DemoItem]
	
    static constraints = {
    }
	
	String name;
	Date dateCreated;
	Date lastUpdated;
	Date dateOfBirth;
	String description
	Boolean publish
	
	Demo demo
	
	String toString() {
		  return "${id} ${name}";
	}
	
}
