package com.ethteck.decodetect.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Models implements Serializable {
    private ArrayList<Model> models;

    public Models(ArrayList<Model> models) {
        this.models = models;
    }

    ArrayList<Model> getModels() {
        return models;
    }

    static Models readFromFile(String path, boolean bundled) throws IOException, ClassNotFoundException {
        InputStream mis;
        if (bundled) {
            mis = Models.class.getResourceAsStream(path);
        } else {
            mis = new FileInputStream(path);
        }
        ObjectInputStream ois = new ObjectInputStream(mis);
        Models modelsRead = (Models) ois.readObject();
        ois.close();
        mis.close();
        return modelsRead;
    }

    private void readObject(ObjectInputStream aInputStream) throws IOException, ClassNotFoundException {
        models = (ArrayList<Model>) aInputStream.readObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws IOException {
        aOutputStream.writeObject(models);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Models models1 = (Models) o;
        return Objects.equals(models, models1.models);
    }

    @Override
    public int hashCode() {
        return Objects.hash(models);
    }

    public void writeToFile(String path) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(path));
        ObjectOutputStream out = new ObjectOutputStream(fos);

        out.writeObject(this);

        out.close();
        fos.close();
    }
}
