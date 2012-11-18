modules = {
	dialog {
		dependsOn 'jquery, jquery-ui'
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
		//resource url:'/js/codemirror/lib/util/searchcursor.js'
		resource url:'/js/codemirror/lib/util/closetag.js'
		resource url:'/js/codemirror/mode/xml/xml.js'
		resource url:'/js/codemirror/mode/javascript/javascript.js'
		resource url:'/js/codemirror/mode/css/css.js'
		resource url:'/js/codemirror/mode/htmlmixed/htmlmixed.js'
		resource url:'/js/dialog.codemirror.js'
		
	}
	
	'dialog-fileuploader' {
		dependsOn 'dialog'
		resource url:'/css/fileuploader.css'
	}
	
	'dialog-dataTables' {
		dependsOn 'dialog'
		resource url:'/js/jquery/jquery.dataTables.min.js'
		resource url:'/js/jquery/jquery.dataTables.rowReordering.js'
		resource url:'/js/jquery/dataTables/jquery.dataTables.pagination.js'
		
		resource url:'/css/datatables.css'
		resource url:'/css/jquery.dataTables_themeroller.css'
		resource url:'/css/jquery.dataTables.css'
				
		//resource url:'/js/jquery/dataTables/localisation/dataTables.en.txt'
		//resource url:'/js/jquery/dataTables/localisation/dataTables.nl.txt'
		
	}
	
}