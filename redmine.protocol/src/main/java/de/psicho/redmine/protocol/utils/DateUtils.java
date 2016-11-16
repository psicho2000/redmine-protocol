package de.psicho.redmine.protocol.utils;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class DateUtils {

    public static String dateToIso(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        String isoDate =
            cal.get(Calendar.YEAR) + "-" + zeroPad(cal.get(Calendar.MONTH) + 1) + "-" + zeroPad(cal.get(Calendar.DAY_OF_MONTH));

        return isoDate;
    }

    public static String dateToGer(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        String isoDate =
            zeroPad(cal.get(Calendar.DAY_OF_MONTH)) + "." + zeroPad(cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.YEAR);

        return isoDate;
    }

    public static String zeroPad(Integer input) {
        return StringUtils.leftPad(input.toString(), 2, "0");
    }
}
