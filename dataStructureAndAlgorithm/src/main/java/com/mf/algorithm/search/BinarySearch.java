package com.mf.algorithm.search;


import java.util.ArrayList;

/**
 * 二分查找
 * todo 前提是该数组必须是有序的，从小到大，从大到小均可
 */
public class BinarySearch {
    public static void main(String[] args) {
        int[] arr = {1,8,10,89,1000,1000,1234};
        ArrayList<Integer> result = binarySearchAll(arr, 0, arr.length - 1, 1000);
        System.out.println(result);
    }


    /**
     * 二分查找法
     * @param arr 数组
     * @param left 左边的索引
     * @param right 右边的索引
     * @param findVal 最后找到的值
     * @return 返回下标否则 -1
     */
    public static int binarySearch(int[] arr,int left,int right,int findVal){
        //当left > right 没找到 返回 -1
        if(left > right){
            return -1;
        }
        int mid = (left + right) /2;
        int midVal = arr[mid];
        if(findVal > midVal){//向右递归
            return binarySearch(arr,mid + 1,right,findVal);
        }else if(findVal < midVal){
            return binarySearch(arr,left,mid -1, findVal);
        }else{
            return mid;
        }
    }


    /**
     * 拓展： 找到所有的值
     * 思路 在以上的基础上，找到mid的时候不要马上返回，
     * 而是mid的向左扫描 所有满足这个值的下标加到arrayList
     * 同样 向右扫描
     */

    public static ArrayList<Integer> binarySearchAll(int[] arr,int left,int right,int findVal){
        //当left > right 没找到 返回 -1
        if(left > right){
            return new ArrayList<Integer>();
        }
        int mid = (left + right) /2;
        int midVal = arr[mid];
        if(findVal > midVal){//向右递归
            return binarySearchAll(arr,mid + 1,right,findVal);
        }else if(findVal < midVal){
            return binarySearchAll(arr,left,mid -1, findVal);
        }else{
            ArrayList<Integer> resultIndexList = new ArrayList<Integer>();
            //向左扫描
            int temp = mid - 1;
            while (true){
                if(temp < 0 || arr[temp] != findVal){ //退出
                    break;
                }
                resultIndexList.add(temp);
                temp -= 1;
            }
            resultIndexList.add(mid);

            //向右扫描
            temp = mid + 1;
            while (true){
                if(temp > arr.length -1 || arr[temp] != findVal){
                    break;
                }
                resultIndexList.add(temp);
                temp += 1;
            }

            return resultIndexList;
        }
    }
}
