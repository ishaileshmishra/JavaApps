package com.shaileshmishra.app.practice;

public class FactorialExample {

    public static int factorial(int n) {
        if (n == 0)
            return 1;
        else
            return n * factorial(n - 1);
    }

    public static void main(String[] args) {
        System.out.println("Factorial of 5 is: " + factorial(5));
    }
}
