package client;

import javax.swing.*;
import java.awt.*;

/**
 *  绘制背景图像
 * @author ：reol
 * @date ：Created in 2020/7/29 9:07
 */
public class MyPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        Image image = new ImageIcon("src\\main\\resources\\image\\asuka.png").getImage();
        g.drawImage(image,0, 0, this.getWidth(), this.getHeight(), null);
    }
    public MyPanel() {
        this.setLayout(null);
    }
}
