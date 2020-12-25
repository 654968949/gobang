package com.wyz.gobang.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 *     时间工具类
 * </p>
 *
 * @author wuyuzi
 * @since 2020/12/13
 */
public class DateUtils {
    /**
     * 得到传入时间与当前的毫秒差值 (除去东八区北京时间的8小时)
     *
     * @param date 传入的时间
     * @return 传入时间和当前时间的毫秒值差
     */
    public static long compareTimeFromNow(Date date) {
        Date now = new Date();
        return now.getTime() - date.getTime() - 8 * 60 * 60 * 1000;
    }

    /**
     * 返回 格式化之后的time（HH:mm:ss）
     *
     * @param time 时间的毫秒值
     * @return 格式化的时间
     */
    public static String formatWith_HH_mm_ss(long time) {
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
        return sf.format(new Date(time));
    }

    /**
     * 返回 格式化之后的time（HH:mm:ss）
     *
     * @param time 时间的毫秒值
     * @return 格式化的时间
     */
    public static String formatWith_yyyy_MM_dd_HH_MM_ss(long time) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        return sf.format(new Date(time));
    }
}
