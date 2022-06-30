package com.mf.dataStructure.tree;

public class BinaryTreeDemo {
    public static void main(String[] args) {
        BinaryTree binaryTree = new BinaryTree();
        HeroNode root = new HeroNode(1, "宋江");
        HeroNode node2 = new HeroNode(2, "吴用");
        HeroNode node3 = new HeroNode(3, "卢俊义");
        HeroNode node4 = new HeroNode(4, "林冲");
        binaryTree.setRoot(root);
        root.setLeft(node2);
        root.setRight(node3);
        node3.setRight(node4);

        //测试
        binaryTree.preOrder();
        System.out.println("------");
        binaryTree.infixOrder();
        System.out.println("-------");
        binaryTree.postOrder();
    }
}

//定义一个二叉树
class BinaryTree{
    private HeroNode root;

    public void setRoot(HeroNode root) {
        this.root = root;
    }

    //遍历由二叉树调用，节点提供遍历方法
    public void preOrder(){
        if(this.root != null){
            //谁调用this就指向谁
            this.root.preOrder();
        }else{
            System.out.println("二叉树为空，无法遍历");
        }
    }

    public void infixOrder(){
        if(this.root != null){
            //谁调用this就指向谁
            this.root.infixOrder();
        }else{
            System.out.println("二叉树为空，无法遍历");
        }
    }

    public void postOrder(){
        if(this.root != null){
            //谁调用this就指向谁
            this.root.postOrder();
        }else{
            System.out.println("二叉树为空，无法遍历");
        }
    }

}


//创建节点
class HeroNode{
    private int no;
    private String name;
    private HeroNode left;
    private HeroNode right;
    public HeroNode(int no,String name){
        this.no = no;
        this.name = name;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HeroNode getLeft() {
        return left;
    }

    public void setLeft(HeroNode left) {
        this.left = left;
    }

    public HeroNode getRight() {
        return right;
    }

    public void setRight(HeroNode right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "HeroNode{" +
                "no=" + no +
                ", name='" + name + '\'' +
                '}';
    }

    //前序遍历
    public void preOrder(){
        System.out.println(this); //先输出父节点
        //递归向左子前序遍历
        if(this.left != null){
            this.left.preOrder();
        }
        if(this.right != null){
            this.right.preOrder();
        }
    }

    //中续遍历
    public void infixOrder(){
        //递归向左子前序遍历
        if(this.left != null){
            this.left.infixOrder();
        }
        System.out.println(this); //输出父节点
        if(this.right != null){
            this.right.infixOrder();
        }
    }

    //后续遍历
    public void postOrder(){
        //递归向左子前序遍历
        if(this.left != null){
            this.left.postOrder();
        }
        if(this.right != null){
            this.right.postOrder();
        }
        System.out.println(this); //输出父节点
    }
}