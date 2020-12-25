package com.wyz.gobang.message;

/**
 * <p>
 *     被动接收端发送的确认悔棋的消息
 * </p>
 *
 * @author wuyuzi
 * @since 2020/12/23
 */
public class ReceiveRegretMessage extends Message{
    /**
     * 接收悔棋方态度 0-没态度  1-接收悔棋  2-不接受
     */
    public static final int ZERO = 0;

    public static final int YES = 1;

    public static final int NO = 2;

    /**
     * 接收悔棋方态度 0-没态度  1-接收悔棋  2-不接受
     */
    private int receiveRegret = ZERO;

    public ReceiveRegretMessage(int receiveRegret) {
        this.receiveRegret = receiveRegret;
    }

    public int getReceiveRegret() {
        return receiveRegret;
    }

    public void setReceiveRegret(int receiveRegret) {
        this.receiveRegret = receiveRegret;
    }

}
