package com.mf.algorithm.sort;

public class BubbleSort {
    public static void main(String[] args) {
        //int arr[] = {3,6,-1,10,-2};
        //80000 9-10秒 800000  936秒  符合 n²的时间复杂度
        int[] arr =  new int[800000];
        for (int i = 0; i < 800000; i++) {
            arr[i] = (int)(Math.random() *8000000);
        }
        long start_time = System.currentTimeMillis();
        bubbleSort(arr);
        long end_time = System.currentTimeMillis();
        //System.out.println(Arrays.toString(arr));
        System.out.println(end_time-start_time);
    }

    public static void bubbleSort(int[] arr){
        //第一趟排序就是将最大的数排到最后
        int temp = 0; //临时变量交换时用
        boolean flag = false; //优化 如果一轮都没有交换则已经排序好
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if(arr[j] > arr[j + 1]){
                    flag = true;
                    temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
            if(!flag){
                break;
            }else{
                flag = false;
            }
        }
    }
}
