package cn.edu.sustc.recoder;


import org.jtransforms.fft.DoubleFFT_1D;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cn.edu.sustc.recoder.Utils.Complex;
import cn.edu.sustc.recoder.Utils.FFT;
import cn.edu.sustc.recoder.Utils.ReadCsv;
import cn.edu.sustc.recoder.Utils.ReadMusicFile;
import cn.edu.sustc.recoder.Utils.funcFFT;
import cn.edu.sustc.recoder.Utils.loopQueue;
import cn.edu.sustc.recoder.Utils.xcorr;

import static cn.edu.sustc.recoder.Utils.xcorr.get_range;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class XcorrTest {
    @Test
    public void readTest() {
        byte[] s1 = ReadMusicFile.read("D:\\studioProject\\Recoder\\app\\src\\test\\java\\cn\\edu\\sustc\\recoder\\s1.txt");
        byte[] s2 = ReadMusicFile.read("D:\\studioProject\\Recoder\\app\\src\\test\\java\\cn\\edu\\sustc\\recoder\\s2.txt");
        System.out.println(s1.length);
        System.out.println(s2.length);
    }
    @Test
    public void CodeingTest() {
        double[] s1 = {1, 2, 3, 4, 5, 6, 7, 8,9};
        DoubleFFT_1D df1 = new DoubleFFT_1D(s1.length);
        double[] s11 = new double[s1.length*2];
        System.arraycopy(s1,0,s11,0,s1.length);
        df1.realForwardFull(s11);
        Complex[] as = Complex.parseDouble(s11);
        double[] s11_c = Complex.decodeDouble(as);
        assertArrayEquals(s11,s11_c,0.001);
    }
    @Test
    public void ComplexFFTTest() {
        double[] s1 = {1, 2, 3, 4, 5, 6, 7, 8,9};
        DoubleFFT_1D df1 = new DoubleFFT_1D(s1.length);
        double[] s11 = new double[s1.length*2];
        System.arraycopy(s1,0,s11,0,s1.length);
        df1.realForwardFull(s11);
        Complex[] as = Complex.parseDouble(s11);
        Complex[]res = FFT.fft(as);
        assertEquals(as.length,res.length);
    }

    @Test
    public void xcorrTest() {
        double[] s11 = {-1, 1, -1, 1, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8};
        double[] s22 = {1, 2, 3, 4, 5, 6, 7, 8};
        double[] ind = FFT.xcorr(s11, s22, true);
        for (double i : ind
                ) {
            System.out.println(i);
        }
    }
    @Test
    public void xcorrFTest() {
        int[] s11 = {-1, 1, -1, 1, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8};
        int[] s22 = {1, 2, 3, 4, 5, 6, 7, 8};
        double[] ind = funcFFT.xcorr(s11, s22, true);
        for (double i : ind
                ) {
            System.out.println(i);
        }
    }
    @Test
    public void mp3ReadTest() {
        byte[] s1 = ReadMusicFile.read("D:\\studioProject\\Recoder\\app\\src\\main\\assets\\music\\Lemon.mp3");
        System.out.println(s1.length);
        for (int i = 0; i < 10; i++) {
            System.out.println(s1[i]);
        }
    }

    @Test
    public void FFTTest() {
        double[] s1 = {1, 2, 3, 4, 5, 6, 7, 8,9};
        double[] s2 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 23};
        double[] s3 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 23};
        DoubleFFT_1D df1 = new DoubleFFT_1D(s1.length);
        double[] s11 = new double[s1.length*2];
        System.arraycopy(s1,0,s11,0,s1.length);
        df1.realForwardFull(s11);
        Complex[] as = Complex.parseDouble(s11);
        System.out.println();
    }

    @Test
    public void listTest() {
        short st= 1;
        double db = (double)st;
        short[] s11 = {-1, 1, -1, 1, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8};
        byte[] s22 = {1, 2, 3, 4, 5, 6, 7, 8};
        LinkedList s = new LinkedList();
        s.add(s11);
        LinkedList s1 = new LinkedList();
        s1.add(s22);
        xcorr.xcorr(s,s1);
    }

    @Test
    public void ListConcurrent() {
        final loopQueue<Short> s = new loopQueue<Short>(200);
        Lock curl = new ReentrantLock();
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int j =0;
                    for (int i = 0; i <10000000 ; i++) {
                        short sh = 2;
                        try {
                            s.add(sh);
                        } catch (ArrayStoreException e) {

                        }

                    }
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int j = 0;
                    for (int i = 0; i <10000000 ; i++) {
                        try {
                            s.removeOne();
                        } catch (IllegalAccessException e) {
                            System.out.println("remove wrong:"+j++);
                        }
                    }
                }
            }).start();
        } catch (Exception e) {
            e.getCause().printStackTrace();
        }
        assertEquals(s.count,0);
    }

    @Test
    public void auto_corr_Test() {
        double[] input = {1, 2, 3, 4, 5, 6, 6};
        double[] ans = xcorr.autoCorr(input);
        for (double e: ans
             ) {
            System.out.println(e);
        }
    }
    @Test
    public void get_range_Test() {
        double[][] input = ReadCsv.ReadCsvFile("D:\\studioProject\\Recoder\\app\\src\\test\\java\\cn\\edu\\sustc\\recoder\\matrix.csv");
        int[]index = get_range(input);
        for (int i = 0; i <index.length ; i++) {
            System.out.println(index[i]);
        }
    }
}

