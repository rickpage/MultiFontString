package biz.rpcodes.apps.multifontstring;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

public class DrawMultilineText {
    private static final String TAG = "DrawMultilineText";

    /**
     * From
     *http://www.skoumal.net/en/android-drawing-multiline-text-on-bitmap/
     * @param gContext
     * @param b
     * @param gText
     * @return
     */
    public static Bitmap drawMultilineTextToBitmap(Context gContext,
                                            // int gResId,
                                            Bitmap b,
                                            String gText) {

        // prepare canvas
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = b;// BitmapFactory.decodeResource(resources, gResId);

        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);

        // new antialiased Paint
        TextPaint paint=new TextPaint();//Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.BLACK);
        // text size in pixels
        int size = canvas.getWidth() / (gText.trim().length());
        size = Math.max(14, size);
        Log.i(TAG, "FONT SIZE " + size);
        paint.setTextSize((int) (size * scale));
        // text shadow
        // paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // set text width to canvas width minus 16dp padding
        int textWidth = canvas.getWidth(); // - (int) (16 * scale);

        // init StaticLayout for text
        StaticLayout textLayout = new StaticLayout(
                gText, paint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

        // get height of multiline text
        int textHeight = textLayout.getHeight();

        // get position of text's top left corner
        float x = (bitmap.getWidth() - textWidth)/2;
        float y = (bitmap.getHeight() - textHeight)/2;

        // draw text to the Canvas center
        canvas.save();
        canvas.translate(x, y);
        textLayout.draw(canvas);
        canvas.restore();

        return bitmap;
    }


    /**
     * Answer at http://stackoverflow.com/questions/3153870/canvas-drawtext-does-not-print-linebreak/15092729#15092729
     * Firect link: http://stackoverflow.com/a/15092729/1759409
     * Created by page on 1/16/16.
     */
    public static void drawMultilineText(String str, int x, int y, Paint paint, Canvas canvas, int fontSize, Rect drawSpace) {
        int      lineHeight = 0;
        int      yoffset    = 0;
        String[] lines      = str.split(" ");

        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setAlpha(100);
        p.setStyle(Paint.Style.STROKE);
        canvas.drawRect(drawSpace, p);
        // set height of each line (height of text + 20%)
        lineHeight = (int) (calculateHeightFromFontSize(str, fontSize) * 1.2);
        // draw each line
        String line = "";
        for (int i = 0; i < lines.length; ++i) {

            if(calculateWidthFromFontSize(line + " " + lines[i], fontSize) <= drawSpace.width()){
                line = line + " " + lines[i];

            }else{
                canvas.drawText(line, x, y + yoffset, paint);
                yoffset = yoffset + lineHeight;
                line = lines[i];
            }
        }
        canvas.drawText(line, x, y + yoffset, paint);

    }

    private static int calculateWidthFromFontSize(String testString, int currentSize)
    {
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(currentSize);
        paint.getTextBounds(testString, 0, testString.length(), bounds);

        return (int) Math.ceil( bounds.width());
    }

    private static int calculateHeightFromFontSize(String testString, int currentSize)
    {
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(currentSize);
        paint.getTextBounds(testString, 0, testString.length(), bounds);

        return (int) Math.ceil( bounds.height());
    }
}
