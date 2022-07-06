package com.mf.dataStructure.tree;

import java.util.*;

public class HuffmanCode {
    public static void main(String[] args) {
        String str = "i like like like java do you like a java";
        byte[] contenBytes = str.getBytes();

        /* todo 分步步骤
        System.out.println(contenBytes.length);
        List<Code> codes = getCodes(contenBytes);
        System.out.println(codes);

        Code huffmanTree = createHuffmanTree(codes);
        preOrder(huffmanTree);
        //getCodes(huffmanTree,"",stringBuilder);
        Map<Byte, String> huffmanCodes = getCodes(huffmanTree);
        System.out.println(huffmanCodes);

        byte[] huffmanCodeBytes = zip(contenBytes, huffmanCodes);
        System.out.println(Arrays.toString(huffmanCodeBytes));
         */
        System.out.println(Arrays.toString(huffmanZip(contenBytes)));

    }

    //完成数据解压
    //思路
    //1.将huffmanCodeBytes 重新转成赫夫曼编码对应的二进制的字符串
    //2.将赫夫曼编码对应的二进制的字符串 对照赫夫曼编码重新转成 字节数组

    /**
     * 解码
     * @param huffmanCodes 赫夫曼编码表map
     * @param huffmanBytes 赫夫曼编码对应的自己数组
     * @return 原来的字符串对应的数组
     */
     private static byte[] decode(Map<Byte,String> huffmanCodes,byte[] huffmanBytes){
        //1。先得到huffmanBytes 对应的二进制字符串
         StringBuilder stringBuilder = new StringBuilder();
         for (int i = 0; i < huffmanBytes.length; i++) {
             byte b = huffmanBytes[i];
             boolean flag = (i == huffmanBytes.length -1);
             stringBuilder.append(byteToBitString(!flag,b));
         }
         //把字符串按指定的赫夫曼编码进行解码
         //把赫夫曼编码进行调换，因为进行反向查询 a->100 => 100->a
     }

    /**
     * 将一个byte转成二进制的字符串
     * @param b
     * @flag 表示是否需要补高位
     * @return
     */
    private static String byteToBitString(boolean flag ,byte b){
        //使用变量保存b
        int temp = b; //将byte转成temp  因为Byte包装类中没有toBinaryString方法  integer中有
        //如果是正数还存在补高位
        if(flag){
            temp |= 256; //按位与 256 =》 1 0000 0000  | 0000 0001 =》 10000 0001
        }

        //todo 注意这里返回的是temp对应的二进制的补码
        String str = Integer.toBinaryString(temp);
        // 应该截取后面的8位
        if(flag){
            return str.substring(str.length() - 8);
        }else{
            return str;
        }

    }

    /**
     * 封装压缩方法
     * @param bytes 原始的字符串对应的额字节数组
     * @return 经过处理以后的字节数组（压缩后）
     */
    private static byte[] huffmanZip(byte[] bytes){
        List<Code> codes = getCodes(bytes);
        //根据codes创建赫夫曼树
        Code huffmanTree = createHuffmanTree(codes);
        //生成对应的huffman编码
        Map<Byte, String> huffmanCodes = getCodes(huffmanTree);
        //根据生成的赫夫曼编码压缩，得到压缩后的赫夫曼编码字节数组
        byte[] huffmanCodeBytes = zip(bytes, huffmanCodes);
        return huffmanCodeBytes;
    }

    //todo  二进制 原码 反码 补码知识 需要学习

    /**
     * 将一个字符串对应的byte数组通过生成的赫夫曼编码表 进行压缩
     * @param bytes 原始的字符串对应的byte数组
     * @param huffmanCodes huffmanCodes生成的赫夫曼编码map
     * @return 赫夫曼编码处理后的byte数组
     */
    private static byte[] zip(byte[] bytes,Map<Byte,String> huffmanCodes){
        //利用赫夫曼编码表 将bytes数组转成赫夫曼编码对应的字符串
        StringBuilder stringBuilder = new StringBuilder();

        //遍历byte数组
        for (byte b : bytes) {
            stringBuilder.append(huffmanCodes.get(b));
        }

        //int len = (stringBuilder.length() + 7) / 8;
        int len;
        if(stringBuilder.length() %8 == 0){
            len = stringBuilder.length() / 8;
        }else{
            len = stringBuilder.length() / 8 + 1;
        }
        byte[] by = new byte[len];
        int index = 0 ; //记录第几个byte
        for (int i = 0; i < stringBuilder.length(); i+=8) {
            String strByte;
            if(i + 8 > stringBuilder.length()){ //不够8位
                strByte = stringBuilder.substring(i);
            }else{
                strByte = stringBuilder.substring(i,i + 8);
            }


            //将strByte转成一个byte 放到by中
            by[index] = (byte)Integer.parseInt(strByte,2); //转成二进制
            index ++;
        }
        return by;
    }


