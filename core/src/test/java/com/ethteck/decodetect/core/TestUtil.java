package com.ethteck.decodetect.core;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class TestUtil {
    private static final String BASE_DIR = "src/test/resources/data/test/";

    @Test
    void testGetBytesFromFile() throws IOException {
        String fileDir = BASE_DIR + "/ru/UTF-8/";
        String fileName = "test.txt";
        Path path = Paths.get(fileDir + fileName);
        
        byte[] fileBytes = Util.getBytesFromFile(path);
        assertNotNull(fileBytes);
        assertTrue(fileBytes.length > 0);
    }

    @Test
    void testGetBytesFromFileFailure() {
        assertThrows(IOException.class, () -> Util.getBytesFromFile(Paths.get("bogus")));
    }

    @Test
    void testLoadData() throws IOException {
        ArrayList<DataFile> dataFiles = Util.loadData(BASE_DIR);
        assertTrue(dataFiles.size() > 0);
    }

    @Test
    void testLoadDataFailure() {
        assertThrows(IOException.class, () -> Util.loadData("bogus"));
    }
}
