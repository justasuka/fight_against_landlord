package client;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：reol
 * @date ：Created in 2020/7/23 13:50
 */
public class Table extends JFrame implements Runnable {

    private final String playerName;
    private final String serverIp;
    Socket sock;

    private static final long serialVersionUID = 1;
    /**
     * 玩家列表
     */
    public static Player[] players = new Player[4];

    public static int LocalNumber;
    /**
     * 地主编号
     */
    public static int whoBoss;
    /**
     * 上一次操作的玩家
     */
    public static int flag;
    /**
     * 正在操作的玩家
     */
    public static int operatingNum;
    /**
     * 上次出牌的玩家
     */
    public static int lastTakeNum;

    /**
     * 容器， 菜单
     */
    public Container container = null;
    public JMenuItem exit, replay, about;

    /**
     * 叫地主/抢地主按钮， 出牌按钮, 等待标签, 图标
     */
    public static JButton[] landLord = new JButton[2];
    public static JButton[] pushCard = new JButton[2];
    JLabel wait;
    public static JLabel[] clock = new JLabel[5];


    /**
     * 玩家昵称列表， 玩家头像， 西边的手牌， 东边的手牌
     */
    JLabel[] playNames = new JLabel[3];
    JLabel[] playPic = new JLabel[3];
    JLabel[] cardsWest = new JLabel[20];
    JLabel[] cardsEast = new JLabel[20];

    /**
     * 地主的手牌
     */
    static List<Card> bossCards = new ArrayList<>();
    /**
     * 自己的出牌
     */
    List<Card> putList = new ArrayList<>();
    List<Card> sendServerCardList = new ArrayList<>();
    /**
     * 上家的出牌
     */
    static List<Card> lastPuts = new ArrayList<>();
    /**
     * 底牌
     */
    JLabel[] bossLabels = new JLabel[3];


    /**
     * 登录完成后创建出游戏姐界面
     */
    public Table(String serverIp, String playerName) throws IOException {
        this.playerName = playerName;
        this.serverIp = serverIp;
        this.sock = new Socket(serverIp, 7777);
        this.setTitle("landlord" + playerName);
        this.setSize(1280, 720);
        setResizable(false);

        // todo music

        wait = new JLabel("等待其他玩家中", JLabel.CENTER);
        wait.setSize(300, 100);
        wait.setVisible(true);
        this.add(wait);
        setLocationRelativeTo(getOwner());

        for (int i = 1; i < 4; i++) {
            players[i] = new Player("未连接", 0);
            players[i].cardList = new ArrayList<>();
        }

        this.setVisible(true);
    }

    /**
     * 初始化
     */
    public void init() {
        wait.setVisible(false);
        // 设置窗口居中
//        setLocationRelativeTo(getOwner());
        container = this.getContentPane();
        container.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        container.setBackground(new Color(83, 193, 227));

//        MyPanel panel = new MyPanel();
//        panel.setBounds(0, 0, 1280, 720);
//        panel.setVisible(true);
//        this.add(panel);
    }

    /**
     * 设置菜单
     */
    public void setMenu() {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu game = new JMenu("游戏");
        JMenu help = new JMenu("帮助");
        replay = new JMenuItem("重新开始");
        exit = new JMenuItem("退出");
        about = new JMenuItem("关于");

        game.add(replay);
        game.add(exit);
        help.add(about);
        jMenuBar.add(game);
        jMenuBar.add(help);
        this.add(jMenuBar);
    }

    /**
     * 设置东边玩家布局，头像姓名， 扑克牌背面，倒计时文本框，小时钟
     */
    public void setEast() {
        // 时钟
        System.out.println("getEastNum:" + getEastNum());
        clock[getEastNum()] = new JLabel(new ImageIcon("src\\main\\resources\\image\\clock.gif"));
        clock[getEastNum()].setBounds(920, 305, 30, 30);
        clock[getEastNum()].setVisible(false);
        this.add(clock[getEastNum()]);

        // 姓名
        playNames[0] = new JLabel(players[getEastNum()].getName());
        playNames[0].setBounds(1100, 270, 80, 70);
        playNames[0].setVisible(true);
        playNames[0].setBackground(new Color(255, 255, 255));
        this.add(playNames[0]);

        // 头像
        playPic[0] = new JLabel(new ImageIcon("src\\main\\resources\\image\\nongmin.png"));
        playPic[0].setBounds(1100, 300, 80, 70);
        playPic[0].setVisible(true);
        this.add(playPic[0]);

        upEast();


    }

