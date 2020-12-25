package com.wyz.gobang;

import com.wyz.gobang.dao.UserDao;
import com.wyz.gobang.entity.User;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * <p>
 *     注册界面
 * </p>
 *
 * @author wuyuzi
 * @since 2020/12/22
 */
public class RegisterStage extends Stage {
    private UserDao userDao = new UserDao();
    /**
     * 构造函数里初始化画板
     */
    public RegisterStage() {
        //获取画板对象
        Pane pane = new Pane();

        //创建Label对象
        Label usernameLabel = new Label("用户名：");
        usernameLabel.setLayoutX(50);
        usernameLabel.setLayoutY(40);
        Label passwordLabel = new Label("密码：");
        passwordLabel.setLayoutX(50);
        passwordLabel.setLayoutY(80);
        Label passwordLabel2 = new Label("确认密码：");
        passwordLabel2.setLayoutX(50);
        passwordLabel2.setLayoutY(120);
        //创建文本框对象
        TextField usernameText = new TextField();
        usernameText.setLayoutX(120);
        usernameText.setLayoutY(35);
        PasswordField passwordText = new PasswordField();
        passwordText.setLayoutX(120);
        passwordText.setLayoutY(75);
        PasswordField passwordText2 = new PasswordField();
        passwordText2.setLayoutX(120);
        passwordText2.setLayoutY(115);


        //注册按钮
        Button Buttoregistern = new Button("注册");
        Buttoregistern.setLayoutX(250);
        Buttoregistern.setLayoutY(250);
        Buttoregistern.setPrefSize(70, 30);
        //注册逻辑

        Buttoregistern.setOnAction(event ->
                {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.initOwner(this);
                    if ("".equals(usernameText.getText().trim()) || "".equals(passwordText.getText().trim())
                        || "".equals(passwordText2.getText().trim())) {
                        Platform.runLater(() -> {
                            alert.setContentText("账号密码不能为空！");
                            alert.showAndWait();
                        });
                    }
                    //登陆判断账号是否重复
                    else if (userDao.isExist(usernameText.getText().trim())) {
                        Platform.runLater(() -> {
                            alert.setContentText("账号已存在");
                            alert.showAndWait();
                        });
                    }
                    else if (!passwordText.getText().equals(passwordText2.getText())) {
                        Platform.runLater(() -> {
                            alert.setContentText("两次输入的密码不一致！");
                            alert.showAndWait();
                        });
                    }
                    else {
                        User user = new User();
                        user.setAccount(usernameText.getText());
                        user.setPassword(passwordText.getText());
                        boolean register = userDao.add(user);
                        if (register) {
                            Platform.runLater(() -> {
                                alert.setContentText("注册成功！");
                                alert.show();
                            });
                        } else {
                            Platform.runLater(() -> {
                                alert.setContentText("注册失败！");
                                alert.show();
                            });
                        }
                        //注册成功，跳转到登陆界面,关闭注册界面
                        UserLoginStage userLoginStage = new UserLoginStage();
                        userLoginStage.show();
                        RegisterStage.this.close();
                    }
                }
        );
        //将文本框对象、Label对象,按钮对象放到画板上
        pane.getChildren().addAll(usernameLabel,passwordLabel,usernameText,passwordText,passwordLabel2,passwordText2,Buttoregistern);
        //创建场景对象，并将画板放到场景上
        Scene scene = new Scene(pane, 400, 350);
        //将场景设置到舞台上
        this.setScene(scene);
    }
}
