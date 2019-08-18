package com.ethteck.decodetect.core;

import java.util.Map;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestNGramCounter {

    @Test
    void testWeightsAndDot() {
        // Two bigrams with even distribution (1 occurrence for each)
        byte[] testBytes = {1, 2, 3};
        NGramCounter counter = new NGramCounter.Builder().addData(testBytes).build();

        for (Map.Entry<Integer, Double> entry : counter.entrySet()) {
            assertEquals(0.5, entry.getValue());
        }

        assertEquals(Math.sqrt(0.5), counter.getDot());
    }

}
