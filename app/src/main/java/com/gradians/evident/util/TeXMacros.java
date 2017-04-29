package com.gradians.evident.util;


import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.gradians.evident.EvidentApp;
import com.himamis.retex.renderer.android.FactoryProviderAndroid;
import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.exception.ParseException;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by adamarla on 3/25/17.
 */

public class TeXMacros {

    public Atom dd(TeXParser tp, String[] args) throws ParseException {
        return new TeXFormula(String.format("\\dfrac{d}{d%s}%s", args[2], args[1])).root;
    }

    public Atom vector(TeXParser tp, String[] args) throws ParseException {
        String format = " ";
        if (args[1].length() > 0) {
            if (args[1].equals("1") || args[1].equals("-1")) {
                args[1] = args[1].replace('1', ' ');
            }
            format += String.format("%s\\hat\\imath ", args[1]);
        }
        if (args[2].length() > 0) {
            if (args[2].equals("1") || args[2].equals("-1")) {
                args[2] = args[2].replace('1', ' ');
            }
            if (!args[2].startsWith("-") && args[1].length() > 0) {
                format += "+";
            }
            format += String.format("%s\\hat\\jmath ", args[2]);
        }
        if (args[3].length() > 0) {
            if (args[3].equals("1") || args[3].equals("-1")) {
                args[3] = args[3].replace('1', ' ');
            }
            if (!args[3].startsWith("-") && args[2].length() > 0) {
                format += "+";
            }
            format += String.format("%s\\hat{\\it k} ", args[3]);
        }
        return new TeXFormula(format).root;
    }

    public Atom ora(TeXParser tp, String[] args) throws ParseException {
        return new TeXFormula(String.format("\\overrightarrow{%s}", args[1])).root;
    }

    public Atom title(TeXParser tp, String[] args) throws ParseException {
        return new TeXFormula(String.format("\\textcolor{blue}{\\text{%s}}", args[1])).root;
    }

    public Atom dydx(TeXParser tp, String[] args) throws ParseException {
        return new TeXFormula("\\dfrac{dy}{dx}").root;
    }

    public Atom ddx(TeXParser tp, String[] args) throws ParseException {
        return new TeXFormula("\\dfrac{d}{dx}").root;
    }

    public Atom prob(TeXParser tp, String[] args) throws ParseException {
        return new TeXFormula(String.format("P\\left(%s\\right)", args[1])).root;
    }

    public Atom condp(TeXParser tp, String[] args) throws ParseException {
        return new TeXFormula(String.format("P\\left(%s\\,\\vert\\,%s\\right)", args[1], args[2])).root;
    }

    public Atom combi(TeXParser tp, String[] args) throws ParseException {
        return new TeXFormula(String.format("\\,^{%s}C_{%s}", args[1], args[2])).root;
    }

    public Atom permi(TeXParser tp, String[] args) throws ParseException {
        return new TeXFormula(String.format("\\,^{%s}P_{%s}", args[1], args[2])).root;
    }

}
