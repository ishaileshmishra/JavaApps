package com.shaileshmishra.app.practice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Student implements Comparable<Student> {

    String name;
    int age;

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    @Override
    public int compareTo(Student student) {
        return this.name.compareTo(student.name);
    }

    public static void main(String[] args) {
        List<Student> students = new ArrayList<>();
        students.add(new Student("Aarya", 8));
        students.add(new Student("Raghav", 2));
        students.add(new Student("Shailesh", 36));
        students.add(new Student("Priya", 28));

        System.out.println("students: " + students.toString());
        // Default sorting based on name
        // Collections.sort(students);

        // Sorting based on age
        Comparator<Student> ageComparator = (s1, s2) -> Integer.compare(s1.age, s2.age);
        Collections.sort(students, ageComparator);
        System.out.println("students after sorting: " + students.toString());
    }
}
