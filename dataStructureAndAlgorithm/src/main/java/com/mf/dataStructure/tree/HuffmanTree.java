package com.mf.dataStructure.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HuffmanTree {
    public static void main(String[] args) {
         int arr[] = {13,7,8,3,29,6,1};
         Node root = createHuffmanTree(arr);
         preOrder(root);
    }

    //前序遍历
    public static void preOrder(Node root){
        if (root != null){
            root.preOrder();
        }else {
            System.out.println("空树");
        }
    }

    //创建
    public static Node createHuffmanTree(int[] arr){
        //1，遍历arr数组
        //2.将每个元素构建成一个node
        //3.将node放入到ArrayList中
        List<Node> nodes = new ArrayList<Node>();
        for (int i : arr) {
            nodes.add(new Node(i));
        }

        while (nodes.size() > 1){
            //排序
            Collections.sort(nodes);
            //System.out.println(nodes);

            //1 去除权值最小的两个节点组成二叉树
            Node leftNode = nodes.get(0);
            Node rightNode = nodes.get(1);

            //构建新的二叉树
            Node parent = new Node(leftNode.value + rightNode.value);
            parent.left = leftNode;
            parent.right = rightNode;
            //从ArrayList中移除处理过的节点
            nodes.remove(leftNode);
            nodes.remove(rightNode);
            //把parent加入到nodes中
            nodes.add(parent);
        }
        return nodes.get(0);
    }

}


// 为了让Node 支持排序 需要实现comparable
class Node implements Comparable<Node>{
    int value; //节点权值
    Node left; //指向左子节点
    Node right; //指向右子节点

    //写一个前序遍历
    public void preOrder(){
        System.out.println(this);
        if(this.left != null){
            this.left.preOrder();
        }
        if(this.right != null){
            this.right.preOrder();
        }

    }

    public Node(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Node{" +
                "value=" + value +
                '}';
    }

    @Override
    public int compareTo(Node o) {
        //从小到大
        return this.value - o.value;
    }
}