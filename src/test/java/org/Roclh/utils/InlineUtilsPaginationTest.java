package org.Roclh.utils;

import org.Roclh.handlers.callbacks.CallbackData;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class InlineUtilsPaginationTest {
    private CallbackData callbackData;
    private final String correctCommand = "tguser deltg {0}";

    @Test
    public void whenCorrectStringPassed_thenPatternMatched() {
        boolean isPatternMatched = InlineUtils.paginationMatches(correctCommand);
        assertTrue(isPatternMatched, "Command was not matched");
    }

//    @Test
    public void whenCorrectStringPassed_thenGetListPaginationMarkupWithPagination() {
        callbackData = CallbackData.builder()
                .callbackCommand(correctCommand)
                .callbackData(correctCommand)
                .telegramId(1234L)
                .locale(Locale.forLanguageTag("en"))
                .build();
        InlineUtils.getListNavigationMarkupWithPagination(Map.of(), (text) -> text, callbackData, () -> null, 2);
    }
}
