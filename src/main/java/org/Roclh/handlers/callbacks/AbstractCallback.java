package org.Roclh.handlers.callbacks;

import org.Roclh.utils.i18n.I18N;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.Locale;

public abstract class AbstractCallback<T extends PartialBotApiMethod<? extends Serializable>> implements Callback<T>{
    protected I18N i18N;

    @Override
    public Callback<T> setI18N(Locale locale) {
        i18N = I18N.from(locale);
        return this;
    }

    protected String trimLastWord(String data) {
        return data.substring(0, data.lastIndexOf(" "));
    }
}
