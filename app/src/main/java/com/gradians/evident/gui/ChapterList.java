package com.gradians.evident.gui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.JsonReader;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gradians.evident.EvidentApp;
import com.gradians.evident.R;
import com.gradians.evident.dom.Chapter;
import com.gradians.evident.dom.Question;
import com.gradians.evident.dom.Skill;
import com.gradians.evident.dom.Snippet;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.parser.JSONArrayParser;
import com.koushikdutta.ion.Ion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by abhinavc on 2/6/15.
 */
public class ChapterList {

    public ChapterList() {
        chapters = new HashMap<>();
    }

    public Chapter getChapter(int chapterId) {
        return chapters.get(chapterId);
    }

    public Chapter[] getChapters() {
        Collection<Chapter> values = chapters.values();
        return values.toArray(new Chapter[values.size()]);
    }

    public HashMap<Integer, Chapter> chapters;

    public void loadCatalog(String idType, AssetManager amgr) {
        try {
            InputStream json = amgr.open("catalog/" + idType + ".json");
            JsonReader reader = new JsonReader(new InputStreamReader(json));
            reader.beginArray();
            while (reader.hasNext()) {
                reader.beginObject();
                int id = 0; String desc = "";
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("id")) {
                        id = reader.nextInt();
                    } else if (name.equals("name")) {
                        desc = reader.nextString();
                    }
                }
                reader.endObject();
                Chapter chapter = new Chapter(id, desc);
                chapters.put(id, chapter);
            }
            reader.endArray();
            reader.close();
        } catch (Exception ex) {
            Log.e("EvidentApp", ex.getMessage());
        }
    }

    public void download(final Activity caller) {
        JsonArray array = null;
        try {
            File json = new File(caller.getExternalFilesDir(null), "assets.json");
            BufferedReader reader = new BufferedReader(new FileReader(json));
            array = new JsonParser().parse(reader).getAsJsonArray();
        } catch (Exception e) { }

        int highestId = 0;
        if (array != null) {
            highestId = parseResult(array, 0);
        }
        final int last = highestId;
        Log.d("EvidentApp", "download started (last = " + last + ")");
        Ion.with(caller)
                .load("http://www.gradians.com/sku/list?last="+last)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {

                    @Override
                    public void onCompleted(Exception e, JsonArray jsonArray) {
                        int newHighestId = parseResult(jsonArray, last);
                        writeResults();
                        Log.d("EvidentApp", "download completed (last = " + newHighestId + ")");
                    }
                });
    }

    private int parseResult(JsonArray result, int highestId) {
        for (JsonElement element: result) {
            JsonObject jsonObject = element.getAsJsonObject();
            int id = jsonObject.get("id").getAsInt();
            int chapterId = jsonObject.get("chapter").getAsInt();
            String path = jsonObject.get("path").getAsString();
            int assetClass = jsonObject.get("type").getAsInt();

            Chapter chapter = getChapter(chapterId);
            if (chapter == null) continue;

            if (id > highestId) highestId = id;
            switch (assetClass) {
                case 4:
                    chapter.addSkill(new Skill(id, path));
                    break;
                case 2:
                    chapter.addSnippet(new Snippet(id, path));
                    break;
                default:
                    chapter.addQuestion(new Question(id, path));
            }
        }
        return highestId;
    }

    private void writeResults() {

    }

}
