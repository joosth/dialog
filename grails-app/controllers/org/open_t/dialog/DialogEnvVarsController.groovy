
class DialogEnvVarsController {

    def messageService 
    
    /**
     * Dynamically generated javascript variable served as a javascript script file.
     */
    def generateEnvVars(def params) {
        // FIXME: dialog.pluginUrl = "${resource(plugin: "dialog")}"; is not working 
        //        but appears to be not used anymore. It is removed for now.
        def dialogScript = """var dialog = {};

dialog.options = {
    "refreshPage": false
};
dialog.baseUrl = "${request.contextPath}";
dialog.language = "${messageService.getMessage("language.code", [], "en")}";

dialog.messages = {};
dialog.messages.ok = "${messageService.getMessage("dialog.messages.ok")}";
dialog.messages.delete = "${messageService.getMessage("dialog.messages.delete")}";
dialog.messages.cancel = "${messageService.getMessage("dialog.messages.cancel")}";

dialog.messages.uploading = "${messageService.getMessage("dialog.messages.uploading")}";
dialog.messages.uploadcompleted = "${messageService.getMessage("dialog.messages.uploadcompleted")}";

dialog.messages['new'] = "${messageService.getMessage("dialog.messages.new")}";
dialog.messages.confirmdelete = "${messageService.getMessage("dialog.messages.confirmdelete")}";
dialog.messages.confirmdeleteTitle = "${messageService.getMessage("dialog.messages.confirmdeleteTitle")}";

dialog.messages.moment = {};
dialog.messages.moment.inputDateFormat = "${messageService.getMessage("dialog.moment.inputDateFormat")}";
dialog.messages.moment.inputTimeFormat = "${messageService.getMessage("dialog.moment.inputTimeFormat")}";

dialog.messages.datetimepicker = {};
dialog.messages.datetimepicker.tooltips = {
    today: "${messageService.getMessage("dialog.datetimepicker.today")}",
    clear: "${messageService.getMessage("dialog.datetimepicker.clear")}",
    close: "${messageService.getMessage("dialog.datetimepicker.close")}",
    selectMonth: "${messageService.getMessage("dialog.datetimepicker.selectMonth")}",
    prevMonth: "${messageService.getMessage("dialog.datetimepicker.prevMonth")}",
    nextMonth: "${messageService.getMessage("dialog.datetimepicker.nextMonth")}",
    selectYear: "${messageService.getMessage("dialog.datetimepicker.selectYear")}",
    prevYear: "${messageService.getMessage("dialog.datetimepicker.prevYear")}",
    nextYear: "${messageService.getMessage("dialog.datetimepicker.nextYear")}",
    selectDecade: "${messageService.getMessage("dialog.datetimepicker.selectDecade")}",
    prevDecade: "${messageService.getMessage("dialog.datetimepicker.prevDecade")}",
    nextDecade: "${messageService.getMessage("dialog.datetimepicker.nextDecade")}",
    prevCentury: "${messageService.getMessage("dialog.datetimepicker.prevCentury")}",
    nextCentury: "${messageService.getMessage("dialog.datetimepicker.nextCentury")}"
}
dialog.messages.maskedinput = {};
dialog.messages.maskedinput.date = "${messageService.getMessage("dialog.maskedinput.date")}";
dialog.messages.maskedinput.time = "${messageService.getMessage("dialog.maskedinput.time")}";
dialog.messages.datatables = {
    "language": {
        "decimal":        "${messageService.getMessage("dialog.datatables.decimal")}",
        "emptyTable":     "${messageService.getMessage("dialog.datatables.emptyTable")}",
        "info":           "${messageService.getMessage("dialog.datatables.info")}",
        "infoEmpty":      "${messageService.getMessage("dialog.datatables.infoEmpty")}",
        "infoFiltered":   "${messageService.getMessage("dialog.datatables.infoFiltered")}",
        "infoPostFix":    "${messageService.getMessage("dialog.datatables.infoPostFix")}",
        "thousands":      "${messageService.getMessage("dialog.datatables.thousands")}",
        "lengthMenu":     "${messageService.getMessage("dialog.datatables.lengthMenu")}",
        "loadingRecords": "${messageService.getMessage("dialog.datatables.loadingRecords")}",
        "processing":     "${messageService.getMessage("dialog.datatables.processing")}",
        "search":         "${messageService.getMessage("dialog.datatables.search")}",
        "zeroRecords":    "${messageService.getMessage("dialog.datatables.zeroRecords")}",
        "paginate": {
            "first":      "${messageService.getMessage("dialog.datatables.paginate.first")}",
            "last":       "${messageService.getMessage("dialog.datatables.paginate.last")}",
            "next":       "${messageService.getMessage("dialog.datatables.paginate.next")}",
            "previous":   "${messageService.getMessage("dialog.datatables.paginate.previous")}",
            "aria": {
                "first":    "${messageService.getMessage("dialog.datatables.paginate.aria.first")}",
                "previous": "${messageService.getMessage("dialog.datatables.paginate.aria.previous")}",
                "next":     "${messageService.getMessage("dialog.datatables.paginate.aria.next")}",
                "last":     "${messageService.getMessage("dialog.datatables.paginate.aria.last")}"
            }
        },
        "aria": {
            "sortAscending":  "${messageService.getMessage("dialog.datatables.aria.sortAscending")}",
            "sortDescending": "${messageService.getMessage("dialog.datatables.aria.sortDescending")}"
        }
    }
};
dialog.messages.validation = {};
dialog.messages.validation.invalidTime="${messageService.getMessage("dialog.validation.invalidTime")}";
dialog.messages.validation.invalidDateTime="${messageService.getMessage("dialog.validation.invalidDateTime")}";
var CKEDITOR_BASEPATH = dialog.baseUrl+"/assets/ext/ckeditor/";
        """
        response.setHeader("Cache-Control", "no-cache")  // should always validate
        response.setHeader("X-Content-Options", "nosniff")
        render(text: dialogScript, contentType: "application/javascript")
    }

    /**
     * Serve script to initial dialog init.
     * FIXME Could remove from here because it's fully static it could be a asset file. 
     *  But it must load last and after dom loading is (almost)done.
     * @return
     */
    def dialogInit() {
        def dialogInitScript = """\$(function() {
    \$(document).trigger("dialog-init", {});
    \$(".dialog-open-events").filter(".dialog-open-first").filter(":not(.dialog-opened)").trigger("dialog-open", {"page": true});
    \$(".dialog-open-events").filter(":not(.dialog-open-first)").filter(":not(.dialog-opened)").trigger("dialog-open", {"page": true}).addClass("dialog-opened");
});
"""
        response.setHeader("Cache-Control", "public")  // all caching allowed
        response.setHeader("X-Content-Options", "nosniff")
        render(text: dialogInitScript, contentType: "application/javascript")
    }

    //def resource() { }
}