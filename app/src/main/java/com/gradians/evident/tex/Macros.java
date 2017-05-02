package com.gradians.evident.tex;


import com.himamis.retex.renderer.share.Atom;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.exception.ParseException;

/**
 * Created by adamarla on 3/25/17.
 */

public class Macros {

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

    public Atom xaxis(TeXParser tp, String[] args) throws ParseException {
        return new TeXFormula("x-\\text{axis}").root;
    }

    public Atom yaxis(TeXParser tp, String[] args) throws ParseException {
        return new TeXFormula("y-\\text{axis}").root;
    }

    public Atom zaxis(TeXParser tp, String[] args) throws ParseException {
        return new TeXFormula("z-\\text{axis}").root;
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

    public Atom fcondp(TeXParser tp, String[] args) throws ParseException {
        return new TeXFormula(String.format("\\dfrac{\\condp{%s}{%s}\\cdot P\\left(%s\\right)}{P\\left(%s\\right)}",
                args[1], args[2], args[1], args[2])).root;
    }

    public Atom combi(TeXParser tp, String[] args) throws ParseException {
        return new TeXFormula(String.format("\\,^{%s}C_{%s}", args[1], args[2])).root;
    }

    public Atom permi(TeXParser tp, String[] args) throws ParseException {
        return new TeXFormula(String.format("\\,^{%s}P_{%s}", args[1], args[2])).root;
    }

    public Atom sini(TeXParser tp, String[] args) throws ParseException {
        return new TeXFormula(String.format("\\sin^{-1}\\left( %s\\right)", args[1])).root;
    }

    public Atom cosi(TeXParser tp, String[] args) throws ParseException {
        return new TeXFormula(String.format("\\sin^{-1}\\left( %s\\right)", args[1])).root;
    }

    public Atom tani(TeXParser tp, String[] args) throws ParseException {
        return new TeXFormula(String.format("\\sin^{-1}\\left( %s\\right)", args[1])).root;
    }

    public Atom csci(TeXParser tp, String[] args) throws ParseException {
        return new TeXFormula(String.format("\\sin^{-1}\\left( %s\\right)", args[1])).root;
    }

}
