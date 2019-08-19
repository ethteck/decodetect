package com.ethteck.decodetect.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;

public class Model implements Serializable {
    private String encoding;
    private String lang;
    private NGramCounter counter;

    public Model(String encoding, String lang, NGramCounter counter) {
        this.encoding = encoding;
        this.lang = lang;
        this.counter = counter;
    }

    double getSimilarity(NGramCounter otherCounter) {
        double thisDot = counter.getDot();
        double otherDot = otherCounter.getDot();

        if (thisDot == 0 || otherDot == 0) {
            return 0;
        }

        double totalScore = 0;

        NGramCounter smallerModel = otherCounter.getSize() > counter.getSize() ? counter : otherCounter;

        for (Map.Entry<Integer, Double> entry : smallerModel.entrySet()) {
            double thisVal = counter.getValAt(entry.getKey());
            double otherVal = otherCounter.getValAt(entry.getKey());

            double score = thisVal * otherVal / (thisDot * otherDot);
            totalScore += score;
        }

        return totalScore;
    }

    @Override
    public String toString() {
        return (encoding == null ? "" : encoding) + " " + (lang == null ? "" : lang);
    }

    /**
     * Returns the encoding this model represents
     * @return a {@link Charset} that this model represents
     */
    public Charset getEncoding() {
        if (encoding.equalsIgnoreCase("utf-7")) {
            return Encodings.UTF_7;
        }
        return Charset.forName(encoding);
    }

    /**
     * Returns the language code that this model represents
     * @return the language code
     */
    public String getLang() {
        return lang;
    }

    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException {
        lang = aInputStream.readUTF();
        encoding = aInputStream.readUTF();
        counter = (NGramCounter) aInputStream.readObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws IOException {
        aOutputStream.writeUTF(lang);
        aOutputStream.writeUTF(encoding);
        aOutputStream.writeObject(counter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Model model = (Model) o;
        return Objects.equals(encoding, model.encoding) &&
                Objects.equals(lang, model.lang) &&
                Objects.equals(counter, model.counter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(encoding, lang, counter);
    }
}
