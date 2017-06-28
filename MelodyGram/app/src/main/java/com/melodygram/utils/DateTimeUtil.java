package com.melodygram.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import android.util.Log;

import com.melodygram.R;
import com.melodygram.constants.GlobalState;

/**
 * Created by LALIT on 15-06-2016.
 */
@SuppressLint("SimpleDateFormat")
public class DateTimeUtil {
    static Calendar calendar;
    static SimpleDateFormat simpleDateFormat;
    static String stringDateTime;



    public static String TweentyFourTo12(String _24HourTime,
                                         Activity activityRef) {
        try {
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(_24HourTime);
            return _12HourSDF.format(_24HourDt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _24HourTime;
    }

    public static String tweentyFourTo12New(String _24HourTime) {



        try {
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            _24HourSDF.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            _12HourSDF.setTimeZone(TimeZone.getDefault());
            Date _24HourDt = _24HourSDF.parse(_24HourTime);


            return _12HourSDF.format(_24HourDt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _24HourTime;
    }

    public static String getDate(String time) {
        try {
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            _24HourSDF.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("dd-MM-yyyy");
            _12HourSDF.setTimeZone(TimeZone.getDefault());
            Date _24HourDt = _24HourSDF.parse(time);
            return _12HourSDF.format(_24HourDt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }


    public static String LastSeenTweentyFourTo12(String _24HourTime1) {
        String _24HourTime = convertUtcToLocalTime(_24HourTime1);
        try {
            SimpleDateFormat _24HourSDF = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(_24HourTime);

            return _12HourSDF.format(_24HourDt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _24HourTime;
    }


    public static String lastChatDateOrTime(String chatDateTime) {
        try {

            String localtime = convertUtcToLocalTime(chatDateTime);
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calenderToday = Calendar.getInstance();
            Calendar calenderYesterday = Calendar.getInstance();
            calenderYesterday.add(Calendar.DATE, -1);
            Date dateServer = formatDate.parse(formatDate.format(format
                    .parse(localtime)));
            Date dateToday = formatDate.parse(formatDate.format(calenderToday
                    .getTime()));
            Date dateYesterday = formatDate.parse(formatDate
                    .format(calenderYesterday.getTime()));
            if (dateServer.compareTo(dateToday) == 0) {
                SimpleDateFormat formatToday = new SimpleDateFormat("h:mm a");

                //   return formatToday.format(format.parse(localtime));

                return formatToday.format(format.parse(chatDateTime));
            } else if (dateServer.compareTo(dateYesterday) == 0) {
                return "Yesterday";
            } else {
                SimpleDateFormat formatOld = new SimpleDateFormat("MMM-dd-yyyy");
                return formatOld.format(dateServer);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return chatDateTime;


    }

    public static String morechatDateFormat() {
        calendar = Calendar.getInstance(Locale.US);
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", new Locale("en"));
        stringDateTime = simpleDateFormat.format(calendar.getTime());
        return stringDateTime;
    }



    public static String morechatTimeFormat() {
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        TimeZone utcZone = TimeZone.getTimeZone("UTC");
        simpleDateFormat.setTimeZone(utcZone);
        stringDateTime = simpleDateFormat.format(calendar.getTime());
        return stringDateTime;
    }

    static String todaysDateFloat1, chatDateTempFloat1, todaysTempFloat1;


    public static String lastSeenDateFormat() {
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        stringDateTime = simpleDateFormat.format(calendar.getTime());
        return stringDateTime;
    }

    public static String dateToCopyDateFormat(String messageDate) {
        String month = messageDate.substring(messageDate.indexOf("-") + 1,
                messageDate.lastIndexOf("-"));
        String day = messageDate.substring(0, messageDate.indexOf("-"));
        messageDate = month + "/" + day;
        return messageDate;
    }

    public static String lastSeenDate(String chatDate,Activity activity) {
        todaysDateFloat1 = null;
        chatDateTempFloat1 = null;
        todaysTempFloat1 = null;
        todaysDateFloat1 = lastSeenDateFormat();
        if (chatDate.equals(todaysDateFloat1)) {
            return activity.getResources().getString(R.string.today);
        } else if (chatDate.substring(chatDate.indexOf("-")).equals(
                todaysDateFloat1.substring(todaysDateFloat1.indexOf("-")))) {
            chatDateTempFloat1 = chatDate
                    .substring(chatDate.lastIndexOf("-") + 1);
            todaysTempFloat1 = todaysDateFloat1.substring(todaysDateFloat1
                    .lastIndexOf("-") + 1);
            if ((Integer.parseInt(chatDateTempFloat1) + 1) == Integer
                    .parseInt(todaysTempFloat1)) {
                activity.getResources().getString(R.string.yesterday);
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date myDate = null;
        try {
            myDate = simpleDateFormat.parse(chatDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String finalDate = simpleDateFormat.format(myDate);
        String[] dateArray = finalDate.split("-");
        return dateArray[2] + ", " + dateArray[1] + ", " + dateArray[0];
    }


    public static String convertUtcToLocalTime(String UtcTime) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date myDate = simpleDateFormat.parse(UtcTime);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            return simpleDateFormat.format(myDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return UtcTime;
    }

    public static Long convertUtcToLocalDate(String UtcTime) {
        long localTime = 0;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            simpleDateFormat.parse(UtcTime);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            long lastSeen = simpleDateFormat.getCalendar().getTimeInMillis();
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            long now = c.getTimeInMillis();
            long difference = now - lastSeen;
            long differenceInSeconds = difference / DateUtils.SECOND_IN_MILLIS;
            localTime = differenceInSeconds;
        } catch (Exception e) {
        }
        return localTime;
    }

}
