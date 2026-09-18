package com.shaileshmishra.app.practice;

// Interpretation: For every index, calculate the sum of the current element and the previous k-1 elements. 
// If fewer than k elements are available, sum whatever elements exist.

// I’m using a rolling sum so that I don't recalculate the entire window for every index. 
// I add the current element to the sum, and once the window exceeds k, I subtract the element that falls out of the window. 
// This gives us O(n) time complexity and O(n) space for the result.

// Complexity: O(n) time, O(n) space for the output array.
public class ArrayLogic {

    public static int[] calculateWindowSum(int[] arr, int k) {
        if (arr == null || arr.length == 0 || k <= 0) {
            return new int[0];
        }

        int[] result = new int[arr.length];

        int windowSum = 0;

        for (int i = 0; i < arr.length; i++) {

            // Add current element
            windowSum += arr[i];

            // Remove element that is outside the window
            if (i >= k) {
                windowSum -= arr[i - k];
            }

            result[i] = windowSum;
        }

        return result;
    }

    public static void main(String[] args) {

        int[] arr = { 1, 2, 3, 4, 5 };
        int k = 3;

        int[] result = calculateWindowSum(arr, k);

        for (int value : result) {
            System.out.print(value + " ");
        }
    }

}
