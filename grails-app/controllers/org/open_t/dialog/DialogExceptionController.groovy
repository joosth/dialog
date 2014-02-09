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
package org.open_t.dialog
import grails.converters.JSON;

/**
 * Controller for showing dialog exception messages
 */
class DialogExceptionController {

	def dialog() {
        println "Request.accept: ${request.getHeader("Accept")}"
		def title="Error handling error"
		def msg=""
		def exceptionCode=request.exception?.message
		def args=[]

		if (request.exception.cause.class==org.open_t.dialog.DialogException || request.exception.cause.class==AssertionError) {
			args=request.exception.cause.args
			exceptionCode=request.exception.cause.message
		}

		try {
			def defaultTitle=message(code:'exception.default.title',args:args,default:"Error")
			title=message(code:'exception.'+exceptionCode+'.title',args:args,default:defaultTitle)
			msg=message(code:'exception.'+exceptionCode+'.message',args:args,default:exceptionCode)

		} catch (Exception e) {
			msg=e.message
		}
        if (request && request.getHeader("Accept") && request.getHeader("Accept").contains("application/json")) {


            def result = [
    		              	success:false,
    		              	message:msg,
							title:title
    		              ]
			def res=[result:result]
			response.status=200
            render res as JSON
        } else {
            [title:title,msg:msg]
        }
	}
}
