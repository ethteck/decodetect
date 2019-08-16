package com.ethteck.decodetect.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class TestDataFile {

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
