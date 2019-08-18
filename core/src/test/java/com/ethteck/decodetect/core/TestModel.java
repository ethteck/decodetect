package com.ethteck.decodetect.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TestModel {
    @SuppressWarnings("WeakerAccess")
    @TempDir
    File tempDir;

    private Model getTestModel() {
        return getTestModel(new byte[]{0, 1, 0});
    }

    private Model getTestModel(byte[] bytes) {
        NGramCounter nGramCounter = new NGramCounter.Builder().addData(bytes).build();
        return new Model("UTF-8", "en", nGramCounter);
    }

    @Test
    void testModelMembers() {
        Model model = getTestModel();
        assertEquals(StandardCharsets.UTF_8, model.getEncoding());
        assertEquals("en", model.getLang());
    }

    @Test
    void testReadWrite() throws IOException, ClassNotFoundException {
        Model model = getTestModel();

        // Write models to temp dir
        FileOutputStream fos = new FileOutputStream(new File(tempDir + "/model.mdl"));
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(model);
        oos.close();
        fos.close();

        // Read models from temp dir
        FileInputStream fis = new FileInputStream(tempDir + "/model.mdl");
        ObjectInputStream ois = new ObjectInputStream(fis);
        Model model2 = (Model) ois.readObject();
        ois.close();
        fis.close();

        assertEquals(model, model2);
    }
}
