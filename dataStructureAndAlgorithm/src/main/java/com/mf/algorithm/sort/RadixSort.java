package com.mf.algorithm.sort;

import java.util.Arrays;

public class RadixSort {
    // 8000万数据 * 11个数组 * 每个int4字节 /1024/1024/1024 = 3.3G
    //数组有负数的时候不要用基数排序 ，如果需要用需要改进
    public static void main(String[] args) {
        int[] arr = {53,3,542,748,14,214};
        radixSort(arr);

    }

    public static void radixSort(int[] arr){
        int max = arr[0]; //假设第一个数就是最大位数
        for (int i = 0; i < arr.length; i++) {
            if(arr[i] > max){
                max = arr[i];
            }
        }

        //得出最大的数是几位数
        //todo 是用小技巧把int转换成字符串求长度
        int maxLength = (max + "").length();

        //定义一个二维数组表示十个桶
        // 很明显是使用空间换时间的方法 每个桶的大小定位arr.length
        int[][] bucket = new int[10][arr.length];
        
        //记录每个桶中实际存放了多少数据
        int[] bucketElementCount = new int[10];

        // 增加一个 n 控制元素的位数
        for (int i = 0 , n = 1; i < maxLength; i++, n *= 10) {
            //针对每个元素对应的位进行排序 个 十 百 。。。。
            for (int j = 0; j < arr.length; j++) {
                //求出每个元素个位的值
                int digitOfElement = arr[j] / n % 10;
                //放到对应的桶中
                bucket[digitOfElement][bucketElementCount[digitOfElement]] = arr[j];
                bucketElementCount[digitOfElement]++;
            }
            //按照这个桶的顺序，重新放回到原来的数组
            int index = 0; //辅助放回的下标
            //遍历每个桶，放入到原来数组
            for (int k = 0; k < bucketElementCount.length; k++) {
                //如果桶中有数据我们才放入到原数组中
                if(bucketElementCount[k] != 0){
                    //循环该桶
                    for (int l = 0; l < bucketElementCount[k]; l++) {
                        //去除元素放入到array中
                        arr[index] = bucket[k][l];
                        index++;
                    }
                }
                // todo 每一轮处理以后粗腰将每个bucketElementCount 置为0
                bucketElementCount[k] = 0;
            }
        }

        System.out.println(Arrays.toString(arr));
    }

}
