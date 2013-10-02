package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import trade.AbstractExpert;

/**
 *
 * @author omar
 */
public class Date {
    private static String time;
    private static String date;
    private static Integer minutes = -1;
    private static Integer hour = -1;
    private static Integer day = -1;
    private static Integer month= -1;
    private static Integer year= -1;
    public static void setTime(String d, String t){
        time = fixTime(t);
        date = d;
        minutes = Integer.parseInt(time.substring(2, 4));
        hour =  Integer.parseInt(time.substring(0, 2));
        
        year = Integer.parseInt(d.substring(0, 4));
        month = Integer.parseInt(d.substring(4, 6));
        day = Integer.parseInt(d.substring(6, 8));
        
    }
    
    public static int getMinutes() {
        return minutes;
    }


    public static int getHora() {
       return hour;
    }

    public static int getDay() {
       return day;
    }


    public static int getMonth() {
       return month;
    }

    public static int getYear() {
        return year;
    }
    
    public static String getDate(){
        return date;
    }
    
    private static String fixTime(Integer val){
        String i = val < 10 ? "0"+val : val.toString();
        return i;
    }
    
    private static String fixTime(String time) {
        String s = "";
        if(time.length() < 6){
            for (int i = time.length(); i < 6; i++) {
                s+="0";
            }
        } 
        return s+time;
    }
    
    public static String horaToString() {
        return getHora() + ":" + getMinutes();
    }
    
    public static Integer dayOfWeek() {
        Integer val = -1;
        String format = "yyyyMMdd";
        SimpleDateFormat df = new SimpleDateFormat(format);
        try {
            java.util.Date date = df.parse(getDate());
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            val = cal.get(Calendar.DAY_OF_WEEK);
        } catch (ParseException ex) {
            Logger.getLogger(AbstractExpert.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        return val;
    }
    public static String dateToString(){
        return getDate()+" " +fixTime(getHora()) + ":" + fixTime(getMinutes());
    }
}
