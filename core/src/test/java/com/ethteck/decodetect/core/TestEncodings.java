package com.ethteck.decodetect.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class TestEncodings {
    @Test
    void testGetCharsetsForLang() {
        List<Charset> jaCharsets =  Encodings.getCharsetsForLang("ja");
        assertFalse(jaCharsets.isEmpty());
    }

    @Test
    void testGetCharsetsForInvalidLang() {
        String invalidLang = "mq";
        Set<String> langs = Encodings.getLangs();
        assertFalse(langs.contains(invalidLang));
        assertThrows(IllegalArgumentException.class, () -> Encodings.getCharsetsForLang("mq"));
    }
}
