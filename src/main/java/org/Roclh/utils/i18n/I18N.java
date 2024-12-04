package org.Roclh.utils.i18n;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.handlers.messaging.MessageData;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;


@Slf4j
public class I18N {
    private final Locale locale;
    private final ResourceBundle resourceBundle;
    private final String prefix;

    public String get(String key) {
        try {
            String staticString = resourceBundle.getString(prefix + key);
            if(Pattern.compile("$*\\$\\{[0-9]}").matcher(staticString).find()){
                log.warn("String {} have placeholders, but objects are not passed", staticString);
            }
            return staticString;
        } catch (Exception e) {
            log.error("Failed to get resource from bundle with locale {} by key {}", locale, key, e);
            return key;
        }
    }

    public String get(String key, Object... objects) {
        try {
            String configurableString = resourceBundle.getString(prefix + key);
            for (int i = 0; i < objects.length; i++) {
                if (!configurableString.contains("${" + i + "}")) {
                    log.warn("String {} does not contain ${}, but amount of objects is {}", configurableString, i, objects.length);
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
        this.prefix = "";
    }

    private I18N(Locale locale, String prefix) {
        this.locale = locale;
        this.resourceBundle = ResourceBundle.getBundle("i18n.localization", locale);
        this.prefix = prefix;
    }

    public static I18N from(MessageData messageData){
        return new I18N(messageData.getLocale());
    }
    public static I18N from(Locale locale) {
        return new I18N(locale);
    }

    public static I18N from(Locale locale, String prefix) {
        return new I18N(locale, prefix);
    }
}
