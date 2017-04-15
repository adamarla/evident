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
import com.gradians.evident.dom.Question;
import com.gradians.evident.dom.Skill;
import com.gradians.evident.dom.Snippet;
import com.gradians.evident.util.TeXMacros;

/**
 * Created by adamarla on 3/19/17.
 */

public class EvidentApp extends Application {

    public static EvidentApp app;

    @Override
    public void onCreate() {
        super.onCreate();
        EvidentApp.app = this;
        Log.d("EvidentApp", "Calling Application class onCreate -->");
        chapters = new HashMap<>();
        questionById = new HashMap<>();
        Context ctx = this.getApplicationContext();
        AssetManager amgr = ctx.getAssets();
        TeXMacros.init(amgr);
        String[] idType = { "chapters", "skills", "snippets", "questions" };
        for (String s : idType) {
            loadCatalog(s, amgr);
        }
    }

    private void loadCatalog(String idType, AssetManager amgr) {
        try {
            InputStream json = amgr.open("catalog/" + idType + ".json");
            JsonReader reader = new JsonReader(new InputStreamReader(json));
            reader.beginArray();
            while (reader.hasNext()) {
                reader.beginObject();
                int id = 0, cid = 0;
                String path = "", desc = "";
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("id")) {
                        id = reader.nextInt();
                        path = "skills/" + id;
                    } else if (name.equals("cid")) {
                        if (reader.peek() != JsonToken.NULL)
                            cid = reader.nextInt();
                        else
                            reader.nextNull();
                    } else if (name.equals("path")) {
                        path = reader.nextString();
                    } else if (name.equals("name")) {
                        desc = reader.nextString();
                    }
                }
                reader.endObject();

                if (idType.equals("chapters")) {
                    chapters.put(id, new Chapter(id, desc));
                } else if (cid != 0) {
                    Chapter chapter = chapters.get(cid);
                    if (idType.equals("skills")) {
                        chapter.addSkill(new Skill(id, path));
                    } else if (idType.equals("snippets")) {
                        chapter.addSnippet(new Snippet(id, path));
                    } else {
                        Question q = new Question(id, path);
                        chapter.addQuestion(q);
                        questionById.put(id, q);
                    }
                }
            }
            reader.endArray();
            reader.close();
        } catch (Exception ex) {
            Log.e("EvidentApp", ex.getMessage());
        }
    }

    public HashMap<Integer, Chapter> chapters;
    public HashMap<Integer, Question> questionById;
}


