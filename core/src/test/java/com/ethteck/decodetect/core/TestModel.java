package com.ethteck.decodetect.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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
        HashMap<Integer, Double> counter = new HashMap<>();
        Util.addDataToCounter(counter, bytes);
        return new Model("UTF-8", "en", counter);
    }

    @Test
    void testModelMembers() {
        Model model = getTestModel();
        assertFalse(model.getCounter().isEmpty());
        assertNotEquals(0, model.getDot());
        assertEquals(StandardCharsets.UTF_8, model.getEncoding());
        assertEquals("en", model.getLang());
    }

    @Test
    void testWeightsAndDot() {
        // Two bigrams with even distribution (1 occurrence for each)
        byte[] testBytes = {1, 2, 3};
        Model model = getTestModel(testBytes);
        for (Map.Entry<Integer, Double> entry : model.getCounter().entrySet()) {
            assertEquals(0.5, entry.getValue());
        }
        assertEquals(Math.sqrt(0.5), model.getDot());
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
