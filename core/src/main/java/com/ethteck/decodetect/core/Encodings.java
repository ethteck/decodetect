package com.ethteck.decodetect.core;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.beetstra.jutf7.CharsetProvider;

public final class Encodings {
	private static final CharsetProvider UTF7CharsetProvider = new CharsetProvider();

	static final Charset UTF_7 = UTF7CharsetProvider.charsetForName("UTF-7");

	private static final Charset[] COMMON = new Charset[] {
			UTF_7,
			StandardCharsets.UTF_8,
			StandardCharsets.UTF_16BE,
			StandardCharsets.UTF_16LE,
			Charset.forName("UTF_32LE"),
			Charset.forName("UTF_32BE"),
	};

	private static final Charset[] ARABIC = new Charset[] {
			Charset.forName("ISO-8859-6"), Charset.forName("windows-1256")
	};

	private static final Charset[] BULGARIAN = new Charset[] {
			Charset.forName("ISO-8859-5"), Charset.forName("windows-1251")
	};

	private static final Charset[] CHINESE = new Charset[] {
			Charset.forName("Big5"), Charset.forName("EUC-TW"), Charset.forName("GB18030"), Charset.forName("GB2312")
	};

	private static final Charset[] DANISH = new Charset[] {
			StandardCharsets.ISO_8859_1, Charset.forName("ISO-8859-15"), Charset.forName("windows-1252")
	};

	private static final Charset[] ENGLISH = new Charset[] {
			StandardCharsets.US_ASCII
	};

	private static final Charset[] ESPERANTO = new Charset[] {
			Charset.forName("ISO-8859-3")
	};

	private static final Charset[] FRENCH = new Charset[] { // same as Danish
		StandardCharsets.ISO_8859_1, Charset.forName("ISO-8859-15"), Charset.forName("windows-1252")
	};

	private static final Charset[] GERMAN = new Charset[] {
			StandardCharsets.ISO_8859_1, Charset.forName("windows-1252")
	};

	private static final Charset[] GREEK = new Charset[] {
			Charset.forName("ISO-8859-7"), Charset.forName("windows-1253")
	};

	private static final Charset[] HEBREW = new Charset[] {
			Charset.forName("ISO-8859-8"), Charset.forName("windows-1255")
	};

	private static final Charset[] HUNGARIAN = new Charset[] {
			Charset.forName("ISO-8859-2"), Charset.forName("windows-1250")
	};

	private static final Charset[] JAPANESE = new Charset[] {
			// look into x-JISAutoDetect
			Charset.forName("ISO-2022-JP"), Charset.forName("ISO-2022-JP-2"), Charset.forName("Shift_JIS"),
			Charset.forName("EUC-JP"), Charset.forName("windows-31j"),

			// Optional
			//Charset.forName("x-SJIS_0213"),
			//Charset.forName("x-MS932_0213"),
			//Charset.forName("x-PCK"),
			//Charset.forName("x-windows-iso2022jp"),
			//Charset.forName("x-IBM33722"),
			//Charset.forName("x-eucJP-Open"),
			//Charset.forName("x-euc-jp-linux")
	};

	private static final Charset[] KOREAN = new Charset[] {
			// x-IBM970
			Charset.forName("ISO-2022-KR"), Charset.forName("EUC-KR")
	};

	private static final Charset[] RUSSIAN = new Charset[] {
			Charset.forName("ISO-8859-5"), Charset.forName("KOI8-R"), Charset.forName("windows-1251"),
			Charset.forName("x-MacCyrillic"), Charset.forName("IBM855"), Charset.forName("IBM866")
	};

	private static final Charset[] SPANISH = new Charset[] {
			StandardCharsets.ISO_8859_1, Charset.forName("ISO-8859-15"), Charset.forName("windows-1252")
	};

	private static final Charset[] THAI = new Charset[] {
			Charset.forName("TIS-620"), Charset.forName("ISO-8859-11")
	};

	private static final Charset[] TURKISH = new Charset[] {
			Charset.forName("ISO-8859-9"), Charset.forName("ISO-8859-3")
	};

	private static final Charset[] VIETNAMESE = new Charset[] {
			Charset.forName("windows-1258")
	};

	private static final HashMap<String, List<Charset>> langToEncodingsMap = new HashMap<>();

	static {
		langToEncodingsMap.put("ar", Arrays.asList(ARABIC));
		langToEncodingsMap.put("bg", Arrays.asList(BULGARIAN));
		langToEncodingsMap.put("zh", Arrays.asList(CHINESE));
		langToEncodingsMap.put("da", Arrays.asList(DANISH));
		langToEncodingsMap.put("en", Arrays.asList(ENGLISH));
		langToEncodingsMap.put("eo", Arrays.asList(ESPERANTO));
		langToEncodingsMap.put("fr", Arrays.asList(FRENCH));
		langToEncodingsMap.put("de", Arrays.asList(GERMAN));
		langToEncodingsMap.put("el", Arrays.asList(GREEK));
		langToEncodingsMap.put("he", Arrays.asList(HEBREW));
		langToEncodingsMap.put("hu", Arrays.asList(HUNGARIAN));
		langToEncodingsMap.put("ja", Arrays.asList(JAPANESE));
		langToEncodingsMap.put("ko", Arrays.asList(KOREAN));
		langToEncodingsMap.put("ru", Arrays.asList(RUSSIAN));
		langToEncodingsMap.put("es", Arrays.asList(SPANISH));
		langToEncodingsMap.put("th", Arrays.asList(THAI));
		langToEncodingsMap.put("tr", Arrays.asList(TURKISH));
		langToEncodingsMap.put("vi", Arrays.asList(VIETNAMESE));
	}

	public static List<Charset> getCharsetsForLang(String lang) {
		if (!langToEncodingsMap.containsKey(lang)) {
			throw new IllegalArgumentException("Lang: " + lang + " not supported!");
		}
		ArrayList<Charset> ret = new ArrayList<>(langToEncodingsMap.get(lang));
		ret.addAll(Arrays.asList(COMMON));
		return ret;
	}

	public static Set<String> getLangs() {
		return langToEncodingsMap.keySet();
	}
}
