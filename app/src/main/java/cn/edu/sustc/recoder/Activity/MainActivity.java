package cn.edu.sustc.recoder.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aditya.filebrowser.Constants;
import com.aditya.filebrowser.FileChooser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.edu.sustc.recoder.Utils.MyRecoder;
import cn.edu.sustc.recoder.R;
import cn.edu.sustc.recoder.Utils.Util;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Uri selectedFile;
    TextView chooseFile;
    Button writeButton;
    Button chooseButton;
    EditText name;
    private String musicPath = "/Lemon.mp3";
    MediaPlayer mMediaPlayer = new MediaPlayer();
    MyRecoder mc = new MyRecoder();
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
        if(isPlaying) {
            Toast.makeText(MainActivity.this,"请先停止播放",Toast.LENGTH_SHORT).show();
            return;
        }
        if (mc.isRecording) {
            record();
        }
        Intent i2 = new Intent(MainActivity.this, FileChooser.class);
        i2.putExtra(Constants.SELECTION_MODE,Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
        startActivityForResult(i2,1);
    }

    public void onRecord(View view){
        if(!isPlaying) {
            record();
            playMusic();
            writeButton.setText("STOP");
            isPlaying = true;
        }else {
            record();
            pauseMusic();
            writeButton.setText("SYNC");
            isPlaying = false;
        }

    }
    public void record(){
        if (mc.isRecording) {
            mc.stopRecording();
            Toast.makeText(MainActivity.this,"结束录音",Toast.LENGTH_SHORT).show();
        } else {
            String filename = name.getText().toString()+".pcm";
            final File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Environment.DIRECTORY_MUSIC + File.separator + filename);
            if (!file.mkdirs()) {
                Log.e(TAG, "Directory not created");
            }
            if (file.exists()) {
                file.delete();
            }
            mc.setOutFile(file);
            mc.record();
            Toast.makeText(MainActivity.this,"开始录音",Toast.LENGTH_SHORT).show();
        }
    }

    public void playMusic(){
        mMediaPlayer.start();
    }

    public void pauseMusic(){
        mMediaPlayer.pause();
    }

    public void initMusicFile(){
        try {
            if (selectedFile == null) {
                AssetFileDescriptor fileDescriptor = getAssets().openFd("music/Lemon.mp3");
                mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            } else {
                mMediaPlayer.setDataSource(selectedFile.getPath());
            }
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
        mMediaPlayer.reset();
        initMusicFile();
    }
}
