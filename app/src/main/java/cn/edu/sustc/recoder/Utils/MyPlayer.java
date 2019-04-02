package cn.edu.sustc.recoder.Utils;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MyPlayer {
    private static final String TAG = "MyRecoder";
    private int EncodingBitRate = AudioFormat.ENCODING_PCM_16BIT;
    private AudioTrack audioTrack;
    private int PlayerBufSize = 0;
    private int SamplingRate = 48000;
    private int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    private File inFile;
    public boolean isPlayering;

    public MyPlayer() {
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

    public void setInFile(File inFile) {
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
                    DataInputStream fileInputStream = new DataInputStream(new FileInputStream(inFile));
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
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                } catch (IOException e) {
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