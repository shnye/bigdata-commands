package com.mf.algorithm.sort;


//希尔排序是希尔（Donald Shell）于1959年提出的一种排序算法。
// 希尔排序也是一种插入排序，它是简单插入排序经过改进之后的一个更高效的版本，也称为缩小增量排序。

public class ShellSort {
    public static void main(String[] args) {
        //int[] arr2 = {8,9,1,7,2,3,5,4,6,0};

        int[] arr =  new int[8000000];
        for (int i = 0; i < 8000000; i++) {
            arr[i] = (int)(Math.random() *8000000);
        }

        System.out.println("排序前");
        //System.out.println(Arrays.toString(arr));
        long start_time = System.currentTimeMillis();
        //8w 5636,交换
        //shellSort(arr);
        //8w 15,80w 130  800w 2199  移位  复杂度 n
        shellSort2(arr);
        long end_time = System.currentTimeMillis();
        System.out.println("排序后");
        //System.out.println(Arrays.toString(arr));
        System.out.println(end_time-start_time);
    }


    public static void shellSort(int[] arr){
        int temp = 0;
        //发现一个交换一个很笨，效率低
        for(int gap = arr.length/2; gap > 0; gap /=2){
            for (int i = gap; i < arr.length; i++) {
                //步长为gap
                for(int j = i - gap; j >= 0; j -= gap){
                    //如果前面的元素大于后面的元素，则交换
                    if(arr[j] > arr[j + gap]){
                        temp = arr[j];
                        arr[j] = arr[j + gap];
                        arr[j + gap] =temp;
                    }
                }
            }
        }
    }

    //优化
    public static void shellSort2(int[] arr){

        //发现一个交换一个很笨，效率低
        for(int gap = arr.length/2; gap > 0; gap /=2){
            for(int i = gap; i < arr.length; i++){
                int j = i;
                int temp = arr[j];
                if(arr[j] < arr [j - gap]){
                    while (j - gap >=0 && temp < arr[j - gap]){
                        //移动
                        arr[j] = arr[j -gap];
                        j -= gap;
                    }
                    //当退出while循环后就给temp找到了插入的位置
                    arr[j] = temp;
                }
            }
        }
    }
}
