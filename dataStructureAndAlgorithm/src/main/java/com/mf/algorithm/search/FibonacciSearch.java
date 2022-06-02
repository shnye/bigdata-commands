package com.mf.algorithm.search;

import java.util.Arrays;

public class FibonacciSearch {
    public static int maxSize = 20;
    public static void main(String[] args) {
        int[] arr = {1,8,10,89,1000,1234};
        System.out.println(fibSearch(arr,89));
    }

    //mid = low + F(k - 1) -1 需要得到斐波那契数列，因此需要先的到一个斐波那契数列
    //使用非递归的方法的到
    public static int[] fib(){
        int[] f = new int[maxSize];
        f[0] = 1;
        f[1] = 1;
        for (int i = 2; i < maxSize; i++) {
            f[i] = f[i -1] + f[i -2];
        }
        return f;
    }


    /**
     * 斐波那契查找算法
     * todo 非递归方式
     * @param a 数组
     * @param key 需要查找的值
     * @return 返回下标否则-1
     */
    public static int fibSearch(int[] a, int key){
        int low = 0;
        int high = a.length - 1;
        int k = 0; //表示斐波那契分割数值的下标
        int mid = 0;

        int[] f = fib();//获取到斐波那契数列
        while (high > f[k] -1){ //还没有找到k
            k ++;
        }
        //todo 因为f[k] 这个值可能大于数组的长度，因此我们需要一个Arrays类构建一个新的数组，并指向temp[]
        // 不足的部分会用0填充
        int[] temp = Arrays.copyOf(a,f[k]);
        // todo 实际上需要用到原数组 最后一个数字 也就是a[high] 填充temp
        for (int i = high + 1; i < temp.length; i++) {
            temp[i] = a[high];
        }

        //使用while来循环处理，找到key
        while (low <= high){//只要这个条件满足就可以找
            mid = low + f[k -1] -1;
            if(key < temp[mid]){ //说明应该继续向数组的前面查找
                high = mid -1;
                // todo  说明为什么是k--
                // todo 1 全部元素 = 前面元素 + 后面元素
                //  2 f[k] = f[k - 1] +f[k -2]
                //  3 因为前面有f[k -1]个元素，可以拆分成 f[k - 1] = f[k -2] = f[k - 3]，即在f[k - 1]前面继续查找k--
                //  4 即下次循环mid = f[k -1 -1] -1
                k--;
            }else if(key > temp[mid]){ //需要向左边查找
                low = mid + 1;
                // todo  说明为什么是k -= 2?  1 全部元素 = 前面元素 + 后面元素
                //  2 f[k] = f[k - 1] +f[k -2]
                //  3 因为前面有f[k -2]个元素 可以拆分成 f[k - 1] = f[k -3] = f[k - 4] 即在f[k - 1]前面继续查找k -= 2
                //  即下次循环mid = f[k -1 -2] -1
                k -= 2;
            }else{
                //需要确定返回哪个下标
                if(mid <= high){
                    return mid;
                }else{
                    return high;
                }
            }
        }
        //没有找到
        return -1;
    }
}
