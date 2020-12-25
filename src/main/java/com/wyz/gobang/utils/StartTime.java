package com.wyz.gobang.utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 * </p>
 *
 * @author wuyuzi
 * @since 2020/12/15
 */
public class StartTime {
    //startBattleTime 的timeline
    private  static Timeline timeline1;
    //startTime 的timeline
    private  static Timeline timeline2;

    /**
     * 开始对局倒计时
     * @param label 对局倒计时的label
     * @param startTime 对局开始的时间
     * @return timeline
     */
    public static Timeline startBattleTime (Label label, AtomicLong startTime) {
        //对局时间显示
        EventHandler<ActionEvent> eventHandler = e -> {
            long time = DateUtils.compareTimeFromNow(new Date(startTime.longValue()));
            String times = DateUtils.formatWith_HH_mm_ss(time);
            label.setText(times);
        };
        //每秒刷新时间
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), eventHandler));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        timeline1 = timeline;
        return timeline;
    }

    /**
     * 开始一个年月日时间
     * @param label 年月日显示的label
     */
    public static void startTime (Label label) {
        //对局时间显示
        EventHandler<ActionEvent> eventHandler = e -> {
            label.setText(DateUtils.formatWith_yyyy_MM_dd_HH_MM_ss(System.currentTimeMillis()));
        };
        //每秒刷新时间
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), eventHandler));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        timeline2 = timeline;
    }

    public static Timeline getBattleTimeline() {
        return timeline1;
    }

    public static Timeline getStartTimeline() {
        return timeline2;
    }
}
