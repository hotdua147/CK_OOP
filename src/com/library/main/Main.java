package com.library.main;

import com.library.repository.BookRepository;
import com.library.repository.BorrowTicketRepository;
import com.library.repository.ReaderRepository;
import com.library.service.BorrowReturnService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Khởi tạo các Repository quản lý dữ liệu
        BookRepository bookRepository = new BookRepository();
        ReaderRepository readerRepository = new ReaderRepository();
        BorrowTicketRepository borrowTicketRepository = new BorrowTicketRepository();

        // Khởi tạo Service xử lý tính năng mượn trả
        BorrowReturnService borrowReturnService = new BorrowReturnService(
                borrowTicketRepository, bookRepository, readerRepository
        );

        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n=======================================================");
            System.out.println("   HỆ THỐNG MÔ PHỎNG QUẢN LÝ THƯ VIỆN SINH VIÊN (NHÓM 4)");
            System.out.println("=======================================================");
            System.out.println("1. Xem danh sách Bạn đọc (Thành viên khác phụ trách)");
            System.out.println("2. Xem danh sách Sách (Thành viên khác phụ trách)");
            System.out.println("3. Thực hiện đăng ký MƯỢN SÁCH (Nghiệp vụ Trưởng nhóm)");
            System.out.println("4. Thực hiện TRẢ SÁCH & PHẠT (Thành viên khác phụ trách)");
            System.out.println("5. Thoát ứng dụng");
            System.out.print("Vui lòng chọn tính năng (1-5): ");

            String option = scanner.nextLine();
            switch (option) {
                case "1":
                case "2":
                case "4":
                    System.out.println("==> Tính năng thuộc nhiệm vụ của thành viên khác trong nhóm.");
                    break;
                case "3":
                    System.out.println("\n--- TIẾN HÀNH THỦ TỤC ĐĂNG KÝ MƯỢN SÁCH ---");
                    System.out.print("Nhập mã Bạn đọc (Reader ID): ");
                    String rId = scanner.nextLine();
                    System.out.print("Nhập mã Sách muốn mượn (Book ID): ");
                    String bId = scanner.nextLine();
                    System.out.print("Nhập số lượng cuốn muốn mượn: ");

                    try {
                        int qty = Integer.parseInt(scanner.nextLine());
                        // Gọi Service xử lý nghiệp vụ mượn sách cốt lõi
                        borrowReturnService.borrowBook(rId, bId, qty);
                    } catch (NumberFormatException e) {
                        System.out.println("MƯỢN THẤT BẠI: Số lượng sách nhập vào phải là một số nguyên!");
                    } catch (Exception e) {
                        // Hiển thị lý do lỗi nghiệp vụ cụ thể theo yêu cầu bài toán
                        System.out.println("MƯỢN THẤT BẠI. Nguyên nhân: " + e.getMessage());
                    }
                    break;
                case "5":
                    System.out.println("Đang tắt hệ thống... Tạm biệt và hẹn gặp lại!");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ! Vui lòng chọn lại.");
            }
        }
        scanner.close();
    }
}