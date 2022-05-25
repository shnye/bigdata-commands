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
    }

    /**
     * 使用递归回溯找路径
     * @param map 哪个地图
     * @param i 哪个位置开始
     * @param j
     * @return 如果找到通路返回true 否则返回false
     */
    public static boolean setWay(int[][] map,int i,int j){

    }
}
