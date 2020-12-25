package com.wyz.gobang.entity;

/**
 * <p>
 *     对局记录表
 * </p>
 *
 * @author wuyuzi
 * @since 2020/12/23
 */
public class Record {
    /**
     * id
     */
    private int id;
    /**
     * 黑方账号
     */
    private String black;
    /**
     * 白方账号
     */
    private String white;
    /**
     * 对局时间
     */
    private String chessTime;
    /**
     * 对局结果 0-黑方胜  1-白方胜
     */
    private int result;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBlack() {
        return black;
    }

    public void setBlack(String black) {
        this.black = black;
    }

    public String getWhite() {
        return white;
    }

    public void setWhite(String white) {
        this.white = white;
    }

    public String getChessTime() {
        return chessTime;
    }

    public void setChessTime(String chessTime) {
        this.chessTime = chessTime;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", black='" + black + '\'' +
                ", white='" + white + '\'' +
                ", chessTime='" + chessTime + '\'' +
                ", result=" + result +
                '}';
    }
}
