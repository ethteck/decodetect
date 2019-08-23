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

public class ModelTrainer {
    private ModelTrainer(String dataPath, String outputPath) throws IOException, DecodetectTrainingException {
        System.out.println("Training new Decodetect model with data at '" + dataPath + "'");
        System.out.print("Loading data files...");
        ArrayList<DataFile> trainingFiles = DataFile.loadDataFiles(dataPath);
        System.out.println("done!");
        Models models = trainModels(trainingFiles);
        System.out.print("Writing models to disk...");
        models.writeToFile(outputPath);
        System.out.println("done!");
        System.out.println("Models were successfully trained and saved at '" + outputPath + "'");
    }

    private static Models trainModels(ArrayList<DataFile> dataFiles) throws IOException, DecodetectTrainingException {
        HashMap<String, NGramCounter.Builder> counters = new HashMap<>();
        int fileCount = 0;
        for (DataFile dataFile : dataFiles) {
            fileCount++;
            double percent = fileCount / (double) dataFiles.size() * 100;
            if (fileCount % 50 == 0) {
                System.out.print(String.format("Training on data files... (%d/%d) %.1f", fileCount, dataFiles.size(), percent) + "%\r");
            }
            List<Charset> applicableEncodings = Encodings.getCharsetsForLang(dataFile.getLang());
            for (Charset transcoding : applicableEncodings) {
                if (!transcoding.canEncode()) {
                    throw new DecodetectTrainingException("Attempting to train on Charset " + transcoding.name() +
                            ", but this charset does not support encoding!");
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

                byte[] fileBytes = dataFile.loadBytes();
                String fileText = new String(fileBytes, dataFile.getEncoding());
                byte[] transcodedBytes = fileText.getBytes(transcoding);

                langSpecificCounter.addData(transcodedBytes);
                generalEncodingCounter.addData(transcodedBytes);
            }
        }
        System.out.println(String.format(
                "Training on data files...done! (%d/%d) %.1f", fileCount, dataFiles.size(), 100.0) + "%");

        ArrayList<Model> models = new ArrayList<>();

        System.out.print("Building models...\r");
        for (Map.Entry<String, NGramCounter.Builder> entry : counters.entrySet()) {
            String[] keySplit = entry.getKey().split(",");
            String encoding = keySplit[0];
            String lang = keySplit.length == 2 ? keySplit[1] : "";
            models.add(new Model(encoding, lang, entry.getValue().build()));
        }
        System.out.println("Building models...done!");

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
        } catch (IOException | DecodetectTrainingException e) {
            e.printStackTrace();
        }
    }

    private static class DecodetectTrainingException extends Exception {
        DecodetectTrainingException(String s) {
            super(s);
        }
    }
}
