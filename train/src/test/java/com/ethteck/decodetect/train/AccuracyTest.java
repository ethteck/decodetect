package com.ethteck.decodetect.train;

import com.ethteck.decodetect.core.DataFile;
import com.ethteck.decodetect.core.Decodetect;
import com.ethteck.decodetect.core.DecodetectResult;
import com.ethteck.decodetect.core.Encodings;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

class AccuracyTest {
    private int successes = 0;
    private int fails = 0;

    @Ignore
    @Test
    void run() throws Decodetect.DecodetectInitializationException, IOException {
        Decodetect decodetect = new Decodetect();
        ArrayList<DataFile> testFiles = DataFile.loadDataFiles("train/src/test/resources/data/test");
        long start = System.currentTimeMillis();
        runTests(decodetect, testFiles);
        long tests = System.currentTimeMillis();

        int totalTests = successes + fails;
        long testTime = tests - start;
        float avgTestTime = testTime / (float) testFiles.size();
        System.out.println("Ran " + totalTests + " tests in " + testTime + " ms (" + avgTestTime + " ms/test)");
        System.out.println(successes + " pass");
        System.out.println(fails + " fail");
    }

    private void runTests(Decodetect decodetect, ArrayList<DataFile> testFiles) throws IOException {
        for (DataFile testFile : testFiles) {
            byte[] fileBytes = testFile.loadBytes();
            String fileText = new String(fileBytes, testFile.getEncoding());
            List<Charset> applicableEncodings = Encodings.getCharsetsForLang(testFile.getLang());

            for (Charset testEncoding : applicableEncodings) {
                byte[] transcodedBytes = fileText.getBytes(testEncoding);

                runTest(decodetect, testFile, transcodedBytes, testEncoding);
            }
        }
    }

    private void runTest(Decodetect decodetect, DataFile file, byte[] bytes, Charset encoding) {
        List<DecodetectResult> modelResults = decodetect.getResults(bytes);

        Charset guessedEncoding = modelResults.get(0).getEncoding();

        if (!guessedEncoding.equals(encoding)) {
            fails++;
        } else {
            successes++;
        }
    }
}
