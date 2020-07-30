package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * 牌类
 * @author ：reol
 * @date ：Created in 2020/7/23 15:07
 *
 * 继承JLabel 类，每张扑克都是一个label
 */
public class Card extends JLabel implements MouseListener {

    /**
     * 图片路径
     * */
    public String name;
    /**
     * 花色
     * */
    public int color;
    /**
     * 点数
     * */
    public int points;
    /**
     * 正反面
     * */
    public boolean upOrDown;
    /**
     * 能否被点击
     * */
    public boolean clickable = false;
    /**
     * 是否被点击过
     * */
    public boolean isClicked = false;

    public Card(String name, boolean upOrDown) {
        this.name = name;
        String[] s = name.split("-");
        this.color = Integer.parseInt(s[0]);
        this.points = Integer.parseInt(s[1]);
        this.upOrDown = upOrDown;

        // 设置正反面
        if (this.upOrDown) {
            this.turnFront();
        } else {
            this.turnRear();
        }

        this.setSize(71, 96);
        this.setVisible(true);
        this.addMouseListener(this);

    }

    @Override
    public String getName() {
        return name;
    }

    public void turnFront() {
        this.setIcon(new ImageIcon("src\\main\\resources\\image\\" + name + ".gif" ));
        this.upOrDown = true;
    }

    public void turnRear() {
        this.setIcon(new ImageIcon("src\\main\\resources\\image\\rear.gif"));
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        if (clickable) {
            Point from = this.getLocation();
            // 移动距离
            int step;
            if (isClicked) {
                step = - 20;
            } else {
                step = 20;
            }

            isClicked = !isClicked;
            this.setLocation(new Point(from.x, from.y - step));
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}










