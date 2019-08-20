package com.ethteck.decodetect.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class Decodetect {
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

    Decodetect(Models models) {
        this.models = models;
    }

    /**
     * Returns a list of {@link DecodetectResult} representing the most likely encodings for the given bytes
     * @param bytes bytes for which the Charset will be detected
     * @return a Charset representing the most likely encoding for the given bytes
     */
    public List<DecodetectResult> getResults(byte[] bytes) {
        NGramCounter.Builder nGramCounterBuilder = new NGramCounter.Builder();
        nGramCounterBuilder.addData(bytes);
        NGramCounter nGramCounter = new NGramCounter.Builder().addData(bytes).build();

        ArrayList<DecodetectResult> results = new ArrayList<>();
        for (Model model : models.getModels()) {
            double score = model.getSimilarity(nGramCounter);
            if (score > MIN_SCORE_THRESHOLD) {
                results.add(new DecodetectResult(model.getEncoding(), model.getLang(), score));
            }
        }

        return results.stream()
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());
    }

    public static class DecodetectInitializationException extends Exception {
        DecodetectInitializationException(String m, Exception e) {
            super(m, e);
        }
    }
}
