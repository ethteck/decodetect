package com.ethteck.decodetect.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

class TestDecodetect {
    // todo move these
    /*@Test
    void testGetResults() {
        String shigeru = "京都府船井郡園部町（現・南丹市）に生まれる[1]。";
        byte[] stringBytes = shigeru.getBytes(Encodings.UTF_7);

        List<DecodetectResult> results = DECODETECT.getResults(stringBytes);
        assertFalse(results.isEmpty());

        DecodetectResult bestResult = results.get(0);
        assertEquals("ja", bestResult.getLang());
        assertEquals(Encodings.UTF_7, bestResult.getEncoding());
        assertTrue(bestResult.getConfidence() > 0 && bestResult.getConfidence() <= 1);
    }

    @Test
    void testShortChineseString() {
        String china = "中華人民共和國與中華民國（現台澎金马地区政府）因中國內戰後遺留有國土主權重疊的爭議和「一中原則」的緣故，" +
            "有部份國家不承認中華人民共和國的存在而承認中華民國為「中國」，也造成了所謂的「台灣問題」。";
        for (Charset charset : Encodings.getCharsetsForLang("zh")) {
            byte[] testBytes = china.getBytes(charset);
            List<DecodetectResult> results = DECODETECT.getResults(testBytes);
            DecodetectResult topResult = results.get(0);
            assertEquals(charset, topResult.getEncoding());
        }
    }*/

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
}
