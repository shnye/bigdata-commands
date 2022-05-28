package com.mf.dataStructure.recursion;

public class Queen8 {
    int max = 8;
    int[] array = new int[max];
    static int count = 0;
    public static void main(String[] args) {
        //测试
        Queen8 queen8 = new Queen8();
        queen8.check(0);
        System.out.println(count);

    }

    //放置
    private void check(int n){
        if(n == max){
            print();
            return;
        }
        for (int i = 0; i < max; i++) {
            //先把当前皇后放到该行的第一列
            array[n] = i;
            //判断当前位置的皇后到i列时，是否冲突
            if(judge(n)){//不冲突
                //接着放n+1个开始递归
                check( n + 1);
            }
            //如果冲突就继续遍历继续执行 array[n] = i;
        }
    }


    /**
     * 判断是否冲突
     * @param n
     * @return
     */
    private boolean judge(int n){
        for (int i = 0; i < n; i++) {
            if(array[i] == array[n] || Math.abs(n - i) == Math.abs(array[n] - array[i])){
                return false;
            }
        }
        return true;
    }

    //写一个方法将摆放位置输出
    private void print(){
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
        }
        System.out.println();
        count++;
    }
}
