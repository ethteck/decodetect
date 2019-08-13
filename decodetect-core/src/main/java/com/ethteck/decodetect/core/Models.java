package com.ethteck.decodetect.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Models implements Serializable {
    private ArrayList<Model> models;

    public Models(ArrayList<Model> models) {
        this.models = models;
    }

    ArrayList<Model> getModels() {
        return models;
    }

    private void readObject(ObjectInputStream aInputStream) throws IOException, ClassNotFoundException {
        models = (ArrayList<Model>) aInputStream.readObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws IOException {
        aOutputStream.writeObject(models);
    }
}
