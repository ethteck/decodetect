package com.ethteck.decodetect.core;

import java.nio.charset.Charset;

public class DecodetectResult implements Comparable<DecodetectResult> {
    private Charset encoding;
    private String lang;
    private double confidence;

    DecodetectResult(Charset encoding, String lang, double confidence) {
        this.encoding = encoding;
        this.lang = lang;
        this.confidence = confidence;
    }

    /**
     * @return the encoding for this result
     */
    public Charset getEncoding() {
        return encoding;
    }

    /**
     * @return the language for this result
     */
    public String getLang() {
        return lang;
    }

    /**
     * @return the confidence for this result
     */
    public double getConfidence() {
        return confidence;
    }

    @Override
    public String toString() {
        String langStr = lang.isEmpty() ? "" : " " + lang;
        return String.format("%.2f (%s%s)", confidence, encoding.name(), langStr);
    }

    @Override
    public int compareTo(DecodetectResult o) {
        return Double.compare(confidence, o.confidence);
    }
}
