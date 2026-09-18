package com.shaileshmishra.app.practice;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// Binary search implementation
// Searching algorithms help locate an element in data structures like arrays or lists. 
// Java provides both linear search and binary search (via Arrays.binarySearch).
public class BinarySearch {

    public static void main(String[] args) {
        List<Integer> itemList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        int findElement = 8;
        int index = Collections.binarySearch(itemList, findElement);
        System.out.println("Index of " + findElement + " is: " + index);
        if (index >= 0) {
            System.out.println("Element found at index: " + index);
        } else {
            System.out.println("Element not found in the list.");
        }
    }
}
