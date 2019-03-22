/*
 * Copyright 2019 Open-T B.V., and individual contributors as indicated
 * by the @author tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 */

CKEDITOR.plugins.add('wfp-documentlink', {

    icons: 'wfp-documentlink',

    init: function(editor) {

        editor.addCommand('insertHtmlDocumentLink', {
            exec: function(editor) {
                var editorName = CKEDITOR.currentInstance.name;
                dialogform = xmlforms.formDialog('documentlink',
                                    'list',
                                    { 'dialogname': 'documentlist'},
                                    {'editorname': editorName});

            }
        });

        editor.ui.addButton('wfp-documentLink', {
            label: 'Link naar document', // NOI18N
            command: 'insertHtmlDocumentLink',
            toolbar: 'links,11'  // after insert/change hyperlink
        });
    }
});
