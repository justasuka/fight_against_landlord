package client;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家行为类
 * 接收来自服务器的信息，做出反应
 *
 * @author ：reol
 * @date ：Created in 2020/7/23 15:29
 */
public class PlayerControl {

    /**
     * 收到地主牌
     */
    public static void getBossCards(JSONObject json, List<Card> bossCards) {
        String s = json.getString("msg");
        String[] ss = s.split(" ");
        for (String value : ss) {
            Card a = new Card(value, true);
            a.clickable = true;
            bossCards.add(a);
        }
    }

    /**
     * 收到牌的人确定地主对象
     */
    public static void determineBoss(JSONObject json, Player[] players, int localNumber) {
        int n = json.getInt("mark");
        players[n].islandLord = true;
        if (n != localNumber) {
            for (int i = 1; i < 4; i++) {
                if (i != n) {
                    continue;
                }
                Card a = new Card("1-1", false);
                // todo false
                a.clickable = true;
                for (int j = 0; j < 3; j++) {
                    players[i].cardList.add(a);
                }
            }
        }
    }


    /**
     * 收到出牌
     *
     * @param json
     * @return list
     */
    public static List<Card> takeCards(JSONObject json) {
        String s = json.getString("msg");
        List<Card> list = new ArrayList<>();
        String[] ss = s.split(" ");
        for (String value : ss) {
            Card a = new Card(value, true);

            // todo false
            a.clickable = true;
            list.add(a);
        }
        if (CardControl.judgeCard(list) == CardType.c1112223344) {
            // todo music
        }
        if (CardControl.judgeCard(list) == CardType.c00) {
            // todo music
        }
        return list;


    }

    /**
     * 收到发牌
     * 收到来自服务器的牌
     *
     * @param json
     * @param players
     * @param num
     */
    public static void releaseCards(JSONObject json, Player[] players, int num) {
        String s = json.getString("msg");
        String[] ss = s.split(" ");
        for (int i = 0; i < ss.length; i++) {
            Card c = new Card(ss[i], true);
            c.clickable = true;
            players[num].cardList.add(c);
        }
    }

    /**
     * 收到玩家序号及其名称
     *
     * @param json
     * @param players
     */
    public static void getLocalPlayer(JSONObject json, Player[] players) {
        String str = json.getString("msg");
        String[] s = str.split(" ");
        for (int i = 1; i < 4; i++) {
            players[i].setName(s[i - 1]);
            players[i].setPlayNumber(i);
        }
    }


}












