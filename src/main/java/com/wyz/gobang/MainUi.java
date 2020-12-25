package com.wyz.gobang;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * <p>
 *     主界面
 * </p>
 * @author wuyuzi
 * @since 2020/12/16
 */
public class MainUi extends Application {
    @Override
    public void start(Stage stage) {
        //获取画板对象
        Pane pane = new Pane();

        //创建按钮对象
        Button singleButton = new Button("单机版");
        singleButton.setLayoutX(30);
        singleButton.setLayoutY(60);
        singleButton.setPrefSize(150, 150);

        //给单机版按钮绑定鼠标点击事件
        singleButton.setOnAction(event -> {
            SingleStage singleStage = new SingleStage();
            //展示舞台
            singleStage.show();
            stage.close();
        });
        Button webButton = new Button("网络版");
        webButton.setLayoutX(230);
        webButton.setLayoutY(60);
        webButton.setPrefSize(150, 150);
        //给网络版按钮绑定鼠标点击事件
        webButton.setOnAction(event -> {
            UserLoginStage userLoginStage = new UserLoginStage();
            userLoginStage.show();
            //关闭主窗口
            stage.close();
            //把这个stage放在最下面
//            stage.toBack();
        });

        //将按钮放到画板上
        pane.getChildren().addAll(singleButton, webButton);
        //创建场景对象，并将画板放到场景上
        Scene scene = new Scene(pane, 400, 300);
        //将场景设置到舞台上
        stage.setScene(scene);
        //展示大舞台
        stage.show();
    }
}
