package org.open_t.dialog
import grails.converters.JSON;

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
			title=message(code:'exception.'+exceptionCode+'.title',args:args)
			msg=message(code:'exception.'+exceptionCode+'.message',args:args)

		} catch (Exception e) {
			msg=e.message
		}
        if (request && request.getHeader("Accept") && request.getHeader("Accept").contains("application/json")) {
            
            
            def result = [
    		              	success:false,
    		              	message:msg							
    		              ]
			def res=[result:result]
            render res as JSON
        } else {
            [title:title,msg:msg]
        }
	}
}
