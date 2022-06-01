package com.mf.algorithm.sort;

import java.util.Arrays;

public class MergeSort {
    public static void main(String[] args) {
        int[] arr = {8,4,5,7,1,3,6,2,0};
        int[] temp = new int[arr.length];
        mergeSort(arr,0,arr.length - 1, temp);
        System.out.println(Arrays.toString(arr));
    }


    public static void mergeSort(int[] arr,int left,int right,int[] temp){
        if(left < right){
            int mid = (left + right) / 2;
            System.out.println(mid);
            //向左进行分分解
            mergeSort(arr,left,mid,temp);
            //向右递归进行分解
            mergeSort(arr,mid + 1,right,temp);
            //到合并时
            merge(arr,left,mid,right,temp);
        }
    }


    /**
     * 归并排序 合并
     * @param arr 原始数组
     * @param left 左边有序的初始索引
     * @param mid 中间索引
     * @param right 右边索引
     * @param temp 临时数组
     */
    public static void merge(int[] arr,int left,int mid,int right,int[] temp){
        int i = left;
        int j = mid + 1;
        int t = 0; //temp 数组当前索引

        //先把左右两边的数据，按照规则拷贝到temp数组，直到左右两边有一遍全部处理完毕
        while (i <= mid && j <= right){//继续
            if(arr[i] <= arr[j]){
                //如果左边的有序序列当前元素，小于等于右边有序序列当前元素
                //即将左边的当前元素拷贝到temp中
                // t i 均向后移动
                temp[t] = arr[i];
                t += 1;
                i += 1;
            }else{ //反之 将右边的有序序列的当前元素 填充到temp中
                temp[t] = arr[j];
                t += 1;
                j += 1;
            }
        }
        // 把有剩余数据一边的数据依次全部填充到temp中
        while (i <= mid){ //左边还有数据，就全部填充到temp
            temp[t] = arr[i];
            t += 1;
            i += 1;
        }

        while (j <= right){ //右边还有数据，就全部填充到temp
            temp[t] = arr[j];
            t += 1;
            j += 1;
        }


        //将temp数组重新拷贝到array
        // 并不是每次拷贝所有
        t = 0;
        int tempLeft = left;
        while (tempLeft <= right){
            arr[tempLeft] = temp[t];
            t += 1;
            tempLeft += 1;
        }

    }
}
