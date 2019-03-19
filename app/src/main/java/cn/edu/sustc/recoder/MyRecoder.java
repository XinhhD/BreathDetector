package cn.edu.sustc.recoder;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import de.siegmar.fastcsv.writer.CsvAppender;
import de.siegmar.fastcsv.writer.CsvWriter;

public class MyRecoder {
    private static final String TAG = "MyRecoder";
    private int EncodingBitRate = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord audioRecord = null;
    private int recordBufSize = 0;
    private int SamplingRate = 48000;
    private int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    private DataOutputStream outFile;
    public boolean isRecording;

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

    public void createAudioRecord() {
        recordBufSize = AudioRecord.getMinBufferSize(SamplingRate, channelConfiguration, EncodingBitRate);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SamplingRate, channelConfiguration, EncodingBitRate, recordBufSize);
    }

    public void setWritter(DataOutputStream outFile) {
        this.outFile = outFile;
    }

    public void record(){
        try  {
            isRecording = true;
            short[] buffer = new short[recordBufSize];
            audioRecord.startRecording();
            while (isRecording) {
                int bufferReadResult = audioRecord.read(buffer, 0, recordBufSize);
                for (int i = 0; i < bufferReadResult; i++) {
                    outFile.writeShort(buffer[i]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        isRecording = false;
        try {
            outFile.flush();
            outFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        audioRecord.stop();
        audioRecord.release();
    }




}
