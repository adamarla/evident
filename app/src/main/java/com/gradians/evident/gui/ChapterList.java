package com.gradians.evident.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.gradians.evident.activity.SelectChapter;
import com.gradians.evident.dom.Asset;
import com.gradians.evident.dom.Chapter;
import com.gradians.evident.dom.Question;
import com.gradians.evident.dom.Skill;
import com.gradians.evident.dom.Snippet;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;

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

    public void loadCatalog(String idType, AssetManager assetManager) {
        try {
            InputStream json = assetManager.open("catalog/" + idType + ".json");
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
            if (json.exists()) {
                JsonReader reader = new JsonReader(new FileReader(json));
                JsonParser parser = new JsonParser();
                array = parser.parse(reader).getAsJsonArray();
            }
        } catch (Exception e) {
            Log.e("EvidentApp", "Error reading assets.json " + e.getMessage());
        }

        int highestId = 0;
        if (array != null) highestId = parseResult(array, 0);

        final int last = highestId;
        final ProgressDialog dialog = ((SelectChapter)caller).getProgressDialog();
        Log.d("EvidentApp", "download started (last = " + last + ")");
        Ion.with(caller)
                .load("http://www.gradians.com/sku/list?last=" + last)
                .progressDialog(dialog)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {

                    @Override
                    public void onCompleted(Exception e, JsonArray jsonArray) {
                        if (e == null) {
                            int newHighestId = parseResult(jsonArray, last);
                            Log.d("EvidentApp", "download completed (last = " + newHighestId + ")");
                            try {
                                writeResults(caller);
                            } catch (Exception ex) {
                                Log.e("EvidentApp", "Error serializing assets.json " + ex.getMessage());
                            }
                        } else {
                            Log.e("EvidentApp", "Error retrieving assets.json " + e.getMessage());
                        }
                        dialog.cancel();
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

    private void writeResults(Activity caller) throws Exception {
        File json = new File(caller.getExternalFilesDir(null), "assets.json");
        JsonWriter writer = new JsonWriter(new FileWriter(json));

        writer.beginArray();
        for (Chapter chapter : chapters.values()) {
            for (Asset asset: chapter.getAllAssets()) {
                int assetType = asset.getPath().contains("skills") ? 4 :
                        (asset.getPath().contains("snippets") ? 2 : 1) ;
                writer.beginObject();
                writer.name("id").value(asset.getId());
                writer.name("chapter").value(chapter.id);
                writer.name("path").value(asset.getPath());
                writer.name("type").value(assetType);
                writer.endObject();
            }
        }
        writer.endArray();
        writer.close();
    }

}
