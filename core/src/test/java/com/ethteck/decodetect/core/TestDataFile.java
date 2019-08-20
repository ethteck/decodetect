package com.ethteck.decodetect.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestDataFile {
    private static final String BASE_DIR = "src/test/resources/data/test/";

    @Test
    void testLoadBytes() throws IOException {
        String fileDir = BASE_DIR + "/ru/UTF-8/";
        String fileName = "test.txt";
        Path path = Paths.get(fileDir + fileName);

        DataFile file = new DataFile(path);

        byte[] fileBytes = file.loadBytes();
        assertNotNull(fileBytes);
        assertTrue(fileBytes.length > 0);
    }

    @Test
    void testLoadbytesFailure() {
        DataFile file = new DataFile(Paths.get("this/path/UTF-8/bogus"));
        assertThrows(IOException.class, file::loadBytes);
    }

    @Test
    void testLoadData() throws IOException {
        ArrayList<DataFile> dataFiles = DataFile.loadDataFiles(BASE_DIR);
        assertTrue(dataFiles.size() > 0);
    }

    @Test
    void testLoadDataFailure() {
        assertThrows(IOException.class, () -> DataFile.loadDataFiles("bogus"));
    }

    @Test
    void testDataFileMembers() {
        String fileDir = "src/test/resources/data/test/ru/UTF-8/";
        String fileName = "test.txt";
        Path path = Paths.get(fileDir + fileName);
        DataFile dataFile = new DataFile(path);

        assertEquals(StandardCharsets.UTF_8, dataFile.getEncoding());
        assertEquals("ru", dataFile.getLang());
        assertEquals(fileName, dataFile.getName());
        assertEquals(path, dataFile.getPath());
    }
}
