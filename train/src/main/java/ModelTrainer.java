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
import com.ethteck.decodetect.core.Util;

public class ModelTrainer {
    private ModelTrainer() throws IOException {
        long start = System.currentTimeMillis();
        ArrayList<DataFile> trainingFiles = Util.loadData("src/main/resources/data/seed");
        long dataLoad = System.currentTimeMillis();
        Models models = trainModels(trainingFiles);
        long trained = System.currentTimeMillis();
        try {
            models.writeToFile("model.mdl");
        } catch (IOException e) {
            e.printStackTrace();
        }
        long saved = System.currentTimeMillis();

        System.out.println("data: " + (dataLoad - start));
        System.out.println("trained: " + (trained - dataLoad));
        System.out.println("written: " + (saved - trained));
    }

    private static Models trainModels(ArrayList<DataFile> dataFiles) throws IOException {
        HashMap<String, HashMap<Integer, Double>> counters = new HashMap<>();
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
                    counters.put(encodingKey, new HashMap<>());
                }
                if (!counters.containsKey(langKey)) {
                    counters.put(langKey, new HashMap<>());
                }
                HashMap<Integer, Double> langSpecificCounter = counters.get(langKey);
                HashMap<Integer, Double> generalEncodingCounter = counters.get(encodingKey);

                byte[] fileBytes = Util.getBytesFromFile(dataFile.getPath());
                String fileText = new String(fileBytes, dataFile.getEncoding());
                byte[] transcodedBytes = fileText.getBytes(transcoding);

                Util.addDataToCounter(langSpecificCounter, transcodedBytes);
                Util.addDataToCounter(generalEncodingCounter, transcodedBytes);
            }
        }

        ArrayList<Model> models = new ArrayList<>();

        for (Map.Entry<String, HashMap<Integer, Double>> entry : counters.entrySet()) {
            String[] keySplit = entry.getKey().split(",");
            String encoding = keySplit[0];
            String lang = keySplit.length == 2 ? keySplit[1] : "";
            models.add(new Model(encoding, lang, entry.getValue()));
        }

        return new Models(models);
    }

    public static void main(String[] args) {
        try {
            ModelTrainer modelTrainer = new ModelTrainer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
