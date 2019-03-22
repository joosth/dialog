/*
 * Copyright 2019 Open-T B.V., and individual contributors as indicated
 * by the @author tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 */

CKEDITOR.plugins.add('wfp-imageinsert', {

    icons: 'wfp-imageinsert',

    init: function(editor) {

        editor.addCommand('insertImageDocument', {
            exec: function(editor) {
                var editorName = CKEDITOR.currentInstance.name;
                dialogform = xmlforms.formDialog('imageinsert',
                                    'list',
                                    { 'dialogname': 'documentlist' },
                                    { 'editorname': editorName });

            }
        });

        editor.ui.addButton('wfp-imageinsert', {
            label: 'Invoegen afbeelding in document.', // NOI18N
            command: 'insertImageDocument',
            toolbar: 'insert,11'  // after insert image
        });
    }
});
