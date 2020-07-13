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
    def dialogService

	def dialog() {
        // If we have loglevel debug, output the stacktrace to the log.
        if (request.exception) {
            log.debug dialogService.exceptionMessage(request.exception)
        }
		def title="Error handling error"
		def msg=""

        def exceptionName=null
        def exceptionMessage=null
        def args=[]
        if (request.exception.message) {
            exceptionName=request?.exception.getClass().getName()
            exceptionMessage=request.exception?.message
            if (request?.exception?.metaClass?.hasProperty("args")) {
                args=request.exception?.args
            }
        }
        if (!exceptionMessage && request.exception.cause?.message) {
            exceptionName=request?.exception?.cause?.getClass().getName()
            exceptionMessage=request?.exception?.cause?.message
            if (request?.exception?.cause?.metaClass?.hasProperty("args")) {
                args=request.exception?.cause?.args
            }
        }

        if (!exceptionMessage && request.exception.cause?.cause?.message) {
            exceptionName=request?.exception?.cause?.cause?.getClass().getName()
            exceptionMessage=request?.exception?.cause?.cause?.message
            if (request?.exception?.cause?.cause?.metaClass?.hasProperty("args")) {
                args=request.exception?.cause?.cause?.args
            }
        }

		try {
            // If the exception code resolves, show that. If not, show generic message with exception message as parameter
            title = message(code:'exception'+exceptionMessage+'.title',args:args,default:"UNRESOLVED")
            if (title=="UNRESOLVED") {
                title=message(code:'exception.default.title',args:[exceptionName,exceptionMessage,args],default:"An exception occurred: {0}:{1} with arguments: {2}")
            }

            // If the exception code resolves, show that. If not, show generic message with exception message as parameter
            msg = message(code:'exception'+exceptionMessage+'.message',args:args,default:"UNRESOLVED")
            if (msg=="UNRESOLVED") {
                msg=message(code:'exception.default.message',args:[exceptionName,exceptionMessage,args],default:"An exception occcurred: {0}:{1} with arguments: {2}")
            }


		} catch (Exception e) {
			msg=e.message
		}
        def result = [
                success:false,
                message:msg,
                title:title,
                code:exceptionMessage,
                args:args
        ]
        def res=[result:result]
        def headerMsg=msg.replaceAll("\\n"," ")
        if (request && request.getHeader("Accept") && request.getHeader("Accept").contains("application/json")) {
            // Because $.getJSON doesn't have a failure handler...
			response.status=200
            response.addHeader("X-Dialog-Error-Message",headerMsg);
            render res as JSON
        } else {
            response.addHeader("X-Dialog-Error-Message",headerMsg);
            return res
        }
	}
}
