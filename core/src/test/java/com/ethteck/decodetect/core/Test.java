package com.ethteck.decodetect.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class Test {
    private Test() throws IOException, Decodetect.DecodetectInitializationException {
        long start = System.currentTimeMillis();
        long modelLoad = System.currentTimeMillis();
        ArrayList<DataFile> testFiles = Util.loadData("src/test/resources/data/test");
        long dataLoad = System.currentTimeMillis();
        runTests(testFiles);
        long tests = System.currentTimeMillis();

        System.out.println("model load: " + (modelLoad - start));
        System.out.println("data load: " + (dataLoad - modelLoad));
        long testTime = tests - modelLoad;
        System.out.println("tests: " + testFiles.size() + " tests in " + testTime);
        System.out.println(testTime / (float) testFiles.size() + " ms/test");
    }

    private void runTests(ArrayList<DataFile> testFiles) throws IOException, Decodetect.DecodetectInitializationException {
        for (DataFile testFile : testFiles) {
            HashMap<Integer, Double> testCounter = new HashMap<>();
            byte[] fileBytes = Util.getBytesFromFile(testFile.getPath());
            Util.addDataToCounter(testCounter, fileBytes);

            runTest(testFile, fileBytes);
        }
    }

    private void runTest(DataFile file, byte[] fileBytes) throws Decodetect.DecodetectInitializationException {
        Decodetect decodetect = new Decodetect();

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

    public static void main(String[] args) {
        try {
            Test test = new Test();
        } catch (IOException | Decodetect.DecodetectInitializationException e) {
            e.printStackTrace();
        }
    }
}