    public void setWest() {
        // 时钟
        System.out.println("getWestNum:" + getWestNum());
        clock[getWestNum()] = new JLabel(new ImageIcon("src\\main\\resources\\image\\clock.gif"));
        clock[getWestNum()].setBounds(280, 305, 30, 30);
        clock[getWestNum()].setVisible(false);
        this.add(clock[getWestNum()]);

        // 姓名
        playNames[2] = new JLabel(players[getWestNum()].getName());
        playNames[2].setBounds(20, 270, 80, 30);
        playNames[2].setVisible(true);
        playNames[2].setBackground(new Color(255, 255, 255));
        this.add(playNames[2]);

        // 头像
        playPic[2] = new JLabel(new ImageIcon("src\\main\\resources\\image\\nongmin.png"));
        playPic[2].setBounds(20, 300, 80, 70);
        playPic[2].setVisible(true);
        this.add(playPic[2]);
        upWest();
    }

    /**
     * 中间地主的牌
     */
    public void setCenter() {
        for (int i = 0; i < 3; i++) {
            bossLabels[i] = new JLabel(new ImageIcon("src\\main\\resources\\image\\rear.png"));
            bossLabels[i].setBounds(400 + i * 45, 275, 71, 96);
            bossLabels[i].setVisible(true);
            this.add(bossLabels[i]);
        }

    }

    /**
     * 设置南面本地玩家布局
     */
    public void setSouth() {
//        LocalNumber = 1;
        playNames[1] = new JLabel(players[LocalNumber].getName());
        playNames[1].setBounds(200, 610, 80, 30);
        playNames[1].setVisible(true);
        playNames[1].setBackground(new Color(255, 255, 255));
        this.add(playNames[1]);

        playPic[1] = new JLabel(new ImageIcon("src\\main\\resources\\image\\nongmin.png"));
        playPic[1].setBounds(200, 450, 80, 70);
        playPic[1].setVisible(true);
        this.add(playPic[1]);

        // 可能有bug
        for (int i = players[LocalNumber].cardList.size() - 1; i >= 0; i--) {
            Card a = players[LocalNumber].cardList.get(i);
            this.add(a);
            a.setLocation(300 + i * 45, 540);
            a.setVisible(true);
        }
    }

