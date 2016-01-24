package biz.rpcodes.apps.multifontstring;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addKeyListener();
        process("TT");

    }

    public void addKeyListener() {

        // get edittext component
        EditText edittext = (EditText) findViewById(R.id.editText);

        // add a keylistener to keep track user input
        edittext.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //if ( event.getAction() != KeyEvent.ACTION_DOWN) {
                    String s = ((EditText) v).getText().toString();
                    process(s);
                //}
                return false;
            }
        });
    }

    public void process(String text){
        if ( text.isEmpty() ){
            return;
        }
        MultiFontString mfs = new MultiFontString(text
                , "fonts", getApplicationContext());
        Bitmap b = mfs.toBitmap(1200,1600);
        ImageView i = (ImageView) findViewById(R.id.iv1);
        i.setImageBitmap(b);
    }
}
