package com.ethteck.decodetect.core;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Decodetect {
    private static final Models models = readModels();

    private static Models readModels() {
        try {
            InputStream mis = Decodetect.class.getResourceAsStream("/data/model.mdl");
            ObjectInputStream ois = new ObjectInputStream(mis);
            Models modelsRead = (Models) ois.readObject();
            ois.close();
            mis.close();
            return modelsRead;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Detects and returns the most likely Charset for the input bytes
     * @param bytes bytes for which the Charset will be detected
     * @return a Charset representing the most likely encoding for the given bytes
     */
    public static Charset getBestResult(byte[] bytes) {
        LinkedHashMap<Model, Double> results = getResults(bytes);
        Map.Entry<Model, Double> bestResult = results.entrySet().iterator().next();
        return bestResult.getKey().getEncoding();
    }

    public static LinkedHashMap<Model, Double> getResults(byte[] bytes) {
        HashMap<Integer, Double> testCounter = new HashMap<>();
        Util.addDataToCounter(testCounter, bytes);
        Model testModel = new Model("UTF-8,", testCounter);

        HashMap<Model, Double> results = new HashMap<>();
        for (com.ethteck.decodetect.core.Model trainedModel : models.getModels()) {
            double score = getModelScore(testModel, trainedModel);
            results.put(trainedModel, score);
        }

        return results.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private static double getModelScore(Model testModel, Model trainModel) {
        double testDot = testModel.getDot();
        double trainDot = trainModel.getDot();

        double score = 0;

        Model smallerModel = testModel.getCounter().size() > trainModel.getCounter().size() ? trainModel : testModel;

        for (Map.Entry<Integer, Double> entry : smallerModel.getCounter().entrySet()) {
            int key = entry.getKey();
            double testRaw = testModel.getCounter().getOrDefault(key, 0d);
            double trainRaw = trainModel.getCounter().getOrDefault(key, 0d);

            double byteScore = testRaw * trainRaw / (testDot * trainDot);
            score += byteScore;
        }

        return score;
    }
}