    public void setLocal() {
        System.out.println("LocalNum:" + LocalNumber);
        System.out.println("设置小时钟");
        clock[LocalNumber] = new JLabel(new ImageIcon("src\\main\\resources\\image\\clock.gif"));
        clock[LocalNumber].setBounds(530, 460, 30, 30);
        clock[LocalNumber].setVisible(false);
        this.add(clock[LocalNumber]);

        // 小时钟线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // todo
                    for (int i = 1; i < 4; i++) {
//                        System.out.println(operatingNum);
//                        System.out.println(i);
                        clock[i].setVisible(operatingNum == i);
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        // 按钮
        landLord[0] = new JButton("要地主");
        landLord[1] = new JButton("不  要");
        pushCard[0] = new JButton("出牌");
        pushCard[1] = new JButton("不要");

        // 设置出牌，抢地主按钮
        System.out.println("设置出牌抢地主按钮，并且不可见");
        for (int i = 0; i < 2; i++) {
            pushCard[i].setBounds(450 + i * 100, 500, 60, 20);
            landLord[i].setBounds(450 + i * 100, 500, 75, 20);
            container.add(landLord[i]);
            landLord[i].setVisible(false);
            pushCard[i].setVisible(false);
            container.add(pushCard[i]);

        }

        // 抢地主按钮监听
        landLord[0].addActionListener(e -> {
            try {
                JSONObject json = new JSONObject();
                json.put("type", 1);
                json.put("mark", LocalNumber);
                json.put("msg", "yes");
                sendMsg2Server(json.toString(), new DataOutputStream(sock.getOutputStream()));
            } catch (Exception e2) {
                e2.printStackTrace();
            }

            // 叫地主按钮不可见
            for (int i = 0; i < 2; i++) {
                landLord[i].setVisible(false);
            }

            // todo 同步
            // 地主牌隐藏
            for (int i = bossLabels.length - 1; i >= 0; i--) {
                bossLabels[i].setVisible(false);
            }

            // 出牌按钮可见
            for (int i = 0; i < 2; i++) {
                pushCard[i].setVisible(true);
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }

            // 地主玩家拿到地主牌
            for (Card c : bossCards) {
                players[LocalNumber].cardList.add(c);
            }

            // 拿到地主牌后，所有手牌设置为未点击状态
            for (int i = 0; i < players[LocalNumber].cardList.size(); i++) {
                players[LocalNumber].cardList.get(i).isClicked = false;
            }
            // 排序
            CardControl.cardListSort(players[LocalNumber].cardList);

            // todo

            // 设置手牌位置
            for (int i = players[LocalNumber].cardList.size() - 1; i >= 0; i--) {
                Card c = players[LocalNumber].cardList.get(i);
                container.add(c);
                //
                c.setLocation(300 + i * 45, 540);
            }

            // todo
            playPic[1].setIcon(new ImageIcon("src\\main\\resources\\image\\dizhu.png"));
//                playPic[1] = new JLabel(new ImageIcon("src\\main\\resources\\image\\dizhu.png"));

//                playPic[1].setBounds(100, 100, 80, 70);
//                playPic[1].setVisible(true);

        });

        // 不要地主
        landLord[1].addActionListener(e -> {
            try {
                JSONObject json = new JSONObject();
                json.put("type", 1);
                json.put("mark", LocalNumber);
                json.put("msg", "no");
                sendMsg2Server(json.toString(), new DataOutputStream(sock.getOutputStream()));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            // 抢地主按钮设置为不可见
            for (int i = 0; i < 2; i++) {
                landLord[i].setVisible(false);
            }
        });


        // 出牌按钮
        pushCard[0].addActionListener(e -> {
            // 将点击起来的牌放入list， 每次点击清空该list
            putList.clear();

            for (int i = 0; i < players[LocalNumber].cardList.size(); i++) {
                Card c = players[LocalNumber].cardList.get(i);
                if (c.isClicked) {
                    putList.add(c);
                }
            }
            // todo
            List<Card> temp = new ArrayList<Card>(putList);

            int count = 0;
            // 上个玩家的出牌
            for (Card c : lastPuts) {
                System.out.print(c.name + " ");
            }
            System.out.println();

            // 自己出的牌
            for (Card c : putList
            ) {
                System.out.print(c.name + " ");
            }
            System.out.println();

            // 能否出牌，比较上家的出牌
            if (CardControl.judgeCard(putList) != CardType.c0 && CardControl.cardCompare(putList, lastPuts)) {

                // 上家出牌不可见
                for (Card c : lastPuts) {
                    c.setVisible(false);
                }
                // 自己出牌移动
                for (Card c : putList) {
                    CardControl.move(c, getLocation(), new Point(400 + count * 45, 275));

                    // todo
                    c.clickable = false;
                    count++;
                }

                // 关闭出牌按钮
                for (int i = 0; i < 2; i++) {
                    pushCard[i].setVisible(false);
                }

                // 将出的牌整合为一个字符串
                StringBuilder putCards = new StringBuilder();
                for (Card c : putList) {
                    putCards.append(c.getName()).append(" ");
                }

                // 发送到服务端
                try {
                    JSONObject json = new JSONObject();
                    json.put("type", 2);
                    json.put("mark", LocalNumber);
                    json.put("msg", putCards.toString().trim());
                    sendMsg2Server(json.toString(), new DataOutputStream(sock.getOutputStream()));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                // 出牌后从手牌中删除
                players[LocalNumber].cardList.removeAll(temp);

                if (players[LocalNumber].cardList.size() == 0) {
                    try {
                        JSONObject json = new JSONObject();
                        json.put("type", 3);
                        json.put("mark", LocalNumber);
                        sendMsg2Server(json.toString(), new DataOutputStream(sock.getOutputStream()));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }

            // 出牌后重新排序
            reSort(players[LocalNumber].cardList);
            for (int i = 0; i < players[LocalNumber].cardList.size(); i++) {

                // todo false
                players[LocalNumber].cardList.get(i).clickable = true;
            }
            // todo
        });

        // 不出牌
        pushCard[1].addActionListener(e -> {
            if (lastPuts.size() != 0) {
                try {
                    JSONObject json = new JSONObject();
                    json.put("type", 2);
                    json.put("mark", LocalNumber);
                    json.put("msg", "0");
                    sendMsg2Server(json.toString(), new DataOutputStream(sock.getOutputStream()));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                // 出牌按钮不可见
                for (int i = 0; i < 2; i++) {
                    pushCard[i].setVisible(false);
                }
            }
        });


    }


    @Override
    public void run() {

        try {

            DataInputStream dis = new DataInputStream(sock.getInputStream());
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
            dos.write(playerName.getBytes());

            while (true) {
                // 重绘
                repaint();

                byte[] buffer = new byte[1024];
                dis.read(buffer);
                String msg = new String(buffer).trim();
                System.out.println("服务端发来：" + msg);
                JSONObject json = new JSONObject(msg);
                int commandType = json.getInt("type");
                switch (commandType) {

                    // 收到确定地主玩家
                    case 1: {
                        System.out.println(" table 627 收到确定地主玩家");
                        // todo music
                        for (int i = 0; i < bossLabels.length - 1; i++) {
                            bossLabels[i].setVisible(false);
                        }
                        // 确定玩家列表
                        PlayerControl.determineBoss(json, players, LocalNumber);
                        int n = json.getInt("mark");

                        operatingNum = n;
                        for (int i = 1; i < 4; i++) {
                            if (players[i].getPlayNumber() == n) {
                                // todo
                                // 确定地主头像
                                if (i == ((LocalNumber + 2) > 3 ? (LocalNumber - 1) : (LocalNumber + 2))) {
                                    playPic[2].setIcon(new ImageIcon("src\\main\\resources\\image\\dizhu.png"));
                                }
                                if (i == ((LocalNumber + 1) > 3 ? (LocalNumber - 3) : (LocalNumber + 1))) {
                                    playPic[0].setIcon(new ImageIcon("src\\main\\resources\\image\\dizhu.png"));
                                }
                            }
                        }

                        // todo
                        for (int i = bossLabels.length - 1; i >= 0; i--) {
                            bossLabels[i].setVisible(false);
                        }
                    }
                    break;


                    // 收到出牌
                    case 2: {
                        int num = json.getInt("mark");
                        // 0 为不出牌
                        if ("0".equals(json.getString("msg"))) {

                            // todo music

                            if (lastTakeNum == LocalNumber) {
                                for (Card c : lastPuts) {
                                    c.setVisible(false);
                                }
                                lastPuts.clear();
                            }
                        }
                        for (Card c : lastPuts) {
                            System.out.println(c.name + "");
                        }
                        // 出牌
                        if (!"0".equals(json.getString("msg"))) {
                            lastTakeNum = num;
                            for (Card c : lastPuts) {
                                c.setVisible(false);
                            }
                            lastPuts = PlayerControl.takeCards(json);
                            for (Card c : lastPuts) {
                                System.out.println(c.name + " ");
                            }
                            System.out.println("\n" + lastTakeNum);

                            if (num != LocalNumber) {
                                // idea 改进
                                if (lastPuts.size() > 0) {
                                    players[num].cardList.subList(0, lastPuts.size()).clear();
                                }
                            }


                            for (Card c : putList) {
                                c.setVisible(false);
                            }
                        }
                        upLastPuts();
                        upExceptLocal();

                        // 下一个人出牌, 3号玩家出牌后轮到第一个
                        num += 1;
                        if (num > 3) {
                            num = 1;
                        }
                        // 本地玩家出牌按钮可见
                        if (num == LocalNumber) {
                            for (int i = 0; i < 2; i++) {
                                pushCard[i].setVisible(true);
                            }
                            operatingNum = num;
                        }
                        operatingNum = num;
                    }
                    break;


                    // 收到发牌
                    case 3: {
                        PlayerControl.releaseCards(json, players, LocalNumber);

                        for (int i = players[LocalNumber].cardList.size() - 1; i >= 0; i--) {
                            Card c = players[LocalNumber].cardList.get(i);

                            c.setVisible(true);

                            this.add(c);
                            Thread.sleep(100);
                            c.setLocation(300 + i * 45, 540);
                        }

//                        MyPanel panel = new MyPanel();
//                        panel.setBounds(0, 0, 1280, 720);
//                        panel.setVisible(true);
//                        this.add(panel);
                    }
                    break;


                    // 收到游戏结束
                    case 4: {
                        // todo music
                        int winNum = json.getInt("mark");
                        String winCamp = players[winNum].islandLord ? "地主" : "农民";
                        // todo show game over

                        this.setVisible(false);

                    }
                    break;

                    // 收到玩家序号及名称
                    case 5: {
                        PlayerControl.getLocalPlayer(json, players);
                        // 确定本地玩家
                        getLocalPlayer(players);

                        for (int i = 1; i < 4; i++) {
                            if (i != LocalNumber) {
                                for (int j = 1; j < 18; j++) {
                                    Card c = new Card("1-1", false);

                                    // todo false
                                    c.clickable = true;
                                    players[i].cardList.add(c);
                                }
                            }
                        }
                    }
                    break;

                    // 收到地主牌
                    // 接收到包含地主的卡牌添加到bossCards
                    case 6: {
                        PlayerControl.getBossCards(json, bossCards);

                    }
                    break;

                    // 收到询问地主命令
                    case 7: {
                        whoBoss = json.getInt("mark");
                        operatingNum = whoBoss;
                        // 初始化界面，设置宽高，背景颜色
                        init();
                        // 设置菜单栏
                        setMenu();
                        // 设置西边玩家， 头像，名称，卡牌位置
                        setWest();
                        setEast();
                        // 显示之间地主牌
                        setCenter();
                        // 南面本地玩家姓名手牌头像
                        setSouth();
                        setLocal();

                        // 绘制背景图片
//                        Thread.sleep(3000);
//                        MyPanel panel = new MyPanel();
//                        panel.setBounds(0, 0, 1280, 720);
//                        panel.setVisible(true);
//                        this.add(panel);

                        if (whoBoss == LocalNumber) {
                            for (int i = 0; i < 2; i++) {
                                landLord[i].setVisible(true);
                            }
                        }
                    }
                    break;


                    default: {
                        System.out.println("table 628 格式错误");
                    }
                    break;
                }


            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 跟新东面
     */
    public void upEast() {
        for (JLabel jLabel : cardsEast) {
            if (jLabel != null) {
                jLabel.setVisible(false);
            }
        }

        // todo
        for (int i = 0; i < players[getEastNum()].cardList.size(); i++) {
            cardsEast[i] = new JLabel(new ImageIcon("src\\main\\resources\\image\\rear.png"));
            cardsEast[i].setBounds(1020, 150 + i * 15, 71, 96);
            cardsEast[i].setVisible(true);
            this.add(cardsEast[i]);
        }
    }

    /**
     * 跟新西面
     */
    public void upWest() {
        for (JLabel jLabel : cardsWest) {
            if (jLabel != null) {
                jLabel.setVisible(false);

            }
        }

        // todo
        for (int i = 0; i < players[getWestNum()].cardList.size(); i++) {
            cardsWest[i] = new JLabel(new ImageIcon("src\\main\\resources\\image\\rear.png"));
            cardsWest[i].setBounds(110, 150 + i * 15, 71, 96);
            cardsWest[i].setVisible(true);
            this.add(cardsWest[i]);
        }
    }


    public void upLastPuts() {
        for (int i = 0; i < lastPuts.size(); i++) {
            lastPuts.get(i).setBounds(400 + i * 45, 275, 71, 96);
            lastPuts.get(i).setVisible(true);
            this.add(lastPuts.get(i));
        }
    }

    /**
     * 更新本地玩家序号
     */
    public void getLocalPlayer(Player[] players) {
        for (int i = 1; i < 4; i++) {
            if (players[i].getName().equals(playerName)) {
                LocalNumber = i;
            }
        }
    }

    /**
     * 跟新本地玩家以外的玩家的手牌
     */
    public void upExceptLocal() {
        upEast();
        upWest();
    }

    /**
     * 获得东西面的玩家序号
     *
     * @return
     */
    public int getEastNum() {
        return (LocalNumber + 1) > 3 ? (LocalNumber + 1 - 3) : (LocalNumber + 1);
    }

    public int getWestNum() {
        return (LocalNumber + 2) > 3 ? (LocalNumber + 2 - 3) : (LocalNumber + 2);
    }


    /**
     * 重新码牌
     */
    private void reSort(List<Card> c) {
        for (int i = players[LocalNumber].cardList.size() - 1; i >= 0; i--) {
            Card a = players[LocalNumber].cardList.get(i);
            this.add(a);
            a.setBounds(300 + i * 45, 540, 71, 96);
        }
    }


    /**
     * 给服务端发送消息
     */
    public void sendMsg2Server(String str, DataOutputStream dos) throws IOException {
        System.out.println("发送给服务器：" + str);
        dos.write(str.getBytes());
    }


//    private void handle(InputStream input, OutputStream output) throws IOException {
//        var writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
//        var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
//        writer.write(this.playerName);
//        writer.newLine();
//        writer.flush();
//    }


}












