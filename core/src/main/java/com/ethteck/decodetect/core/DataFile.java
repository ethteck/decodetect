package com.ethteck.decodetect.core;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DataFile {
    private final Path path;
    private final String name;
    private final Charset encoding;
    private final String lang;

    DataFile(Path path) {
        this.path = path;
        this.name = path.getName(path.getNameCount() - 1).toString();
        this.encoding = Charset.forName(path.getName(path.getNameCount() - 2).toString());
        this.lang = path.getName(path.getNameCount() - 3).toString();
    }

    public Path getPath() {
        return path;
    }

    public String getLang() {
        return lang;
    }

    public Charset getEncoding() {
        return encoding;
    }

    public String getName() {
        return name;
    }

    public byte[] loadBytes() throws IOException {
        byte[] fileBytes;
        fileBytes = Files.readAllBytes(path);
        return fileBytes;
    }

    public static ArrayList<DataFile> loadDataFiles(String dir) throws IOException {
        ArrayList<DataFile> ret = new ArrayList<>();

        Files.walk(Paths.get(dir))
                .filter(Files::isRegularFile)
                .forEach(path -> ret.add(new DataFile(path)));
        return ret;
    }
}
