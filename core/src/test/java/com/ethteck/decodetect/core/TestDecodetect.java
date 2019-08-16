package com.ethteck.decodetect.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TestDecodetect {
    private static Decodetect DECODETECT;

    @BeforeAll
    static void setup() throws Exception{
        DECODETECT = new Decodetect();
    }

    @Test
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
            DecodetectResult testResult = DECODETECT.getResults(testBytes).get(0);
            assertEquals(charset, testResult.getEncoding());
        }
    }

    @Test
    void testCustomModel(@TempDir File tempDir) throws IOException, ClassNotFoundException {
        Models models = TestModels.getTestModels();
        models.writeToFile(tempDir + "/model.mdl");

        Decodetect custom = new Decodetect(tempDir + "/model.mdl");
        custom.getResults(new byte[]{1, 2, 3});
    }
}
