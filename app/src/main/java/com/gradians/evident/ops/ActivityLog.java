package com.gradians.evident.ops;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.JsonObject;
import com.gradians.evident.dom.Asset;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by adamarla on 5/20/17.
 */

public class ActivityLog {

    public ActivityLog(Context context) {
        this.context = context;
        logs = new File(context.getExternalFilesDir(null), "logs");
        todaysLog = new File(logs, getLogDay());

        if (!logs.exists()) logs.mkdir();
        try {
            if (!todaysLog.exists()) todaysLog.createNewFile();
            writer = new PrintWriter(new FileWriter(todaysLog), true);
        } catch (Exception e) {
            Log.e("EvidentApp", e.getMessage());
        }

        SharedPreferences prefs = context.getSharedPreferences("profile", Context.MODE_PRIVATE);
        uid = prefs.getInt("userId", 0);
    }

    public void record(Asset asset) {
        writer.println(String.format("%s:%s:%s", asset.getChapterId(), asset.getId(), asset.getPath()));
    }

    public void transmit()  {
        if (!isConnected()) return;
        HashMap<String, Recorder> recorders = new HashMap<>();
        for (File log: logs.listFiles()) {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(log));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(":");
                    final String chapterId = tokens[0], assetId = tokens[1], assetPath = tokens[2];
                    Recorder recorder = recorders.get(chapterId);
                    if (recorder == null) {
                        recorder = new Recorder(context, Integer.valueOf(chapterId));
                        recorders.put(chapterId, recorder);
                    }

                    String entry = recorder.read(Integer.parseInt(assetId), assetPath);
                    int bits = 0, counter = 0;
                    if (entry != null) {
                        for (String s: entry.split(",")) {
                            int bit = Boolean.parseBoolean(s) ? 1 : 0;
                            bits += (bit * Math.pow(2, counter++));
                        }

                        JsonObject params = new JsonObject() ;
                        params.addProperty("id", Integer.valueOf(assetId));
                        params.addProperty("sku_type", assetPath.startsWith("q") ? "Question" : "Snippet");
                        params.addProperty("bits", bits);
                        params.addProperty("date", Integer.parseInt(log.getName()));
                        params.addProperty("uid", uid);

                        Ion.with(context)
                                .load("http://www.gradians.com/activity/update")
                                .setJsonObjectBody(params)
                                .asJsonObject()
                                .setCallback(new FutureCallback<JsonObject>() {
                                    @Override
                                    public void onCompleted(Exception e, JsonObject result) {
                                        Log.d("EvidentApp", "updated " + assetId);
                                    }
                                });
                    }
                }

                if (!log.delete())
                    Log.d("EvidentApp", "Could not delete activity log " + log.getName());

            } catch (Exception e) {
                Log.e("EvidentApp", e.getMessage());
            }
        }
    }

    private int uid;
    private PrintWriter writer;
    private File todaysLog, logs;
    private Context context;

    private String getLogDay() {
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
