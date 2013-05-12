modules = {
	dialog {
		dependsOn 'jquery'
		resource url:'/js/jquery/jquery-ui-1.9.2.custom.min.js'
		resource url: 'css/bootstrap-theme/jquery-ui-1.9.2.custom.css'		
		resource url:'/js/dialog.js'
		resource url:'/css/dialog.css'
		
	}
	
	'dialog-altselect' {
		dependsOn 'dialog'
		resource url:'/js/jquery/jquery.ui.altselect.js'
		resource url:'/css/ui.altselect.css'
	}
	
	'dialog-cluetip' {
		dependsOn 'dialog'
		resource url:'/js/jquery/jquery.cluetip-patched.js'
		resource url:'/css/jquery.cluetip.css'
	}
	
	'dialog-tinymce' {		
		dependsOn 'dialog'
		resource url:'/js/tiny_mce/tiny_mce.js'
		resource url:'/js/tiny_mce/jquery.tinymce.js'
		resource url:'/js/dialog.tinymce.js'
	}
	
	'dialog-ckeditor' {		
		dependsOn 'dialog'
		resource url:'js/ckeditor/ckeditor.js'
		resource url:'/js/dialog.ckeditor.js'
	}
	
	'dialog-codemirror' {
		dependsOn 'dialog'
		resource url:'/css/dialog.codemirror.css'
		resource url:'js/codemirror/lib/codemirror.css'		
		resource url:'/js/codemirror/lib/codemirror.js'
		//resource url:'/js/codemirror/lib/util/closetag.js'
		
		resource url: '/js/codemirror/addon/runmode/runmode.js'
		resource url: '/js/codemirror/addon/runmode/colorize.js'		
		resource url:'/js/codemirror/mode/xml/xml.js'
		resource url:'/js/codemirror/mode/javascript/javascript.js'
		resource url:'/js/codemirror/mode/css/css.js'
		resource url:'/js/codemirror/mode/htmlmixed/htmlmixed.js'
		
		resource url:'/js/codemirror/mode/groovy/groovy.js'
		resource url:'/js/codemirror/mode/less/less.js'
		resource url:'/js/codemirror/mode/shell/shell.js'
		resource url:'/js/codemirror/mode/clike/clike.js'
		resource url:'/js/dialog.codemirror.js'
		
	}
	
	'dialog-fileuploader' {
		dependsOn 'dialog'
		resource url:'/css/fileuploader.css'
	    resource url:'/js/fileuploader.js'
		resource url:'/js/dialog.fileuploader.js'
		
	}
	
	'dialog-dataTables' {
		dependsOn 'dialog'
		resource url:'/js/jquery/jquery.dataTables.js'
		resource url:'/js/jquery/jquery.dataTables.rowReordering.js'
		resource url:'/js/jquery/dataTables/DT_bootstrap.js'
		resource url:'/css/DT_bootstrap.css'						
	}
	
	'dialog-tree' {
		dependsOn 'dialog-bootstrap'
		resource url:'js/jquery/jquery.jstree.js'
		resource url:'js/dialog.tree.js'
		resource url:'/css/jquery.jstree.css'
		
	}
	
	'dialog-bootstrap' {
		dependsOn 'dialog'
		resource url:'/js/jquery/jquery.dataTables.js'
	}
	
	'dialog-last' {
		dependsOn 'dialog'
		resource url:'/js/dialog.last.js'
	}
	
	'dialog-demo' {
		dependsOn 'dialog,dialog-cluetip,dialog-altselect,dialog-dataTables,bootstrap-responsive-css,bootstrap-tooltip,bootstrap-popover,bootstrap-modal,dialog-bootstrap'
	}
	
	'dialog-bootbox' {
		dependsOn 'jquery'
		resource url:'/js/bootbox.js'				
	}
	
	'dialog-flot' {
		dependsOn 'jquery'
		resource url:'/js/flot/jquery.flot.js'
	}
	
	
}
