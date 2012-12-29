package org.open_t.dialog

class DialogExceptionController {
    
	def dialog = {
		def title="Error handling error"
		def msg=""
		def exceptionCode=request.exception.message
		def args=[]

		if (request.exception.cause.class==org.open_t.dialog.DialogException || request.exception.cause.class==java.lang.AssertionError) {
			args=request.exception.cause.args
			exceptionCode=request.exception.cause.message
		}
		
		try {
			title=message(code:'exception.'+exceptionCode+'.title',args:args)
			msg=message(code:'exception.'+exceptionCode+'.message',args:args)
			
		} catch (Exception e) {
			msg=e.message
		}
		[title:title,msg:msg]
	}
}