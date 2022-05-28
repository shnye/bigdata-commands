package com.mf.algorithm.sort;

import java.util.Arrays;

public class SelectSort {
    public static void main(String[] args) {
        int[] arr = {101,34,119,1};
        System.out.println("排序前");
        System.out.println(Arrays.toString(arr));
        selectSort(arr);
        System.out.println("排序后");
        System.out.println(Arrays.toString(arr));
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
