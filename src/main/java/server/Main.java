package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * @author ：reol
 * @date ：Created in 2020/7/23 14:30
 */
public class Main {
    public static void main(String[] args) throws IOException {
//        var ss = new ServerSocket(7777);
//        System.out.println("Server is running...");
//
//        while (true) {
//            var sock = ss.accept();
//            Thread t = new Handler(sock);
//            t.start();
//        }


        try {
            Socket[] socks = new Socket[5];
            DataOutputStream[] dos = new DataOutputStream[5];
            DataInputStream[] dis = new DataInputStream[5];
            Thread[] ts = new Thread[5];
            String[] playerNames = new String[5];
            int count = 1;

            ServerSocket ss = new ServerSocket(7777);
            System.out.println("Server is running...");

            // 等待连接
            do {
                socks[count] = ss.accept();

                dos[count] = new DataOutputStream(socks[count].getOutputStream());
                dis[count] = new DataInputStream(socks[count].getInputStream());

                System.out.println(socks[count].getInetAddress().getHostAddress() + "connected");

                byte[] buffer = new byte[1024];

                dis[count].read(buffer);
                playerNames[count] = new String(buffer).trim();
                System.out.println(playerNames[count] + "进来了");
                count++;
            } while (count != 4);

            // 创建服务端控制线程
            ServerControl sc = new ServerControl(socks, dos, dis);
            for (int i = 1; i < 4; i++) {
                ts[i] = new Thread(new SocketThread(sc, dis[i]));
            }

            // 玩家姓名列表,  发送到全部玩家客户端
            StringBuilder playerName = new StringBuilder();
            for (int i = 1; i < 4; i++) {
                playerName.append(playerNames[i]).append(" ");
            }
            System.out.println("所有玩家：");
            sc.sendPlayers(playerName.toString().trim());

            // 随机一个人开始叫地主
            int ran = new Random().nextInt(3) + 1;
            System.out.println("Server.Main 69 从几号开始叫地主");
            System.out.println(ran);
            sc.callLandLord(ran);

            // 开启三个线程接收客户端消息
            for (int i = 1; i < 4; i++) {
                ts[i].start();
            }

            // 发牌
            sc.sendCards();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//
//class Handler extends Thread {
//    Socket sock;
//
//    public Handler(Socket sock) {
//        this.sock = sock;
//    }
//
//    @Override
//    public void run() {
//        try (InputStream input = this.sock.getInputStream()) {
//            try (OutputStream output = this.sock.getOutputStream()) {
//                handle(input, output);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            try {
//                this.sock.close();
//            } catch (IOException ioException) {
//                System.out.println("disconnected!!");
//            }
//        }
//    }
//
//
//    private void handle(InputStream input, OutputStream output) throws IOException {
//        var writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
//        var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
//        String name = reader.readLine();
//        System.out.println(name + "进入了");
//
//    }
//}



















