package com.mf.algorithm.sort;


/**
 * 选择排序（select sorting）也是一种简单的排序方法。它的基本思想是：第一次从arr[0]~arr[n-1]中选取最小值，
 * 与arr[0]交换，第二次从arr[1]~arr[n-1]中选取最小值，与arr[1]交换，第三次从arr[2]~arr[n-1]中选取最小值，
 * 与arr[2]交换，…，第i次从arr[i-1]~arr[n-1]中选取最小值，与arr[i-1]交换，…, 第n-1次从arr[n-2]~arr[n-1]中选取最小值，
 * 与arr[n-2]交换，总共通过n-1次，得到一个按排序码从小到大排列的有序序列。
 */
public class SelectSort {
    public static void main(String[] args) {
        //int[] arr = {101,34,119,1};
        int[] arr =  new int[800000];
        for (int i = 0; i < 800000; i++) {
            arr[i] = (int)(Math.random() *8000000);
        }
        //8w 2658,交换的次数比较少 //80W 229382秒
        System.out.println("排序前");
        //System.out.println(Arrays.toString(arr));
        long start_time = System.currentTimeMillis();
        selectSort(arr);
        long end_time = System.currentTimeMillis();
        System.out.println("排序后");
        //System.out.println(Arrays.toString(arr));
        System.out.println(end_time-start_time);
    }


    /**
     * 时间复杂度也是n²
     * @param arr
     */
    public static void selectSort(int[] arr){
        for (int i = 0; i < arr.length - 1; i++) {
            int minIndex = i;
            int min = arr[i];

            for (int j = i + 1; j < arr.length; j++) {
                if (min > arr[j]){ //控制从大到小还是从小到大
                    min = arr[j];
                    minIndex = j;
                }
            }
            //将最小值放在arr[0]
            if(minIndex != i){
                arr[minIndex] = arr[i];
                arr[i] = min;
            }

        }

    }
}
