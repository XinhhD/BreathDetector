package cn.edu.sustc.recoder.Utils;

import org.jtransforms.fft.DoubleFFT_1D;
public class FFT {
    public static Complex[] fft(double[] real) {
        DoubleFFT_1D df1 = new DoubleFFT_1D(real.length);
        double[] s11 = new double[real.length*2];
        System.arraycopy(real,0,s11,0,real.length);
        df1.realForwardFull(s11);
        return Complex.parseDouble(s11);
    }

    public static Complex[] fft(Complex[] cpx) {
        double[] complex = Complex.decodeDouble(cpx);
        DoubleFFT_1D df1 = new DoubleFFT_1D(complex.length/2);
        df1.complexForward(complex);
        return Complex.parseDouble(complex);
    }

    public static Complex[] ifft(Complex[] x) {
        int N = x.length;
        Complex[] y = new Complex[N];

        // take conjugate
        for (int i = 0; i < N; i++) {
            y[i] = x[i].conjugate();
        }
        // compute forward FFT
        y = fft(y);
        // take conjugate again
        for (int i = 0; i < N; i++) {
            y[i] = y[i].conjugate();
        }
        // divide by N
        for (int i = 0; i < N; i++) {
            y[i] = y[i].times(1.0 / N);
        }
        return y;

    }
    public static Complex[] mul(Complex[] x,Complex[] y) {
        int N = x.length;
        Complex[] ret = new Complex[N];
        // take conjugate
        for(int i=0;i<N;i++)
        {
            ret[i] = x[i].times(y[i]);
        }
        return ret;
    }

    public static Complex[] conj(Complex s[])
    {
        Complex[] ret = new Complex[s.length];
        for(int i=0;i<s.length;i++)
        {
            ret[i] = s[i].conjugate();
        }
        return ret;
    }
    public static double[] complex2real(Complex[] complex)
    {
        double[] ret = new double[complex.length];
        // original data
        for (int i = 0; i < complex.length; i++) {
            ret[i] = complex[i].abs();
        }
        return ret;
    }
    public static double[] xcorr(double[] recData, double[] rawData, boolean firstLargerThanSecond) {
        if (firstLargerThanSecond) {
            int length1 = recData.length;
            int length2 = rawData.length;
            double[] rawExt = new double[length1];
            for (int i = 0; i < length2; i++) {
                rawExt[i] = rawData[i];
            }
            Complex[] recFFT = fft(recData);// 有偏移的数据，
            Complex[] rawFFT = fft(rawExt); // 原始的无偏移的数据
            Complex[] conjRawFFT = conj(rawFFT);
            Complex[] temp = mul(conjRawFFT, recFFT);
            Complex[] ret = ifft(temp);
            return complex2real(ret);
        } else {
            int length1 = recData.length;
            int length2 = rawData.length;
            double[] rawExt = new double[length1];
            for (int i = 0; i < length2; i++) {
                rawExt[i] = rawData[i];
            }
            Complex[] recFFT = fft(recData);// 有偏移的数据，
            Complex[] rawFFT = fft(rawExt); // 原始的无偏移的数据
            Complex[] conjRawFFT = conj(rawFFT);
            Complex[] temp = mul(conjRawFFT, recFFT);
            Complex[] ret = ifft(temp);
            return complex2real(ret);
        }

    }

}
