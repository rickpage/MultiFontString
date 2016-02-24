package biz.rpcodes.apps.multifontstring;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Holds paints and list of MFCs
 * Supports using the same Map as other rows, so we dont repeat letters until we cycle all of them
 * Created by page on 2/23/16.
 */
public class MultiFontCharRow {

    HashMap<Character, Short> mMap;

    ArrayList<MultiFontChar> mList;

    ArrayList<FontPaint> mFonts;

    int mRowWidth, mRowHeight, mMeasuredWidth;
    private float mXOffset = 0f;

    public float getXOffset(){
        return mXOffset;
    }

    // TODO: Abstract logging out of the class so non Android can use
    private void l(String s){
        Log.v("MFC ROW", s);
    }
    /**
     * We use a clone of the FontPaints so we can change the
     * size as needed. Could also create them here
     * @param row
     * @param fonts
     */
    public MultiFontCharRow(String row
        , ArrayList<FontPaint> fonts
        , int rowWidth
        , int rowHeight){

        this(row, fonts, rowWidth, rowHeight
                , new HashMap<Character, Short>(128));


    }

    public MultiFontCharRow(String row
            , ArrayList<FontPaint> fonts
            , int rowWidth
            , int rowHeight
            , HashMap<Character, Short> map) {
        mMap = map;

        mList = new ArrayList<MultiFontChar>(row.length());
        mFonts = (ArrayList<FontPaint>) fonts.clone();

        mRowHeight = rowHeight;
        mRowWidth = rowWidth;
        mMeasuredWidth = 0;

        createMFCarray(row);
        // this(row, fonts, rowHeight, rowHeight);
    }

    private void createMFCarray(String substring) {

        // set all the paints to font size == rowHeight
        int fontSize = mRowHeight;

        l("Setting font sizes to " + fontSize);
        setFontSize(fontSize);

        char[] list = substring.toCharArray();
        Short font_number = 0;
        for (char c : list) {
            // increment font number for character
            font_number = mMap.get(c);
            // if exists
            if (font_number != null) {
                // increment
                font_number++;
                // if too high
                if (font_number == mFonts.size()) {
                    // reset to 0
                    font_number = 0;
                }
            } else {
                // start at 0 (TODO: SETTING start at random font)
                font_number = 0;
            }
            mMap.put(c, font_number);
            FontPaint fp = mFonts.get(font_number);
            MultiFontChar mfc = new MultiFontChar(c, fp.getPaint());
            mList.add(mfc);
            //Log.v(TAG, "ADDED " + c + " : " + font_number);

            mMeasuredWidth += mfc.getWidth();
            // TODO if too big already, do something
        }
        l("Resulting measurement: " + mMeasuredWidth + " vs " + mRowWidth);
        // Now, if too wide, figure out new font size
        // width(size) == k*size => row width
        // k = measured width(size) / size
        // new size = row width / k
        if ( mMeasuredWidth > mRowWidth){
            float k = ((float) mMeasuredWidth) / (float) fontSize;
            l("K is " + k);
            float newSize = mRowWidth / k;
            int iSize = (int) Math.floor(newSize);
            l("iSize is now " + newSize + " becomes " + iSize);
            boolean tooLarge = true;
            // make sure we didnt get same font
            // if so, reduce and try again
            while (tooLarge){
                if ( iSize >= fontSize){
                    l("Oops, got " + iSize + " which is no better than " + fontSize);
                    iSize = fontSize-1;
                    l("set to " + iSize);
                    if ( iSize == MultiFontString.MIN_FONT_SIZE ){
                        tooLarge = false;
                        l("Exiting loop: Font Size at minimum of " + MultiFontString.MIN_FONT_SIZE);
                        continue;
                    }
                }
                // set font
                fontSize = iSize;
                setFontSize(fontSize);
                // re-measure
                mMeasuredWidth = 0;
                for ( MultiFontChar mfc : mList){
                    mMeasuredWidth += mfc.getWidth();
                }
                // if still too large, reduce font by 1 or let it be
                // a minimum number
                // iSize == fontSize here, so will trigger an Oops
                tooLarge = (mMeasuredWidth > mRowWidth);
                l("Is font still too large?" + String.valueOf(tooLarge));
            }
        }
        // If there is any x offset, record it
        mXOffset = (float) (mRowWidth - mMeasuredWidth) / 2.0f;

    }


    public void setFontSize(int fontSize){
        if ( null != mFonts) {
            l("Setting font sizes to " + fontSize);

            // access them directly, they are used as wk ptr in MFC instances
            // for : in loop copies, we dont want to copy
            for (int i = 0; i < mFonts.size(); i++) {
                mFonts.get(i).getPaint().setTextSize(fontSize);
            }
        }
        else throw new IllegalStateException("No fonts for row to process!");
    }

    public ArrayList<MultiFontChar> getList() {
        return mList;
    }
}
