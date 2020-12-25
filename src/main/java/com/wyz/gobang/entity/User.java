package com.wyz.gobang.entity;

/**
 * <p>
 *     用户表
 * </p>
 *
 * @author wuyuzi
 * @since 2020/12/22
 */
public class User {
    /**
     * 自增id
     */
    private int id;
    /**
     * 账户名
     */
    private String account;
    /**
     * 密码
     */
    private String password;
    /**
     * 对局时间
     */
    private String regTime;
    /**
     * 积分
     */
    private int score;
    /**
     * 玩的总局数
     */
    private int totalNums;
    /**
     * 赢的总局数
     */
    private int winNums;
    /**
     * 输的总局数
     */
    private int lostNums;
    /**
     * 和棋局数
     */
    private int drawNums;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalNums() {
        return totalNums;
    }

    public void setTotalNums(int totalNums) {
        this.totalNums = totalNums;
    }

    public int getWinNums() {
        return winNums;
    }

    public void setWinNums(int winNums) {
        this.winNums = winNums;
    }

    public int getLostNums() {
        return lostNums;
    }

    public void setLostNums(int lostNums) {
        this.lostNums = lostNums;
    }

    public int getDrawNums() {
        return drawNums;
    }

    public void setDrawNums(int drawNums) {
        this.drawNums = drawNums;
    }

}
