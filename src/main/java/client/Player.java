package client;

import java.util.List;

/**
 * @author ：reol
 * @date ：Created in 2020/7/23 15:05
 */
public class Player {


    // 玩家姓名
    private  String name;
    // 是否地主
    boolean islandLord;
    // 玩家序号
    private  int playNumber;
    // 手牌数量
    private int cardNumber;
    // 手牌
    List<Card> cardList;
    // 本地玩家
    boolean isLocal;


    public List<Card> getCardList() {
        return cardList;
    }

    public Player(String name, int playNumber) {
        super();
        this.name = name;
        this.playNumber = playNumber;
        this.islandLord = false;
        this.isLocal = false;
    }

    public void setCards(List<Card> list) {
        this.cardNumber += list.size();
        this.cardList.addAll(list);
    }

    public String getName() {
        return name;
    }

    public boolean isIslandLord() {
        return islandLord;
    }

    public int getPlayNumber() {
        return playNumber;
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlayNumber(int playNumber) {
        this.playNumber = playNumber;
    }
}
















