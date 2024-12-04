package org.Roclh.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter defaultFormatter = DateTimeFormatter.ISO_DATE_TIME;

    public static String getDefaultFormatPattern(){
        return defaultFormatter.toFormat().toString();
    }
    public static boolean validate(String dateTime) {
        try {
            LocalDate.from(defaultFormatter.parse(dateTime));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
