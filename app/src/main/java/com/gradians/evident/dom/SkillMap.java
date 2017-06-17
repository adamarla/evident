package com.gradians.evident.dom;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by adamarla on 6/17/17.
 */

public class SkillMap {

    public static SkillMap getInstance(Context context) {
        if (instance == null) {
            instance = new SkillMap(context);
        }
        return instance;
    }

    private SkillMap(Context context) {
        File file = new File(context.getExternalFilesDir(null), "vault/skillmap.txt");
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
            load(fis);
        } catch (FileNotFoundException e) {
            Log.e("EvidentApp", "TeXSourceParser Error");
        }
    }

    public int[] getSkillId(String path) {
        String value = map.get(path);
        if (value == null) return null;
        String[] tokens = map.get(path).split(",");
        int[] ids = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) ids[i] = Integer.parseInt(tokens[i]);
        return ids;
    }

    private void load(FileInputStream fis) {
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        map = new HashMap<>();
        try {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(":");
                tokens[0] = tokens[0].trim();
                tokens[1] = tokens[1].trim();
                if (map.containsKey(tokens[0])) {
                    tokens[1] = map.get(tokens[0]) + "," + tokens[1];
                }
                map.put(tokens[0], tokens[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String> map;
    private static SkillMap instance;

}
