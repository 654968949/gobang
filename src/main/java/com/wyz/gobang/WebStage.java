package com.wyz.gobang;

import com.wyz.gobang.dao.UserDao;
import com.wyz.gobang.utils.*;
import com.wyz.gobang.dao.RecordDao;
import com.wyz.gobang.entity.Record;
import com.wyz.gobang.entity.User;
import com.wyz.gobang.message.ChessMessage;
import com.wyz.gobang.message.HeartMessage;
import com.wyz.gobang.message.ReceiveRegretMessage;
import com.wyz.gobang.message.SendRegretMessage;
import com.wyz.gobang.utils.*;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * <p>
 *     网络版五子棋
 * </p>
 *
 * @author wuyuzi
 * @since 2020/12/16
 */
public class WebStage extends Stage {
    /**
     * 棋盘边线距边框的距离
     */
    private final int margin = 60;
    /**
     * 棋盘中线与线之间的距离
     */
    private final int padding = 40;
    /**
     * 棋盘中水平线和垂直线的条数
     */
    private final int lineCount = 15;
    /**
     * 画板的宽
     */
    private final int width = 680;
    /**
     * 画板的高
     */
    private final int height = 730;
    /**
     * 棋子的颜色true为黑色
     */
    private Boolean isBlack = true;
    /**
     * 装棋子的容器
     */
    private List<Chess> chessmen = new ArrayList<>();
    /**
     * 画板对象
     */
    private Pane pane = null;
    /**
     * 判断胜利的计数器
     */
    private int isWinCount = 1;
    private Stage primaryStage = null;
    /**
     * 对战标识符 -1 还未开始 0 还未分出胜负 1 你赢了  2 对手赢了（既你输了）
     */
    private int battleFlag = BattleStatus.NOTOK.getValue();
    /**
     * 我是否已经准备好 true-准备好了
     */
    private boolean myReady = false;
    /**
     * 对手否已经准备好 true-准备好了
     */
    private boolean oppoReady = false;
    /**
     * 网络对战中是否该你下
     */
    private boolean canPlay = true;
    /**
     * 游戏开始时间
     */
    private AtomicLong startTime = new AtomicLong(0L);
    /**
     * 网络连接是否成功的标志位
     */
    private boolean isConnected = false;
    /**
     * 准备按钮和展示准备信息的label
     */
    private Button readyButton = null;
    private Label readyLabel = null;

    private RecordDao recordDao = new RecordDao();
    private UserDao userDao = new UserDao();



