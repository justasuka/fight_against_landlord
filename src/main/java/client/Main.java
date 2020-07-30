package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 创建客户端
 * @author ：reol
 * @date ：Created in 2020/7/23 11:33
 */
public class Main {
    private static final Component INTERVAL = Box.createVerticalStrut(10);

    public static void main(String[] args) throws UnknownHostException {
        final JFrame loginUI = new JFrame("landlord");

        loginUI.setLayout(new FlowLayout());

        loginUI.setBounds(400,100,300, 100);
        loginUI.setLocationRelativeTo(loginUI.getOwner());
        loginUI.setResizable(false);

        Box box1 = Box.createVerticalBox();
        box1.add(new JLabel("服务器地址"));
        box1.add(INTERVAL);
        box1.add(new JLabel("姓名"));
        box1.add(INTERVAL);

        Box box2 = Box.createVerticalBox();
        final JTextField tf1 = new JTextField(10);
        box2.add(tf1);
        box2.add(INTERVAL);

        final JTextField tf2 = new JTextField(10);
        box2.add(tf2);
        box2.add(INTERVAL);

        Box baseBox = Box.createHorizontalBox();

        baseBox.add(box1);
        baseBox.add(INTERVAL);
        baseBox.add(box2);
        loginUI.add(baseBox);

        JButton btn1 = new JButton("登录");
        btn1.setLocation(50, 50);

        loginUI.add(btn1);

        loginUI.setLocationRelativeTo(loginUI.getOwner());
        loginUI.setVisible(true);
        loginUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        InetAddress ip = InetAddress.getLocalHost();
        String ips = ip.toString();
        String[] ipList = ips.split("\\/");

        tf1.setText("localhost");

        btn1.addActionListener(e -> {
            try {
                Table table = new Table(tf1.getText(), tf2.getText());
                new Thread(table).start();
                loginUI.setVisible(false);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }


}

























