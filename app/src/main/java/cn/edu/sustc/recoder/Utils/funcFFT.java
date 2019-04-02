package cn.edu.sustc.recoder.Utils;

public class funcFFT {


    public static Complex[] fft(Complex[] x) {
        int N = x.length;
        // base case
        if (N == 1) return new Complex[] { x[0] };
        // radix 2 Cooley-Tukey FFT
        if (N % 2 != 0) { throw new RuntimeException("N is not a power of 2:"+N); }

        // fft of even terms
        Complex[] even = new Complex[N/2];
        for (int k = 0; k < N/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] q = fft(even);
        // fft of odd terms
        Complex[] odd  = even;  // reuse the array
        for (int k = 0; k < N/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] r = fft(odd);

        // combine
        Complex[] y = new Complex[N];
        for (int k = 0; k < N/2; k++) {
            double kth = -2 * k * Math.PI / N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k]       = q[k].plus(wk.times(r[k]));
            y[k + N/2] = q[k].minus(wk.times(r[k]));
        }
        return y;
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

    public static Complex[] int2complex(int[] real)
    {
        Complex[] ret = new Complex[real.length];

        // original data
        for (int i = 0; i < real.length; i++) {
            ret[i] = new Complex((double)real[i],0);
        }
        return ret;
    }

    public static Complex[] real2complex(double[] real)
    {
        Complex[] ret = new Complex[real.length];

        // original data
        for (int i = 0; i < real.length; i++) {
            ret[i] = new Complex(real[i],0);
        }
        return ret;
    }


    public static double[] xcorr(int[] recData,int[] rawData)
    {
        Complex[] recFFT = fft(int2complex(recData));// 有偏移的数据，
        Complex[] rowFFT = fft(int2complex(rawData)); // 原始的无偏移的数据
        Complex[] conjRowFFT = funcFFT.conj(rowFFT);
        Complex[] temp = funcFFT.mul(conjRowFFT,recFFT);
        Complex[] ret = funcFFT.ifft(temp);
        return complex2real(ret);
    }

    public static double[] xcorr(int[] recData, int[] rawData, boolean recLaargerThanRaw) {
        int length1 = recData.length;
        int length2 = rawData.length;
        int[] rawExt = new int[length1];
        for (int i = 0; i < length2; i++) {
            rawExt[i] = rawData[i];
        }
        Complex[] recFFT = fft(int2complex(recData));// 有偏移的数据，
        Complex[] rawFFT = fft(int2complex(rawExt)); // 原始的无偏移的数据
        Complex[] conjRawFFT = funcFFT.conj(rawFFT);
        Complex[] temp = funcFFT.mul(conjRawFFT,recFFT);
        Complex[] ret = funcFFT.ifft(temp);
        return complex2real(ret);
    }

    public static int[] byte2int16(byte[] byteData)
    {
        if (byteData.length % 2 == 0) {
            int[] ret = new int[byteData.length / 2];
            for (int i = 0; i+1 < byteData.length; i = i + 2) {
                ret[i / 2] = byteData[i] + byteData[i + 1] * 256;
            }
            return ret;
        } else {
            throw new IllegalStateException("array must in odd length");
        }

    }
}