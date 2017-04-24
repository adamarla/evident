package com.gradians.evident;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.gradians.evident.dom.Chapter;
import com.gradians.evident.dom.Question;
import com.gradians.evident.dom.Skill;
import com.gradians.evident.dom.Snippet;
import com.gradians.evident.util.TeXMacros;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

/**
 * Created by adamarla on 3/19/17.
 */

public class EvidentApp extends Application {

    public static EvidentApp app;

    @Override
    public void onCreate() {
        super.onCreate();
        EvidentApp.app = this;
        chapters = new HashMap<>();
        questionById = new HashMap<>();
        ctx = this.getApplicationContext();
        AssetManager amgr = ctx.getAssets();
        TeXMacros.init(amgr);
        loadCatalog("chapters", amgr);
    }

    private void download(final Chapter chapter) {
        Log.d("EvidentApp", "Chapter " + chapter.id);
        Ion.with(ctx)
            .load("http://www.gradians.com/sku/list?c=" + chapter.id)
            .asJsonArray()
            .setCallback(new FutureCallback<JsonArray>() {

                @Override
                public void onCompleted(Exception e, JsonArray jsonArray) {
                    parseResult(chapter, jsonArray);
                }
            });
    }

    private void parseResult(Chapter chapter, JsonArray result) {
        for (JsonElement element: result) {
            JsonObject jsonObject = element.getAsJsonObject();
            int id = jsonObject.get("id").getAsInt();
            String path = jsonObject.get("path").getAsString();
            String assetClass = jsonObject.get("assetClass").getAsString();

            switch (assetClass) {
                case "Skill":
                    chapter.addSkill(new Skill(id, path));
                    break;
                case "Snippet":
                    chapter.addSnippet(new Snippet(id, path));
                    break;
                default:
                    chapter.addQuestion(new Question(id, path));
            }
            Log.d("EvidentApp", id + " " + path);
        }
    }

    private void loadCatalog(String idType, AssetManager amgr) {
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
                download(chapter);
            }
            reader.endArray();
            reader.close();
        } catch (Exception ex) {
            Log.e("EvidentApp", ex.getMessage());
        }
    }

    Context ctx;
    public HashMap<Integer, Chapter> chapters;
    public HashMap<Integer, Question> questionById;
}


