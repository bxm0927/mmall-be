package com.mmall.util;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 日期时间工具类
 * 使用了 joda-time 工具库
 * http://blog.csdn.net/cwcwj3069/article/details/52164559
 */
public class DateTimeUtils {

    // 标准的字符串格式化模板
    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 字符串转日期
     * 传统方法：new SimpleDateFormat(format).parse(str)
     *
     * @param dataStr 字符串
     * @param format  日期格式，如"yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static Date str2Date(String dataStr, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(format);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dataStr);
        return dateTime.toDate();
    }

    /**
     * 日期转字符串
     * 传统方法：new SimpleDateFormat(format).format(date)
     *
     * @param date   日期
     * @param format 日期格式
     * @return
     */
    public static String date2Str(Date date, String format) {
        if (date == null) {
            return StringUtils.EMPTY;
        }

        DateTime dateTime = new DateTime(date);
        return dateTime.toString(format);
    }

    // 时间戳转日期

    // 日期转时间戳

    // 获取某一时间的时间戳

}
