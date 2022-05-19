package com.mf.dataStructure.linkedlist;

public class Josepfu {
    public static void main(String[] args) {
        //测试数据结构功能
        CircleSingleLinkedList circleSingleLinkedList = new CircleSingleLinkedList();
        circleSingleLinkedList.add(5);
        circleSingleLinkedList.show();
    }
}


//创建一个环形的单向链表
class CircleSingleLinkedList{
    //创建一个默认的first节点
    private Boy first = new Boy(-1);

    //添加节点构成一个环形链表
    public void add(int nums){
        //输入数据校验
        if(nums < 1){
            System.out.println("输入的值nums 不正确");
            return;
        }
        Boy curBoy = null;  // 辅助指针，帮助构建环形链表

        for (int i = 1; i <= nums; i++) {
            //根据编号，创建节点
            Boy boy = new Boy(i);
            //如果是第一个节点
            if(i == 1){
                first = boy;
                first.setNext(first); //构成环
                curBoy = first;
            }else{
                curBoy.setNext(boy);
                boy.setNext(first);
                curBoy = boy;
            }
        }
    }

    public void show(){
        //判断是否为空
        if(first == null){
            return;
        }
        Boy curBoy = first;
        while (true){
            System.out.printf("小孩的编号%d \n",curBoy.getNo());
            if (curBoy.getNext() == first){
                break; //完成遍历
            }else {
                curBoy = curBoy.getNext();
            }
        }

    }


}




//创建一个节点类
class Boy{
    private int no;
    private Boy next;


    public Boy(int no) {
        this.no = no;
    }


    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public Boy getNext() {
        return next;
    }

    public void setNext(Boy next) {
        this.next = next;
    }
}