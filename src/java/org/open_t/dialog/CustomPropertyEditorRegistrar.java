package org.open_t.dialog;

import java.util.Date;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;

public class CustomPropertyEditorRegistrar implements PropertyEditorRegistrar {

    public void registerCustomEditors(PropertyEditorRegistry registry) {
      registry.registerCustomEditor(Date.class, new DateTimePropertyEditor("yyyy-MM-dd'T'HH:mm"));
    }
}