    public WebStage() {
        this.primaryStage = this;
        //创建画板
        pane = getPane();
        //落子
        moveChess();
        //创建场景
        Scene scene = new Scene(pane,width,height);
        //将场景放到舞台上
        primaryStage.setScene(scene);
        primaryStage.setTitle("五子棋("+ Global.myAccount+")");

        //给舞台绑定点击X关闭事件
        primaryStage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(primaryStage);
            alert.setContentText("你确定要返回主界面吗？");
            Optional<ButtonType> optional = alert.showAndWait();

            if (ButtonType.OK == optional.get()) {
                WebStage.this.close();
            } else {
                //不退出
                event.consume();
            }
        });
        //展示舞台
        primaryStage.show();
    }
    /**
     * 落子的逻辑 （可以下，代表已经准备好了）
     */
    private void moveChess() {
        //给画板绑定鼠标点击事件
        pane.setOnMouseClicked(event -> {
            //游戏如果是赢的状态 或者 不该你下 或者 你输了，就不能再落子
            if (battleFlag == BattleStatus.WIN.getValue() || !canPlay || battleFlag == BattleStatus.LOST.getValue()) {
                return;
            }
            //如果自己没准备好或者对方没准备好，不能下棋
            if (!myReady || !oppoReady) {
                return;
            }
            double x = event.getX();
            double y = event.getY();
            //判断是否出棋盘
            if (x < margin || x > width - margin) {
                System.out.println("超出");
            }
            else if (y < margin || y > margin + (lineCount - 1) * padding ) {
                System.out.println("超出");
            }
            //落子实现黑白交替
            else {
                int newX = ((int)x - margin + padding/2)/padding;
                int newY = ((int)y - margin + padding/2)/padding;

                //判断该位置是否已经存在该棋子
                if (isHasCell(newX,newY)) {
                    System.out.println("已存在棋子");
                    return;
                }
                //确定账号是否是白方还是黑方，如果下棋的时候放棋子的list集合没有棋子则表示我们是黑方,
                if (chessmen.size() == 0) {
                    Global.blackAccount = Global.myAccount;
                    Global.whiteAccount = Global.oppoAccount;
                    //设定对局时间
                    Global.chessTime = LocalTime.now().toString();
                }
                //如果里面有一颗棋子，则表示我们是白方
                if (chessmen.size() == 1) {
                    Global.whiteAccount = Global.myAccount;
                    Global.blackAccount = Global.oppoAccount;
                }

                Circle circle = null;
                Chess chess = null;
                if (isBlack) {
                    circle = new Circle(newX*padding + margin,newY*padding + margin,15, Color.BLACK);
                    isBlack = false;
                    chess = new Chess(newX,newY,Color.BLACK);
                }
                else {
                    circle = new Circle(newX*padding + margin,newY*padding + margin,15, Color.WHITE);
                    isBlack = true;
                    chess = new Chess(newX,newY,Color.WHITE);
                }
                //将棋子放到容器中
                chessmen.add(chess);
                pane.getChildren().add(circle);
                //默认双方连接后都可以先落子，落子的就是黑方，如果我落子了就传数据
                try {
                    Socket s = new Socket(Global.getOppoIp(), Integer.parseInt(Global.getOppoPort()));
                    ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                    oos.writeObject(new ChessMessage(newX,newY,!isBlack));
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR,"连接对手出错！对手估计掉线了^-^");
                    alert.show();
                    return;
                }
                canPlay = false;
                System.out.println("能不能下棋" + canPlay);
                List<Chess> lists = new ArrayList<>();
                //检查棋子
                if (isWin(chess,lists)) {
                    lists.add(chess);
                    //TODO 为什么捕捉到的棋子还是有问题
                    System.out.println("捕捉到的棋子数量:"+lists.size()+"个");
                    //找对应的Circle
                    List<Node> newCircles = pane.getChildren().stream().filter(node -> {
                        //如果是node是Circle并且该circle是我们的赢棋子返回true表示收集该元素
                        if (node instanceof Circle) {
                            double nodeX = ((Circle) node).getCenterX();
                            double nodeY = ((Circle) node).getCenterY();
                            for(Chess ch : lists) {
                                if (nodeX == (ch.getX()*padding + margin) && (nodeY == (ch.getY()*padding + margin))) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }).collect(Collectors.toList());
                    //实现五子连珠的动画
                    //在判断五子棋赢的时候就记录下赢的五子棋的五颗棋子信息x和y，然后用这五颗棋子在画板list里面找到对应的Circle。放到集合，然后放到我们的动画函数里
                    GobangAnimationUtils.startAnimation(newCircles);

                    //如果是输或者赢，对局倒计时就停
                    StartTime.getBattleTimeline().stop();
                    //把双方准备的flag都变成false，如果再来一局应由玩家主动点准备
                    myReady = false;
                    oppoReady = false;
                    readyButton.setText("未准备");
                    readyLabel.setText("对手未准备");
                    battleFlag = BattleStatus.WIN.getValue();
                    //添加五子连珠的动画
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("对局结果");
                    alert.setHeaderText("朋友");
                    alert.setContentText("你胜利了!");
                    alert.initOwner(primaryStage);
                    alert.showAndWait();
                    canPlay = false;
                    //增加一条对战记录，输的一方就不加了
                    Record record = new Record();
                    record.setBlack(Global.blackAccount);
                    record.setWhite(Global.whiteAccount);
                    record.setChessTime(Global.chessTime);
                    if (isBlack) {
                        record.setResult(Global.WHITE_WIN);
                    } else {
                        record.setResult(Global.BLACK_WIN);
                    }
                    //更新user对应的账户的记录，score+1，totalnums+1，winnums+1，如果是输棋lostnums+1
                    User oldUser = userDao.getOneUser(Global.myAccount);
                    //赢了一方积分+2
                    oldUser.setScore(oldUser.getScore() +2);
                    oldUser.setTotalNums(oldUser.getTotalNums()+1);
                    oldUser.setWinNums(oldUser.getWinNums()+1);

                    //开启事务不自动提交
                    Connection conn = DBUtils.getConnection();
                    try {
                        conn.setAutoCommit(false);
                        recordDao.addOneRecord(record);
                        userDao.update(oldUser);
                        conn.commit();
                    } catch (SQLException e) {
                        try {
                            //出错就回滚
                            conn.rollback();
                        } catch (SQLException sqlException) {
                            sqlException.printStackTrace();
                        }
                        e.printStackTrace();
                    } finally {
                        try {
                            conn.setAutoCommit(true);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    /**
     *初始化画板
     */
    private Pane getPane() {
        Pane pane = new Pane();
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(219,181,95),null,null)));

        //将线条放到画板
        int increment = 0;
        for (int i = 0; i < lineCount ; i++) {
            Line verticalLine = new Line(margin,increment + margin,width - margin,increment + margin);
            Line crosswiseLine = new Line(increment + margin,margin,increment + margin,width - margin);

            //把线条放画板
            pane.getChildren().add(verticalLine);
            pane.getChildren().add(crosswiseLine);
            increment += padding;
        }
        //创建按钮对象
        readyButton = getNewButton("未准备",52,650,40,90);
        Button regretButton = getNewButton("悔棋",200,650,40,90);
        Button saveButton = getNewButton("保存棋谱",380,650,40,90);
        Button exitButton = getNewButton("退出游戏",537,650,40,90);

        //倒计时
        Label text = new Label("对局时间 :");
        text.setPrefSize(100,40);
        text.setLayoutX(465);
        text.setLayoutY(0);
        text.setVisible(false);
        text.setFont(Font.font(null, FontPosture.REGULAR,20.0));
        //准备按钮
        readyLabel = new Label();
        readyLabel.setLayoutX(300);
        readyLabel.setLayoutY(10);
        readyLabel.setFont(Font.font(null,FontPosture.REGULAR,25));
        //对局时间label
        Label battleTimeLabel = new Label();
        battleTimeLabel.setPrefSize(100,40);
        battleTimeLabel.setLayoutX(565);
        battleTimeLabel.setLayoutY(0);
        battleTimeLabel.setFont(Font.font(null, FontPosture.ITALIC,20.0));
        battleTimeLabel.setVisible(false);
        //当前时间label
        Label nowTimeLabel = new Label();
        nowTimeLabel.setPrefSize(200,40);
        nowTimeLabel.setLayoutX(40);
        nowTimeLabel.setLayoutY(0);
        //对局倒计时时间显示
        StartTime.startTime(nowTimeLabel);

        /*
         * 准备按钮游戏逻辑
         */
        readyButton.setOnMouseClicked(event -> {
            myReady = !myReady;
            if (myReady) {
                readyButton.setText("已准备");
            } else {
                readyButton.setText("未准备");
            }
        });
        /*
         * 退出逻辑
         */
        exitButton.setOnMouseClicked(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(primaryStage);
            alert.setContentText("你确定要退出吗？");
            Optional<ButtonType> optional = alert.showAndWait();
            if (ButtonType.OK == optional.get()) {
                System.exit(0);
            } else {
                //不退出
                event.consume();
            }
        });
        /*
         *  保存棋谱逻辑
         */
        saveButton.setOnAction(event -> {
            //如果还没有棋局结束不能保存
            if ((battleFlag == BattleStatus.ING.getValue() || battleFlag == BattleStatus.NOTOK.getValue())) {
                return;
            }
            FileChooser fl = new FileChooser();
            fl.setTitle("保存棋谱");
            fl.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files","*.txt"));
            //显示一个新的文件保存对话框,放置舞台
            File file = fl.showSaveDialog(primaryStage);
            if (Objects.isNull(file)) {
                event.consume();
                return;
            }
            //创建高效字符输出流对象
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(file));
                for (Chess chess : chessmen) {
                    bw.write(chess.getX() + "," + chess.getY() + ","
                            + (chess.getColor().equals(Color.WHITE) ? "white" : "black"));//txt里存white和black
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (!Objects.isNull(bw)) {
                        bw.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        /*
            悔棋逻辑
         */
        regretButton.setOnMouseClicked(event -> {
            if (chessmen.size() <= 0 || (battleFlag == BattleStatus.WIN.getValue() || battleFlag == BattleStatus.LOST.getValue())) {
                return;
            }
            //先向对手发送一个悔棋请求
            Socket socket = null;
            try {
                socket = new Socket(Global.getOppoIp(), Integer.parseInt(Global.getOppoPort()));
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                SendRegretMessage rm = new SendRegretMessage();
                rm.setSendRegret(true);
                oos.writeObject(rm);
            } catch (IOException e) {
                System.out.println("发送悔棋请求失败");
            }
        });

        /*
            新开一个线程监控对手发来的棋子数据和心跳请求
         */
        new Thread(() -> {
            ServerSocket ss = null;
            try {
                ss = new ServerSocket(Integer.parseInt(Global.myPort));
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            //一直监控对手发的数据过来
            while (true) {
                try (Socket s = ss.accept()){
                    ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                    Object message = ois.readObject();
                    if (Objects.isNull(message)) {
                        continue;//为空的话接收下一次连接
                    }
                    //判断是否是心跳消息
                    if (message instanceof HeartMessage) {
//                        System.out.println("连接对手成功！");
                        isConnected = true;
                        //多态向下转型
                        oppoReady = ((HeartMessage)message).isReady();
                        Global.oppoAccount = ((HeartMessage)message).getMyAcount();
                        Platform.runLater(() -> {
                            if (isConnected && oppoReady) {
                                readyLabel.setText("对手已准备");
                            } else {
                                readyLabel.setText("对手未准备");
                            }
                        });
                    }
                    //对方发来的悔棋消息
                    else if (message instanceof SendRegretMessage) {
                        System.out.println("接收对方发来的悔棋请求");
                        Platform.runLater(() -> {
                            try {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.initOwner(primaryStage);
                                alert.setContentText("对方请求悔棋，你同意吗？");
                                Optional<ButtonType> optional = alert.showAndWait();
                                //同意悔棋
                                if (ButtonType.OK == optional.get()) {
                                    Socket socket = new Socket(Global.getOppoIp(), Integer.parseInt(Global.getOppoPort()));
                                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                                    oos.writeObject(new ReceiveRegretMessage(ReceiveRegretMessage.YES));
                                    //同意悔棋操作的那一方
                                    if (canPlay) {
                                        System.out.println("同意悔棋，并且减一");
                                        //如果是我们正在下过程中对方悔棋，减一
                                        chessmen.remove(chessmen.size()-1);
                                        pane.getChildren().remove(pane.getChildren().size()-1);
                                        isBlack = !isBlack;
                                    } else {
                                        System.out.println("同意悔棋，并且减二");
                                        //如果不是我们正在下的过程，减二
                                        chessmen.remove(chessmen.size()-1);
                                        chessmen.remove(chessmen.size()-1);
                                        pane.getChildren().remove(pane.getChildren().size()-1);
                                        pane.getChildren().remove(pane.getChildren().size()-1);
                                    }
                                    //别人悔棋就是我们不能下
                                    canPlay = false;
                                } else {
                                    //我方不同意悔棋
                                    Socket socket = new Socket(Global.getOppoIp(), Integer.parseInt(Global.getOppoPort()));
                                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                                    oos.writeObject(new ReceiveRegretMessage(ReceiveRegretMessage.NO));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    //被悔棋方方发来的确认悔棋消息
                    else if (message instanceof ReceiveRegretMessage) {
                        System.out.println("接收悔棋确认消息");
                        if (((ReceiveRegretMessage)message).getReceiveRegret() == ReceiveRegretMessage.YES) {
                            if (!canPlay) {
                                System.out.println("悔棋确认，并且减一");
                                //如果是在对方还在下的过程中悔棋，悔一颗就ok了
                                chessmen.remove(chessmen.size()-1);
                                Platform.runLater(() -> {
                                    pane.getChildren().remove(pane.getChildren().size()-1);
                                });
                                isBlack = !isBlack;
                            } else {
                                System.out.println("悔棋确认，并且减二");
                                //如果是在自己还在下的过程中悔棋，悔两颗
                                chessmen.remove(chessmen.size()-1);
                                chessmen.remove(chessmen.size() -1);
                                Platform.runLater(() -> {
                                    pane.getChildren().remove(pane.getChildren().size() - 1);
                                    pane.getChildren().remove(pane.getChildren().size() - 1);
                                });
                            }
                            //悔棋就是要自己可以下
                            canPlay = true;
                        } else if (((ReceiveRegretMessage)message).getReceiveRegret() == ReceiveRegretMessage.NO){
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.initOwner(primaryStage);
                                alert.setContentText("对方不同意你的悔棋要求");
                                alert.showAndWait();
                            });
                        }
                    }
                    else if (message instanceof ChessMessage){
                        updateUi((ChessMessage)message);
                    }
//                    System.out.println(chessMessage.toString());
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        //用java定时任务开启对 1.发送心跳检测网络连通 2.对局的准备和未准备 3.对局计时器开始和结束
        ScheduledExecutorService se = new ScheduledThreadPoolExecutor(5, new ThreadFactory() {
            private final AtomicLong mCount = new AtomicLong(1);
            @Override
            public Thread newThread(Runnable r) {
                Thread myThread = new Thread(r);
                myThread.setName("_wyzScheduleServiceThread_" + mCount.getAndIncrement());
                return myThread;
            }
        });
        /*
            定义任务具体的内容
         */
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //心跳任务
                isConnected = NetStatusUtil.monitorSocket(myReady);
//                System.out.println("网络连接状态"+isConnected);
                //如果网络连接都是false 那么
                if (!isConnected) {
                    oppoReady = false;
                    Platform.runLater(() -> readyLabel.setText("对手未在线上"));
                    return;
                }
                //如果还在对局中就结束该方法
                if (battleFlag == BattleStatus.ING.getValue()) {
                    return;
                }
                //如果是第一局游戏,并且双方都准备就只用调用计时器就可以了
                if (battleFlag == BattleStatus.NOTOK.getValue() && myReady && oppoReady) {
                    //设置对局倒计时Label可见
                    text.setVisible(true);
                    battleTimeLabel.setVisible(true);
                    //置为对战中
                    battleFlag = BattleStatus.ING.getValue();
                    //开始倒计时
                    startTime.set(System.currentTimeMillis());
                    StartTime.startBattleTime(battleTimeLabel,startTime);
                }
                //在游戏结束的之后，A 和 B不都点准备，就不用倒计时开始，因为大家可以观看一会儿棋局
                if ((!myReady || !oppoReady) && (battleFlag == BattleStatus.WIN.getValue() || battleFlag == BattleStatus.LOST.getValue())){
                    return;
                }
                //如果游戏结束，双方又都准备了，就要重新棋盘，重新开始
                if (myReady && oppoReady && (battleFlag == BattleStatus.WIN.getValue() || battleFlag == BattleStatus.LOST.getValue())) {
                    //重置倒计时
                    startTime.set(System.currentTimeMillis());
                    StartTime.getBattleTimeline().play();
                    //修改对局状态
                    battleFlag = BattleStatus.ING.getValue();
                    isBlack = true;
                    //将数组容器中的棋子置空
                    chessmen.clear();
                    //该为都可以下棋，谁先下就是黑方
                    canPlay = true;

                    Platform.runLater(() ->  {
                        //清空画板上的棋子
                        pane.getChildren().removeIf(node -> node instanceof Circle);
                    });
                }
            }
        };
        //执行任务
        se.scheduleAtFixedRate(timerTask,0,500,TimeUnit.MILLISECONDS);

        //把按钮放在画板上
        pane.getChildren().addAll(
                readyButton,exitButton,regretButton,saveButton,
                battleTimeLabel,nowTimeLabel,text,readyLabel);
        return pane;
    }

    /**
     * 创建按钮
     */
    private Button getNewButton(String text,int startX,int startY,int height,int width) {
        Button button = new Button(text);
        button.setLayoutX(startX);
        button.setLayoutY(startY);
        button.setPrefSize(width,height);
        return button;
    }

    /**
     * 检查打谱棋盘是否还有棋子
     * @param pane 当前棋盘
     * @return true 还有棋子  false没有棋子
     */
    private boolean checkChess(Pane pane) {
        boolean flag = false;
        for ( Node node : pane.getChildren()) {
            if (node instanceof Circle) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 该坐标是否有棋子
     */
    private Boolean isHasCell(int x,int y) {
        for (Chess chess : chessmen) {
            if (Objects.isNull(chess)) {
                return false;
            }
            if (chess.getX() == x && chess.getY() == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否胜利
     */
    private boolean isWin(Chess chess,List<Chess> animationChess) {
        boolean isWin = false;
        //向左判断是否连续五个相同颜色
        int x = chess.getX();
        int y = chess.getY();
        for (int i = x - 1;i >= 0 && i >= x - 4 ;i--) {
            Chess _chess = getChess(i,y);
            if (_chess != null && _chess.getColor().equals(chess.getColor())) {
                animationChess.add(_chess);
                isWinCount++;
            }
            else {
                break;
            }
        }

        //向右判断是否五个相同颜色
        for (int i = x + 1; i <= lineCount && i <= x + 4;i++) {
            if (isWin) {
                break;
            }
            Chess _chess = getChess(i,y);
            if (_chess != null && _chess.getColor().equals(chess.getColor())) {
                animationChess.add(_chess);
                isWinCount++;
            }
            else {
                break;
            }
        }
        //如果向左和向右的其中某个方向有连续值大于5的。就赢了
        isWin = isHasFive(isWinCount, isWin);
        if (!isWin) {animationChess.clear();}
        //向上
        for (int i = y - 1;i >= y - 4 && i >= 0;i--) {
            if (isWin) {
                break;
            }
            Chess _chess = getChess(x,i);
            if (_chess != null && _chess.getColor().equals(chess.getColor())) {
                animationChess.add(_chess);
                isWinCount++;
            }
            else {
                break;
            }
        }
        //向下
        for (int i = y + 1; i <= y + 4; i++) {
            if (isWin) {
                break;
            }
            Chess _chess = getChess(x,i);
            if (_chess != null && _chess.getColor().equals(chess.getColor())) {
                animationChess.add(_chess);
                isWinCount++;
            }
            else {
                break;
            }
        }
        isWin = isHasFive(isWinCount, isWin);
        if (!isWin) {animationChess.clear();}
        //左斜上判断,x-1,y-1
        for (int i = x - 1, j = y - 1; i >= 0 && j >= 0 && i >= x - 4 && j >= y - 4; i--, j--) {
            if (isWin) {
                break;
            }
            Chess _chess = getChess(i, j);
            if (_chess != null && _chess.getColor().equals(chess.getColor())) {
                animationChess.add(_chess);
                isWinCount++;
            } else {
                break;
            }
        }
        //右斜下x+1,y+1
        for (int i = x + 1, j = y + 1; i <= lineCount && j <= lineCount && i <= x + 4 && j <= y + 4; i++, j++) {
            if (isWin) {
                break;
            }
            Chess _chess = getChess(i, j);
            if (_chess != null && _chess.getColor().equals(chess.getColor())) {
                animationChess.add(_chess);
                isWinCount++;
            } else {
                break;
            }
        }
        isWin = isHasFive(isWinCount, isWin);
        if (!isWin) {animationChess.clear();}
        //右斜上,x+1,y-1
        for (int i = x + 1, j = y - 1; i <= lineCount && j >= 0 && i <= x + 4 && j >= y - 4; i++, j--) {
            if (isWin) {
                break;
            }
            Chess _chess = getChess(i,j);
            if (_chess != null && _chess.getColor().equals(chess.getColor())) {
                animationChess.add(_chess);
                isWinCount++;
            }
            else {
                break;
            }
        }
        //左斜下x-1,y+1
        for (int i = x - 1, j = y + 1; i >= 0 && j <= lineCount && i >= x - 4 && j <= y + 4; i--, j++) {
            if (isWin) {
                break;
            }
            Chess _chess = getChess(i, j);
            if (_chess != null && _chess.getColor().equals(chess.getColor())) {
                animationChess.add(_chess);
                isWinCount++;
            } else {
                break;
            }
        }
        isWin = isHasFive(isWinCount, isWin);
        if (!isWin) {animationChess.clear();}
        return isWin;
    }

    /**
     * 是否有五子
     */
    private boolean isHasFive(int count,boolean winFlag) {
        if (winFlag) {
            return true;
        }
        if (count >= 5) {
            isWinCount = 1;
            return true;
        }
        //五子连珠没有判断成功的话，计数器归为一
        isWinCount = 1;
        return false;
    }
    /**
     * 获取某位置上的棋子
     */
    private Chess getChess(int x,int y ) {
        for (Chess chess : chessmen) {
            if (Objects.isNull(chess)) {
                return null;
            }
            if (chess.getX() == x && chess.getY() == y) {
                return chess;
            }

        }
        return null;
    }

    private void updateUi(ChessMessage chessMessage) {
        Platform.runLater(() -> {
            int newX = chessMessage.getX();
            int newY = chessMessage.getY();
            System.out.println("更新对手的下棋");
            Circle circle = null;
            Chess chess = null;
            if (chessMessage.isBlack) {
                circle = new Circle(newX * padding + margin, newY * padding + margin, 15, Color.BLACK);
                isBlack = false;
                chess = new Chess(newX, newY, Color.BLACK);
            } else {
                circle = new Circle(newX * padding + margin, newY * padding + margin, 15, Color.WHITE);
                isBlack = true;
                chess = new Chess(newX, newY, Color.WHITE);
            }
            //将棋子放到容器中
            chessmen.add(chess);
            pane.getChildren().add(circle);
            //在对手下完棋的时候判断是否输了
            Chess oppoChess = new Chess(newX,newY,(chessMessage.isBlack ? Color.BLACK : Color.WHITE));
            //判断对手是否五子连珠
            List<Chess> lists = new ArrayList<>();
            battleFlag = (isWin(oppoChess,lists)) ? BattleStatus.LOST.getValue() : BattleStatus.ING.getValue();

            if (battleFlag == BattleStatus.LOST.getValue()) {
                //五子连珠动画
                lists.add(oppoChess);
                //在画板元素中找对应的Circle
                List<Node> newCircles = pane.getChildren().stream().filter(node -> {
                    //如果是node是Circle并且该circle是我们的赢棋子返回true表示收集该元素
                    if (node instanceof Circle) {
                        double nodeX = ((Circle) node).getCenterX();
                        double nodeY = ((Circle) node).getCenterY();
                        System.out.println(nodeX+"sss"+nodeY);
                        for(Chess ch : lists) {
                            if (nodeX == (ch.getX()*padding + margin) && (nodeY == (ch.getY()*padding + margin))) {
                                return true;
                            }
                        }
                    }
                    return false;
                }).collect(Collectors.toList());
                System.out.println(newCircles.size());
                //实现五子连珠的动画
                //在判断五子棋赢的时候就记录下赢的五子棋的五颗棋子信息x和y，然后用这五颗棋子在画板list里面找到对应的Circle。放到集合，然后放到我们的动画函数里
                GobangAnimationUtils.startAnimation(newCircles);

                //如果是输，对局倒计时就停
                StartTime.getBattleTimeline().stop();
                //把双方准备的flag都变成false，如果再来一局应由玩家主动点准备
                myReady = false;
                oppoReady = false;
                readyButton.setText("未准备");
                readyLabel.setText("对手未准备");

                //更新user对应的账户的记录，score+1，totalnums+1，winnums+1，如果是输棋lostnums+1
                User oldUser = userDao.getOneUser(Global.myAccount);
                //输了积分+1
                oldUser.setScore(oldUser.getScore() +1);
                oldUser.setTotalNums(oldUser.getTotalNums() +1);
                oldUser.setLostNums(oldUser.getLostNums() +1);
                userDao.update(oldUser);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("对局结果");
                alert.setHeaderText("很遗憾");
                alert.setContentText("你输了!");
                alert.initOwner(primaryStage);
                alert.showAndWait();
                return;//棋局结束
            }
            canPlay = true;
        });

    }
}
