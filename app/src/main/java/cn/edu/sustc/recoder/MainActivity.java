package cn.edu.sustc.recoder;

import android.Manifest;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aditya.filebrowser.Constants;
import com.aditya.filebrowser.FileChooser;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Uri selectedFile;
    TextView chooseFile;
    Button writeButton;
    Button chooseButton;
    EditText name;
    private String musicPath = "/Lemon.mp3";
    MediaPlayer mMediaPlayer = new MediaPlayer();
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.requestPermission(this, Manifest.permission.RECORD_AUDIO);
        Util.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        setContentView(R.layout.activity_main);
        writeButton = (Button)findViewById(R.id.write);
        chooseButton= (Button)findViewById(R.id.choose);
        name = (EditText)findViewById(R.id.file);
        chooseFile = (TextView)findViewById(R.id.filename) ;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("于dd日HH_mm_ss创建");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        name.setText(simpleDateFormat.format(date));
        initMusicFile();
    }

    public void onChooseFile(View view){
        chooseFile();
    }

    public void chooseFile(){
        Intent i2 = new Intent(MainActivity.this, FileChooser.class);
        i2.putExtra(Constants.SELECTION_MODE,Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
        startActivityForResult(i2,1);
    }

    public void onRecord(View view){
        if(!isPlaying) {
            playMusic();
            record();
            writeButton.setText("Recording");
            isPlaying = true;
        }else {
            pauseMusic();
            writeButton.setText("Record");
            isPlaying = false;
        }

    }
    public void record(){
        MyRecoder mc = new MyRecoder();
        if (mc.isRecording) {
            mc.stopRecording();
            Toast.makeText(MainActivity.this,"结束录音",Toast.LENGTH_LONG).show();
        } else {
            String storageState = Environment.getExternalStorageState();
            String filename = name.getText().toString()+".pcm";
            if (storageState.equals(Environment.MEDIA_MOUNTED)) { //路径： /storage/emulated/0/Android/data/com.yoryky.demo/cache/yoryky.txt
                filename = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +Environment.DIRECTORY_MUSIC+File.separator+ filename;
                DataOutputStream writer = null;
                try {
                    OutputStream os = new FileOutputStream(filename);
                    BufferedOutputStream bos = new BufferedOutputStream(os);
                    writer = new DataOutputStream(bos);
                } catch (FileNotFoundException e1) {
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
    }

    public void playMusic(){
        mMediaPlayer.start();
        Toast.makeText(MainActivity.this,"开始放音乐了",Toast.LENGTH_LONG).show();
    }

    public void pauseMusic(){
        mMediaPlayer.pause();
        Toast.makeText(MainActivity.this,"暂时停止了",Toast.LENGTH_LONG).show();
    }

    public void initMusicFile(){
        try {
            //设置音频文件到MediaPlayer对象中
            AssetFileDescriptor fileDescriptor = getAssets().openFd("music/Lemon.mp3");
            mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            //让MediaPlayer对象准备
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && data!=null) {
            if (resultCode == RESULT_OK) {
                selectedFile = data.getData();
            }
        }
        chooseFile.setText(selectedFile.toString());
    }
}
