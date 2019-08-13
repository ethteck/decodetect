import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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

class ModelTrainer {
    private ModelTrainer() {
        long start = System.currentTimeMillis();
        ArrayList<DataFile> trainingFiles = Util.loadData("src/main/resources/data/seed");
        long dataLoad = System.currentTimeMillis();
        Models models = trainModels(trainingFiles);
        long trained = System.currentTimeMillis();
        try {
            writeModels(models);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long saved = System.currentTimeMillis();

        System.out.println("data: " + (dataLoad - start));
        System.out.println("trained: " + (trained - dataLoad));
        System.out.println("written: " + (saved - trained));
    }

    private void writeModels(Models models) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File("src/main/resources/model.mdl"));
        ObjectOutputStream out = new ObjectOutputStream(fos);

        out.writeObject(models);

        out.close();
        fos.close();
    }

    private static Models trainModels(ArrayList<DataFile> dataFiles) {
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
            models.add(new Model(entry.getKey(), entry.getValue()));
        }

        return new Models(models);
    }

    public static void main(String[] args) {
        ModelTrainer modelTrainer = new ModelTrainer();
    }
}
