package com.ethteck.decodetect.train;

import com.ethteck.decodetect.core.Decodetect;
import com.ethteck.decodetect.core.DecodetectResult;
import com.ethteck.decodetect.core.Encodings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmallTests {
	private static Decodetect DECODETECT;

	@BeforeAll
	static void setup() throws Decodetect.DecodetectInitializationException {
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
            List<DecodetectResult> results = DECODETECT.getResults(testBytes);
            DecodetectResult topResult = results.get(0);
            assertEquals(charset, topResult.getEncoding());
        }
    }
}
