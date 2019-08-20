package com.ethteck.decodetect.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

class TestDecodetect {
    @Test
    void testCustomModel(@TempDir File tempDir) throws IOException, Decodetect.DecodetectInitializationException {
        String engutfStr = "This will become UTF-7";
        String otherutfStr = "I am also that same encoding!";

        NGramCounter counter = new NGramCounter.Builder().addData(engutfStr.getBytes(Encodings.UTF_7)).build();

        Model model = new Model("UTF-7", "en", counter);
        ArrayList<Model> modelList = new ArrayList<>();
        modelList.add(model);

        Models models = new Models(modelList);
        models.writeToFile(tempDir + "/model.mdl");

        Decodetect custom = new Decodetect(tempDir + "/model.mdl");
        List<DecodetectResult> results = custom.getResults(otherutfStr.getBytes(Encodings.UTF_7));

        DecodetectResult bestResult = results.get(0);
        assertEquals("en", bestResult.getLang());
        assertEquals(Encodings.UTF_7, bestResult.getEncoding());
        assertTrue(bestResult.getConfidence() > 0 && bestResult.getConfidence() <= 1);
    }

    @Test
    void testCustomModelInvalid() {
        assertThrows(Decodetect.DecodetectInitializationException.class, () -> new Decodetect("bogus"));
    }

    @Test
    void testEmptyBytes() {
        Decodetect decodetect = new Decodetect(TestModels.getTestModels());
        List<DecodetectResult> results = decodetect.getResults(new byte[]{});
        assertTrue(results.isEmpty());
    }

    @Test
    void testLoadBundledModel() throws Decodetect.DecodetectInitializationException {
        Decodetect decodetect = new Decodetect();
        List<DecodetectResult> results = decodetect.getResults("Hey this is some sample text!".getBytes());
        assertFalse(results.isEmpty());
        assertEquals(StandardCharsets.UTF_8, results.get(0).getEncoding());
        assertEquals("en", results.get(0).getLang());
    }
}
