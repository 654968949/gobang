package com.wyz.gobang.utils;

/**
 * <p>
 *     全局变量
 * </p>
 *
 * @author wuyuzi
 * @since 2020/12/16
 */
public class Global {
    public static final int BLACK_WIN = 0;
    public static final int WHITE_WIN = 1;
    /**
     * 自己的Ip
     */
    public static String myIp;
    /**
     *  自己的端口
     */
    public static String myPort;
    /**
     * 对手的IP
     */
    public static String oppoIp;
    /**
     * 对手的端口
     */
    public static String oppoPort;
    /**
     *  对战时间
     */
    public static String chessTime;
    /**
     * 白方账号
     */
    public static String blackAccount;
    /**
     * 黑方账号
     */
    public static String whiteAccount;
    /**
     * 我的账号
     */
    public static String myAccount;

    /**
     * 对手的账号
     */
    public static String oppoAccount;

    public static String getChessTime() {
        return chessTime;
    }
    public static void setChessTime(String chessTime) {
        Global.chessTime = chessTime;
    }

    public static String getMyIp() {
        return myIp;
    }

    public static void setMyIp(String myIp) {
        Global.myIp = myIp;
    }

    public static String getMyPort() {
        return myPort;
    }

    public static void setMyPort(String myPort) {
        Global.myPort = myPort;
    }

    public static String getOppoIp() {
        return oppoIp;
    }

    public static void setOppoIp(String oppoIp) {
        Global.oppoIp = oppoIp;
    }

    public static String getOppoPort() {
        return oppoPort;
    }

    public static void setOppoPort(String oppoPort) {
        Global.oppoPort = oppoPort;
    }

    public static String getBlackAccount() {
        return blackAccount;
    }

    public static void setBlackAccount(String blackAccount) {
        Global.blackAccount = blackAccount;
    }

    public static String getWhiteAccount() {
        return whiteAccount;
    }

    public static void setWhiteAccount(String whiteAccount) {
        Global.whiteAccount = whiteAccount;
    }

    public static String getOppoAccount() {
        return oppoAccount;
    }

    public static void setOppoAccount(String oppoAccount) {
        Global.oppoAccount = oppoAccount;
    }
}
