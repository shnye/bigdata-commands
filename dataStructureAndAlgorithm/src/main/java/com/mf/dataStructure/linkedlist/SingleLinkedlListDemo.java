package com.mf.dataStructure.linkedlist;

import java.util.Stack;

import static com.mf.dataStructure.linkedlist.SingleLinkedlList.*;

public class SingleLinkedlListDemo {
    public static void main(String[] args) {
        //测试
        //1 创建节点
        HeroNode hero1 = new HeroNode(1, "宋江", "及时雨");
        HeroNode hero2 = new HeroNode(2,"卢俊义","玉麒麟");
        HeroNode hero3 = new HeroNode(3,"吴用","智多星");
        HeroNode hero4 = new HeroNode(4,"林冲","豹子头");

        //2创建链表
        SingleLinkedlList singleLinkedlList = new SingleLinkedlList();
//        singleLinkedlList.add(hero1);
//        singleLinkedlList.add(hero2);
//        singleLinkedlList.add(hero3);
//        singleLinkedlList.add(hero4);
        singleLinkedlList.addByOrder(hero1);
        singleLinkedlList.addByOrder(hero4);
        singleLinkedlList.addByOrder(hero2);
        singleLinkedlList.addByOrder(hero3);
        //3 输出
        singleLinkedlList.list();

        //todo 测试逆序打印单链表
        System.out.println("---逆序打印---");
        reversePrint(singleLinkedlList.getHead());
        System.out.println("---------");


        //todo 测试单链表反转
        System.out.println("---反转---");
        reverseList(singleLinkedlList.getHead());
        singleLinkedlList.list();
        System.out.println("------");

        //测试修改节点
        HeroNode newHeroNode3 = new HeroNode(2,"小卢","小玉");
        HeroNode newHeroNode10 = new HeroNode(10,"小卢","小玉");
        singleLinkedlList.update(newHeroNode3);
        singleLinkedlList.update(newHeroNode10);
        singleLinkedlList.list();

        singleLinkedlList.delete(1);
        singleLinkedlList.delete(10);
        singleLinkedlList.list();

        //todo 测试有多少个有效节点
        System.out.println("一共有"+ getLength(singleLinkedlList.getHead())+"个节点");

        //todo 测试单链表的倒数第K个节点
        int k = 1;
        HeroNode res = findLastIndexNode(singleLinkedlList.getHead(),k);
        System.out.println("res=" + res);

        //todo 合并两个单链表，合并后仍然有序



    }
}

//SingleLinkedList来管理hero节点
class SingleLinkedlList{

    //先初始化头结点
    private HeroNode head = new HeroNode(0,"","");

    public HeroNode getHead() {
        return head;
    }

    //添加节点
    //当不考虑编号的顺序时
    // 1 找到这个链表娥最后一个节点
    // 2 将最后这个节点的next 指向新的节点
    public void add(HeroNode heroNode){
        //因为head节点不能动，因此我们需要一个辅助的遍历temp
        HeroNode temp = head;
        //开始遍历链表，找到最后
        while (true){
            if(temp.next == null){
                break;
            }
            //如果没有找到最后一个节点，则将temp后移
            temp = temp.next;
        }
        //则将这个节点的next指向新的节点
        temp.next = heroNode;
    }

    //顺序添加节点，已经有则添加是失败
    public void addByOrder(HeroNode heroNode){
        //辅助变量来帮忙找到添加的位置（前一个节点）
        HeroNode temp = head;
        boolean flag = false; //是否存在默认为false
        while (true){
            if(temp.next == null){
                //已经到链表的最后
                break;
            }
            if(temp.next.no > heroNode.no){
                //找到位置，就在temp后面插入
                break;
            }else if(temp.next.no == heroNode.no){
                //添加的node已经存在
                flag = true;
                break;
            }
            temp = temp.next; //后移遍历当前的链表
        }
        //判断flag的值
        if(flag){
            System.out.printf("准备插入的编号%d 已经存在了，不能加入\n",heroNode.no);
        }else{
            heroNode.next = temp.next;
            temp.next = heroNode;
        }
    }

