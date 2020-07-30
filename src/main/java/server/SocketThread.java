package server;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * 只接收来自客户端的信息，并且调用ServerControl来处理
 *
 * @author ：reol
 * @date ：Created in 2020/7/24 9:25
 */
public class SocketThread implements Runnable {

    private DataInputStream dis;
    private ServerControl sc;
    private int bossFlg;

    public SocketThread(ServerControl sc, DataInputStream dis) {
        this.sc = sc;
        this.dis = dis;
        // todo
        this.bossFlg = 1;
    }

    @Override
    public void run() {

        try {
            while (true) {
                byte[] buffer = new byte[1024];
                this.dis.read(buffer);

                String clientMsg = new String(buffer).trim();
                JSONObject json = new JSONObject(clientMsg);
                System.out.println("SocketThread 33 服务器收到:" + clientMsg);

                switch (json.getInt("type")) {
                    case 1:
                        receiveSetBoss(json);
                        break;
                    case 2:
                        receivePutCards(json);
                        break;
                    case 3:
                        receiveGameOver(json);
                        break;
                    default:
                        System.out.println("SocketThread 49 不合法的json数据");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务端收到叫地主
     */
    public void receiveSetBoss(JSONObject json) throws IOException {
        System.out.println("SocketThread 59 收到叫地主信息：" + json.toString());
        int num = json.getInt("mark");
        String flag = json.getString("msg");
        if ("yes".equals(flag)) {
            System.out.println("SocketThread 67 yes ");
            sc.makeLandLord(num);
            sc.giveBossCards(num);
        } else if ("no".equals(flag)) {
            System.out.println("SocketThread 71 no  bossFlag++");
            bossFlg++;
            if (bossFlg == 3) {
                sc.sendGameOver(0);
            } else {
                System.out.println("socketThread 69 确定地主");
                sc.callLandLord((num + 1) > 3 ? (num - 2) : (num + 1));
            }
        }
    }

    /**
     * 收到出牌命令
     */
    public void receivePutCards(JSONObject json) throws IOException {
        System.out.println("SocketThread 86 收到出牌命令：" + json.toString());
        int num = json.getInt("mark");
        String card = json.getString("msg");
        sc.play(num, card);
    }

    /**
     * 游戏完结
     */
    public void receiveGameOver(JSONObject json) throws IOException {
        int num = json.getInt("mark");
        sc.sendGameOver(num);
    }
}
























