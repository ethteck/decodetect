package com.ethteck.decodetect.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class TestDecodetectResult {
    @Test
    void testCompare() {
        DecodetectResult first = new DecodetectResult(StandardCharsets.UTF_8, "en", 0.5);
        DecodetectResult second = new DecodetectResult(StandardCharsets.UTF_8, "en", 0.6);

        assertTrue(first.compareTo(second) < 0);
    }

    @Test
    void testToString() {
        DecodetectResult tmp = new DecodetectResult(Encodings.UTF_7, "ja", 0.5);
        assertEquals("0.50 (UTF-7 ja)", tmp.toString());
    }
}
