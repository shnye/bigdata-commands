package com.mf.dataStructure.hashtab;


import java.util.Scanner;

public class HashTabDemo {
    public static void main(String[] args) {
        HashTab hashTab = new HashTab(7);
        String key = "";
        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.println("add: 添加雇员");
            System.out.println("list：显示雇员");
            System.out.println("find：查找雇员");
            System.out.println("exit: 退出系统");
            key = scanner.next();
            while (true){
                if(key.equals("add")){
                    System.out.println("输入id");
                    int id = scanner.nextInt();
                    System.out.println("输入名字");
                    String name = scanner.next();
                    Emp emp = new Emp(id,name);
                    hashTab.add(emp);
                    break;
                }else if(key.equals("list")){
                    hashTab.list();
                    break;
                }else if (key.equals("exit")){
                    scanner.close();
                    System.exit(0);
                }else if (key.equals("find")){
                    System.out.println("输入查找的id");
                    int id = scanner.nextInt();
                    hashTab.findEmpById(id);
                    break;
                }
                else{
                    break;
                }
            }
        }
    }
}

//创建hash表管理多条链表
class HashTab{
    private EmpLinkedList[] empLinkedListArray;
    private int size;


    public HashTab(int size) {
        this.size = size;
        //根据大小初始化链表
        empLinkedListArray = new EmpLinkedList[size];
        //todo 这里有个坑 需要初始化每一条链表
        for (int i = 0; i < size; i++) {
            empLinkedListArray[i] = new EmpLinkedList();
        }
    }
    //添加雇员
    public void add(Emp emp){
        //根据员工的id得到该员工应当添加到哪条链表中
        int empLinkedListNO = hashFun(emp.id);
        //将emp加入到对应的链表中
        empLinkedListArray[empLinkedListNO].add(emp);
    }

    //编写散列函数，使用简单的取模法
    public int hashFun(int id){
        return id % size;
    }

    //遍历所有链表
    public void list(){
        for (int i = 0; i < size; i++) {
            empLinkedListArray[i].list(i);
        }
    }

    public void findEmpById(int id){
        //使用散列函数确定到哪条链表查找
        int empLinkListNo = hashFun(id);
        Emp emp = empLinkedListArray[empLinkListNo].findEmpById(id);
        if(emp != null){
            System.out.println("在第" + (empLinkListNo + 1) + " 条中找到 id = " + id);
        }else{
            System.out.println("没找到");
        }

    }

}



class Emp{
    public int id;
    public String name;
    public Emp next;

    public Emp(int id,String name){
        super();
        this.id = id;
        this.name = name;
    }
}

class EmpLinkedList{
    private Emp head;

    //因为id为自增，所以直接加到本链表的最后即可
    public void add (Emp emp){
        //如果是添加第一个雇员
        if(head == null){
            head = emp;
            return;
        }
        //如果不是添加第一个雇员，则使用辅助指针帮助定位到最后
        Emp CurEmp = head;
        while (true){
            if(CurEmp.next == null){
                break;
            }
            CurEmp = CurEmp.next;
        }
    }


    public void list(int no){
        if(head == null){
            System.out.println("第 " + (no + 1) + " 条链表为空");
            return;
        }
        Emp curEmp = head;
        while (true){
            System.out.println("第 " + (no + 1) + " 条链表为" + "id = " + curEmp.id +" name = " + curEmp.name);
            if (curEmp.next ==null){
                break;
            }
            curEmp = curEmp.next;
        }
    }

    public Emp findEmpById(int id){
        if(head == null){
            System.out.println("链表为空");
            return null;
        }
        //辅助指针
        Emp curEmp = head;
        while (true){
            if (curEmp.id == id){
                break;
            }
            if (curEmp.next == null){
                curEmp = null;
            }
            curEmp = curEmp.next;
        }
        return curEmp;
    }
}
