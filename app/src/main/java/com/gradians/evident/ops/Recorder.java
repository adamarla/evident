package com.gradians.evident.ops;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.gradians.evident.dom.Asset;
import com.gradians.evident.dom.Chapter;
import com.gradians.evident.dom.Question;
import com.gradians.evident.dom.Step;
import com.gradians.evident.gui.ICard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Created by adamarla on 5/16/17.
 */

public class Recorder {

    public Recorder(Context context, Chapter chapter) {
        File usage = new File(context.getExternalFilesDir(null), "usage");
        thisChapter = new File(usage, "ch" + chapter.id);
        properties = new Properties();
        try {
            if (!usage.exists()) usage.mkdir();
            if (!thisChapter.exists()) thisChapter.createNewFile();
            properties.load(new FileInputStream(thisChapter));
        } catch (Exception e) {
            Log.e("EvidentApp", "Recorder file open failed: " + e.getMessage());
        }
    }

    public void writeToRecord(Asset asset) {
        ICard card = asset.getCard();
        if (card.wasAttempted()) {
            String key = getKey(asset);
            String value = String.valueOf(card.getAttempt());
            if (card.hasFurtherSteps()) {
                Step[] steps = ((Question)asset).getSteps();
                Boolean[] attempts = new Boolean[steps.length];
                for (int i = 0; i < steps.length; i++)
                    attempts[i] = steps[i].getAttempt();
                value = TextUtils.join(",", attempts);
            }
            properties.put(key, value);
        }
    }

    public void readFromRecord(Asset asset) {
        ICard card = asset.getCard();
        String key = getKey(asset);
        if (properties.get(key) != null) {
            String value = (String)properties.get(key);
            if (card.hasFurtherSteps()) {
                Step[] steps = ((Question)asset).getSteps();
                String[] values = value.split(",");
                for (int i = 0; i < steps.length; i++)
                    steps[i].setAttempt(Boolean.parseBoolean(values[i]));

                card.setAttempt(Boolean.parseBoolean(value));
            } else {
                card.setAttempt(Boolean.parseBoolean(value));
            }
        }
    }

    public void commit() {
        try {
            properties.store(new FileOutputStream(thisChapter), null);
        } catch (Exception e) {
            Log.e("EvidentApp", e.getMessage());
        }
    }

    private Properties properties;
    private File thisChapter;

    private String getKey(Asset asset) {
        return asset.getId() + "-" + asset.getPath();
    }

}
