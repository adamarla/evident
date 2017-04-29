package com.gradians.evident;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.JsonReader;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.gradians.evident.dom.Chapter;
import com.gradians.evident.dom.Question;
import com.gradians.evident.dom.Skill;
import com.gradians.evident.dom.Snippet;
import com.gradians.evident.gui.ChapterList;
import com.himamis.retex.renderer.android.FactoryProviderAndroid;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
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
        chapterList = new ChapterList();
        initJLaTeXMath(getApplicationContext());
    }

    public ChapterList chapterList;

    private void initJLaTeXMath(Context context) {
        AssetManager amgr = context.getAssets();
        FactoryProvider.INSTANCE = new FactoryProviderAndroid(context);
        Log.d("EvidentApp", "Macros: static block initialized");

        Map<String, String> map = TeXFormula.predefinedTeXFormulasAsString;
        for (String key : map.keySet()) TeXFormula.get(key);

        try {
            InputStream istream = amgr.open("tex/Macros.xml");
            TeXFormula.addPredefinedCommands(istream);
        } catch (Exception e) {
            Log.e("EvidentApp", e.getMessage());
        }
    }

}


