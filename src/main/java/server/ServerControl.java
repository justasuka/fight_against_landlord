package server;

import client.Card;
import client.CardControl;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 *  只用来处理客户端发来的信息，
 *  接收客户端信息的功能由SocketThread来处理
 *  分发到所有客户端
 *
 * @author ：reol
 * @date ：Created in 2020/7/23 17:10
 */
public class ServerControl {

    Socket[] socks = new Socket[5];

    DataOutputStream[] dos = new DataOutputStream[5];
    DataInputStream[] dis = new DataInputStream[5];
    List<Card> bossList;

    public ServerControl(Socket[] socks, DataOutputStream[] dos, DataInputStream[] dis) {
        super();
        this.socks = socks;
        this.dos = dos;
        this.dis = dis;
    }

    /**
     * 给全体发送json
     * */
    private void sendAll(JSONObject json) throws IOException {
        for (int i = 1; i < 4; i++) {
            dos[i].write(json.toString().getBytes());
        }
        System.out.println("ServerControl 46 发送:" + json.toString());
    }

    /**
     * 确定地主
     */
    public void makeLandLord(int num) throws IOException {
        System.out.println("Server Control 53 确定地主为：" + num);
        JSONObject json = new JSONObject();
        json.put("type", 1);
        json.put("mark", num);
        sendAll(json);
    }

    /**
     * 叫地主
     */
    public void callLandLord(int num) throws IOException {
        JSONObject json = new JSONObject();
        json.put("type", 7);
        json.put("mark", num);
        System.out.println("收到叫地主信息: " + json.toString());
        System.out.println("发出叫地主信息");
        sendAll(json);
        System.out.println("ServerControl 66： 服务端主线程发出完成， 开启三个服务端线程准备接收客户端的信息 开始发牌");
    }

    /**
     * 出牌
     * */
    public void play(int num, String cards) throws IOException {
        JSONObject json = new JSONObject();
        json.put("type", 2);
        json.put("mark", num);
        json.put("msg", cards);
        System.out.println("ServerControl 81 打出卡牌：" + json.toString());
        sendAll(json);
    }

    /**
     * 发牌
     * */
    public void sendCards() throws IOException {

        // todo 初始化


        // 全部54张牌
        List<Card> list = new ArrayList<>();

        // 玩家 123 的牌
        List<Card> player1 = new ArrayList<>();
        List<Card> player2 = new ArrayList<>();
        List<Card> player3 = new ArrayList<>();

        this.bossList = new ArrayList<>();

        // 创建整幅牌
        for (int i = 1; i < 5; i++) {
            for (int j = 1; j < 14; j++) {
                list.add(new Card(i + "-" + j, false));
            }
        }
        // 添加大小王
        list.add(new Card("5-2", false));
        list.add(new Card("5-1", false));

        // 洗牌
        Collections.shuffle(list);

        System.out.println("洗牌完成");

        // 发牌
        for (int i = 0; i < 54;) {
            player1.add(list.get(i++));
            player2.add(list.get(i++));
            player3.add(list.get(i++));
            // 底牌
            if (bossList.size() < 3) {
                bossList.add(list.get(i++));
            }
        }

        // 码牌
        CardControl.cardListSort(player1);
        CardControl.cardListSort(player2);
        CardControl.cardListSort(player3);

        // 获取得到的卡牌信息
        StringBuilder cards1 = new StringBuilder();
        StringBuilder cards2 = new StringBuilder();
        StringBuilder cards3 = new StringBuilder();
        for (int i = 0; i < 17; i++) {
            cards1.append(player1.get(i).name).append(" ");
            cards2.append(player2.get(i).name).append(" ");
            cards3.append(player3.get(i).name).append(" ");
        }
        System.out.println("正在发牌");

        // 保持为json，转换为字节数组发送
        JSONObject j1 = new JSONObject();
        JSONObject j2 = new JSONObject();
        JSONObject j3 = new JSONObject();

        j1.put("type", 3);
        j1.put("mark", 1);
        j1.put("msg", cards1.toString().trim());

        j2.put("type", 3);
        j2.put("mark", 2);
        j2.put("msg", cards2.toString().trim());

        j3.put("type", 3);
        j3.put("mark", 3);
        j3.put("msg", cards3.toString().trim());


        dos[1].write(j1.toString().getBytes());
        dos[2].write(j2.toString().getBytes());
        dos[3].write(j3.toString().getBytes());
        System.out.println("ServerControl 158 服务端主线程发牌完成， 三个客户端收到自己的牌  type ：3  mark ：玩家编号 " +
                " msg : 牌， Table 的run收到信息， table发出的信息由socketThread 接收 ");
    }

    /**
     * 地主的牌
     * */
    public void giveBossCards(int num) throws IOException {
        System.out.println("ServerControl 170 发出地主牌");
        StringBuilder bossString = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            bossString.append(bossList.get(i).name).append(" ");
        }
        JSONObject json = new JSONObject();
        json.put("type", 6);
        json.put("mark", num);
        json.put("msg", bossString.toString());

        System.out.println("发出地主牌: " + json.toString());
        dos[num].write(json.toString().getBytes());
    }

    /**
     * 发送玩家信息列表
     * */
    public void sendPlayers(String playerName) throws IOException {
        JSONObject json = new JSONObject();
        json.put("type", 5);
        json.put("msg", playerName);
        System.out.println("所有玩家");
        System.out.println(json.toString());
        sendAll(json);
    }

    /**
     * 游戏结束
     * */
    public void sendGameOver(int num) throws IOException {
        if (num == 0) {
            sendCards();
        } else {
            JSONObject json = new JSONObject();
            json.put("type", 4);
            json.put("mark", num);
            sendAll(json);
        }
    }






}















