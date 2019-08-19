package com.ethteck.decodetect.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TestModels {
    @SuppressWarnings("WeakerAccess")
    @TempDir
    File tempDir;

    static Models getTestModels() {
        byte[] b1 = {0, 1, 2};
        byte[] b2 = {2, 3, 4};
        NGramCounter c1 = new NGramCounter.Builder().addData(b1).build();
        NGramCounter c2 = new NGramCounter.Builder().addData(b2).build();

        Model m1 = new Model("UTF-8", "en", c1);
        Model m2 = new Model("Shift-JIS", "ja", c2);

        ArrayList<Model> modelList = new ArrayList<>();
        modelList.add(m1);
        modelList.add(m2);

        return new Models(modelList);
    }

    @Test
    void testReadWrite() throws IOException, ClassNotFoundException {
        Models models = getTestModels();

        assertNotNull(models);
        assertFalse(models.getModels().isEmpty());

        // Write models to temp dir
        models.writeToFile(tempDir + "/model.mdl");

        // Read models from temp dir
        Models models2 = Models.readFromFile(tempDir + "/model.mdl", false);

        assertEquals(models, models2);
        assertEquals(models.hashCode(), models2.hashCode());
    }

    @Test
    void testToString() {
        Model m1 = new Model("Shift-JIS", "ja", new NGramCounter.Builder().build());
        assertEquals("Shift-JIS ja", m1.toString());
    }

    @Test
    void testReadFromPathFailure() {
        assertThrows(FileNotFoundException.class, () -> Models.readFromFile("bogus", false));
    }
}
