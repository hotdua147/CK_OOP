package com.library.utils;

import com.library.model.BorrowTicket;
import java.util.List;

public class IdGenerator {
    public static String generateTicketId(List<BorrowTicket> existingTickets) {
        int max = 0;
        for (BorrowTicket t : existingTickets) {
            try {
                int num = Integer.parseInt(t.getTicketId().substring(3));
                if (num > max) max = num;
            } catch (Exception ignored) {}
        }
        return String.format("PM-%03d", max + 1);
    }
}