package com.library.utils;

import java.util.Scanner;

/**
 * Lớp tiện ích hỗ trợ đọc dữ liệu an toàn từ Console.
 * Thiết kế phòng vệ (Defensive Programming) giúp chống crash ứng dụng tuyệt đối
 * khi người dùng nhập sai kiểu dữ liệu (nhập chữ vào ô số).
 */
public final class InputUtils {
    // Khởi tạo một đối tượng Scanner duy nhất dùng chung toàn hệ thống
    private static final Scanner scanner = new Scanner(System.in);

    // Chặn khởi tạo đối tượng từ bên ngoài (Utility Class Pattern)
    private InputUtils() {
        throw new UnsupportedOperationException("Đây là lớp tiện ích, không được phép khởi tạo đối tượng!");
    }

    /**
     * Đọc một chuỗi ký tự từ bàn phím, bắt buộc không được phép để trống.
     *
     * @param prompt Câu nhắc hiển thị cho người dùng (Thủ thư)
     * @return Chuỗi dữ liệu hợp lệ đã được cắt bỏ khoảng trắng thừa đầu/cuối
     */
    public static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("⚠️ Lỗi: Dữ liệu nhập vào không được để trống. Vui lòng thử lại!");
        }
    }

    /**
     * Đọc một số nguyên an toàn nằm trong một khoảng giới hạn đóng [min, max].
     * Tự động bắt ngoại lệ NumberFormatException nếu người dùng gõ chữ, yêu cầu nhập lại thay vì crash.
     *
     * @param prompt Câu nhắc hiển thị cho người dùng
     * @param min    Giá trị nhỏ nhất được chấp nhận
     * @param max    Giá trị lớn nhất được chấp nhận
     * @return Số nguyên hợp lệ thỏa mãn điều kiện khoảng dữ liệu
     */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("⚠️ Lỗi: Số nhập vào phải nằm trong khoảng từ %d đến %d!\n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Lỗi: Định dạng không hợp lệ! Vui lòng nhập vào một số nguyên (không nhập chữ/ký tự đặc biệt).");
            }
        }
    }

    /**
     * Đọc một số nguyên dương lớn hơn hoặc bằng 1 (Phục vụ việc nhập số lượng mượn sách).
     *
     * @param prompt Câu nhắc hiển thị cho người dùng
     * @return Số nguyên dương hợp lệ
     */
    public static int readPositiveInt(String prompt) {
        return readInt(prompt, 1, Integer.MAX_VALUE);
    }
}