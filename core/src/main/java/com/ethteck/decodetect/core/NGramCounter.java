package com.ethteck.decodetect.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class NGramCounter implements Serializable {
    private final int n;
    private final HashMap<Integer, Double> counter;
    private final double dot;

    private NGramCounter(int n, HashMap<Integer, Double> counter, double dot) {
        this.n = n;
        this.counter = counter;
        this.dot = dot;
    }

    public double getDot() {
        return dot;
    }

    int getSize() {
        return counter.size();
    }

    Set<Map.Entry<Integer, Double>> entrySet() {
        return counter.entrySet();
    }

    double getValAt(int key) {
        return counter.getOrDefault(key, 0d);
    }

    public static class Builder {
        static final int n = 2; //todo change possibly?
        final HashMap<Integer, Double> counter;

        double totalNGrams = 0;

        public Builder() {
            counter = new HashMap<>();
        }

        public NGramCounter build() {
            double dot = normalizeAndCalcDot();
            return new NGramCounter(n, counter, dot);
        }

        public Builder addData(byte[] bytes) {
            for (int i = 0; i < bytes.length - n + 1; i++) {
                byte[] tmpBytes = new byte[n];
                System.arraycopy(bytes, i, tmpBytes, 0, n);
                int ord = getOrd(tmpBytes);
                counter.put(ord, counter.getOrDefault(ord, 0d) + 1);
                totalNGrams++;
            }
            return this;
        }

        private int getOrd(byte[] bytes) {
            int ord = 0;
            for (int i = 0; i < n; i++) {
                int val = Byte.toUnsignedInt(bytes[i]);
                ord += Math.pow(256, n - i - 1) * val;
            }
            return ord;
        }

        private double normalizeAndCalcDot() {
            double sumSquares = 0;

            for (Map.Entry<Integer, Double> entry : counter.entrySet()) {
                double normalized = entry.getValue() / totalNGrams;
                entry.setValue(entry.getValue() / totalNGrams);
                sumSquares += normalized * normalized;
            }

            return Math.sqrt(sumSquares);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NGramCounter that = (NGramCounter) o;
        return n == that.n &&
                Double.compare(that.dot, dot) == 0 &&
                Objects.equals(counter, that.counter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(n, counter, dot);
    }
}
