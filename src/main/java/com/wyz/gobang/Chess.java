package com.wyz.gobang;

import javafx.scene.paint.Color;

import java.io.Serializable;

public class Chess implements Serializable{
    public Chess() {
    }

    public Chess(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    private int x;
    private int y;
    private Color color;

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

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
