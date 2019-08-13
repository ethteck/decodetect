package com.ethteck.decodetect.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Model implements Serializable {
    private String lang;
    private String encoding;
    private HashMap<Integer, Double> counter;

    private double dot;

    public Model(String langAndEncoding, HashMap<Integer, Double> counter) {
        String[] langEncodingSplit = langAndEncoding.split(",");
        encoding = langEncodingSplit[0];
        if (langEncodingSplit.length == 1) {
            this.lang = "";
        } else {
            this.lang = langEncodingSplit[1];
        }
        this.counter = counter;

        normalizeWeights();
        calculateDot();
    }

    double getDot() {
        return dot;
    }

    private void normalizeWeights() {
        double sum = 0;
        for (Map.Entry<Integer, Double> entry : counter.entrySet()) {
            sum += entry.getValue();
        }

        for (Integer key : counter.keySet()) {
            counter.put(key, counter.get(key) / sum);
        }
    }

    private void calculateDot() {
        double sumSquares = 0;
        for (Integer key : counter.keySet()) {
            sumSquares += Math.pow(counter.get(key), 2);
        }
        this.dot = Math.sqrt(sumSquares);
    }

    HashMap<Integer, Double> getCounter() {
        return counter;
    }

    @Override
    public String toString() {
        return (encoding == null ? "null" : encoding) +
                (lang == null ? "null" : lang);
    }

    public Charset getEncoding() {
        if (encoding.equalsIgnoreCase("utf-7")) {
            return Encodings.UTF_7;
        }
        return Charset.forName(encoding);
    }

    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException {
        lang = aInputStream.readUTF();
        encoding = aInputStream.readUTF();
        counter = (HashMap<Integer, Double>) aInputStream.readObject();
        dot = aInputStream.readDouble();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws IOException {
        aOutputStream.writeUTF(lang);
        aOutputStream.writeUTF(encoding);
        aOutputStream.writeObject(counter);
        aOutputStream.writeDouble(dot);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Model model = (Model) o;
        return Double.compare(model.dot, dot) == 0 &&
                Objects.equals(lang, model.lang) &&
                Objects.equals(encoding, model.encoding) &&
                counter.equals(model.counter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lang, encoding, counter, dot);
    }
}
