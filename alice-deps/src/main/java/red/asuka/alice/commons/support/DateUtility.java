package red.asuka.alice.commons.support;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author Tomonori
 * @Mail gutrse3321@live.com
 * @Date 2020-09-20 2:18 AM
 */
@UtilityClass
public class DateUtility extends StringUtils {

    /**
     * 获取 获取1970至今的天数
     *
     * @return
     */
    public static int getCurDay() {
        TimeZone zone = TimeZone.getDefault();    //默认时区
        long s = System.currentTimeMillis() / 1000;
        if (zone.getRawOffset() != 0) {
            s = s + zone.getRawOffset() / 1000;
        }
        s = s / 86400; //86400 = 24 * 60 * 60 (一天时间的秒数)
        return (int) s;
    }

    /**
     * 获取当前零点时间戳
     * @return
     */
    public static Long currentZeroPointTimestamps() {
        long current = System.currentTimeMillis();
        long zero = current/(1000*3600*24)*(1000*3600*24) - TimeZone.getDefault().getRawOffset();
        return zero;
    }

    /**
     * 获取当前23点59分时间戳
     * @return
     */
    public static Long currentLastTimestamps() {
        Long zeroPointTimestamps = currentZeroPointTimestamps();
        Long lastTime = zeroPointTimestamps + 24 * 60 * 60 * 1000 - 1;
        return lastTime;
    }

