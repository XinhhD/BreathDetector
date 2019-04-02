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
import android.widget.Toast;

import com.aditya.filebrowser.Constants;
import com.aditya.filebrowser.FileChooser;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.edu.sustc.recoder.Utils.MyRecoder;
import cn.edu.sustc.recoder.R;
import cn.edu.sustc.recoder.Utils.Util;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Uri selectedFile;
    Button startButton;
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
    public final float MAX_AXIS = 100f;
    private float interval = 1f; // 坐标间隔
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.requestPermission(this, Manifest.permission.RECORD_AUDIO);
        Util.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        setContentView(R.layout.activity_main);
        startButton = (Button)findViewById(R.id.start);
        chooseButton = (Button)findViewById(R.id.choose);
        name = (EditText)findViewById(R.id.file);
        {
            // 图表设置
            chart = (LineChart) findViewById(R.id.chart);
            XAxis axis = chart.getXAxis();
            axis.setAxisMaximum(MAX_AXIS);
            chart.disableScroll(); // 禁止滑动
//            chart.setBackgroundColor(Color.GREEN);
            //数据设置
            List<Entry> entries = new ArrayList<Entry>();
            Entry e = new Entry(0, 1);
            entries.add(e);
            dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
//            dataSet
            dataSet.setColor(Color.BLACK);
            dataSet.setValueTextColor(Color.RED); // styling, ...
            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.invalidate();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("于dd日HH_mm_ss创建");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        name.setText(simpleDateFormat.format(date));
        initMusicFile();
        startTimer(); // 定时更新图标
    }

    public void onChooseFile(View view){
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
            startButton.setText("STOP");
            isPlaying = true;
        }else {
            record();
            pauseMusic();
            startButton.setText("START");
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
        mMediaPlayer.reset();
        initMusicFile();
    }

    public void startTimer(){
        Timer timer = new Timer();
        long delay = 1 * 100;
        long period = 100;
        timer.schedule(new UpdateTimerTask(), delay, period);
    }

    class UpdateTimerTask extends TimerTask{
        @Override
        public void run() {
            updateData((float)Math.random());
            System.out.println("Update"+graphIndexNow);
        }
    }


    public void updateData(float y){
        Entry newData = new Entry(graphIndexNow, y);
//        dataSet.removeEntryByXValue(graphIndexNow);
        dataSet.addEntry(newData);
        graphIndexNow += interval;
        if (graphIndexNow > MAX_AXIS){
            dataSet.clear();
            graphIndexNow = 0;
        }
        dataSet.notifyDataSetChanged();
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

}
