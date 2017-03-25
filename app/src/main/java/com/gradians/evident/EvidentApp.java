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

import com.gradians.evident.dom.Chapter;
import com.gradians.evident.util.TeXMacros;

/**
 * Created by adamarla on 3/19/17.
 */

public class EvidentApp extends Application {

    public static EvidentApp app;

    public EvidentApp() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EvidentApp.app = this;
        Log.d("EvidentApp", "Calling Application class onCreate -->");
        chapters = new HashMap<Integer, Chapter>();
        Context ctx = this.getApplicationContext();
        AssetManager amgr = ctx.getAssets();
        String[] idType = { "skills", "snippets", "questions" };
        JsonReader reader;
        try {
            for (String s : idType) {
                Log.d("EvidentApp", "catalog/" + s + ".json");
                InputStream json = amgr.open("catalog/" + s + ".json");
                reader = new JsonReader(new InputStreamReader(json));
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.beginObject();
                    int id = 0, cid = 0;
                    while (reader.hasNext()) {
                        String name = reader.nextName();
                        if (name.equals("id")) {
                            id = reader.nextInt();
                        } else {
                            if (reader.peek() != JsonToken.NULL)
                                cid = reader.nextInt();
                            else
                                reader.nextNull();
                        }
                    }
                    reader.endObject();
                    if (cid != 0) {
                        Chapter chapter = chapters.get(cid);
                        if (chapter == null) {
                            chapter = new Chapter(cid);
                            chapters.put(cid, chapter);
                        }
                        if (s.equals("skills")) {
                            chapter.addSkill(id);
                        } else if (s.equals("snippets")) {
                            chapter.addSnippet(id);
                        } else {
                            chapter.addProblem(id);
                        }
                        Log.d("EvidentApp", chapter.toString());
                    }
                }
                reader.endArray();
                reader.close();
                TeXMacros macros = new TeXMacros();
            }
        } catch (Exception ex) {
            Log.e("init", ex.getMessage());
        }
    }

    public HashMap<Integer, Chapter> chapters;
}
