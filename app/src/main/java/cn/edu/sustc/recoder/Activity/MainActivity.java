package cn.edu.sustc.recoder.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aditya.filebrowser.Constants;
import com.aditya.filebrowser.FileChooser;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import cn.edu.sustc.recoder.R;
import cn.edu.sustc.recoder.Utils.Util;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Uri selectedFile;
    Button startButton;
    Button chooseButton;
    String gengrateFileName;
    private String musicPath = "music/Lemon.wav";
    MediaPlayer mMediaPlayer;
    MyPlayer myPlayer = new MyPlayer();  // 这里要初始化
    MyRecoder mc;
    private boolean isPlaying = false;
    private LineChart chart;
    // graph data
    LineDataSet dataSet;
    private float graphIndexNow=0;
    public final float MAX_AXIS = 100f;
    private Handler auto_stop;
    private float interval = 1f; // 坐标间隔
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.requestPermission(this, Manifest.permission.RECORD_AUDIO);
        Util.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        setContentView(R.layout.activity_main);
        startButton = (Button)findViewById(R.id.start);
        chooseButton = (Button)findViewById(R.id.choose);
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
            dataSet.setColor(Color.BLACK);
            dataSet.setValueTextColor(Color.RED); // styling, ...
            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.invalidate();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("于dd日HH_mm_ss创建");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        gengrateFileName = simpleDateFormat.format(date);
        auto_stop = new Handler(){
            @Override
            public void handleMessage(Message message) {
                rec();
            }
        };
        mMediaPlayer = new MediaPlayer();
        mc = new MyRecoder();
        initMusicFile();
//        startTimer(); // 定时更新图标
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
        rec();
    }

    private void rec() {
        if(!isPlaying) {
            record();
            mMediaPlayer.start();
//            myPlayer.startPlayering();
            startButton.setText("STOP");
            isPlaying = true;
        }else {
            record();
            mMediaPlayer.stop();
//            myPlayer.stopPlayering();
            startButton.setText("START");
            isPlaying = false;
        }
    }


    public void record(){
        if (mc.isRecording) {
            mc.stopRecording();
            Toast.makeText(MainActivity.this,"结束录音",Toast.LENGTH_SHORT).show();
        } else {

            String filename = gengrateFileName+".pcm";
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


    public void initMusicFile(){
        try {
            if (selectedFile == null) {
                AssetFileDescriptor fileDescriptor = getAssets().openFd(musicPath);
//                myPlayer.setInFile(fileDescriptor.createInputStream());
                mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            } else {
//                myPlayer.setInFile(new FileInputStream(selectedFile.getPath()));
                mMediaPlayer.setDataSource(selectedFile.getPath());
            }
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data!=null) {
            if (resultCode == RESULT_OK) {
                selectedFile = data.getData();
            }
        }
        //mMediaPlayer.reset();
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

    class MyPlayer{    private static final String TAG = "MyRecoder";
        private int EncodingBitRate = AudioFormat.ENCODING_PCM_8BIT;
        private AudioTrack audioTrack;
        private int PlayerBufSize = 0;
        private int SamplingRate = 48000;
        private int channelConfiguration = AudioFormat.CHANNEL_OUT_MONO;
        private FileInputStream inFile;
        public boolean isPlayering;
        private Vector<Byte> playData = new Vector<>();

        public MyPlayer(FileInputStream file) {
            createAudioPlayer();
            this.inFile = file;
        }

        public MyPlayer(){
            createAudioPlayer();
        }

        public void setEncodingBitRate(int encodingBitRate) {
            EncodingBitRate = encodingBitRate;
        }

        public void setSamplingRate(int samplingRate) {
            SamplingRate = samplingRate;
        }

        public void setChannelConfiguration(int channelConfiguration) {
            this.channelConfiguration = channelConfiguration;
        }
        public Byte getPlayData(int index){
            if (playData.size()>0) {
                return playData.get(index);
            }
            return null;
        }

        public void createAudioPlayer() {
            PlayerBufSize = AudioTrack.getMinBufferSize(SamplingRate, channelConfiguration, EncodingBitRate);
            audioTrack = new AudioTrack(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build(),
                    new AudioFormat.Builder().setSampleRate(SamplingRate)
                            .setEncoding(EncodingBitRate)
                            .setChannelMask(channelConfiguration)
                            .build(),
                    PlayerBufSize,
                    AudioTrack.MODE_STREAM,
                    AudioManager.AUDIO_SESSION_ID_GENERATE);
        }

        public void setInFile(FileInputStream inFile) {
            this.inFile = inFile;
        }


        public void startPlayering() {
            final short data[] = new short[PlayerBufSize];
            audioTrack.play();
            isPlayering = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DataInputStream fileInputStream = new DataInputStream(inFile);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    byte[] tempBuffer = new byte[PlayerBufSize];
                                    while (fileInputStream.available() > 0) {
                                        int readCount = fileInputStream.read(tempBuffer);
                                        if (readCount == AudioTrack.ERROR_INVALID_OPERATION ||
                                                readCount == AudioTrack.ERROR_BAD_VALUE) {
                                            continue;
                                        }
                                        if (readCount != 0 && readCount != -1) {
                                            audioTrack.write(tempBuffer, 0, readCount);
                                            for (byte e:tempBuffer
                                                    ) {
                                                playData.add(e);
                                            }
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        public void stopPlayering() {
            isPlayering = false;
            audioTrack.stop();
        }
    }

    class MyRecoder{    private static final String TAG = "MyRecoder";
        private MainActivity mainActivity;
        private int EncodingBitRate = AudioFormat.ENCODING_PCM_16BIT;
        private AudioRecord audioRecord = null;
        private int recordBufSize = 0;
        private int SamplingRate = 48000;
        private int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
        private File outFile;
        public boolean isRecording;
        ArrayList<Byte> dataArray = new ArrayList<>();

        public MyRecoder() {
            createAudioRecord();
        }

        public void setEncodingBitRate(int encodingBitRate) {
            EncodingBitRate = encodingBitRate;
        }

        public void setSamplingRate(int samplingRate) {
            SamplingRate = samplingRate;
        }

        public void setChannelConfiguration(int channelConfiguration) {
            this.channelConfiguration = channelConfiguration;
        }

        public Byte getUnCheckBuffer(int index){
            if (dataArray.size()>0) {
                return dataArray.get(index);
            }
            return null;
        }


        public void createAudioRecord() {
            recordBufSize = AudioRecord.getMinBufferSize(SamplingRate, channelConfiguration, EncodingBitRate);
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SamplingRate, channelConfiguration, EncodingBitRate, recordBufSize);
        }

        public void setOutFile(File outFile) {
            this.outFile = outFile;
        }


        public void record() {
            final byte data[] = new byte[recordBufSize];
            audioRecord.startRecording();
            isRecording = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileOutputStream os = null;
                    try {
                        os = new FileOutputStream(outFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    long start_time = System.currentTimeMillis();
                    if (null != os) {
                        while (isRecording && System.currentTimeMillis()-start_time<=22000) {
                            int read = audioRecord.read(data, 0, recordBufSize);
                            // 如果读取音频数据没有出现错误，就将数据写入到文件
                            if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                                for (int i = 0; i < data.length; i++) {
                                    dataArray.add(data[i]);

                                }
                                try {
                                    os.write(data);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                        if (isRecording) {
                            auto_stop.sendEmptyMessage(0);
                        }

                        try {
                            Log.i(TAG, "run: close file output stream !");
                            os.flush();
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        public void stopRecording() {
            isRecording = false;
            audioRecord.stop();
        }
    }

}
