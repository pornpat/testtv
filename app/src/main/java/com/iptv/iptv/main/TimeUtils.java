package com.iptv.iptv.main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Asus N46V on 15/7/2017.
 */

public class TimeUtils {

    public static boolean isScheduleLive(Date currentTime, String programTime, int duration) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date startProgram = format.parse(programTime);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(format.parse(programTime));
            calendar.add(Calendar.MINUTE, duration);
            Date endProgram = calendar.getTime();

            if (currentTime.compareTo(startProgram) > 0 && currentTime.compareTo(endProgram) < 0) {
                return true;
            } else {
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
