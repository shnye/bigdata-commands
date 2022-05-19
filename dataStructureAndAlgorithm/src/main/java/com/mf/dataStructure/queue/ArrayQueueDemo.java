package com.mf.dataStructure.queue;

import java.util.Scanner;


/**
 * todo 需求
 * 出队列操作getQueue
 * 显示队列的情况showQueue
 * 查看队列头元素headQueue
 * 退出系统exit
 */
public class ArrayQueueDemo {

    //todo 数组无法重复使用需要优化
    //todo 将这个数组使用算法，该进程一个环形数组，取模来实现

    public static void main(String[] args) {
        //测试
        ArrayQueue queue = new ArrayQueue(3);
        char key = ' ';
        Scanner scanner = new Scanner(System.in);
        boolean loop = true;
        while (loop){
            System.out.println("s(show):显示队列");
            System.out.println("e(exit:退出程序");
            System.out.println("a(add):添加数据到队列");
            System.out.println("g(get):从队列取出数据");
            System.out.println("h(head):查看队列头的数据");
            key = scanner.next().charAt(0);

            switch (key){
                case 's':
                    queue.showQueue();
                    break;
                case 'a':
                    System.out.println("输入一个数");
                    int value = scanner.nextInt();
                    queue.addQueue(value);
                    break;
                case 'g':
                    try {
                        int res = queue.getQueue();
                        System.out.printf("取出的数据是%d\n",res);
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case 'h':
                    try {
                        int res = queue.headQueue();
                        System.out.printf("头部的数据是%d\n",res);
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case 'e':
                    scanner.close();
                    loop = false;
                    break;
                default:
                    break;
            }
        }
        System.out.println("程序退出");

    }
}

class ArrayQueue{
    private int maxSize; //表示最大容量
    private int front; //队列头
    private int rear; //队列尾
    private int[] arr; //该数组用来存放数据模拟队列

    //创建构造器
    public ArrayQueue(int arrMaxSize){
        maxSize = arrMaxSize;
        arr = new int[maxSize];
        front = -1; //队列头的前一个位置 元素的前一个位置
        rear = -1;  //指向队列的尾部的位置 元素的位置
    }

    //判断队列是否满
    public boolean isFull(){
        return rear == maxSize -1;
    }


    // 判断队列是否为空
    public boolean isEmpty(){
        return rear == front;
    }

    //添加数据到队列
    public void addQueue(int n) {
        if(isFull()){
            System.out.println("队列已满，无法加入数据");
            return;
        }
        //rear 后移
        rear++;
        //添加元素
        arr[rear] = n;
    }

    //获取队列数据,出队列
    public int getQueue(){
        //判断队列是否空
        if(isEmpty()){
            //通过抛出异常处理
            throw new RuntimeException("队列为空，无法获取");
        }
        front++;
        return arr[front];
    }
    //显示队列的所有数据
    public void showQueue(){
        //遍历
        if(isEmpty()){
            System.out.println("队列为空没有数据");
            return;
        }
        for (int i = 0; i < arr.length; i++) {
            System.out.printf("arr[%d]=%d\n",i,arr[i]);
        }
    }

    //显示队列的头部数据，不是取出数据
    public int headQueue(){
        if(isEmpty()){
            throw new RuntimeException("队列为空，无法获取");
        }
        return arr[front + 1];
    }

}