package cn.edu.sustc.recoder.Utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

public class MyRecoder {
    private static final String TAG = "MyRecoder";
    private int EncodingBitRate = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord audioRecord = null;
    private int recordBufSize = 0;
    private int SamplingRate = 48000;
    private int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    private File outFile;
    public boolean isRecording;
    private Vector<Short> dataArray = new Vector<>();

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

    public Short getUnCheckBuffer(int index){
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
        final short data[] = new short[recordBufSize];
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

                if (null != os) {
                    while (isRecording) {
                        int read = audioRecord.read(data, 0, recordBufSize);
                        // 如果读取音频数据没有出现错误，就将数据写入到文件
                        if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                            for (int i = 0; i < data.length; i++) {
                                dataArray.add(data[i]);
                            }
                        }
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
