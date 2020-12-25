package com.wyz.gobang;

import com.wyz.gobang.utils.Global;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * <p>
 *     网络版登录界面
 * </p>
 *
 * @author wuyuzi
 * @since 2020/12/16
 */
public class LoginStage extends Stage {
    public LoginStage() {
        //获取画板对象
        Pane pane = new Pane();

        //创建Label对象
        Label ipLabel = new Label("我的IP：");
        ipLabel.setLayoutX(50);
        ipLabel.setLayoutY(40);
        Label portLabel = new Label("我的端口：");
        portLabel.setLayoutX(50);
        portLabel.setLayoutY(80);
        Label oipLabel = new Label("对方IP：");
        oipLabel.setLayoutX(50);
        oipLabel.setLayoutY(120);
        Label oportLabel = new Label("对方端口：");
        oportLabel.setLayoutX(50);
        oportLabel.setLayoutY(160);
        //创建文本框对象
        TextField myIpText = new TextField();
        myIpText.setLayoutX(120);
        myIpText.setLayoutY(35);
        TextField myPortText = new TextField();
        myPortText.setLayoutX(120);
        myPortText.setLayoutY(75);
        TextField oIpText = new TextField();
        oIpText.setLayoutX(120);
        oIpText.setLayoutY(115);
        TextField oppoPortText = new TextField();
        oppoPortText.setLayoutX(120);
        oppoPortText.setLayoutY(155);
        //创建确定按钮和取消按钮对象
        Button startButton = new Button("确定");
        startButton.setLayoutX(120);
        startButton.setLayoutY(250);
        startButton.setPrefSize(70, 30);
        //确定的进入网络对战的逻辑
        startButton.setOnMouseClicked(event -> {
            Global.myIp = myIpText.getText();
            Global.myPort = myPortText.getText();
            Global.oppoIp = oIpText.getText();
            Global.oppoPort = oppoPortText.getText();
            WebStage webStage = new WebStage();
            webStage.show();
            LoginStage.this.close();
        });

        Button closeButton = new Button("取消");
        closeButton.setLayoutX(250);
        closeButton.setLayoutY(250);
        closeButton.setPrefSize(70, 30);
        //给取消按钮绑定鼠标点击事件
        closeButton.setOnAction(event -> LoginStage.this.close());

        //将文本框对象、Label对象,按钮对象放到画板上
        pane.getChildren().addAll(
                myIpText, myPortText, oIpText, oppoPortText,
                ipLabel, portLabel, oipLabel, oportLabel,
                startButton,closeButton);
        //创建场景对象，并将画板放到场景上
        Scene scene = new Scene(pane, 400, 350);
        //将场景设置到舞台上
        this.setScene(scene);
    }
}
