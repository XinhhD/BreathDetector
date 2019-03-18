package cn.edu.sustc.recoder;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    private Writer outFile;
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

    public void setWritter(Writer outFile) {
        this.outFile = outFile;
    }

    public void record(){
        CsvWriter csvWriter = new CsvWriter();
        try (CsvAppender csvAppender = csvWriter.append(outFile)) {
            // header
            audioRecord.startRecording();
            isRecording = true;
            short[] f = new short[recordBufSize];
            short lf = f[0];
            while (isRecording) {
                audioRecord.read(f, 0, recordBufSize, AudioRecord.READ_NON_BLOCKING);
                try {
                    csvAppender.appendLine(Long.toString(System.currentTimeMillis()), Short.toString(f[0]));
                } catch (IOException e) {
                    e.printStackTrace();
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

    public void playSound() {
        int bufferSize = AudioTrack.getMinBufferSize(SamplingRate,channelConfiguration, EncodingBitRate);
        AudioTrack player = new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(EncodingBitRate)
                        .setSampleRate(SamplingRate)
                        .setChannelMask(channelConfiguration)
                        .build())
                .setBufferSizeInBytes(bufferSize)
                .build();
        player.play();
        new Thread(new Runnable() {

            @Override
            public void run() {

            }
        }).start();

    }


}
