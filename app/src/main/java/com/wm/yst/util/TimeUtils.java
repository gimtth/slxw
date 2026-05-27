package com.wm.yst.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class TimeUtils {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private TimeUtils() {
    }

    public static String now() {
        return new SimpleDateFormat(DATE_TIME_PATTERN, Locale.CHINA).format(new Date());
    }
}
