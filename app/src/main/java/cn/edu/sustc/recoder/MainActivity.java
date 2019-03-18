package cn.edu.sustc.recoder;

import android.Manifest;
import android.content.Intent;
import android.media.AudioRecord;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aditya.filebrowser.Constants;
import com.aditya.filebrowser.FileChooser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;


import de.siegmar.fastcsv.writer.CsvAppender;
import de.siegmar.fastcsv.writer.CsvWriter;
import omrecorder.AudioChunk;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;

public class MainActivity extends AppCompatActivity {
    Uri selectedFile;
    TextView choosefile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.requestPermission(this, Manifest.permission.RECORD_AUDIO);
        Util.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        setContentView(R.layout.activity_main);
        Button writeButton = (Button)findViewById(R.id.write);
        Button chooseButton= (Button)findViewById(R.id.choose);
        EditText name = (EditText)findViewById(R.id.file);
        choosefile = (TextView)findViewById(R.id.filename) ;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("于dd日HH_mm_ss创建");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        name.setText(simpleDateFormat.format(date));
        MyRecoder mc = new MyRecoder();
        writeButton.setOnClickListener(e -> {
            if (mc.isRecording) {
                mc.stopRecording();
                Toast.makeText(MainActivity.this,"结束录音",Toast.LENGTH_LONG).show();
            } else {
                String storageState = Environment.getExternalStorageState();
                String filename = name.getText().toString()+".csv";
                if (storageState.equals(Environment.MEDIA_MOUNTED)) { //路径： /storage/emulated/0/Android/data/com.yoryky.demo/cache/yoryky.txt
                    filename = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +Environment.DIRECTORY_MUSIC+File.separator+ filename;
                    BufferedWriter writer = null;
                    try {
                        FileWriter w = new FileWriter(filename);
                        writer = new BufferedWriter(w);
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    mc.setWritter(writer);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mc.record();
                        }
                    }).start();
                    Toast.makeText(MainActivity.this,"开始录音",Toast.LENGTH_LONG).show();
                }
            }
        });
        chooseButton.setOnClickListener(e ->{
            Intent i2 = new Intent(MainActivity.this, FileChooser.class);
            i2.putExtra(Constants.SELECTION_MODE,Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
            startActivityForResult(i2,1);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && data!=null) {
            if (resultCode == RESULT_OK) {
                selectedFile = data.getData();
            }
        }
        choosefile.setText(selectedFile.toString());
    }
}
