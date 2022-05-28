package com.mf.dataStructure.recursion;

public class MiGong {
    public static void main(String[] args) {
        //创建一个二维数组，模拟迷宫 地图
        int[][] map = new int[8][7];
        int Long = 7;
        int Wide = 8;
        //边缘置为1
        for (int i = 0; i < Long; i++) {
            map[0][i] = 1;
            map[Wide - 1][i] = 1;
        }
        for (int i = 0; i < Wide; i++) {
            map[i][0] = 1;
            map[i][Long - 1] = 1;
        }
        //设置挡板
        map[3][1] = 1;
        map[3][2] = 1;
        for (int i = 0; i < Wide; i++) {
            for (int j = 0; j < Long; j++) {
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }


        System.out.println("--------------");
        setWay(map,1,1);
        for (int i = 0; i < Wide; i++) {
            for (int j = 0; j < Long; j++) {
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * 使用递归回溯找路径
     * @param map 哪个地图
     * @param i 哪个位置开始
     * @param j
     * @return 如果找到通路返回true 否则返回false
     */
    //下面代码的找路策略是:下右上左
    public static boolean setWay(int[][] map, int i, int j) {
        if (map[6][5] == 2) { // 表示路已经找到了
            return true;
        } else {
            if (map[i][j] == 0) { // 0: 可以走还没有走
                // 这里开始递归回溯
                map[i][j] = 2; // 认为该点是可以走通,但是不一定
                if (setWay(map, i + 1, j)) { // 下找
                    return true;
                } else if (setWay(map, i, j + 1)) { // 右
                    return true;
                } else if (setWay(map, i - 1, j)) { // 上
                    return true;
                } else if (setWay(map, i, j - 1)) { // 左
                    return true;
                } else {// 走不通
                    map[i][j] = 3;
                    return false;
                }
            } else {
                //如果map(i)(j)!=0
                //则值 1,2,3
                return false;}
        }}
}
