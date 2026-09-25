package com.shaileshmishra.app.practice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TwoSumHashMapPattern {

    public static void main(String[] args) {
        int nums[] = { 2, 7, 11, 15 };
        var target = 7;

        int[] result = twoSum(nums, target);
        System.out.println(Arrays.toString(result));
    }

    private static int[] twoSum(int[] nums, int target) {

        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[] { map.get(complement), i };
            }
            map.put(nums[i], i);
        }

        return new int[] {};
    }
}
