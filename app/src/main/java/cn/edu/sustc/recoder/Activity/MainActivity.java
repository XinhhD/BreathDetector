package cn.edu.sustc.recoder.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.edu.sustc.recoder.Utils.MyRecoder;
import cn.edu.sustc.recoder.R;
import cn.edu.sustc.recoder.Utils.Util;
import cn.edu.sustc.recoder.Utils.XYData;

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
    private LineChart chart;
    // graph data
    LineDataSet dataSet;
    private float graphIndexNow=0;
    private float interval = 0.001f; // 坐标间隔


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
        chart = (LineChart) findViewById(R.id.chart);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("于dd日HH_mm_ss创建");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        name.setText(simpleDateFormat.format(date));
        initMusicFile();
        setData(makeRandomData());
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

    public XYData[] makeRandomData(){
        XYData[] data = new XYData[100];
        for (int i=0; i<100; i++){
            data[i] = new XYData((float)i, (float) Math.random());
        }
        return data;
    }

    public void setData(XYData xydata[]){
        List<Entry> entries = new ArrayList<Entry>();
        for (XYData data : xydata){
            entries.add(new Entry(data.getX(), data.getY()));
        }
        dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setColor(Color.BLACK);
        dataSet.setValueTextColor(Color.RED); // styling, ...
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh1111111111111111111111111111161
    }

    public void updateData(float y){
        Entry newData = new Entry(graphIndexNow, y);
        graphIndexNow += interval;
        if (graphIndexNow > 10000){  // 超出坐标设成0
            graphIndexNow = 0;
        }
//        dataSet.removeEntry();
        dataSet.addEntry(newData);
    }

}
