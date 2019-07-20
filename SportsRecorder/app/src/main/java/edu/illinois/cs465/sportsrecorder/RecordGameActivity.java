package edu.illinois.cs465.sportsrecorder;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

public class RecordGameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_game);
        getActionBar().hide();

//        File imgFile = new File("drawable-v24/basketballcourt.jpg");
//        if(imgFile.exists()){
//
//            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//
//            ImageView myImage = (ImageView) findViewById(R.id.courtPic);
//
//            myImage.setImageBitmap(myBitmap);
//
//        }

    }


}
