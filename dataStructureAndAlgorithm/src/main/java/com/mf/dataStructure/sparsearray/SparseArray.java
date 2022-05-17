package com.mf.dataStructure.sparsearray;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SparseArray {
    public static void main(String[] args) throws Exception {
        //创建一个原始的二维数组
        //0 表示没有棋子，1为黑子 2 为蓝子
        int chessArr1[][] = new int[11][11];
        chessArr1[1][2] = 1;
        chessArr1[2][3] = 2;
        chessArr1[4][5] = 1;
        for(int[] row : chessArr1){
            for (int data: row){
                System.out.printf("%d\t",data);
            }
            System.out.println();
        }
        //将二维数组转换成稀疏数组
        //1 先遍历二维数组 得到非0数据的个数
        int sum = 0;
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++){
                if(chessArr1[i][j] != 0){
                    sum++;
                }
            }
        }

        //2 创建对应的稀疏数组
        int  sparseArr[][] = new int[sum + 1][3];
        sparseArr[0][0] = 11;
        sparseArr[0][1] = 11;
        sparseArr[0][2] = sum;



        //遍历二维数组，给稀疏数组赋值
        int count = 0; //计数器
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++){
                if(chessArr1[i][j] != 0){
                    count++;
                    sparseArr[count][0] = i;
                    sparseArr[count][1] = j;
                    sparseArr[count][2] = chessArr1[i][j];
                }
            }
        }
        File file = new File("/Users/mufeng/Desktop/workspace/dataStructureAndAlgorithm/src/main/local_file/SparseArray.data");
        FileWriter out = new FileWriter(file);
        //输出稀疏数组的形式
        System.out.println();
        System.out.println("得到的稀疏数组为");
        for (int i = 0; i < sparseArr.length; i++) {
            System.out.printf("%d\t%d\t%d\t\n",sparseArr[i][0],sparseArr[i][1],sparseArr[i][2]);
            //存到本地文件夹
            out.write(sparseArr[i][0]+"\t");
            out.write(sparseArr[i][1]+"\t");
            out.write(sparseArr[i][2]+"\n");
        }
        out.close();
        System.out.println();



        //将稀疏数组恢复成原始的二维数组
        //读取文件获取稀疏数组
        int[][] sparseArr2 = getFile("/Users/mufeng/Desktop/workspace/dataStructureAndAlgorithm/src/main/local_file/SparseArray.data");

        //1 读取稀疏数组第一行，根据值创建二维数组
        int chessArr2[][] = new int[sparseArr2[0][0]][sparseArr2[0][1]];

        //2.读取后面几行数据赋值给二维数组即可
        for (int i = 1; i < sparseArr2.length; i++) {
            chessArr2[sparseArr2[i][0]][sparseArr2[i][1]] = sparseArr2[i][2];
        }

        System.out.println();
        System.out.println("恢复以后的二维数组");
        for (int[] row : chessArr2) {
            for (int data : row) {
                System.out.printf("%d\t",data);
            }
            System.out.println();
        }





    }

    private static int[][] getFile(String pathName) throws Exception {
        File file = new File(pathName);
        if (!file.exists())
            throw new RuntimeException("Not File!");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str;
        List<int[]> list = new ArrayList<int[]>();
        while ((str = br.readLine()) != null) {
            int s = 0;
            String[] arr = str.split("\t");
            int[] dArr = new int[arr.length];
            for (String ss : arr) {
                if (ss != null) {
                    dArr[s++] = Integer.parseInt(ss);
                }

            }
            list.add(dArr);
        }
        int max = 0;
        for (int i = 0; i < list.size(); i++) {
            if (max < list.get(i).length)
                max = list.get(i).length;
        }
        int[][] array = new int[list.size()][max];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < list.get(i).length; j++) {
                array[i][j] = list.get(i)[j];
            }
        }
        return array;
    }

}
