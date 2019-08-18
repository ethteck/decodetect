package com.ethteck.decodetect.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Util {
    private static int NGRAM_N = 2;
    //todo make this a member of Decodetect, not static
    static int MODEL_SIZE = (int) Math.pow(256, NGRAM_N);

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
}
