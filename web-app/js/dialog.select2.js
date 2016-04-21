/*
* Select2 module for dialog plugin
*
* Grails Dialog plug-in
* Copyright 2016 Open-T B.V., and individual contributors as indicated
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
dialog.select2 = {};

dialog.select2.descriptionTemplate = function (item) {
    if (!item.description) { return item.text; }
    var renderedString='<span><span class="select2-label">'+item.text+'</span><span class="select2-description">'+item.description+'</span></span>'
    renderedItem=$(renderedString);
    return renderedItem;
}
/*
 * Select2 initialization
 * Attributes used:
 * - tags : switches on tagging support of Select2
 * - jsonUrl            : URL of data source , needs to return JSON {value,label, description (optional)}
                          based on sent term parameter. Empty means no datasource, only use options in control
 * - minimumInputLength : minumum input length for search, defaults to 0
 * - separatorsString   : separator string for tags. Each char acts as a separator.
 * - useDescription     : if true, the description returned from the JSON data source is shown.
 */
dialog.select2.open =function open (e,params) {
    var useTags=$(this).attr("tags")==="true";
    var minimumInputLength=$(this).attr("minimum-input-length")||0;
    var separatorsString=$(this).attr("separators")||"";
    var separators=separatorsString.split("");
    var jsonUrl=$(this).attr("jsonUrl");
    var useDescription=$(this).attr("description");

    if (jsonUrl!=undefined && jsonUrl.length>0 ) {
        // AJAX version
        var descriptionTemplate=undefined;
        if (useDescription) {
            descriptionTemplate=dialog.select2.descriptionTemplate;
        }

        $(this).select2({
            templateResult: descriptionTemplate,
            ajax:{
                url:jsonUrl,
                dataType: 'json',
                delay: 250,

                data: function (params) {
                    return {
                        term: params.term
                    };
                },

                processResults: function (data, params) {
                    params.page = params.page || 1;
                    var items=[];
                    for (i in data) {
                        if (data[i].description) {
                            items.push ({ id: data[i].value, text: data[i].label,description:data[i].description});
                        } else {
                            items.push ({ id: data[i].value, text: data[i].label});
                        }
                    }
                    return {
                        results: items
                    };
                }
            },
            tags:useTags,
            minimumInputLength: minimumInputLength,
            tokenSeparators:separators
        });

    } else {
        $(this).select2({
            tags:useTags,
            minimumInputLength: minimumInputLength,
            tokenSeparators:separators
        });
    }
};

$(function() {
    // http://stackoverflow.com/questions/18487056/select2-doesnt-work-when-embedded-in-a-bootstrap-modal
    $.fn.modal.Constructor.prototype.enforceFocus = function() {};
    $(document).on("dialog-open","select.select2",dialog.select2.open);
});
