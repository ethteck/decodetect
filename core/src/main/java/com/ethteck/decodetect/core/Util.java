package com.ethteck.decodetect.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

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

        Files.walk(Paths.get(dir))
                .filter(Files::isRegularFile)
                .forEach(path -> ret.add(new DataFile(path)));
        return ret;
    }
}
