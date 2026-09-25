package com.shaileshmishra.app.practice;

public class EqualsExmple {

    // Whenever equals() is overridden, hashCode() must also be overridden
    // consistently.
    // Follow-up : why
    // Because HashMap and HashSet use hashCode() to locate a bucket and equals() to
    // identify the matching object.
    public static void main(String[] args) {
        String a = new String("hello");
        String b = new String("hello");

        if (a == b) { // false
            System.out.println("a is equal to b : reference equality");
        }

        if (a.equals(b)) { // true
            System.out.println("a is equals to b : matches value");
        }

        // Explain the equals() / hashCode() contract.

        if (a.hashCode() == b.hashCode()) {
            System.out.println("If a.equals(b) is true then a.hashCode() == b.hashCode() also must be true");
        }
    }
}
