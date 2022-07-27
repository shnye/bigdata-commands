package com.mf.dataStructure.binarysorttree;

public class BinarySortTree {
    private Node root;

    public Node getRoot(){
        return root;
    }

    public void add(Node node){
        if (root == null){
            root = node;
        }else {
            root.add(node);
        }
    }

    public void infixOrder(){
        if (root != null){
            root.infixOrder();
        }else{
            System.out.println("为空，无法遍历");
        }
    }
}
