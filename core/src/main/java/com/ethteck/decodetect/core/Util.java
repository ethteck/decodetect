package com.ethteck.decodetect.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class Util {
    public static byte[] getBytesFromFile(Path filePath) throws IOException {
        byte[] fileBytes;
        try {
            fileBytes = Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new IOException("Error getting bytes from file", e);
        }
        return fileBytes;
    }

    public static ArrayList<DataFile> loadData(String dir) throws IOException {
        ArrayList<DataFile> ret = new ArrayList<>();

        try {
            Files.walk(Paths.get(dir))
                    .filter(Files::isRegularFile)
                    .forEach(path -> ret.add(new DataFile(path)));
        } catch (IOException e) {
            throw new IOException("Error loading data", e);
        }

        return ret;
    }

     private static void addDataToCounter(HashMap<Integer, Double> counter, byte[] bytes, int len) {
        for (int i = 0; i < len - 1; i++) {
            int b1 = Byte.toUnsignedInt(bytes[i]);
            int b2 = Byte.toUnsignedInt(bytes[i + 1]);
            int ord = 256 * b1 + b2;
            counter.put(ord, counter.getOrDefault(ord, 0d) + 1);
        }
    }

    public static void addDataToCounter(HashMap<Integer, Double> counter, byte[] bytes) {
        addDataToCounter(counter, bytes, bytes.length);
    }
}
