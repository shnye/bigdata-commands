package com.mf.dataStructure.binarysorttree;

public class Node {
    int value;
    Node left;
    Node right;


    @Override
    public String toString() {
        return "Node{" +
                "value=" + value +
                '}';
    }

    public Node(int value) {
        this.value = value;
    }

    /**
     * 寻找要删除的节点位置
     * @param value
     * @return
     */
    public Node search(int value){
        if (this.value == value){
            return this;
        }
        if (value < this.value && this.left != null){
            return this.left.search(value);
        }
        if (value > this.value && this.right != null){
            return this.right.search(value);
        }
        return null;
    }



    /**
     * 添加节点
     * @param node
     */
    public void add(Node node){
        if (node != null) {
           if(node.value < this.value){
               if(this.left == null){
                   this.left = node;
               }else{
                   this.left.add(node);
               }
           }else if(this.right == null){
               this.right = node;
           }else{
               this.right.add(node);
           }
        }
    }

    public void infixOrder() {
        if (left != null) {
            left.infixOrder();
        }
        System.out.println(value);
        if (right != null) {
            right.infixOrder();
        }
    }

}
