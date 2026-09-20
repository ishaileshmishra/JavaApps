---
name: practice-dsa
description: >-
  Use this skill when working on, adding, or testing algorithmic problems and data structures in the practice sandbox package.
---

# Practice & DSA Skill

This skill documents the standalone algorithmic examples and data structures residing in `com.shaileshmishra.app.practice`.

---

## Package Overview

The `practice` package is a self-contained sandbox used for data structure implementations and algorithmic exercises. It is decoupled from Spring Web and MongoDB persistence.

### Key Implementations

- **LRU Cache**: [`LRUCache.java`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/practice/LRUCache.java), [`LeastRecentUsedCacheExample.java`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/practice/LeastRecentUsedCacheExample.java)
  - Custom cache eviction using a HashMap coupled with a Doubly Linked List for $O(1)$ lookup and update.
- **Search Algorithms**: [`BinarySearch.java`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/practice/BinarySearch.java)
  - Standard divide-and-conquer binary search on sorted integer arrays.
- **Data Structure Utilities**:
  - [`StackEample.java`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/practice/StackEample.java): LIFO operations and traversal.
  - [`QueueExample.java`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/practice/QueueExample.java): FIFO queues.
  - [`ArrayLogic.java`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/practice/ArrayLogic.java): Array manipulation techniques.
  - [`FactorialExample.java`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/practice/FactorialExample.java): Recursive / iterative computation.

---

## Execution & Testing

Classes in this package include `main` methods for isolated execution or can be unit tested via standard JUnit tests in `src/test/java`.
