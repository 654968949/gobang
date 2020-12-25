package com.wyz.gobang.message;


/**
 * <p>
 *     棋子信息类
 * </p>
 *
 * @author wuyuzi
 * @since 2020/12/16
 */
public class ChessMessage extends Message{

    /**
     * 棋子的x坐标
     */
    public int x;
    /**
     * 棋子的y坐标
     */
    public int y;
    /**
     * 棋子的颜色 true-是黑色 false-白色
     */
    public boolean isBlack;

    public ChessMessage() {
    }

    public ChessMessage(int x, int y, boolean isBlack) {
        this.x = x;
        this.y = y;
        this.isBlack = isBlack;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public void setBlack(boolean black) {
        isBlack = black;
    }

    @Override
    public String toString() {
        return "ChessMessage{" +
                "x=" + x +
                ", y=" + y +
                ", isBlack=" + isBlack +
                '}';
    }
}
