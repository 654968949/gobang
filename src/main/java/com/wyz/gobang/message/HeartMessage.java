package com.wyz.gobang.message;

/**
 * <p>
 *     心跳消息类
 * </p>
 *
 * @author wuyuzi
 * @since 2020/12/23
 */
public class HeartMessage extends Message {

    /**
     * 对方是否准备好了 true-准备好了
     */
    private boolean isReady;

    /**
     * 我自己的账号
     */
    private String myAcount;

    public HeartMessage() {
    }

    public HeartMessage(boolean isReady) {
        this.isReady = isReady;
    }

    public HeartMessage(boolean isReady, String myAcount) {
        this.isReady = isReady;
        this.myAcount = myAcount;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public String getMyAcount() {
        return myAcount;
    }

    public void setMyAcount(String myAcount) {
        this.myAcount = myAcount;
    }
}
