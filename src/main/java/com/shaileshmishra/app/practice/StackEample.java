package com.shaileshmishra.app.practice;

import java.util.ArrayDeque;

public class StackEample {

    public static void main(String[] args) {
        
        ArrayDeque<Integer> stack = new ArrayDeque<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);      

        System.out.println("Stack: " + stack);
        System.out.println("Popped element: " + stack.pop());
        System.out.println("Stack after pop: " + stack);
    }
}
