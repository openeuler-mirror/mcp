package com.hnkylin.cloud.core.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * 日期处理
 */
public class DateUtils {

    /**
     * 时间格式(yyyy-MM-dd)
     */
    public final static String DATE_YYYY_MM_DD = "yyyy-MM-dd";

    /**
     * 时间格式(yyyy-MM-dd HH:mm:ss)
     */
    public final static String DATE_ALL_PATTEN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间格式(yyyy-MM-dd HH:mm:ss)
     */
    public final static String DATE_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

    /**
     * 时间格式(HH:mm:ss)
     */
    public final static String DATE_TIME_PATTERN_HOUR = "HH:mm:ss";

    /**
     * 时间格式(yyyyMMddHHmmss)
     */
    public final static String DATE_YYYYMMddHHmmss = "yyyyMMddHHmmss";

    /**
     * 时间格式(yyyyMMdd)
     */
    public final static String DATE_YYYYMMdd = "yyyyMMdd";

    /**
     * 时间格式(yyyyMM)
     */
    public final static String DATE_YYYYMM = "yyyyMM";
    /**
     * 时间格式(yyyy-MM)
     */
    public final static String DATE_YYYY_MM = "yyyy-MM";
    /**
     * 时间格式(yyyy年M月)
     */
    public final static String DATE_YYYY_M = "yyyy年M月";


    public final static String DAY_END = " 23:59:59";


    public final static String DAY_START = " 00:00:00";

    public static String format(Date date) {
        return format(date, DATE_YYYY_MM_DD);
    }

    public static Date strFormatDate(String date) {
        SimpleDateFormat df = new SimpleDateFormat(DATE_ALL_PATTEN);
        try {
            return df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static String format(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }

    public static String formatYYYYMMDD(Date date) {
        return format(date, DATE_YYYYMMdd);
    }


    /**
     * 计算几个月后的时间
     */

    public static Date getMonthAfter(Date initialDate, int afterMonth) {
        initialDate = Objects.isNull(initialDate) ? new Date() : initialDate;
        LocalDateTime localDateTime = initialDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime().plusMonths(afterMonth);
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return date;
    }


    /**
     * 计算时间的最后一秒  2021-6-25 12：01：00  得到 2021-6-25 23：59：59
     */
    public static Date getDayEndTime(Date date) {

        return parse(format(date, DateUtils.DATE_YYYY_MM_DD) + DAY_END, DATE_ALL_PATTEN);

    }

    /**
     * 通过时间秒毫秒数判断两个时间的间隔。天
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(Date date1, Date date2) {
        Date startDate = parse(format(date1, DATE_YYYY_MM_DD), DATE_YYYY_MM_DD);
        Date endDate = parse(format(date2, DATE_YYYY_MM_DD), DATE_YYYY_MM_DD);
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(endDate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 计算距离现在多久，精确
     *
     * @param date
     * @return
     */
    public static String getTimeBeforeAccurate(Date date) {
        Date now = new Date();
        long l = now.getTime() - date.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        String r = "";
        if (day > 0) {
            r += day + "天";
        }
        if (hour > 0) {
            r += hour + "小时";
        }
        if (min > 0) {
            r += min + "分";
        }
        if (s > 0) {
            r += s + "秒";
        }
        r += "前";
        return r;
    }

    /**
     * 两个时间的间隔秒
     *
     * @param one   当前时间
     * @param other 旧的时间
     * @return
     */
    public static int getBetweenTime(Date one, Date other) {
        return (int) (Math.abs(one.getTime() - other.getTime()) / 1000);
    }

    public static Date parse(String date, String pattern) {
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            try {
                return df.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Date getStartTime(String date) {
        if (date != null) {
            return parse(date + " 00:00:00", DATE_ALL_PATTEN);
        }
        return null;
    }

    public static Date getEndTime(String date) {
        if (date != null) {
            return parse(date + " 23:59:59", DATE_ALL_PATTEN);
        }
        return null;
    }


    /**
     * 生成时间戳
     */
    public static String getDateTime() {
        DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdf.format(new Date());
    }


    public static void main(String[] args) {
//    System.out.println(parse("2018-12-27 00:00:00", DATE_ALL_PATTEN).getTime());
//    System.out.println(parse("2018-12-27 23:59:59", DATE_ALL_PATTEN).getTime());
//    System.out.println(format(secondToDate(1543852800000L),DATE_ALL_PATTEN));
//    String day="2019-01-04";
//    System.out.println(day.substring(5));


        System.out.println(parse("2018-06-01 00:00:00", DATE_ALL_PATTEN).getTime());
        System.out.println(parse("2018-12-31 23:59:59", DATE_ALL_PATTEN).getTime());
        System.out.printf(format(getMonthAfter(new Date(), 2), DATE_ALL_PATTEN));
    }

}
