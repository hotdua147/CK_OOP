package com.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // Kích hoạt tính năng quét Bean tự động và chạy Web Server ngầm (Tomcat)
public class LibraryApplication {
    public static void main(String[] args) {
        // Lệnh kích hoạt toàn bộ hệ thống API Backend
        SpringApplication.run(LibraryApplication.class, args);
        System.out.println("\n [SUCCESS] Hệ thống API Thư viện Spring Boot đã ON! Sẵn sàng phục vụ Frontend.");
    }
}