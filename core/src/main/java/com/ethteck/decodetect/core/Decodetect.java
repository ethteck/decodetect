package com.ethteck.decodetect.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Decodetect {
    private static final String BUNDLED_MODEL_LOCATION = "/data/model.mdl";

    private static final double MIN_SCORE_THRESHOLD = 0.001;

    private final Models models;

    public Decodetect() throws DecodetectInitializationException {
        try {
            models = Models.readFromFile(BUNDLED_MODEL_LOCATION, true);
        } catch (IOException | ClassNotFoundException e) {
            throw new DecodetectInitializationException("The bundled model file could not be loaded", e);
        }
    }

    public Decodetect(String modelPath) throws DecodetectInitializationException {
        try {
            models = Models.readFromFile(modelPath, false);
        } catch (IOException | ClassNotFoundException e) {
            throw new DecodetectInitializationException("The model file at " + modelPath + " could not be loaded", e);
        }
    }

    /**
     * Returns a list of {@link DecodetectResult} representing the most likely encodings for the given bytes
     * @param bytes bytes for which the Charset will be detected
     * @return a Charset representing the most likely encoding for the given bytes
     */
    public List<DecodetectResult> getResults(byte[] bytes) {
        HashMap<Integer, Double> inputCounter = new HashMap<>();
        Util.addDataToCounter(inputCounter, bytes);
        Model inputModel = new Model(null, null, inputCounter);

        ArrayList<DecodetectResult> results = new ArrayList<>();
        for (Model trainedModel : models.getModels()) {
            double score = getModelScore(inputModel, trainedModel);
            results.add(new DecodetectResult(trainedModel.getEncoding(), trainedModel.getLang(), score));
        }

        return results.stream()
                .filter(result -> result.getConfidence() > MIN_SCORE_THRESHOLD)
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());
    }


    private static double getModelScore(Model testData, Model trainedModel) {
        double testDot = testData.getDot();
        double trainDot = trainedModel.getDot();

        if (testDot == 0 || trainDot == 0) {
            return 0;
        }

        double score = 0;

        Model smallerModel = testData.getCounter().size() > trainedModel.getCounter().size() ? trainedModel : testData;

        for (Map.Entry<Integer, Double> entry : smallerModel.getCounter().entrySet()) {
            int key = entry.getKey();
            double testRaw = testData.getCounter().getOrDefault(key, 0d);
            double trainRaw = trainedModel.getCounter().getOrDefault(key, 0d);

            double byteScore = testRaw * trainRaw / (testDot * trainDot);
            score += byteScore;
        }

        return score;
    }

    static class DecodetectInitializationException extends Exception {
        DecodetectInitializationException(String m, Exception e) {
            super(m, e);
        }
    }
}
