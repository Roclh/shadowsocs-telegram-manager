package org.Roclh.utils.i18n;

import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.ResourceBundle;


@Slf4j
public class I18N {
    private final Locale locale;
    private final ResourceBundle resourceBundle;

    public String get(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (Exception e) {
            log.error("Failed to get resource from bundle with locale {} by key {}", locale, key, e);
            return key;
        }
    }

    public String get(String key, Object... objects) {
        try {
            String configurableString = resourceBundle.getString(key);
            for (int i = 0; i < objects.length; i++) {
                if (!configurableString.contains("${" + i + "}")){
                    log.error("String {} does not contain ${}, but amount of objects is {}", configurableString, i, objects.length);
                }
            }
            for (int i = 0; i < objects.length; i++) {
                configurableString = configurableString.replace("${" + i + "}", objects[i].toString());
            }
            return configurableString;
        } catch (Exception e) {
            log.error("Failed to get resource from bundle with locale {} by key {}", locale, key, e);
            return key;
        }
    }

    private I18N(Locale locale) {
        this.locale = locale;
        this.resourceBundle = ResourceBundle.getBundle("i18n.localization", locale);
    }

    public static I18N from(Locale locale) {
        return new I18N(locale);
    }

}
