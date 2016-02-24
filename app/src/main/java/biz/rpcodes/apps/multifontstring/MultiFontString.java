package biz.rpcodes.apps.multifontstring;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Draw a string using multiple fonts from a list.
 * Each letter is cycled i.e. 11323 will have the second 1 and second 3 drawn in the second font in the list.
 *
 * TODO: Measure each word using its own font, then
     * see if the word length + current word length is too
     * large to fit. If so, reduce the font of this word and
     * the previous. This assumes we account for multi line text
     * by chopping up the
     * words / characters to fit close to evenly
     * (i.e. the cat \n a dog \n airplane)
 *
 * Created by page on 1/16/16.
 */
public class MultiFontString {
    private static boolean DO_MULTICOLOR = true;
    private static final String TAG = "MultiFontString";
    public static final int MIN_FONT_SIZE = 9;
    final String mOriginalString;
    ArrayList<MultiFontChar> mList;
    ArrayList<FontPaint> mFonts;

    HashMap<Character, Short> mMap;
    private Context mContext;
    private int mPaintingX;
    private int mPaintingY;
    private ArrayList<String> mDisplayRows;

    MultiFontString(String o, String path, Context context){
        mOriginalString = o;

        mContext = context;

        AssetManager assets = context.getAssets();
        try {
            loadFontsFromPath(assets, path);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        // buildSegments();
    }

    private void loadFontsFromPath(AssetManager assets, String path) throws IOException {

        String[] list;
        try {
            list =assets.list(path);
        } catch (IOException e) {
            throw new IOException("Cannot load " + path);
        }

        if (DO_MULTICOLOR && list.length == 5){
            createMultiColoredFonts(assets, path, list);
        } else {
            createAllBlackFonts(assets, path, list);
        }


    }

    private void createMultiColoredFonts(AssetManager assets, String path, String[] list){
        mFonts = new ArrayList<>();
        Typeface myTypeface = null;
        FontPaint fp = null;

        int colors[] = { Color.BLUE, Color.RED, Color.GRAY, Color.GREEN, Color.CYAN};
        for (int i = list.length - 1; i >= 0; i--){
            String s = list[i];
            int color = colors[i];
            myTypeface = Typeface.createFromAsset(assets, path + '/' + s);
            fp = new FontPaint(myTypeface, color);
            mFonts.add(fp);
        }
    }

    private void createAllBlackFonts(AssetManager assets, String path, String[] list){
        mFonts = new ArrayList<>();
        Typeface myTypeface = null;
        FontPaint fp = null;

        int color = Color.BLACK;
        for (int i = list.length - 1; i >= 0; i--){
            String s = list[i];
            myTypeface = Typeface.createFromAsset(assets, path + '/' + s);
            fp = new FontPaint(myTypeface, color);
            mFonts.add(fp);
        }
    }

    /** Take word and change into 1 to 3 MFChar Rows
     *
     */
    public Bitmap rowBasedBitmap(int bmpW, int bmpH){
        Bitmap b = Bitmap.createBitmap(bmpW, bmpH, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(b);
        canvas.drawColor(Color.WHITE);

        // get width
        int bitmapWidth = bmpW;

        // get rows and calc row size
        int rowHeight = 0;
        // first get row info
        storeWordInformation();
        // then calc
        rowHeight = bmpH / mDisplayRows.size();
        // for each row, make MFC Row
        // Use the map technique
        float rowX = 0;
        float rowY = rowHeight * 0.80f;
        int dRow = 0;
        for ( String s : mDisplayRows){
            dRow++;
            l("Row #" + dRow + " == " + s);
            // make a new row
            // copies fonts and chars and
            // represents
            MultiFontCharRow mfcRow = new MultiFontCharRow(s
                    , mFonts, bitmapWidth, rowHeight);
            rowX = (int) mfcRow.getXOffset();
            // loop characters
            for ( MultiFontChar mfc : mfcRow.getList()){
                canvas.drawText(String.valueOf(mfc.getChar())
                        , rowX, rowY, mfc.getPaint());
                rowX += mfc.getWidth();
            }
            l("Row X ends at " + rowX);
            rowY += rowHeight;
            rowX = 0;
        }

        return b;
    }


    void l(String s){
        Log.v(TAG, s);
    }
    private void storeWordInformation(){

        // length of whole thing
        int originalStringLength = mOriginalString.length();
        l("total string length " + originalStringLength);

        if (originalStringLength == 0){
            throw new IllegalStateException("Cannot parse 0 length original string");
        }
        // split into words
        String strs[] = mOriginalString.split("\\s+");
        ArrayList<Integer> lens = new ArrayList<>();

        // how many words is that
        int mNumberOfWords = strs.length;

        int longestWordLength = 0;
        int longestWordIndex = 0;
        for (String s : strs){
            l("Parsing: "  + s);
            lens.add(s.length());
            if ( s.length() > longestWordLength){
                longestWordLength = s.length();
                longestWordIndex = lens.size() - 1;
            }
        }
        l("longest word " + strs[longestWordIndex] + " : " + longestWordLength + " chars");
        int rows = 1;

        // if more than 3 words, break up somewhat evenly
        if (mNumberOfWords > 3){
            rows = 3;
        }
        // if 3 words, or less, one on each row.
        else {
            rows = strs.length;
        }
        l("rows " + rows);
        // TODO: figure out the font size
        // do this by figuring out how many characters in each row
        // We would need to know how much width we have here
        // also, would make sense to have smaller/larger for realism

        int averageCharactersPerRow = Math.max(originalStringLength / rows, 1);
        l("Avg chars " + averageCharactersPerRow);

        int maxCharPerRow = averageCharactersPerRow;
        // TODO: evaluate
        if ( longestWordLength > averageCharactersPerRow){
            maxCharPerRow = longestWordLength;
        }
        l("max chars per row: " + maxCharPerRow);
        // Did we just decrease our row count?
        // We will need to check actual rows after

        ArrayList<String> displayRowSubstrings = new ArrayList<>(rows);
        String temp = strs[0];

        int rowCount = 0;
        // add a word to the row until we are over max characters
        for ( int index = 1; index < mNumberOfWords; index++){

            // +1 below is for the " " we want to add after word(s) in the row
            if ( displayRowSubstrings.size() != (rows - 1) &&
                    (temp.length() + strs[index].length() + 1) >= maxCharPerRow){
                displayRowSubstrings.add(temp);
                temp = strs[index];
                l("Row #" + (displayRowSubstrings.size())
                        + ": " + displayRowSubstrings.get(displayRowSubstrings.size()-1));
            } else {
                temp += " " + strs[index];
            }
        }
        // add the last row. if one row, we never added a single row yet
        if (temp != ""){
//            String s = displayRowSubstrings.remove(rows - 1);
//            s += temp;
            displayRowSubstrings.add(temp);
            l("Row #" + (displayRowSubstrings.size())
                    + ": " + displayRowSubstrings.get(displayRowSubstrings.size()-1));
        }

        // populate members
        mDisplayRows = displayRowSubstrings;
    }

}
