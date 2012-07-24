package net.iksela.xbmc.companion;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.widget.RelativeLayout;

public class UIHelper {

	private static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
		int sourceWidth = source.getWidth();
		int sourceHeight = source.getHeight();

		// Compute the scaling factors to fit the new height and width,
		// respectively.
		// To cover the final image, the final scaling will be the bigger
		// of these two.
		float xScale = (float) newWidth / sourceWidth;
		float yScale = (float) newHeight / sourceHeight;
		float scale = Math.max(xScale, yScale);

		// Now get the size of the source bitmap when scaled
		float scaledWidth = scale * sourceWidth;
		float scaledHeight = scale * sourceHeight;

		// Let's find out the upper left coordinates if the scaled bitmap
		// should be centered in the new size give by the parameters
		float left = (newWidth - scaledWidth) / 2;
		float top = (newHeight - scaledHeight) / 2;

		// The target rectangle for the new, scaled version of the source bitmap
		// will now
		// be
		RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

		// Finally, we create a new bitmap of the specified size and draw our
		// new,
		// scaled bitmap onto it.
		Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
		Canvas canvas = new Canvas(dest);
		canvas.drawBitmap(source, null, targetRect, null);

		return dest;
	}

	public static BitmapDrawable getOptimizedDrawable(Bitmap source, Resources res, RelativeLayout layout) {
		Rect viewBounds = new Rect();
		layout.getDrawingRect(viewBounds);

		Bitmap cropped = UIHelper.scaleCenterCrop(source, viewBounds.height(), viewBounds.width());
		cropped = UIHelper.darken(cropped, -25);
		return new BitmapDrawable(res, cropped);
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
