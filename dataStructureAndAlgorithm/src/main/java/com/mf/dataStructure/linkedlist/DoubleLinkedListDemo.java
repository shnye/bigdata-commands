package com.mf.dataStructure.linkedlist;

public class DoubleLinkedListDemo {
    public static void main(String[] args) {
        //测试
        //1 创建节点
        HeroNode2 hero1 = new HeroNode2(1, "宋江", "及时雨");
        HeroNode2 hero2 = new HeroNode2(2,"卢俊义","玉麒麟");
        HeroNode2 hero3 = new HeroNode2(3,"吴用","智多星");
        HeroNode2 hero4 = new HeroNode2(4,"林冲","豹子头");

        //2创建链表
        DoubleLinkedList doubleLinkedlList = new DoubleLinkedList();
        doubleLinkedlList.add(hero1);
        doubleLinkedlList.add(hero2);
        doubleLinkedlList.add(hero3);
        doubleLinkedlList.add(hero4);

        doubleLinkedlList.list();

        //修改
        HeroNode2 newHeroNode = new HeroNode2(4,"公孙胜","入云龙");
        doubleLinkedlList.update(newHeroNode);
        System.out.println("修改后");
        doubleLinkedlList.list();

        //删除
        doubleLinkedlList.delete(3);
        System.out.println("删除后的链表情况");
        doubleLinkedlList.list();

    }
}

class DoubleLinkedList{
    //先初始化头结点
    private HeroNode2 head = new HeroNode2(0,"","");

    public HeroNode2 getHead(){
        return head;
    }

    public void add(HeroNode2 heroNode){
        //因为head节点不能动，因此我们需要一个辅助的遍历temp
        HeroNode2 temp = head;
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
        heroNode.pre = temp;
    }

    public void addByOrder(HeroNode2 heroNode){
        //todo 需要完成按顺序添加
    }


    //修改节点信息，根据编号修改（编号不能改）
    // 和单向链表几乎一样
    public void update(HeroNode2 newHeroNode){
        //判断是否为空
        if(head.next == null){
            System.out.println("链表为空");
            return;
        }
        //找到需要修改的节点
        HeroNode2 temp = head.next;
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
    //对于双向链表可以直接找到删除的节点 而不需要找到被删除节点的前一个节点。删除即可
    public void delete(int no){
        if(head.next == null){
            System.out.println("链表为空 无法删除");
            return;
        }
        HeroNode2 temp = head.next;
        boolean flag = false;
        while (true){
            if(temp == null){
                break;
            }
            if(temp.no == no){
                //找到待删除节点的前一个节点
                flag = true;
                break;
            }
            temp = temp.next;
        }
        if(flag){
            temp.pre.next = temp.next;
            //如果是最后一个节点下面会有空指针异常
            if(temp.next != null){
                temp.next.pre = temp.pre;
            }

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
        HeroNode2 temp = head.next;
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
}


class HeroNode2{
    public int no;
    public String name;
    public String nickname;
    public HeroNode2 next;
    public HeroNode2 pre;


    public HeroNode2(int no, String name, String nickname) {
        this.no = no;
        this.name = name;
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "HeroNode2{" +
                "no=" + no +
                ", name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
