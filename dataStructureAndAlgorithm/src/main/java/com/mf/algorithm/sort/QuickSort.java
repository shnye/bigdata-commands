package com.mf.algorithm.sort;

//快速排序（Quicksort）是对冒泡排序的一种改进。
// 基本思想是：通过一趟排序将要排序的数据分割成独立的两部分，其中一部分的所有数据都比另外一部分的所有数据都要小，
// 然后再按此方法对这两部分数据分别进行快速排序，整个排序过程可以递归进行，以此达到整个数据变成有序序列

public class QuickSort {
    public static void main(String[] args) {
        //int[] arr = {-9,78,0,23,-567,70};

        int[] arr =  new int[8000000];
        for (int i = 0; i < 8000000; i++) {
            arr[i] = (int)(Math.random() *8000000);
        }

        System.out.println("排序前");
        //System.out.println(Arrays.toString(arr));
        long start_time = System.currentTimeMillis();
        //8w 33,80w 107 800w 1219
        quickSort(arr,0,arr.length - 1);
        long end_time = System.currentTimeMillis();
        System.out.println("排序后");
        //System.out.println(Arrays.toString(arr));
        System.out.println(end_time-start_time);
    }

    public static void quickSort(int[] arr,int left,int right){
        int l = left; //左下标
        int r = right; //右下标
        int pivot = arr[(left + right) / 2]; //中轴值
        int temp = 0;
        // 让比pivot小的放左边 大的放右边。
        while (l < r){
            //在左边一直找，找到才退出
            while (arr[l] < pivot){
                l += 1;
            }
            while (arr[r] > pivot){
                r -= 1;
            }
            //如果l>=r 成立说明左右两边的值以及按照左边全部是小于pivot右边大于pivot
            if(l >= r){
                break;
            }
            //交换
            temp = arr[l];
            arr[l] = arr[r];
            arr[r] = temp;

            //如果交换完以后发现arr[l] = pivot 往前再走一步
            if (arr[l] == pivot){
                r -= 1;
            }
            //如果交换完以后发现arr[r] = pivot 往后再走一步
            if (arr[r] == pivot){
                l += 1;
            }
        }
        //如果l == r 必须要l++ r-- 否则会出现栈溢出
        if (l == r) {
            l += 1;
            r -= 1;
        }

        //向左递归
        if(left < r){
            quickSort(arr,left,r);
        }
        //向右递归
        if(right > l){
            quickSort(arr,l,right);
        }

    }
}
