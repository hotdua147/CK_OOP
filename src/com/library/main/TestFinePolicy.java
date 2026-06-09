package com.library.main;

import com.library.model.LecturerReader;
import com.library.model.PriorityStudentReader;
import com.library.model.Reader;
import com.library.model.StudentReader;

public class TestFinePolicy {

    public static void main(String[] args) {

        Reader normalStudent =
                new StudentReader("SV001",
                        "Nguyen Van A",
                        "0123456789");

        Reader priorityStudent =
                new PriorityStudentReader("SV002",
                        "Tran Thi B",
                        "0987654321");

        Reader lecturer =
                new LecturerReader("GV001",
                        "Le Van C",
                        "0911222333");

        int overdueDays = 5;

        System.out.println("=== TEST TINH PHI PHAT ===");

        System.out.println(
                normalStudent.getName()
                        + " - Sinh vien thuong: "
                        + normalStudent.getFinePolicy()
                        .calculateFine(overdueDays)
                        + " VND");

        System.out.println(
                priorityStudent.getName()
                        + " - Sinh vien uu tien: "
                        + priorityStudent.getFinePolicy()
                        .calculateFine(overdueDays)
                        + " VND");

        System.out.println(
                lecturer.getName()
                        + " - Giang vien: "
                        + lecturer.getFinePolicy()
                        .calculateFine(overdueDays)
                        + " VND");
    }
}