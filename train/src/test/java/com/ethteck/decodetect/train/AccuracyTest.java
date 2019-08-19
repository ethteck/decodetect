package com.ethteck.decodetect.train;

import com.ethteck.decodetect.core.DataFile;
import com.ethteck.decodetect.core.Decodetect;
import com.ethteck.decodetect.core.DecodetectResult;
import com.ethteck.decodetect.core.Util;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;

class AccuracyTest {

    @Test
    void run() throws Decodetect.DecodetectInitializationException, IOException {
        long start = System.currentTimeMillis();
        Decodetect decodetect = new Decodetect();
        long modelLoad = System.currentTimeMillis();
        ArrayList<DataFile> testFiles = Util.loadData("train/src/test/resources/data/test");
        long dataLoad = System.currentTimeMillis();
        runTests(decodetect, testFiles);
        long tests = System.currentTimeMillis();

        System.out.println("model load: " + (modelLoad - start));
        System.out.println("data load: " + (dataLoad - modelLoad));
        long testTime = tests - modelLoad;
        System.out.println("tests: " + testFiles.size() + " tests in " + testTime);
        System.out.println(testTime / (float) testFiles.size() + " ms/test");
    }

    private void runTests(Decodetect decodetect, ArrayList<DataFile> testFiles) throws IOException {
        for (DataFile testFile : testFiles) {
            byte[] fileBytes = Util.getBytesFromFile(testFile.getPath());

            runTest(decodetect, testFile, fileBytes);
        }
    }

    private void runTest(Decodetect decodetect, DataFile file, byte[] fileBytes) {


        List<DecodetectResult> modelResults = decodetect.getResults(fileBytes);
        Charset correctEncoding = file.getEncoding();

        for (DecodetectResult result : modelResults) {
            Charset guessedEncoding = result.getEncoding();

            if (!guessedEncoding.equals(correctEncoding)) {
                CharsetDecoder guessedDecoder = guessedEncoding.newDecoder();
                CharsetDecoder correctDecoder = correctEncoding.newDecoder();
                CharBuffer guessedDecodeResult;
                CharBuffer correctDecodeResult;
                String msg = file.getName() + ": " + correctEncoding + ", guessed " + guessedEncoding;
                try {
                    guessedDecodeResult = guessedDecoder.decode(ByteBuffer.wrap(fileBytes));
                    correctDecodeResult = correctDecoder.decode(ByteBuffer.wrap(fileBytes));
                    if (guessedDecodeResult.equals(correctDecodeResult)) {
                        System.out.println("O   " + msg);
                    } else {
                        System.out.println("    " + msg);
                    }
                } catch (CharacterCodingException e) {
                    System.out.println("    " + msg);
                }
            } else {
                System.out.println("Y   " + file.getName() + ": " + correctEncoding);
            }
            return;
        }
    }
}
