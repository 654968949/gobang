package com.wyz.gobang;

import com.wyz.gobang.utils.GobangAnimationUtils;
import com.wyz.gobang.utils.StartTime;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
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
import javafx.stage.WindowEvent;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * <p>
 *     单机版五子棋
 * </p>
 *
 * @author wuyuzi
 * @since 2020/12/16
 */
public class SingleStage extends Stage {
    private final int margin = 60; //棋盘边线距边框的距离
    private final int padding = 40; //棋盘中线与线之间的距离
    private final int lineCount = 15; //棋盘中水平线和垂直线的条数

    private final int width = 680; //画板的宽
    private final int height = 730; //画板的高
    private Boolean isBlack = true; //棋子的颜色true为黑色
    private List<Chess> chessmen = new ArrayList<>();//装棋子的容器
    private Pane pane = null;//画板对象
    private int isWinCount = 1;//判断胜利的计数器
    private Stage primaryStage = null;
    private boolean winFlag = false;//是否五子连珠的标志 true为五子连珠
    //    private boolean printFlag = false;//打谱的标志 true为打谱中 false为没有打谱
    private boolean isBegin = false;//游戏是否开始 默认没开始
    private AtomicLong startTime = new AtomicLong(0L);//游戏开始时间

    /**
     *  通过构造方法来实现单机版的运行
     */
    public SingleStage() {
        this.primaryStage = this;
        //创建画板
        pane = getPane();
        //落子
        MoveChess();
        //创建场景
        Scene scene = new Scene(pane,width,height);
        //将场景放到舞台上
        primaryStage.setScene(scene);
        primaryStage.setTitle("五子棋");


        //给舞台绑定点击X关闭事件
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
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
            }
        });
        //展示舞台
        primaryStage.show();
    }

    /**
     * 落子的逻辑
     */
    private void MoveChess() {
        //给画板绑定鼠标点击事件
        pane.setOnMouseClicked(event -> {
            //游戏如果是赢的状态，就不能再落子
            if (winFlag) {
                return;
            }
            //游戏没开始，不能落子
            if (!isBegin) {
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
                //利用了一个int会丢失小数点后面的数的原理，得到与想要坐标相近的x坐标，y坐标
                int _x = ((int)x - margin + padding/2)/padding;
                int _y = ((int)y - margin + padding/2)/padding;

                //判断该位置是否已经存在该棋子
                if (isHasCell(_x,_y)) {
                    System.out.println("已存在棋子");
                    return;
                }
                Circle circle = null;
                Chess chess = null;
                if (isBlack) {
                    circle = new Circle(_x*padding + margin,_y*padding + margin,15, Color.BLACK);
                    isBlack = false;
                    chess = new Chess(_x,_y,Color.BLACK);
                }
                else {
                    circle = new Circle(_x*padding + margin,_y*padding + margin,15, Color.WHITE);
                    isBlack = true;
                    chess = new Chess(_x,_y,Color.WHITE);
                }
                //将棋子放到容器中
                chessmen.add(chess);
                pane.getChildren().add(circle);
                List<Chess> lists = new ArrayList<>();
                //检查棋子
                if (isWin(chess,lists)) {
                    lists.add(chess);
                    //找对应的Circle
                    List<Node> newCircles = pane.getChildren().stream().filter(node -> {
                        //如果是node是Circle并且该circle是我们的赢棋子返回true表示收集该元素
                        if (node instanceof Circle) {
                            double nodeX = ((Circle) node).getCenterX();
                            double nodeY = ((Circle) node).getCenterY();
                            System.out.println(nodeX+"sss"+nodeY);
                            for(Chess ch : lists) {
                                 if (nodeX == (ch.getX()*padding + margin) && (nodeY == (ch.getY()*padding + margin))) {
                                     System.out.println("进入。。。");
                                     return true;
                                 }
                            }
                        }
                        return false;
                    }).collect(Collectors.toList());
                    System.out.println(newCircles.size());
                    //实现五子连珠的动画
                    //TODO 思路：在判断五子棋赢的时候就记录下赢的五子棋的五颗棋子信息x和y，然后用这五颗棋子在画板list里面找到对应的Circle。放到集合，然后放到我们的动画函数里
                    GobangAnimationUtils.startAnimation(newCircles);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("标题");
                    alert.setHeaderText("朋友");
                    alert.setContentText("你胜利了!");
                    alert.initOwner(primaryStage);
                    alert.showAndWait();
                    winFlag = true;
                    StartTime.getBattleTimeline().stop();
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
        Button startButton = getNewButton("开始游戏",52,650,40,90);
        Button nextButton = getNewButton("再来一局",149,650,40,90);
        Button regretButton = getNewButton("悔棋",246,650,40,90);
        Button saveButton = getNewButton("保存棋谱",343,650,40,90);
        Button printButton = getNewButton("打谱",440,650,40,90);
        Button exitButton = getNewButton("退出游戏",537,650,40,90);


        //初始化打谱用按钮
        Button goAheadButton = getNewButton(">",645,255,15,35);
        Button goBackButton = getNewButton("<",645,295,15,35);
        Button goExitButton = getNewButton("X",645,335,15,35);
        //倒计时
        Label text = new Label("对局时间 :");
        text.setPrefSize(100,40);
        text.setLayoutX(465);
        text.setLayoutY(0);
        text.setVisible(false);
        text.setFont(Font.font(null, FontPosture.REGULAR,20.0));
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
        //设置打谱用按钮不可见
        operationalOfButton(false,goAheadButton,goBackButton,goExitButton);
        //对局倒计时时间显示
        StartTime.startTime(nowTimeLabel);

        /*
         * 开始游戏逻辑
         */
        startButton.setOnMouseClicked(event -> {
            isBegin = true;
            //对局时间开启
            startButton.setVisible(false);
            battleTimeLabel.setVisible(true);
            text.setVisible(true);
            startTime.set(System.currentTimeMillis());
            StartTime.startBattleTime(battleTimeLabel,startTime);//倒计时开始
        });

        /*
         * 再来一局逻辑
         */
        nextButton.setOnMouseClicked(event -> {
            //如果没下棋就点再来一局直接return
            if (!checkChess(pane)) {
                return;
            }
            //如果没赢,在下的过程中，想要结束本局，先询问
            if (checkChess(pane) && !winFlag) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initOwner(primaryStage);
                alert.setContentText("本局还没结果，确定要结束吗？");
                Optional<ButtonType> optional = alert.showAndWait();
                if (ButtonType.OK == optional.get()) {
                    //重置胜利标识符
                    winFlag = false;
                    //清空画板上的棋子
                    pane.getChildren().removeIf(node -> node instanceof Circle);
                    //将数组容器中的棋子置空
                    chessmen.clear();
                    isBlack = true;
                    //重置计时器
                    startTime.set(System.currentTimeMillis());
                    StartTime.startBattleTime(battleTimeLabel,startTime);
                } else {
                    //不退出
                    event.consume();
                }
            }
            else  {
                //重置胜利标识符
                winFlag = false;
                //清空画板上的棋子
                pane.getChildren().removeIf(node -> node instanceof Circle);
                //将数组容器中的棋子置空
                chessmen.clear();
                isBlack = true;
                startTime.set(System.currentTimeMillis());
                StartTime.startBattleTime(battleTimeLabel,startTime);
            }
        });
        /*
         * 悔棋逻辑
         */
        regretButton.setOnMouseClicked(event -> {
            if (chessmen.size() <= 0 || winFlag) {
                return;
            }
            chessmen.remove(chessmen.size()-1);
            pane.getChildren().remove(pane.getChildren().size()-1);
            isBlack = !isBlack;
        });

        /**
         * 打谱逻辑
         */
        //读取打谱的文件
        //一个放打谱的棋子的List容器
        List<Chess> printList = new ArrayList<>();

        printButton.setOnMouseClicked(event -> {
            HashMap<String,Object> map = new HashMap<>();
            map.put("nextButton",nextButton);
            map.put("goAheadButton",goAheadButton);
            map.put("goBackButton",goBackButton);
            map.put("goExitButton",goExitButton);
            map.put("printButton",printButton);
            map.put("saveButton",saveButton);
            map.put("regretButton",regretButton);
            map.put("battleTimeLabel",battleTimeLabel);
            map.put("text",text);
            map.put("startButton",startButton);

            if (!checkChess(pane)) {
                loadChessFile(pane,map, printList, event);
            } else {
                //如果还在对局中，点击打谱，询问是否放弃棋局
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initOwner(primaryStage);
                alert.setContentText("是否放弃本局，打开历史棋谱打谱？");
                Optional<ButtonType> optional = alert.showAndWait();
                if (ButtonType.OK == optional.get()) {
                    //加载打谱文件，用List集合顺序存棋子
                    loadChessFile(pane, map,printList, event);
                }  else {
                    event.consume();
                }
            }
        });
        //控制棋子的计数器(原子性)
        AtomicInteger countPrint = new AtomicInteger(0);
        //打谱--前进
        goAheadButton.setOnMouseClicked(event -> {
            //判断棋子是不是最后一颗
            if (countPrint.get() > printList.size()-1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initOwner(primaryStage);
                alert.setContentText("已经是本局最后一步棋！");
                alert.show();
                return;
            }
            //得到一枚棋子
            Chess chess = printList.get(countPrint.get());
            pane.getChildren().add(new Circle(chess.getX()*padding + margin,
                    chess.getY()*padding + margin,
                    15, chess.getColor()));
            countPrint.getAndIncrement();
        });
        //打谱--后退
        goBackButton.setOnMouseClicked(event -> {
            //检查是否还有棋盘上是否还有棋子,没有就不后退了。
            if (!checkChess(pane)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initOwner(primaryStage);
                alert.setContentText("老铁，第一步还没打呢，后退不了^-^");
                alert.show();
                return;
            }
            //先清空上一步棋子
            pane.getChildren().remove(pane.getChildren().size()-1);
            //回退一个单位List容器的索引
            countPrint.decrementAndGet();

        });
        //打谱--退出
        goExitButton.setOnMouseClicked(event -> {
            //清空装打谱棋子的容器
            printList.clear();
            //清空计数器
            countPrint.getAndSet(0);
            //清空画板
            pane.getChildren().removeIf(node -> node instanceof Circle);
            operationalOfButton(false,goAheadButton,goBackButton,goExitButton);
            nextButton.setVisible(true);
            saveButton.setVisible(true);
            regretButton.setVisible(true);
            printButton.setVisible(true);
            startButton.setVisible(true);
//            //设置退出打谱状态
//            printFlag = false;
            isBegin = false;
        });

        /*
         *  保存棋谱逻辑
         */
        saveButton.setOnAction(event -> {
            if (!winFlag) {
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


        //把按钮放在画板上
        pane.getChildren().addAll(
                startButton,nextButton, regretButton, saveButton, printButton, exitButton,
                goAheadButton, goBackButton, goExitButton,
                battleTimeLabel,nowTimeLabel,text);
        return pane;
    }

    /**
     * 封装了加载文件的方法
     */
    private void loadChessFile(Pane pane, HashMap<String,Object> buttons,List<Chess> printList, MouseEvent event) {
        FileChooser fl = new FileChooser();
        fl.setTitle("选择你的打谱文件");
        fl.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files","*.txt"));
        //显示一个新的文件保存对话框,放置舞台
        File file = fl.showOpenDialog(primaryStage);
        //如果点开文件框又取消了动作
        if (Objects.isNull(file)) {
            operationalOfButton(false, (Button) buttons.get("goAheadButton"), (Button) buttons.get("goBackButton"),(Button) buttons.get("goExitButton"));
            event.consume();
        } else {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                String ch;
                while ((ch = br.readLine()) != null) {
                    Chess chess = new Chess();
                    String[] strs = ch.split(",");
                    chess.setX(Integer.parseInt(strs[0]));
                    chess.setY(Integer.parseInt(strs[1]));
                    chess.setColor(("white".equals(strs[2]) ? Color.WHITE : Color.BLACK));
                    printList.add(chess);
                }
                //重置胜利标识符
                winFlag = false;
                //清空画板上的棋子
                pane.getChildren().removeIf(node -> node instanceof Circle);
                //将数组容器中的棋子置空
                chessmen.clear();
                isBlack = true;
                //打谱组合按钮可见
                operationalOfButton(true, (Button) buttons.get("goAheadButton"),(Button)buttons.get("goBackButton"), (Button) buttons.get("goExitButton"));
                //设置其他按钮不可见
                ((Button)buttons.get("startButton")).setVisible(false);
                ((Button)buttons.get("nextButton")).setVisible(false);
                ((Button)buttons.get("regretButton")).setVisible(false);
                ((Button)buttons.get("saveButton")).setVisible(false);
                ((Button)buttons.get("printButton")).setVisible(false);
                ((Label)buttons.get("text")).setVisible(false);
                ((Label)buttons.get("battleTimeLabel")).setVisible(false);
                //暂停对局时间
                if (!Objects.isNull(StartTime.getBattleTimeline())) {
                    StartTime.getBattleTimeline().stop();
                }
                //设置对局结束
                isBegin = false;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (!Objects.isNull(br)) {
                        br.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
     *
     * @param isOpen 是否开启打谱组合按钮 true：打开 false 关闭
     */
    private void operationalOfButton(boolean isOpen,Button goAheadButton,Button goBackButton,Button goExitButton) {
        if (isOpen) {
            goAheadButton.setVisible(true);
            goBackButton.setVisible(true);
            goExitButton.setVisible(true);
        } else {
            goAheadButton.setVisible(false);
            goBackButton.setVisible(false);
            goExitButton.setVisible(false);
        }
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
                return true;
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
    private Boolean isWin(Chess chess,List<Chess> animationChess) {

;        //向左判断是否连续五个相同颜色
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
            if (winFlag) {
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
        winFlag = isHasFive(isWinCount, winFlag);
        if (!winFlag) {animationChess.clear();}
        //向上
        for (int i = y - 1;i >= y - 4 && i >= 0;i--) {
            if (winFlag) {
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
            if (winFlag) {
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
        winFlag = isHasFive(isWinCount, winFlag);
        if (!winFlag) {animationChess.clear();}
        //左斜上判断,x-1,y-1
        for (int i = x - 1, j = y - 1; i >= 0 && j >= 0 && i >= x - 4 && j >= y - 4; i--, j--) {
            if (winFlag) {
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
            if (winFlag) {
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
        winFlag = isHasFive(isWinCount, winFlag);
        if (!winFlag) {animationChess.clear();}
        //右斜上,x+1,y-1
        for (int i = x + 1, j = y - 1; i <= lineCount && j >= 0 && i <= x + 4 && j >= y - 4; i++, j--) {
            if (winFlag) {
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
            if (winFlag) {
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
        winFlag = isHasFive(isWinCount, winFlag);
        if (!winFlag) {animationChess.clear();}
        return winFlag;
    }

    /**
     * 是否有五子
     */
    private Boolean isHasFive(int count,boolean flag) {
        if (flag) {
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
}
