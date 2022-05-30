package com.mf.algorithm.sort;


//插入排序（Insertion Sorting）的基本思想是：把n个待排序的元素看成为一个有序表和一个无序表，
// 开始时有序表中只包含一个元素，无序表中包含有n-1个元素，排序过程中每次从无序表中取出第一个元素，
// 把它的排序码依次与有序表元素的排序码进行比较，将它插入到有序表中的适当位置，使之成为新的有序表。

//todo 当需要插入的数是较小的数时，后移的次数明显增多，对效率有影响. ==》希尔排序

public class InsertSort {
    public static void main(String[] args) {
        //int[] arr = {101,34,119,1};


        int[] arr =  new int[800000];
        for (int i = 0; i < 800000; i++) {
            arr[i] = (int)(Math.random() *8000000);
        }
        //8w 651,交换的次数比较少 //80W 65274
        System.out.println("排序前");
       // System.out.println(Arrays.toString(arr));
        long start_time = System.currentTimeMillis();
        insertSort(arr);
        long end_time = System.currentTimeMillis();
        System.out.println("排序后");
        //System.out.println(Arrays.toString(arr));
        System.out.println(end_time-start_time);
    }

    public static void insertSort(int[] arr){
        for (int i = 1; i < arr.length; i++) {
            int insertVal = arr[i];
            int insertIndex = i - 1;
            //1 再给insertVal插入的时候不越界
            //2 insertVal < arr[insertIndex] 待插入的数没有找到插入位置
            //3 arr[insertIndex]后移
            while(insertIndex >= 0 && insertVal < arr[insertIndex]){
                //腾出位置
                arr[insertIndex + 1] = arr[insertIndex];
                insertIndex--;
            }
            //说明插入的位置找到了 insertIndex + 1
            // 判断是否需要重置
            if (insertIndex + 1 != i){
                arr[insertIndex + 1] = insertVal;
            }

        }
    }
}
