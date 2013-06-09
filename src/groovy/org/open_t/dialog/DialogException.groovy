package org.open_t.dialog

class DialogException extends RuntimeException {

	def args=[]

	DialogException (String message) {
		super(message)
	}

	DialogException (String message,args) {
		super(message)
		this.args=args
	}
}
