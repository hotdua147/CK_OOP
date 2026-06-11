package com.library.repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {

    private static final String FILE_PATH = "data/books.txt";

    public List<Book> loadBooks() {

        List<Book> books = new ArrayList<>();

        try (BufferedReader br =
                     new BufferedReader(new FileReader(FILE_PATH))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] data = line.split(";");

                Book book = new Book(
                        data[0],
                        data[1],
                        data[2],
                        data[3],
                        Integer.parseInt(data[4]),
                        Double.parseDouble(data[5])
                );

                books.add(book);
            }

        } catch (IOException e) {
            System.out.println("Lỗi đọc file!");
        }

        return books;
    }

    public void saveBooks(List<Book> books) {

        try (BufferedWriter bw =
                     new BufferedWriter(new FileWriter(FILE_PATH))) {

            for (Book book : books) {

                bw.write(
                        book.getBookId() + ";" +
                                book.getTitle() + ";" +
                                book.getAuthor() + ";" +
                                book.getCategory() + ";" +
                                book.getQuantity() + ";" +
                                book.getValue()
                );

                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println("Lỗi ghi file!");
        }
    }
}