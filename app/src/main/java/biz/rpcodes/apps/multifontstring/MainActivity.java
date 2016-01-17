package biz.rpcodes.apps.multifontstring;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        process("TT");

    }

    public void process(String text){
        MultiFontString mfs = new MultiFontString(text
                , "fonts", getApplicationContext());
        Bitmap b = mfs.toBitmap(1200,1600);
        ImageView i = (ImageView) findViewById(R.id.iv1);
        i.setImageBitmap(b);
    }
}
