package com.himamis.retex.renderer.android.graphics;

import com.caverock.androidsvg.SVG;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.Image;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;

public class ImageA implements Image {
	
	private Bitmap mBitmap;
	private Canvas mCanvas;
	
	public ImageA(int width, int height, int type) {
		mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
	}

	public int getWidth() {
		return mBitmap.getWidth();
	}

	public int getHeight() {
		return mBitmap.getHeight();
	}

	public Graphics2DInterface createGraphics2D() {
		Canvas canvas = new Canvas(mBitmap);
		Graphics2DA g2 = new Graphics2DA(canvas);
		return g2;
	}

	public Bitmap getBitmap() {
		return mBitmap;
	}

	public void setSVG(SVG svg) {
		mCanvas = new Canvas(mBitmap);
		// Clear background to white
		// mCanvas.drawRGB(255, 255, 255);
		// Render our document onto our canvas
		svg.renderToCanvas(mCanvas);
	}
}
