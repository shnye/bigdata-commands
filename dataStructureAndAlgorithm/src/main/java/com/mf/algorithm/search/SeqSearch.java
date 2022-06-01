package com.mf.algorithm.search;

public class SeqSearch {
    public static void main(String[] args) {
        int[] arr = {1,9,11,-1,34,89};
        int index = seqSearch(arr,11);
        if (index == -1){
            System.out.println("未找到");
        }else{
            System.out.println("index = " + index);
        }
    }

    /**
     * 实现线性查找（找到一个就返回）
     * @param arr
     * @param value
     * @return
     */
   public static int seqSearch(int[] arr, int value){
        //逐一比对
       for (int i = 0; i < arr.length; i++) {
           if (arr[i] ==value){
               return i;
           }
       }
       return -1;
   }

}
