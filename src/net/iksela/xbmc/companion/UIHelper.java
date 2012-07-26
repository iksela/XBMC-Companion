package net.iksela.xbmc.companion;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class UIHelper {

	private final static String TAG = "UIHelper";

	private static Bitmap[] getBitmapPieces(int pieces, Bitmap source, int newHeight, int newWidth) {
		int sourceWidth = source.getWidth();
		int sourceHeight = source.getHeight();

		int totalWidth = newWidth * pieces;

		Log.v(TAG, "src: " + sourceWidth + "x" + sourceHeight);
		Log.v(TAG, "pieces: " + totalWidth + "x" + newHeight);

		// Compute the scaling factors to fit the new height and width,
		// respectively.
		// To cover the final image, the final scaling will be the bigger
		// of these two.
		float xScale = (float) totalWidth / sourceWidth;
		float yScale = (float) newHeight / sourceHeight;
		float scale = Math.max(xScale, yScale);

		// Now get the size of the source bitmap when scaled
		float scaledWidth = scale * sourceWidth;
		float scaledHeight = scale * sourceHeight;

		Bitmap scaled = Bitmap.createScaledBitmap(source, (int) scaledWidth, (int) scaledHeight, false);

		Bitmap[] backgrounds = new Bitmap[pieces];
		for (int i = 0; i < pieces; i++) {
			int left = i * newWidth;
			int top = (int) ((scaledHeight - newHeight) / 2);
			Bitmap dest = Bitmap.createBitmap(scaled, left, top, newWidth, newHeight);
			backgrounds[i] = dest;
			Log.v(TAG, "piece " + i + ": " + dest.getWidth() + "x" + dest.getHeight() + " starting at " + left + "x" + top);
		}

		return backgrounds;
	}

	public static BitmapDrawable[] getPieces(int pieces, Bitmap source, int newHeight, int newWidth, Resources res) {
		Bitmap[] bitmaps = UIHelper.getBitmapPieces(pieces, source, newHeight, newWidth);
		BitmapDrawable[] backgrounds = new BitmapDrawable[pieces];
		for (int i = 0; i < pieces; i++) {
			backgrounds[i] = new BitmapDrawable(res, UIHelper.darken(bitmaps[i], -25));
		}
		return backgrounds;
	}

	private static Bitmap darken(Bitmap source, int offset) {
		ColorMatrix cm = new ColorMatrix();

		cm.getArray()[4] = offset;
		cm.getArray()[9] = offset;
		cm.getArray()[14] = offset;

		ColorMatrixColorFilter cmcf = new ColorMatrixColorFilter(cm);

		Paint p = new Paint();
		p.setColorFilter(cmcf);

		Bitmap converted = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.RGB_565);

		Canvas c = new Canvas(converted);
		c.drawBitmap(source, 0, 0, p);

		return converted;
	}
}