    /**
     * 计算两天之间的天数
     *
     * @param startStr
     * @param endStr
     * @return
     */
    public static int daysBetween(String startStr, String endStr) {
        int daysBetween = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            Date date1 = sdf.parse(startStr);
            Calendar startDate = Calendar.getInstance();
            startDate.setTime(date1);

            Date date2 = sdf.parse(endStr);
            Calendar endDate = Calendar.getInstance();
            endDate.setTime(date2);

            Calendar date = (Calendar) startDate.clone();

            while (date.before(endDate)) {
                date.add(Calendar.DAY_OF_MONTH, 1);
                daysBetween++;
            }

        } catch (ParseException e) {
            return -1;
        }
        return daysBetween;
    }

    public static String convertDateTime(Date currentDate, String datetimeFormat) {
        if (currentDate == null || datetimeFormat == null || "".equals(datetimeFormat)) {
            return "";
        } else {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(datetimeFormat);
                return formatter.format(currentDate);
            } catch (Exception e) {
                return "";
            }
        }
    }

    public static String convertTimestamp(Long timestamp, String datetimeFormat) {
        if (timestamp == null || datetimeFormat == null || "".equals(datetimeFormat)) {
            return "";
        } else {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(datetimeFormat);
                return formatter.format(timestamp);
            } catch (Exception e) {
                return "";
            }
        }
    }

    public static String getCurrentDate() {
        return getYYYYMMDDBybar(getUnixTimestamp());
    }

    /**
     * 获取当前时间
     * 格式：yyyy-MM-dd
     *
     * @return 当前时间的字符串形式
     * @author xieyj
     */
    public static String getCurrentDate_YYYYMMDD() {
        return convertDateTime(new Date(), "yyyy-MM-dd");
    }

    /**
     * 时间格式转换
     * 格式：yyyyMMdd
     *
     * @param date
     * @return
     */
    public static String getYYYYMMDDBybar(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formatter = new SimpleDateFormat("yyyyMMdd");
        return formatter.format(newDate);
    }

    public static String getYYYYMMDDByDot(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date newDate = null;
        try {
            newDate = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formatter = new SimpleDateFormat("yyyy.MM.dd");
        return formatter.format(newDate);
    }

    public static String getUnixTimestamp() {
        StringBuilder builder = new StringBuilder();
        builder.append(System.currentTimeMillis());
        return builder.substring(0, 10);
    }

    public static Long getUnixTimestamp(Long timestamp) {
        StringBuilder builder = new StringBuilder();
        builder.append(timestamp);
        return Long.valueOf(builder.substring(0, 10));
    }

    /**
     * 日期加年，月，日，时，分，秒 数
     * @param currentDate 当前时间
     * @param addType 添加类型 年，月，日，时，分，秒 数
     * @param dateTimeCount 数量
     * @return
     */
    public static Date addDateTime(Date currentDate, int addType, int dateTimeCount) {
        Date addedDate = currentDate;
        if (currentDate != null && dateTimeCount != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(addType, dateTimeCount);
            addedDate = calendar.getTime();
        }
        return addedDate;
    }

    public static String getDateByTimeStamp(String seconds, String format) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds)));
    }

    /**
     * 计算本周周一和周日日期
     *
     * @return MAP
     * @author jiangfengcheng
     */
    public static Map<String, Long> getThisWeekTimestamp() {
        Map<String, Long> dayMap = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        dayMap.put("first", cal.getTime().getTime());

        cal.add(Calendar.DATE, 6);
        dayMap.put("last", cal.getTime().getTime());
        return dayMap;
    }

    /**
     * 获取日期前几天或后几天的日期
     * @param date
     * @param day
     * @param format
     * @return
     */
    public static String calcDayDate(Date date, int day, String format) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(c.DATE, day);
        SimpleDateFormat f = new SimpleDateFormat(format);
        return f.format(c.getTime());
    }

    /**
     * 根据当前日期获得上周的日期区间（上周周一和周日日期）
     *
     * @return
     * @author jiangfengcheng
     */
    public static Map<String, Long> getLastWeekTimestamp() {
        Map<String, Long> dayMap = new HashMap<>();
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        int dayOfWeek = calendar1.get(Calendar.DAY_OF_WEEK) - 1;
        int offset1 = 1 - dayOfWeek;
        int offset2 = 7 - dayOfWeek;
        calendar1.add(Calendar.DATE, offset1 - 7);
        calendar2.add(Calendar.DATE, offset2 - 7);

        dayMap.put("first", calendar1.getTime().getTime());
        dayMap.put("last", calendar2.getTime().getTime());
        return dayMap;
    }

    /**
     * 获取本月的第一天的时间戳
     * @return
     */
    public static Long getThisMonthTimestamp() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(c.DAY_OF_MONTH, 0);
        c.set(c.MONTH, 0);
        return c.getTimeInMillis();
    }

    /**
     * 获取这个月的上一个月的第一天时间戳
     * @return
     */
    public static Long getPrevMonthTimestamp() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(c.DAY_OF_MONTH, 1);
        c.add(c.MONTH, -1);
        return c.getTimeInMillis();
    }

    /**
     * 获取当前日期的下一个月最后一天的时间戳
     * @return
     */
    public static Long getNextMonthTimestamp() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, 2);
        c.set(Calendar.DATE, 0);
        return c.getTimeInMillis();
    }

    /**
     * 获取当前时间的年初时间戳
     * @return
     */
    public static Long getYearFirstTimestamp() {
        Calendar c = Calendar.getInstance();
        c.clear(Calendar.MONTH);
        c.add(Calendar.YEAR, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    /**
     * 获取当前时间的年末时间戳
     * @return
     */
    public static Long getYearLastTimestamp() {
        Calendar c = Calendar.getInstance();
        c.clear(Calendar.MONTH);
        c.set(Calendar.MONTH, 11);
        c.set(Calendar.DAY_OF_MONTH,
        c.getActualMaximum(Calendar.DAY_OF_MONTH));
        //将小时至23
        c.set(Calendar.HOUR_OF_DAY, 23);
        //将分钟至59
        c.set(Calendar.MINUTE, 59);
        //将秒至59
        c.set(Calendar.SECOND, 59);
        //将毫秒至999
        c.set(Calendar.MILLISECOND, 999);
        return c.getTimeInMillis();
    }

    /**
     * 判断时间是否在范围内
     * @param nowTime
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime() || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断时间是在开始时间之前 还是结束时间之后，配合isEffectiveDate函数使用
     * @param nowTime
     * @param startTime
     * @param endTime
     * @return
     */
    public static Boolean currentDateBeforeOrAfter(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.before(begin)) {
            return true;
        }
        if (date.after(end) || nowTime.getTime() == endTime.getTime()) {
            return false;
        }
        return null;
    }
}
