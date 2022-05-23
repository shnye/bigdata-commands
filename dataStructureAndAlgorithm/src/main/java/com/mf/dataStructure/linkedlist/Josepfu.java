package com.mf.dataStructure.linkedlist;

public class Josepfu {
    public static void main(String[] args) {
        //测试数据结构功能
        CircleSingleLinkedList circleSingleLinkedList = new CircleSingleLinkedList();
        circleSingleLinkedList.add(5);
        circleSingleLinkedList.show();

        //测试约瑟夫问题
        circleSingleLinkedList.count(1,2,5);
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

    /**
     * 根据用户输入，计算出出圈顺序
     * @param startNo 起始位置
     * @param countNum 数几次
     * @param nums 最原始有几个
     */
    public void count(int startNo,int countNum,int nums){
        //数据校验
        if(first == null || startNo < 1 || startNo >nums){
            System.out.println("参数输入有误");
            return;
        }
        //创建一个辅助指针，指向环形链表最后一个节点，帮助完成出圈
        Boy helper = first;
        while (true){
            if(helper.getNext() == first){
                break;
            }
            helper = helper.getNext();
        }
        // 报数前 移动first 和 helper k-1次
        for (int i = 0; i < startNo - 1; i++) {
            first = first.getNext();
            helper = helper.getNext();
        }
        //报数时 移动m-1次 然后出圈 当只剩一个节点时结束
        while (true) {
            if (helper == first){
                break;
            }
            for (int i = 0; i < countNum - 1; i++) {
                first = first.getNext();
                helper = helper.getNext();
            }
            System.out.println(first.getNo() + "出圈");
            first = first.getNext();
            helper.setNext(first);

        }
        System.out.println("最后的编号为" + helper.getNo());
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