package client;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

/**
 * 卡牌的行为
 *
 * @author ：reol
 * @date ：Created in 2020/7/23 19:27
 */
public class CardControl {

    /**
     * 码牌
     */
    public static void cardListSort(List<Card> l) {
        l.sort((o1, o2) -> {
            int a1 = o1.color;
            int a2 = o2.color;

            int b1 = o1.points;
            int b2 = o2.points;

            int flag;

            // todo 存疑
            // 王
            if (a1 == 5) {
                b1 += 100;
            }
            if (a1 == 5 && b1 == 1) {
                b1 += 50;
            }
            if (a2 == 5) {
                b2 += 100;
            }
            if (a2 == 5 && b2 == 1) {
                b2 += 50;
            }

            // A
            if (b1 == 1) {
                b1 += 20;
            }
            if (b2 == 1) {
                b2 += 20;
            }

            // 2
            if (b1 == 2) {
                b1 += 30;
            }
            if (b2 == 2) {
                b2 += 30;
            }
            flag = b2 - b1;
            // 其他的卡牌已经数值已经排好序了
            // 如果数值相同，按照花色排序
            if (flag == 0) {
                return a2 - a1;
            } else {
                return flag;
            }
        });

    }


    /**
     * 牌型的比较
     */
    public static boolean cardCompare(List<Card> a, List<Card> b) {
        // 有一方不出
        if (a.size() == 0) { return false; }
        if (b.size() == 0) { return true; }

        // 有一方王炸
        if (judgeCard(b) == CardType.c00) { return false; }
        if (judgeCard(a) == CardType.c00) { return true; }

        // 有一方炸弹另一个不是炸弹
        if (judgeCard(a) != judgeCard(b)) {
            if (judgeCard(a) == CardType.c4) {
                return true;
            }
            if (judgeCard(b) == CardType.c4) {
                return false;
            }
        }

        //
        int len = a.size();
        switch (judgeCard(a)) {
            // todo bug
            case c1, c2, c3 : {
                return cardCompare(a.get(0), b.get(0));
            }
            case c31 : {
                return cardCompare(a.get(1), b.get(1));
            }
            case c32 : {
                return cardCompare(a.get(2), b.get(2));
            }
            // todo 存疑
            case c1122, c111222: {
                return cardCompare(a.get(0), b.get(0)) && len == b.size();
            }
            case c11122234: {
                return cardCompare(a.get(2), b.get(2)) && judgeCard(b) == CardType.c11122234;
            }




            default : {
            }
        }
        return false;
    }

    /**
     * 飞机的比较
     */
    private static boolean planeCompare(List<Card> a, List<Card> b) {
        return false;
    }

    /**
     * 判断牌型
     */
    public static CardType judgeCard(List<Card> l) {
        // 排序过的list
        int len = l.size();
        if (len <= 4) {
            // 排好序的卡牌，首尾相同==全部相同
            if (l.size() > 0 && l.get(0).points == l.get(len - 1).points) {
                switch (len) {
                    // 单张
                    case 1:
                        return CardType.c1;
                    // 对子
                    case 2:
                        return CardType.c2;
                    // 三不带
                    case 3:
                        return CardType.c3;
                    // 炸弹
                    case 4:
                        return CardType.c4;
                    default:

                }
            }
            // todo 存疑
            // 王炸
            if (len == 2 && l.get(0).color == 5 && l.get(1).color == 5) {
                return CardType.c00;
            }

            // 三带一
            if (len == 4 && (l.get(0).points == l.get(2).points || l.get(1).points == l.get(3).points)) {
                return CardType.c31;
            }
        }

        if (len >= 5) {
            // 三代二
            if (len == 5) {
                if (l.get(0).points == l.get(2).points) {
                    if (l.get(3).points == l.get(4).points) {
                        return CardType.c32;
                    }
                }
                if (l.get(2).points == l.get(4).points) {
                    if (l.get(0).points == l.get(1).points) {
                        return CardType.c32;
                    }
                }
            }

            // todo
            // 连对

            // 飞机

            // 顺子


        }
        return CardType.c0;
    }

    /**
     * 卡牌的比较
     */
    public static boolean cardCompare(Card a, Card b) {

        int a1 = a.color;
        int a2 = b.color;
        int b1 = a.points;
        int b2 = b.points;

        // todo 存疑
        // 王
        if (a1 == 5) {
            b1 += 99;
        }
        if (a1 == 5 && b1 == 1) {
            b1 += 50;
        }
        if (a2 == 5) {
            b2 += 99;
        }
        if (a2 == 5 && b2 == 1) {
            b2 += 50;
        }

        // todo 可能会出现bug
        // A
        b1 = b1 == 1 ? b1 + 20 : b1;
        b2 = b2 == 1 ? b2 + 20 : b2;

        // 2
        b1 = b1 == 2 ? b1 + 30 : b1;
        b2 = b2 == 2 ? b2 + 30 : b2;

        return b1 - b2 > 0;

    }

    /**
     * 沿着线性函数移动卡牌
     */
    public static void move(Card card, Point from, Point to) {
        if (to.x != from.x) {
            // 权重
            double k = (1.0) * (to.y - from.y) / (to.x - from.x);
            // 偏置
            double b = to.y - to.x * k;
            // 向左还是向右
            int deltaX;
            if (from.x < to.x) {
                deltaX = 20;
            } else {
                deltaX = -20;
            }
            // 沿着函数直线移动
            for (int i = from.x; Math.abs(i - to.x) > 20; i += deltaX) {
                double y = k * i + b;
                card.setLocation(i, (int) y);
                // 移动延迟
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        // 校准位置
//        card.setBounds(to.x, to.y, 71, 96);

    }


}



























