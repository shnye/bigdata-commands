package com.mf.dataStructure.queue;

import java.util.Scanner;


/**
 * 对前面的数组模拟队列的优化，充分利用数组.   因此将数组看做是一个环形的。(通过取模的方 式来实现即可)
 * 分析说明：
 * 尾索引的下一个为头索引时表示队列满，即将队 列容量空出一个作为约定,这个在做判断队列满的 时候需要注意 (rear + 1) % maxSize == front 满]
 * rear == front [空]
 */
public class CircleArrayQueueDemo2 {

    //todo 将这个数组使用算法，该进程一个环形数组，取模来实现

    //todo  约定的缓冲位置会动态变化 队列不能指定index取出 所以没有影响


    public static void main(String[] args) {
        //测试CircleArrayQueueDemo2
        CircleArray queue = new CircleArray(4); //这里设置的4 其队列的有效数据是3
        char key = ' ';
        Scanner scanner = new Scanner(System.in);
        boolean loop = true;
        while (loop){
            System.out.println("s(show):显示队列");
            System.out.println("e(exit):退出程序");
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

class CircleArray{
    private int maxSize; //表示最大容量
    private int front; //队列头
    private int rear; //队列尾
    private int[] arr; //该数组用来存放数据模拟队列

    //创建构造器
    public CircleArray(int arrMaxSize){
        maxSize = arrMaxSize;
        arr = new int[maxSize];
        front = 0; //队列头的元素的位置
        rear =  0;  //指向队列的尾部的位置的后一个位置
    }

    //判断队列是否满
    public boolean isFull(){
        return (rear + 1) % maxSize == front;
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
        //添加元素
        arr[rear] = n;
        //将rear考虑后移必须考虑取模进行位置校正
        rear = (rear + 1) % maxSize;
    }

    //获取队列数据,出队列
    public int getQueue(){
        //判断队列是否空
        if(isEmpty()){
            //通过抛出异常处理
            throw new RuntimeException("队列为空，无法获取");
        }
        //先把front对应的值保存到一个临时变量
        //在将front后移(位置校正防止数组越界) ，再将临时变量返回
        int value = arr[front];
        front = (front + 1) % maxSize;
        return value;
    }
    //显示队列的所有数据
    public void showQueue(){
        //遍历
        if(isEmpty()){
            System.out.println("队列为空没有数据");
            return;
        }
        //思路 从front开始遍历，遍历多少个元素就可以
        for (int i = front; i < front + size(); i++) {
            System.out.printf("arr[%d]=%d\n",i % maxSize,arr[i % maxSize]);
        }
    }

    //求当前数据有效个数
    public int size(){
        return (rear + maxSize - front) % maxSize;
    }

    //显示队列的头部数据，不是取出数据
    public int headQueue(){
        if(isEmpty()){
            throw new RuntimeException("队列为空，无法获取");
        }
        return arr[front];
    }

}