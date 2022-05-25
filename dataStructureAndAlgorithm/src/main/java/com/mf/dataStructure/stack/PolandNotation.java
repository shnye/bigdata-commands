package com.mf.dataStructure.stack;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PolandNotation {
    public static void main(String[] args) {

        //中缀表达式转后缀表达式
        //例如 1+((2+3)*4)-5  => 1 2 3 + 4 * + 5 -
        // 1 因为对str操作不方便，所以现将str转换成list
        //即 ==》 ArrayList[1,+,(,(,2,+,3),*,4,),-,5]
        //2 将一个中缀表达式转换成后缀表达式对应的list
        String expression = "1+((2+3)*4)-5";
        List<String> infixExpressionList = toInfixExpressionList(expression);
        System.out.println(infixExpressionList);
        List<String> parseSuffixExpressionList = parseSuffixExpressionList(infixExpressionList);
        System.out.println(parseSuffixExpressionList);
        int result = calculate(parseSuffixExpressionList);
        System.out.println(result);

//        //先定义逆波兰表达式
//        //(3+4)*5-6  => 3 4 + 5 * 6 -
//        String suffixExpression = "30 4 + 5 * 6 -";
//        // 先将表达式放到ArrayList中
//        //将ArrayList 传递给一个方法 遍历ArrayList  配合栈完成计算
//        List<String> rpnList = getListString(suffixExpression);
//        System.out.println(rpnList);
//        int res = calculate(rpnList);
//        System.out.println("结果是：" + res);
    }


    /**
     * 因为对str操作不方便，所以现将str转换成list
     * 即 ==》 ArrayList[1,+,(,(,2,+,3),*,4,),-,5]
     * @param s
     * @return
     */
    public static List<String> toInfixExpressionList(String s){
        //定义一个list存放中缀表达式对应的元素
        ArrayList<String> ls = new ArrayList<String>();
        int i = 0; //指针 用于遍历str
        String str;
        char c ;
        do{
            //如果是非数字则直接加到ls中
            if((c = s.charAt(i)) < 48 || (c = s.charAt(i)) > 57){
                ls.add("" + c);
                i++;
            }else{ //如果是数字则需要考虑多位数的问题
                str = ""; //先将str置空
                while (i < s.length() && (c = s.charAt(i)) >= 48 && (c = s.charAt(i)) <= 57){
                    str += c; //拼接
                    i++;
                }
                ls.add(str);
            }

        }while (i < s.length());
            return ls;

    }

// todo 中缀表达式转换为后缀表达式
//    1 初始化两个栈：运算符栈s1和储存中间结果的栈s2；
//    2 从左至右扫描中缀表达式；
//    3 遇到操作数时，将其压s2；
//    4 遇到运算符时，比较其与s1栈顶运算符的优先级：
//         如果s1为空，或栈顶运算符为左括号“(”，则直接将此运算符入栈；
//         否则，若优先级比栈顶运算符的高，也将运算符压入s1；
//         否则，将s1栈顶的运算符弹出并压入到s2中，再次转到(4-1)与s1中新的栈顶运算符相比较；
//    5 遇到括号时：
//      (1) 如果是左括号“(”，则直接压入s1
//       (2) 如果是右括号“)”，则依次弹出s1栈顶的运算符，并压入s2，直到遇到左括号为止，此时将这一对括号丢弃
//    6 重复步骤2至5，直到表达式的最右边
//    7 将s1中剩余的运算符依次弹出并压入s2
//    8 依次弹出s2中的元素并输出，结果的逆序即为中缀表达式对应的后缀表达式

    public static List<String> parseSuffixExpressionList(List<String> ls){
        //初始化栈
        Stack<String> s1 = new Stack<String>(); //符号栈
        // 因为S2这个栈在整个过程中没有Pop操作且需要逆序输出 所以可以不用栈使用ArrayList
        ArrayList<String> s2 = new ArrayList<String>();

        for (String item : ls) {
            if(item.matches("\\d+")){
                s2.add(item);
            }else if(item.equals("(")) {
                s1.push(item);
            }else if(item.equals(")")){
                while (!s1.peek().equals("(")){
                    s2.add(s1.pop());
                }
                s1.pop();//将"("弹出消除小括号
            }else {
                //当item的优先级小于等于栈顶运算符的优先级，将s1栈顶的运算符弹出加入到S2中
                while (s1.size() != 0 && operation.getValue(s1.peek()) >= operation.getValue(item)){
                    s2.add(s1.pop());
                }
                //还需要将item压入栈中
                s1.push(item);
            }
        }
        //将S1中剩余的运算符一次弹出加入S2中
        while (s1.size() != 0){
            s2.add(s1.pop());
        }

        return s2; //因为list有序 所以顺序输出则为逆波兰表达式

    }



    //将逆波兰表达式一次将数据和运算符放入到一个ArrayList中
    public static List<String> getListString(String suffixExpression){
        //将suffixExpression 分割
        String[] split = suffixExpression.split(" ");
        List<String> list = new ArrayList<String>();
        for (String ele : split) {
            list.add(ele);
        }
        return list;
    }

    /**
     * 完成对逆波兰表达式的运算
     * @param list
     * @return
     */
    public static int calculate(List<String> list){
        //创建栈
        Stack<String> stack = new Stack<String>();
        for (String item : list) {
            //这里使用正则表达式取数
            if (item.matches("\\d+")) { //匹配的是多位数
                stack.push(item);
            } else {
                int num2 = Integer.parseInt(stack.pop());
                int num1 = Integer.parseInt(stack.pop());
                int res = 0;
                if (item.equals("+")) {
                    res = num1 + num2;
                } else if (item.equals("-")) {
                    res = num1 - num2;
                } else if (item.equals("*")) {
                    res = num1 * num2;
                } else if (item.equals("/")) {
                    res = num1 / num2;
                } else {
                    throw new RuntimeException("运算符有误");
                }
                stack.push("" + res);
            }
        }
        return (Integer.parseInt(stack.pop()));
    }

}

//编写一个类可以返回一个运算符对应的优先级
class operation{
    private static int ADD = 1;
    private static int SUB = 1;
    private static int MUL = 2;
    private static int DIV = 2;
    //写一个方法返回对应的优先级数字
    public static int getValue(String operation){
        int result = 0;
        if ("+".equals(operation)) {
            result = ADD;
        } else if ("-".equals(operation)) {
            result = SUB;
        } else if ("*".equals(operation)) {
            result = MUL;
        } else if ("/".equals(operation)) {
            result = DIV;
        }
        return result;
    }
}