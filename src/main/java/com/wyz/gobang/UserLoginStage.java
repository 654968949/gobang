package com.wyz.gobang;

import com.wyz.gobang.dao.UserDao;
import com.wyz.gobang.entity.User;
import com.wyz.gobang.utils.Global;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * <p>
 *     用户登录界面UI
 * </p>
 *
 * @author wuyuzi
 * @since 2020/12/22
 */
public class UserLoginStage extends Stage {
    private UserDao userDao = new UserDao();
    /**
     * 构造函数里初始化画板
     */
    public UserLoginStage() {
        //获取画板对象
        Pane pane = new Pane();

        //创建Label对象
        Label usernameLabel = new Label("用户名：");
        usernameLabel.setLayoutX(50);
        usernameLabel.setLayoutY(40);
        Label passwordLabel = new Label("密码：");
        passwordLabel.setLayoutX(50);
        passwordLabel.setLayoutY(80);
        //创建文本框对象
        TextField usernameText = new TextField();
        usernameText.setLayoutX(120);
        usernameText.setLayoutY(35);
        PasswordField passwordText = new PasswordField();
        passwordText.setLayoutX(120);
        passwordText.setLayoutY(75);

        //登陆按钮
        Button loginButton = new Button("登陆");
        loginButton.setLayoutX(120);
        loginButton.setLayoutY(250);
        loginButton.setPrefSize(70, 30);
        //登陆逻辑
        loginButton.setOnMouseClicked(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initOwner(this);
            //判断文本框是否为空
            String str = usernameText.getText().trim();
            String str2 = passwordText.getText().trim();
            if ("".equals(str)|| "".equals(str2)) {
                Platform.runLater(() -> {
                    alert.setContentText("账号密码不能为空");
                    alert.showAndWait();
                });
                return;
            }
            //判断账号密码是否正确
            User user = userDao.getOneUser(str);
            if (user == null) {
                Platform.runLater(() -> {
                    alert.setContentText("不存在该用户");
                    alert.showAndWait();
                });
                return;
            }
            //密码正确
            if (user.getAccount().equals(usernameText.getText().trim())
                    && user.getPassword().equals(passwordText.getText().trim())) {
                //跳转到填写端口界面
                LoginStage loginStage = new LoginStage();
                loginStage.show();
                Global.myAccount = usernameText.getText().trim();//放到全局静态变量
                UserLoginStage.this.close();
            } else {
                Platform.runLater(() -> {
                    alert.setContentText("密码错误");
                    alert.showAndWait();
                });
            }
        });
        //注册按钮
        Button registerButton = new Button("注册");
        registerButton.setLayoutX(250);
        registerButton.setLayoutY(250);
        registerButton.setPrefSize(70, 30);
        //注册逻辑

        registerButton.setOnAction(event ->
            {
                //跳转到注册界面
                RegisterStage registerStage = new RegisterStage();
                registerStage.show();
                UserLoginStage.this.close();
            }
        );
        //将文本框对象、Label对象,按钮对象放到画板上
        pane.getChildren().addAll(usernameLabel,passwordLabel,usernameText,passwordText,registerButton,loginButton);
        //创建场景对象，并将画板放到场景上
        Scene scene = new Scene(pane, 400, 350);
        //将场景设置到舞台上
        this.setScene(scene);
    }
}
