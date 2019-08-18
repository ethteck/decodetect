package com.ethteck.decodetect.train;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ethteck.decodetect.core.DataFile;
import com.ethteck.decodetect.core.Encodings;
import com.ethteck.decodetect.core.Model;
import com.ethteck.decodetect.core.Models;
import com.ethteck.decodetect.core.NGramCounter;
import com.ethteck.decodetect.core.Util;

public class ModelTrainer {
    private ModelTrainer(String dataPath, String outputPath) throws IOException {
        long start = System.currentTimeMillis();
        ArrayList<DataFile> trainingFiles = Util.loadData(dataPath);
        long dataLoad = System.currentTimeMillis();
        Models models = trainModels(trainingFiles);
        long trained = System.currentTimeMillis();
        try {
            models.writeToFile(outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long saved = System.currentTimeMillis();

        System.out.println("data: " + (dataLoad - start));
        System.out.println("trained: " + (trained - dataLoad));
        System.out.println("written: " + (saved - trained));
    }

    private static Models trainModels(ArrayList<DataFile> dataFiles) throws IOException {
        HashMap<String, NGramCounter.Builder> counters = new HashMap<>();
        for (DataFile dataFile : dataFiles) {
            List<Charset> applicableEncodings = Encodings.getCharsetsForLang(dataFile.getLang());
            for (Charset transcoding : applicableEncodings) {
                if (!transcoding.canEncode()) {
                    continue;
                }

                String transcodingName = transcoding.name();
                String encodingKey = transcodingName + ",";
                String langKey = encodingKey + dataFile.getLang();
                if (!counters.containsKey(encodingKey)) {
                    counters.put(encodingKey, new NGramCounter.Builder());
                }
                if (!counters.containsKey(langKey)) {
                    counters.put(langKey, new NGramCounter.Builder());
            }
                NGramCounter.Builder langSpecificCounter = counters.get(langKey);
                NGramCounter.Builder generalEncodingCounter = counters.get(encodingKey);

                byte[] fileBytes = Util.getBytesFromFile(dataFile.getPath());
                String fileText = new String(fileBytes, dataFile.getEncoding());
                byte[] transcodedBytes = fileText.getBytes(transcoding);

                langSpecificCounter.addData(transcodedBytes);
                generalEncodingCounter.addData(transcodedBytes);
            }
        }

        ArrayList<Model> models = new ArrayList<>();

        for (Map.Entry<String, NGramCounter.Builder> entry : counters.entrySet()) {
            String[] keySplit = entry.getKey().split(",");
            String encoding = keySplit[0];
            String lang = keySplit.length == 2 ? keySplit[1] : "";
            models.add(new Model(encoding, lang, entry.getValue().build()));
        }

        return new Models(models);
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Invalid number of arguments: must be 2 but got " + args.length);
        }
        String dataPath = args[0];
        String outputPath = args[1];
        try {
            ModelTrainer modelTrainer = new ModelTrainer(dataPath, outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
