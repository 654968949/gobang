package com.wyz.gobang.utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 *     五子棋的五子连珠的动画
 * </p>
 *
 * @author wuyuzi
 * @since 2020/12/22
 */
public class GobangAnimationUtils {

    //当前的Timeline实例
    private  static Timeline currentTimeline;


    public static void startAnimation(List<Node> lists ) {
        AtomicBoolean flag = new AtomicBoolean(true);
        //创建一个handler
        EventHandler<ActionEvent> eventHandler = e -> {
            if (flag.get()) {
                for( Node node : lists ) {
                    //后面两个参数是圆的坐标，以适应在当前坐标缩放
                    Scale scale = new Scale(1.25,1.25,((Circle)node).getCenterX(),((Circle)node).getCenterY());
                    node.getTransforms().add(scale);
                }
                flag.set(false);
            } else {
                for( Node node:lists ) {
                    node.getTransforms().removeIf(transform -> transform instanceof Scale);
                }
                flag.set(true);
            }
        };
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), eventHandler));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        //赋给静态变量
        currentTimeline = timeline;
    }

    //停止五子棋动画
    public static void stopAnimation() {
        currentTimeline.stop();
    }

}
