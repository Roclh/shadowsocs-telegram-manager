package org.Roclh.handlers.callbacks;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;

public interface Callback<T extends PartialBotApiMethod<? extends Serializable>>{
    T apply(CallbackData callbackData);

    String getName();
}
