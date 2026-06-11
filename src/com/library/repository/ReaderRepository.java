package com.library.repository;

import com.library.model.Reader;
import com.library.model.StudentReader;
import com.library.model.PriorityStudentReader;
import com.library.model.LecturerReader;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReaderRepository {
    private static final String FILE_PATH = "data/readers.txt";
    private final List<Reader> readers = new ArrayList<>();

    public ReaderRepository() { loadFromFile(); }

    public List<Reader> getAll() { return Collections.unmodifiableList(readers); }

    public Reader findById(String readerId) {
        if (readerId == null) return null;
        for (Reader r : readers) {
            if (r.getUserId().equalsIgnoreCase(readerId.trim())) return r;
        }
        return null;
    }

    private void loadFromFile() {
        readers.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) { initMockData(); return; }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                Reader r = parseLine(line);
                if (r != null) readers.add(r);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void saveToFile() {
        File file = new File(FILE_PATH);
        if (file.getParentFile() != null) file.getParentFile().mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Reader r : readers) bw.write(toLine(r) + "\n");
        } catch (IOException e) { e.printStackTrace(); }
    }

    private Reader parseLine(String line) {
        String[] p = line.split("\\|");
        if (p.length < 4) return null;
        switch (p[3].trim()) {
            case "STUDENT": return new StudentReader(p[0].trim(), p[1].trim(), p[2].trim());
            case "PRIORITY_STUDENT": return new PriorityStudentReader(p[0].trim(), p[1].trim(), p[2].trim());
            case "LECTURER": return new LecturerReader(p[0].trim(), p[1].trim(), p[2].trim());
            default: return null;
        }
    }

    private String toLine(Reader r) {
        String type = (r instanceof LecturerReader) ? "LECTURER" : (r instanceof PriorityStudentReader) ? "PRIORITY_STUDENT" : "STUDENT";
        return r.getUserId() + "|" + r.getFullName() + "|" + r.getPhoneNumber() + "|" + type;
    }

    private void initMockData() {
        readers.add(new StudentReader("SV001", "Nguyen Van An", "0912345678"));
        readers.add(new LecturerReader("GV001", "Tran Duc Thanh", "0944556677"));
        saveToFile();
    }

    // Hàm bọc tương thích ngược
    public List<Reader> readAll() { return new ArrayList<>(readers); }
    public void writeAll(List<Reader> list) { this.readers.clear(); this.readers.addAll(list); saveToFile(); }
}