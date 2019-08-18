package com.ethteck.decodetect.train;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.ethteck.decodetect.core.Encodings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class WikiDownload {
    private static final String SEED_DIR = "src/main/resources/data/seed/";

    private WikiDownload() {
    }

    private void dlData() {
        for (String lang : Encodings.getLangs()) {
            int numToDownload = 100;
            HashMap<String, String> articles = new HashMap<>();

            int numAlreadyDownloaded = getNumAlreadyDownloaded(lang);
            if (numAlreadyDownloaded > 0) {
                System.out.println(numAlreadyDownloaded + " files for " + lang + " were already downloaded");
            }

            numToDownload -= numAlreadyDownloaded;

            int numLeft = numToDownload;
            while (numLeft > 0) {
                System.out.println("Downloading " + numLeft + " " + lang + " articles...");
                HashMap<String, String> pages = getPages(lang, numLeft);

                for (Map.Entry<String, String> entry : pages.entrySet()) {
                    if (entry.getValue().length() > 1000) {
                        articles.put(entry.getKey(), entry.getValue());
                    }
                }

                numLeft = numToDownload - articles.size();
                if (numLeft > 0) {
                    System.out.print(numLeft + " were not large enough to be used. ");
                }
            }
            saveArticles(lang, articles);
        }
    }

    private int getNumAlreadyDownloaded(String lang) {
        String langDir = SEED_DIR + lang + "/utf-8/";
        long count;
        try (Stream<Path> files = Files.list(Paths.get(langDir))) {
            count = files.count();
        } catch (IOException e) {
            return 0;
        }
        return (int) count;
    }

    private void saveArticles(String lang, HashMap<String, String> articles) {
        String langDir = SEED_DIR + lang + "/utf-8/";
        try {
            Files.createDirectories(Paths.get(langDir));
        } catch (IOException e) {
            throw new RuntimeException(e); //todo convert
        }

        for (Map.Entry<String, String> article : articles.entrySet()) {
            String articleName = article.getKey();
            articleName = articleName.replaceAll("/", "_");
            File file = new File(langDir + articleName + ".txt");
            try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
                out.write(article.getValue());
            } catch (IOException e) {
                throw new RuntimeException(e); //todo convert
            }
        }
    }

    private HashMap<String, String> getPages(String lang, int numLeft) {
        String queryURL = "https://" + lang + ".wikipedia.org/w/api.php?action=query&list=random&rnnamespace=0" +
                "&format=json&rnlimit=" + numLeft;
        JsonObject root = getJsonObjectFromURL(queryURL);

        ArrayList<String> titles = new ArrayList<>();
        JsonObject rootobj = root.getAsJsonObject();
        JsonArray titlesJson = rootobj.get("query").getAsJsonObject().get("random").getAsJsonArray();
        for (JsonElement element : titlesJson) {
            String title = element.getAsJsonObject().get("title").getAsString();
            titles.add(title);
        }
        System.out.println("\tfetched page titles");
        return getPagesText(lang, titles);
    }

    private static HashMap<String, String> getPagesText (String lang, Collection<String> titles) {
        HashMap<String, String> ret = new HashMap<>();
        String base = "https://" + lang + ".wikipedia.org/w/api.php?" +
                "action=query&prop=extracts&explaintext=y&exsectionformat=plain&exlimit=1&format=json&titles=";

        for (String title : titles) {
            String queryURL = base + title;
            queryURL = queryURL.replaceAll(" ", "%20");
            JsonObject root = getJsonObjectFromURL(queryURL);
            JsonObject pageData = root.get("query").getAsJsonObject().get("pages").getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : pageData.entrySet()) {
                String pageText = entry.getValue().getAsJsonObject().get("extract").getAsString();
                ret.put(title, pageText);
                System.out.println("\tdownloaded " + lang + "/" + title);
            }
        }
        return ret;
    }

    private static JsonObject getJsonObjectFromURL(String webURL) {
        JsonElement root;
        try {
            URL url = new URL(webURL);
            URLConnection request = url.openConnection();
            request.connect();
            JsonParser jp = new JsonParser();
            root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
        } catch (IOException e) {
            throw new RuntimeException(e); //todo convert
        }
        return root.getAsJsonObject();
    }

    public static void main(String[] args) {
        WikiDownload wikiDownload = new WikiDownload();
        wikiDownload.dlData();
    }
}
