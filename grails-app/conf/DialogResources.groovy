modules = {

    dialog {
        dependsOn 'dialog-jquery'

        resource url: [plugin: 'jquery-dialog', dir:'js', file:'dialog.js']
        resource url: [plugin: 'jquery-dialog', dir:'css', file:'dialog.css']
    }

    'dialog-jquery' {
        resource url: [plugin: 'jquery-dialog', dir:'ext/jquery', file: 'jquery-1.12.1.js']
    }

	'dialog-altselect' {
		dependsOn 'dialog'
		//resource url: [plugin: 'jquery-dialog', dir:'js/jquery', file:'jquery.ui.altselect.js']
		//resource url: [plugin: 'jquery-dialog', dir:'css', file:'ui.altselect.css']
	}

	'dialog-tinymce' {
		dependsOn 'dialog'
		resource url: [plugin: 'jquery-dialog', dir:'js/tiny_mce', file:'tiny_mce.js']
		resource url: [plugin: 'jquery-dialog', dir:'js/tiny_mce', file:'jquery.tinymce.js']
		resource url: [plugin: 'jquery-dialog', dir:'js',file:'dialog.tinymce.js']
	}

	'dialog-ckeditor' {
		dependsOn 'dialog'
		resource url: [plugin: 'jquery-dialog', dir:'js/ckeditor', file:'ckeditor.js']
		resource url: [plugin: 'jquery-dialog', dir:'js', file:'dialog.ckeditor.js']
	}

	'dialog-codemirror' {
		dependsOn 'dialog'
		resource url: [plugin: 'jquery-dialog', dir:'css',file:'dialog.codemirror.css']
		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/lib/',file:'codemirror.css']
		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/lib/',file:'codemirror.js']

		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/addon/runmode/',file:'runmode.js']
		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/addon/runmode/',file:'colorize.js']
		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/mode/xml/',file:'xml.js']
		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/mode/javascript/',file:'javascript.js']
		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/mode/css/',file:'css.js']
		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/mode/htmlmixed/',file:'htmlmixed.js']

		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/mode/groovy/',file:'groovy.js']
		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/mode/shell/',file:'shell.js']
		resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/mode/clike/',file:'clike.js']
        resource url: [plugin: 'jquery-dialog', dir:'ext/codemirror/mode/sql/',file:'sql.js']
		resource url: [plugin: 'jquery-dialog', dir:'js',file:'dialog.codemirror.js']

	}

	'dialog-fileuploader' {
		dependsOn 'dialog'
		resource url: [plugin: 'jquery-dialog', dir:'/css/',file:'fileuploader.css']
	    resource url: [plugin: 'jquery-dialog', dir:'/js/',file:'fileuploader.js']
		resource url: [plugin: 'jquery-dialog', dir:'/js/',file:'dialog.fileuploader.js']

	}

	'dialog-dataTables' {
		dependsOn 'dialog'
		resource url: [plugin: 'jquery-dialog', dir: 'ext/DataTables/css/', file: 'dataTables.bootstrap.css']
		resource url: [plugin: 'jquery-dialog', dir: 'ext/DataTables/css/', file: 'rowReorder.bootstrap.css']
		resource url: [plugin: 'jquery-dialog', dir: 'ext/DataTables/js/', file: 'jquery.dataTables.js']
		resource url: [plugin: 'jquery-dialog', dir: 'ext/DataTables/js/', file: 'dataTables.bootstrap.js']
		resource url: [plugin: 'jquery-dialog', dir: 'ext/DataTables/js/', file: 'dataTables.rowReorder.js']
		resource url: [plugin: 'jquery-dialog', dir: '/js/', file: 'dialog.datatables.js']
	}

	'dialog-tree' {
		dependsOn 'dialog-bootstrap'
		resource url: [plugin: 'jquery-dialog', dir:'js/jquery/',file:'jquery.jstree.js']
		resource url: [plugin: 'jquery-dialog', dir:'js/',file:'dialog.tree.js']
		resource url: [plugin: 'jquery-dialog', dir:'/css/',file:'jquery.jstree.css']

	}

	'dialog-bootstrap' {
		dependsOn 'dialog'
	    resource url: [plugin: 'jquery-dialog', dir:'ext/bootstrap/css', file:'bootstrap.css']
	    resource url: [plugin: 'jquery-dialog', dir:'ext/bootstrap/js', file:'bootstrap.js']
	}

	'dialog-fontawesome' {
		dependsOn 'dialog'
	    resource url: [plugin: 'jquery-dialog', dir: 'ext/font-awesome/css', file: 'font-awesome.css']
	}

	'dialog-last' {
		dependsOn 'dialog'
		resource url: [plugin: 'jquery-dialog', dir:'/js/',file:'dialog.last.js']
	}

	'dialog-bootbox' {
		dependsOn 'dialog-jquery'
		resource url: [plugin: 'jquery-dialog', dir:'/js/',file:'bootbox.js']
	}

	'dialog-flot' {
		dependsOn 'dialog-jquery'
		resource url: [plugin: 'jquery-dialog', dir:'ext/flot', file:'jquery.flot.js']
	}

	'dialog-autocomplete' {
		dependsOn 'dialog'
		resource url: [plugin: 'jquery-dialog', dir:'/js/',file:'dialog.autocomplete.js']
	}

    'dialog-maskedinput' {
		dependsOn 'dialog'
		resource url: [plugin: 'jquery-dialog', dir:'js/jquery/',file:'jquery.maskedinput.js']
	}


    'dialog-datepicker' {
		dependsOn 'dialog,dialog-maskedinput,dialog-modernizr'
		resource url: [plugin: 'jquery-dialog', dir:'/js/',file:'dialog.datepicker.js']
	}

    'dialog-timepicker' {
		dependsOn 'dialog'
        resource url: [plugin: 'jquery-dialog', dir:'/js/jquery/',file:'jquery-ui-sliderAccess.js']
        resource url: [plugin: 'jquery-dialog', dir:'/js/jquery/',file:'jquery-ui-timepicker-addon.js']
        resource url: [plugin: 'jquery-dialog', dir:'/css/',file:'jquery-ui-timepicker-addon.css']
	}

	'dialog-modernizr' {
		dependsOn 'dialog'
		resource url: [plugin: 'jquery-dialog', dir:'ext/modernizr',file:'modernizr.js']
	}

	'dialog-angularjs' {
		resource url: [plugin: 'jquery-dialog', dir:'ext/angularjs', file:'angular.js']
		resource url: [plugin: 'jquery-dialog', dir:'ext/angularjs', file:'module.js']
	}

}
