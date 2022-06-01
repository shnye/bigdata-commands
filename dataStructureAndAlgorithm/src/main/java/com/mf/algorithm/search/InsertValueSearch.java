package com.mf.algorithm.search;

import java.util.Arrays;

public class InsertValueSearch {
    public static void main(String[] args) {
        int[] arr = new int[100];
        for (int i = 0; i < 100; i++) {
            arr[i] = i + 1;
        }
        System.out.println(Arrays.toString(arr));
    }


    /**
     * 插值查找
     * @param arr 数组
     * @param left 左边索引
     * @param right 右边索引
     * @param findVal 查找的值
     * @return
     */
    public static int insertValueSearch(int[] arr, int left ,int right,int findVal){
        // todo left > right 则表明遍历结束以后均未找到该值
        //todo 要求数组有序，所以当查找值 小于最左 大于最右（从大到小顺序）即退出查找 防止数组越界 比如输入一个很大或者很小的值，下面的mid会计算出有问题的值就会产生越界
        if(left > right || findVal < arr [0] || findVal > arr[arr.length -1]){
            return -1;
        }
        //求出mid
        int mid = left + (right - left) * (findVal - arr[left]) / (arr[right] - arr[left]); //固定算法
        int midVal = arr[mid];
        if(findVal > midVal){
            //向右查找
            return insertValueSearch(arr,mid + 1,right,findVal);
        }else if (findVal < midVal){
            //向左查找
            return insertValueSearch(arr,left,mid - 1,findVal);
        }else{
            //找到
            return mid;
        }
    }
}
