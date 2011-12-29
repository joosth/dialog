package org.open_t.dialog;
import java.util.Date;

import org.codehaus.groovy.grails.web.binding.*;
import org.springframework.beans.*;
public class CustomPropertyEditorRegistrar implements PropertyEditorRegistrar {
	 
    public void registerCustomEditors(PropertyEditorRegistry registry) {
      registry.registerCustomEditor(Date.class, new DateTimePropertyEditor("yyyy-MM-dd'T'HH:mm"));
    }
}