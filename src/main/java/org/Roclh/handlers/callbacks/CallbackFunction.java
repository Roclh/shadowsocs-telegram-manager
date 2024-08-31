package org.Roclh.handlers.callbacks;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackFunction {
    SendMessage apply(Callback callback, Update update);
}
