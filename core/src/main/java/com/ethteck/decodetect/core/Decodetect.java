package com.ethteck.decodetect.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Decodetect {
    private static final String BUNDLED_MODEL_LOCATION = "/data/model.mdl";

    private final Models models;

    public Decodetect() throws IOException, ClassNotFoundException{
        models = Models.readFromFile(BUNDLED_MODEL_LOCATION, true);
    }

    public Decodetect(String modelPath) throws IOException, ClassNotFoundException {
        models = Models.readFromFile(modelPath, false);
    }

    /**
     * Returns todo
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

        results.sort(Collections.reverseOrder());
        return results;
    }


    private static double getModelScore(Model testData, Model trainedModel) {
        double testDot = testData.getDot();
        double trainDot = trainedModel.getDot();

        double score = 0;

        Model smallerModel = testData.getCounter().size() > trainedModel.getCounter().size() ? trainedModel : testData;

        for (Map.Entry<Integer, Double> entry : smallerModel.getCounter().entrySet()) {
            int key = entry.getKey();
            double testRaw = testData.getCounter().getOrDefault(key, 0d);
            double trainRaw = trainedModel.getCounter().getOrDefault(key, 0d);

            // todo watch out for div/0
            double byteScore = testRaw * trainRaw / (testDot * trainDot);
            score += byteScore;
        }

        return score;
    }
}
