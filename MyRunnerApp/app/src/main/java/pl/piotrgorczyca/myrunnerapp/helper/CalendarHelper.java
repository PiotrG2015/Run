package pl.piotrgorczyca.myrunnerapp.helper;

import android.util.Log;

import java.util.Calendar;

/**
 * Created by Piotr on 2015-12-27. Enjoy!
 */
public class CalendarHelper {
    final static int DAY_IM = 1000*60*60*24;
    final static int HOUR_IM = 1000*60*60;
    final static int MIN_IM = 1000*60;

    //count the remaining time fill given date in 'yyyy-mm-dd hh:mm' format
    public static String countRemainingTime(String date) {
        Calendar now = Calendar.getInstance();
        Calendar later = Calendar.getInstance();
        later.set(Integer.parseInt(date.substring(0, 4)),
                Integer.parseInt(date.substring(5, 7))-1,
                Integer.parseInt(date.substring(8, 10)),
                Integer.parseInt(date.substring(11, 13)),
                Integer.parseInt(date.substring(14, 16)));
        long diff = later.getTimeInMillis() - now.getTimeInMillis();
        now.setTimeInMillis(diff);
        if(DAY_IM < diff){
            return String.format("%ta %tR", later, later);
        } else if(diff>HOUR_IM) {
            return (int) diff/HOUR_IM + " h left";
        } else if (diff>MIN_IM) {
            return (int) diff/MIN_IM + " min left";
        } else {
            return "";
        }
    }

    //count the remaining time fill given date in 'yyyy-mm-dd hh:mm' format
    public static String countPassedTime(String date) {
        Calendar now = Calendar.getInstance();
        Calendar before = Calendar.getInstance();
        before.set(Integer.parseInt(date.substring(0, 4)),
                Integer.parseInt(date.substring(5, 7)) - 1,
                Integer.parseInt(date.substring(8, 10)),
                Integer.parseInt(date.substring(11, 13)),
                Integer.parseInt(date.substring(14, 16)));
        long diff = now.getTimeInMillis() - before.getTimeInMillis();
        now.setTimeInMillis(diff);
        if(DAY_IM < diff){
            return String.format("%ta", before);
        } else if(diff>HOUR_IM) {
            return (int) diff/HOUR_IM + " h ago";
        } else if (diff>MIN_IM) {
            return (int) diff/MIN_IM + " min ago";
        } else {
            return "";
        }
    }

    public static String countAge(String date_of_birth) {
        Calendar now = Calendar.getInstance();
        Calendar birth = Calendar.getInstance();
        birth.set(Integer.parseInt(date_of_birth.substring(0, 4)),
                Integer.parseInt(date_of_birth.substring(5, 7))-1,
                Integer.parseInt(date_of_birth.substring(8, 10)),
                Integer.parseInt(date_of_birth.substring(11, 13)),
                Integer.parseInt(date_of_birth.substring(14, 16)));
        long age = now.getTimeInMillis() - birth.getTimeInMillis();
        return Long.toString((int) ((age/DAY_IM)/365));
    }
}
