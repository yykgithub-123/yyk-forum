package yc.star.forum.utils;


import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String formatDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return simpleDateFormat.format(date);
    }
}