    //修改节点信息，根据编号修改（编号不能改）
    public void update(HeroNode newHeroNode){
        //判断是否为空
        if(head.next == null){
            System.out.println("链表为空");
            return;
        }
        //找到需要修改的节点
        HeroNode temp = head.next;
        boolean flag = false;
        while (true){
            if(temp == null){
                break;
            }
            if(temp.no == newHeroNode.no){
                flag = true;
                break;
            }
            temp = temp.next;
        }
        if(flag){
            temp.name = newHeroNode.name;
            temp.nickname = newHeroNode.nickname;
        }else{
            System.out.printf("没有找到编号%d节点，不能修改\n",newHeroNode.no);
        }
    }

    //删除节点
    public void delete(int no){
        HeroNode temp = head;
        boolean flag = false;
        while (true){
            if(temp.next == null){
                break;
            }
            if(temp.next.no == no){
                //找到待删除节点的前一个节点
                flag = true;
                break;
            }
            temp = temp.next;
        }
        if(flag){
          temp.next = temp.next.next;
        }else{
            System.out.printf("要删除的%d的节点不存在\n",no);
        }
    }


    //显示链表（遍历）
    public void list(){
        //判断链表是否为空
        if(head.next == null){
            System.out.println("链表为空");
            return;
        }
        //辅助变量遍历
        HeroNode temp = head.next;
        while (true){
            //判断是否到了最后一个
            if(temp == null){
                break;
            }
            //显示节点信息
            System.out.println(temp);
            //将temp后移
            temp = temp.next;
        }

    }


    /**
     * todo 获取到单链表的节点的个数（不统计头结点）
     * @param head 链表的头结点
     * @return 返回有效节点个数
     */
    public static int getLength(HeroNode head){
        if(head.next == null){
            return 0;
        }
        int length = 0;
        HeroNode cur = head.next;
        while (cur != null){
            length++;
            cur = cur.next;
        }
        return length;
    }

    /**
     * todo 查找单链表的倒数第K个节点
     * @param head
     * @param index
     * @return
     */
    public static HeroNode findLastIndexNode(HeroNode head ,int index){
        if(head.next == null){
            return null;
        }
        //第一次遍历得到链表的个数size
        int size = getLength(head);
        //第二次遍历 size - index 位置就是倒数的第index个节点
        //index校验
        if(index <= 0 || index > size){
            return null;
        }
        HeroNode cur = head.next;
        for(int i = 0 ; i < size - index; i++){
            cur = cur.next;
        }
        return cur;
    }

    /**
     * todo 反转单链表
     * @param head
     */
    public static void reverseList(HeroNode head){
        //如果当前链表为空，或者只有一个节点，无需处理直接返回
        if (head.next == null || head.next.next == null){
            return;
        }
        //定义一个辅助变量，辅助遍历链表
        HeroNode cur = head.next; //当前节点
        HeroNode next = null; //指向当前节点的下一个节点
        HeroNode reverseHead = new HeroNode(0,"","");

        //遍历原来的链表，每遍历一个节点将其取出，并放在新的链表reverseHead的最前端
        while (cur != null){
            next = cur.next; //先保存当前节点的下一个节点
            cur.next = reverseHead.next; //将cur的下一个节点指向新的链表的最前端
            reverseHead.next = cur; //将cur连接到新的链表上
            cur = next; //移动
        }
        //将head.next指向reverseHead.next 实现单链表的反转
        head.next = reverseHead.next;
    }

    /**
     * todo 逆序打印
     * 反转 + 打印会破坏链表结构所以不可取
     * 利用栈的数据结构将节点压入栈中，然后打印
     * @param head
     */
    public static void reversePrint(HeroNode head){
        if (head.next == null){
            return;
        }
        Stack<HeroNode> stack = new Stack<HeroNode>();
        HeroNode cur = head.next;
        //压栈
        while (cur != null){
            stack.push(cur);
            cur = cur.next;
        }
        //打印
        while (stack.size() > 0){
            System.out.println(stack.pop());
        }
    }





}

class HeroNode{
    public int no;
    public String name;
    public String nickname;
    public HeroNode next;


    public HeroNode(int no, String name, String nickname) {
        this.no = no;
        this.name = name;
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "HeroNode{" +
                "no=" + no +
                ", name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
