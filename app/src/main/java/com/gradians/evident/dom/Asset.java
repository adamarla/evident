package com.gradians.evident.dom;

import android.content.Context;
import android.util.Log;

/**
 * Created by adamarla on 3/19/17.
 */

public abstract class Asset {

    protected int id;

    public String getFront() {
        return toPureTeX(front);
    }

    public String getBack() {
        return toPureTeX(back);
    }

    public abstract void load(Context context);
    public String front, back;

    private String toPureTeX(String tex) {
        String[] lines = tex.split("\n");
        StringBuilder sb = new StringBuilder();

        boolean textMode = false;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.startsWith("%text")) {
                textMode = true;
                continue;
            } else if (line.startsWith("%")) {
                textMode = false;
                continue;
            }

//            if (line.contains("title")) {
//                Log.d("EvidentApp", line);
//                line = line.replaceAll("\\\\title\\{(.*)\\}", "\\\\textcolor{blue}{\\\\text{$1}}");
//                Log.d("EvidentApp", line);
//            }


            if (textMode)
                sb.append(String.format("\\text{%s} \\\\\n", line));
            else
                sb.append(String.format("%s\n", line));
        }
        return sb.toString();
    }

}
