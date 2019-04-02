package cn.edu.sustc.recoder;


import org.jtransforms.fft.DoubleFFT_1D;
import org.junit.Assert;
import org.junit.Test;

import cn.edu.sustc.recoder.Utils.Complex;
import cn.edu.sustc.recoder.Utils.FFT;
import cn.edu.sustc.recoder.Utils.ReadMusicFile;
import cn.edu.sustc.recoder.Utils.funcFFT;

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

}