    //生成赫夫曼编码表
    //1.将赫夫曼编码表存放在Map<Byte,String>   32->01,97->100....
    static Map<Byte,String> huffmanCodes = new HashMap<Byte, String>();
    //2.生成赫夫曼编码表时候要去拼接路径 所以需要定义一个StringBuilder存储某个叶子节点的路径
    static StringBuilder stringBuilder = new StringBuilder();


    //todo 重载方法，方便调用
    private static Map<Byte,String> getCodes(Code root){
        if (root == null){
            return null;
        }
        getCodes(root.left,"0",stringBuilder);
        getCodes(root.right,"1",stringBuilder);
        return huffmanCodes;
    }

    /**
     * 将传入的code节点的所有叶子节点的赫夫曼编码，存放到huffmanCodes集合中
     * @param code 传入的节点 默认root节点
     * @param co 路径 左子几点是 0 右子节点是 1
     * @param stringBuilder 用于拼接路径
     */
    private static void getCodes(Code code,String co,StringBuilder stringBuilder){
        StringBuilder stringBuilder2 = new StringBuilder(stringBuilder);
        //将传入的co 加入到StringBuilder2
        stringBuilder2.append(co);
        if(code != null){ //如果code等于空则不处理
            //判断当前code是叶子结点还是非叶子节点
            if(code.data == null){ //非叶子节点
                //递归处理
                //向左递归
                getCodes(code.left,"0",stringBuilder2);

                //向右递归
                getCodes(code.right,"1",stringBuilder2);
            }else { //说明是叶子节点
                //表示找到了某个叶子结点的最后
                huffmanCodes.put(code.data,stringBuilder2.toString());
            }
        }
    }

    private static void preOrder(Code root){
        if(root != null){
            root.preOrder();
        }else{
            System.out.println("为空 无法遍历！");
        }
    }


    /**
     *
     * @param bytes
     * @return 返回list data =  weight =
     */
    private static List<Code> getCodes(byte[] bytes){
        //创建一个ArrayList
        ArrayList<Code> codes = new ArrayList<Code>();
        //遍历bytes 存储每个byte出现的次数 用map存储 不知道个数的情况下
        Map<Byte,Integer> counts = new HashMap<Byte,Integer>();
        for (byte b : bytes) {
            Integer count = counts.get(b);
            if(count == null){
                counts.put(b,1);
            }else{
                counts.put(b,count + 1);
            }
        }
        for (Map.Entry<Byte, Integer> entry : counts.entrySet()) {
            codes.add(new Code(entry.getKey(),entry.getValue()));
        }
        return codes;
    }

    private static Code createHuffmanTree(List<Code> codes){
        while (codes.size() > 1){
            // 排序 从小到大
            Collections.sort(codes);
            //取出第一颗最小二叉树
            Code leftCode = codes.get(0);
            //取出第二颗最小二叉树
            Code rightCode = codes.get(1);
            //创建一颗新的二叉树，他没有根节点，没有data，只有权值
            Code parent = new Code(null,leftCode.weight + rightCode.weight);
            parent.left = leftCode;
            parent.right = rightCode;

            //将已经处理的两颗二叉树移除
            codes.remove(leftCode);
            codes.remove(rightCode);
            //将新的二叉树加入到codes
            codes.add(parent);
        }
        //最后留下的就是哈夫曼树的根节点
        return codes.get(0);
    }
}

class Code implements Comparable<Code>{
    Byte data;  //存放数据本身
    int weight; //权值,表示字符出现几次
    Code left;
    Code right;


    public Code(Byte data, int weight) {
        this.data = data;
        this.weight = weight;
    }


    @Override
    public int compareTo(Code o) {
        return this.weight - o.weight;
    }


    @Override
    public String toString() {
        return "Code{" +
                "data=" + data +
                ", weight=" + weight +
                '}';
    }

    //前序遍历
    public void preOrder(){
        System.out.println(this);
        if(this.left != null){
            this.left.preOrder();
        }
        if(this.right != null){
            this.right.preOrder();
        }
    }
}