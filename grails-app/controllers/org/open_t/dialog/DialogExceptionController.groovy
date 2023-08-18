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
        log.debug "Dialog Exception with request ${request} ${request.exception?.class?.name} ${request.exception}"
        def msg="Exception while handling exception"
        def title="Exception while handling exception"

        def exception=null
        def exceptionMessage=null
        def exceptionName=null
        def args=[]
        try {
            // Strategy: find the first DialogException or ApiException, if that fails get the first that has a message
            if (request.exception  && request.exception?.getClass()?.getSimpleName() in ["ApiException","DialogException"]) {
                exception=request.exception
            }

            if (!exception && request.exception?.cause && (request.exception?.cause?.getClass()?.getSimpleName() in ["ApiException","DialogException"])) {
                exception=request.exception.cause
            }

            if (!exception && request.exception?.cause?.cause && (request.exception?.cause?.cause?.getClass()?.getSimpleName() in ["ApiException","DialogException"])) {
                exception=request.exception.cause.cause
            }


            if (!exception && request.exception) {

                if (request.exception && request.exception?.message) {
                    exception=request.exception
                }
                if (!exceptionMessage && request.exception?.cause?.message) {
                    exception=request.exception.cause
                }

                if (!exceptionMessage && request.exception?.cause?.cause?.message) {
                    exception=request.exception.cause.cause
                }
            }
            // If we have loglevel debug, output the stacktrace to the log.
            log.debug dialogService.exceptionMessage(exception)
            if (exception) {
                exceptionName=exception.getClass().getName()
                exceptionMessage=exception.message
               try {
                    args=exception?.args
                } catch (Exception ee) {}

                // If the exception code resolves, show that. If not, show generic message with exception message as parameter
                title = message(code:'exception.'+exceptionMessage+'.title',args:args,default:"UNRESOLVED")
                if (title=="UNRESOLVED") {
                    title=message(code:'exception.default.title',args:[exceptionName,exceptionMessage,args],default:"An exception occurred: {0}:{1} with arguments: {2}")
                }

                // If the exception code resolves, show that. If not, show generic message with exception message as parameter
                msg = message(code:'exception.'+exceptionMessage+'.message',args:args,default:"UNRESOLVED")
                if (msg=="UNRESOLVED") {
                    msg=message(code:'exception.default.message',args:[exceptionName,exceptionMessage,args],default:"An exception occcurred: {0}:{1} with arguments: {2}")
                }
            } else {
                title = "Server error"
                exceptionMessage=title
                msg   = "A server error occurred while processing your request."
            }

		} catch (Exception e) {
            log.debug "we have an exception: ${e}"
            log.debug dialogService.exceptionMessage(e)
			msg=e?.message
		}

        if (!exceptionMessage) {
            exceptionMessage="Exception"
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
