package com.wyz.gobang.message;

/**
 * <p>
 *     主动发送悔棋消息类
 * </p>
 *
 * @author wuyuzi
 * @since 2020/12/23
 */
public class SendRegretMessage extends Message{

    /**
     * 发送悔棋方态度 true-想悔棋
     */
    private boolean sendRegret = false;


    public SendRegretMessage() {
    }

    public SendRegretMessage(boolean sendRegret) {
        this.sendRegret = sendRegret;
    }

    public boolean isSendRegret() {
        return sendRegret;
    }

    public void setSendRegret(boolean sendRegret) {
        this.sendRegret = sendRegret;
    }


    @Override
    public String toString() {
        return "RegretMessage{" +
                "sendRegret=" + sendRegret +
                '}';
    }
}
