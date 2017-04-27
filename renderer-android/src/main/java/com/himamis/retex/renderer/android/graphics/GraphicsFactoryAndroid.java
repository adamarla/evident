package com.himamis.retex.renderer.android.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.caverock.androidsvg.SVG;
import com.himamis.retex.renderer.share.platform.graphics.BasicStroke;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.GraphicsFactory;
import com.himamis.retex.renderer.share.platform.graphics.Image;
import com.himamis.retex.renderer.share.platform.graphics.Transform;

import java.io.File;
import java.io.FileInputStream;

public class GraphicsFactoryAndroid extends GraphicsFactory {

	private Context mContext;

	public GraphicsFactoryAndroid(Context context) {
		mContext = context;
	}

	@Override
	public BasicStroke createBasicStroke(float width, int cap, int join,
			float miterLimit) {
		return new BasicStrokeA(width, miterLimit, cap, join);
	}

	@Override
	public Color createColor(int red, int green, int blue) {
		return new ColorA(red, green, blue);
	}

	@Override
	public Image createImage(int width, int height, int type) {
		return new ImageA(width, height, type);
	}

	@Override
	public Image getImage(String path) {
		// TODO Get image in graphics factory
		File file = new File(mContext.getExternalFilesDir(null), "vault/" + path);

		// Read an SVG from the assets folder
		SVG svg = null;
		try {
			svg = SVG.getFromInputStream(new FileInputStream(file));
		} catch (Exception e) {
			Log.e("EvidentApp", "Could not read svg " + e.getMessage());
		}

		// Create a canvas to draw onto
		Image image = null;
		if (svg != null) {
			if (svg.getDocumentWidth() != -1) {
				image = new ImageA((int)Math.ceil(svg.getDocumentWidth()),
						(int)Math.ceil(svg.getDocumentHeight()), 0);
				((ImageA)image).setSVG(svg);
			}
		}
		return image;
	}

	@Override
	public Transform createTransform() {
		return new TransformA();
	}

}
